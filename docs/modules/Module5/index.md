# Module 5: Operate and Monitor AKS
In this module you will learn how to operate and monitor Azure Kubernetes Service.  You will learn about [Container Insights](https://learn.microsoft.com/en-us/azure/azure-monitor/containers/container-insights-overview), [Azure Policy for Kubernetes](https://learn.microsoft.com/en-us/azure/governance/policy/concepts/policy-for-kubernetes), and [Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction).

## Getting Started
In order to complete the hands-on portion of this module, you will need an Azure Kuberentes Service Cluster as described in the Module 0 Prerequisites.  You will also need a Bash Shell, the Azure CLI and the following environment variables:

```bash
# replace bracketed values with names from your environment
GROUP="[AKS-CLUSTER-RESOURCE-GROUP-NAME]"
CLUSTER="[AKS-CLUSTER-NAME]"
WORKSPACE="[DESIRED-LOG-ANALYTICS-WORKSPACE-NAME]"
``` 

Next, for timing reasons, let's jump ahead a bit and make sure our Azure Subscription is ready to use Azure Policy.  To do this, check to see if Microsoft.PolicyInsights is registered:
```bash
az provider show --namespace Microsoft.PolicyInsights
```

If the provider is not present, you must register it:

```bash
az provider register --namespace Microsoft.PolicyInsights
```

## Container Insights
Container Insights is a feature designed to monitor the performance of container workloads deployed to the cloud. It gives you performance visibility by collecting memory and processor metrics from controllers, nodes, and containers that are available in Kubernetes through the Metrics API. After you enable monitoring from Kubernetes clusters, metrics and Container logs are automatically collected for you through a containerized version of the Log Analytics agent for Linux. Metrics are sent to the [metrics database in Azure Monitor](https://learn.microsoft.com/en-us/azure/azure-monitor/essentials/data-platform-metrics). Log data is sent to your [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).

### Enable Container Insights
Container Insights is designed to store its data in a [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).  You can let the enablement process create a Log Analytics workspace for this purpose, or if you already have a workspace, you can use that one.  See [Designing your Azure Monitor Logs deployment](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/workspace-design) to learn more about best practices for Log Analytics.

Here, let's begin by creating a Log Analytics workspace in order to support Container Insights.  Right now, we will do this using the Azure CLI.  Later, we will augment our Bicep templates in order to perform this same work.

```bash
az monitor log-analytics workspace create --resource-group $GROUP --workspace-name $WORKSPACE
WORKSPACEID=$(az monitor log-analytics workspace show --resource-group $GROUP --workspace-name $WORKSPACE --query id -o tsv)
```

Now, let's augment our cluster and enable Container Insights.

```bash
az aks enable-addons -a monitoring --resource-group $GROUP --name $CLUSTER --workspace-resource-id $WORKSPACEID
```

Let's verify that the Container Insights agent and solution were successfully deployed.  First, we'll verify the daemonset was deployed:

```bash
kubectl get daemonset ama-logs --namespace=kube-system
```

The output should resemble the following:

```bash
NAME       DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
ama-logs   3         3         3       3            3           <none>          45m
```

Next, we'll verify that the deployment was created:

```bash
kubectl get deployment ama-logs-rs --namespace=kube-system
```

The output should resemble the following:

```bash
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
ama-logs-rs   1/1     1            1           47m
```

### Using Container Insights

Now that Container Insights has been enabled, we can turn our attention to the Azure Portal and see the results of our labor.  (It may take a few minutes for data to flow into the Log Analytics workspace.)

Within the Portal, navigate to the cluster.  Once inside the cluster, check out the Monitoring section of the menu system and open the Insights tab.  Here, you will be presented with a nice visualization of your cluster, showing node count, CPU, and memory utilization.  You'll also a graph showing the active pod count.  These views are dynamic.  You can change the time range, or even look at live data from the cluster.

![Container Insights Dashboard](../../assets/images/module5/ContainerInsightsCluster.png)

> Aside from the Cluster view within the Insights tab, you will find lists that desscribe your cluster's nodes, controllers and containers.  You will also find a tab dedicated to reports.  These data driven reports provide additional insight into your cluster nodes, resource utilization, networking, and billing.

Now, let's take a moment and test Container Insights by applying some load to our cluster.

First, let's create a namespace to hold our work.

```bash
kubectl create namespace containerinsightstest
kubectl config set-context --current --namespace containerinsightstest
```

Next, let's run an interactive bash Pod on the cluster:

```bash
kubectl run test-shell --rm -i --tty --image ubuntu -- bash
```

Now, within the test-shell Pod, update, install and run stress:

```bash
apt update
apt install stress
stress -c 10
```

The above commands will generate a sustained CPU spike in the cluster.  Return to Container Insights and view the Cluster tab.  Turn on Live updates and you should see the Node CPU Utilization graph jump as a result of the stress command.

<img alt="Container Insights Dashboard" src="../../assets/images/module5/ContainerInsightsClusterNodeCPU.png" height="200px">

Next, change the view by clicking on the Nodes tab.  Here, you will see a summary of what's happening inside the cluster.  Notice that one of your nodes (The one running stress) should be much more busy than the others.

![Container Insights Nodes Tab](../../assets//images/module5/ContainerInsightsClusterNodes.png)

Find the node that appears to be the most busy in your cluster and expand its line item.  Here, you will see a list of the processes running on that node.  You should see our test-shell pod running stress at the top of this list.

![Container Insights Node Details](../../assets/images/module5/ContainerInsightsClusterNodesProcesses.png)

Next, change the view by clicking on the Containers tab.  Here, you will be presented with a list of containers running on the cluster.  Notice that our test-shell pod is at the top of the list.

![Container Insights Containers Tab](../../assets/images/module5/ContainerInsightsClusterContainers.png)

Select the test-shell container and you'll get a description of the container.

![Container Insights Container Overview](../../assets/images/module5/ContainerInsightsClusterContainersOverview.png)

Here, you can also see a live stream of the container console and events.

<img alt="Container Insights Container Events" src="../../assets/images/module5/ContainerInsightsClusterContainersLiveEvents.png" height="480px">

Return to test-shell and type ctrl-c to terminate stress.  Then, exit the pod.

```bash
exit
```

Now, let's clean our cluster:

```bash
kubectl delete namespace containerinsightstest
kubectl config set-context --current --namespace default
```

### Additional Diagnostics 

Container Insights provides excellent visibility within our Kubernetes Clusters.  However, we can get even more visibility by streaming diagnostics data into the Azure Log Analytics workspace we just created.  AKS offers you the ability to stream many types of diagnostic data, including log data from various sources as well as performance metrics.

Use the following CLI command to turn begin streaming select diagnostics data into Log Analytics

```bash
CLUSTERID=$(az aks show --resource-group $GROUP --name $CLUSTER --query id -o tsv)
echo '['>diag.config
echo '{"category": "cluster-autoscaler", "enabled": true},'>>diag.config
echo '{"category": "guard", "enabled" :true},'>>diag.config
echo '{"category": "kube-apiserver", "enabled": true},'>>diag.config
echo '{"category": "kube-audit", "enabled": true},'>>diag.config
echo '{"category": "kube-audit-admin", "enabled": true},'>>diag.config
echo '{"category": "kube-controller-manager", "enabled": true},'>>diag.config
echo '{"category": "kube-scheduler", "enabled": true}'>>diag.config
echo ']'>>diag.config

az monitor diagnostic-settings create \
--name "diag01" \
--resource "$CLUSTERID" \
--workspace "$WORKSPACEID" \
--logs @diag.config

rm diag.config
```

### Update Bicep Templates

Now that we have enabled Container Insights, let's go back and update our Bicep tempaltes in order to make sure our deployment process picks up the changes.

First, add the Log Analytics workspace to the template:

```bash
// Parameters...

@description('Log Analytics Workspace name')
param workspaceName string

// Log Analytics Workspace Definition 
resource workspace 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: workspaceName
  location: location
}

// Cluster Definition...
```

Next, adjust the AKS cluster and enable Container Insights:

```bash
// Inside Cluster Definition; add the following to properties

addonProfiles: {
    omsAgent: {
        enabled: true
        config: {
            logAnalyticsWorkspaceResourceID: workspace.id
        }
    }

    // ...
}
```

Finally, add in Diagnostics at the end of the template:

```bash
resource diag01 'Microsoft.Insights/diagnosticSettings@2021-05-01-preview' = {
    name: 'diag01'
    scope: aks
    properties: {
        logs: [{
            category: 'cluster-autoscaler'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        }, {
            category: 'guard'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        }, {
            category: 'kube-apiserver'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        },
        {
            category: 'kube-audit'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            } 
        }, {
            category: 'kube-audit-admin'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        }, {
            category: 'kube-controller-manager'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        }, {
            category: 'kube-scheduler'
            enabled: true
            retentionPolicy: {
                days: 0
                enabled: false
            }
        }]
        workspaceId: workspace.id
    }
}
```

## Azure Policy for Kubernetes
Azure Policy is a feature of the Azure Resource Management Platform which allows organizations to define and enforce stanards across their environments.  Azure Policy for Kubernetes is a Kubernetes add-on that works with Azure Policy.  The add-on extends extends [Gatekeeper](https://github.com/open-policy-agent/gatekeeper) v3, an _admission controller webhook_ for [Open Policy Agent](https://www.openpolicyagent.org/) (OPA).  Once installed, the add-on does the following:

- Check with Azure Policy Service for policy assignments to the cluster
- Deploys policy definitions into the cluster as [constraint template](https://open-policy-agent.github.io/gatekeeper/website/docs/howto/#constraint-templates) and [constraint](https://github.com/open-policy-agent/gatekeeper#constraints) custom resources
- Reports auditing and compliance details back to the Azure Policy Service

### Enable Azure Policy for Containers

To begin, check to see if Microsoft.PolicyInsights is registered in your Azure Subscription:
```bash
az provider show --namespace Microsoft.PolicyInsights
```

If the provider is not present, you must register it:

```bash
az provider register --namespace Microsoft.PolicyInsights
```

Next, let's return to the AKS Cluster and install the add-on:

```bash
az aks --resource-group $GROUP --name $CLUSTER enable-addons azure-policy
```

Then, verify that the add-on has been installed:

```bash
az aks show --resource-group $GROUP --name $CLUSTER --query addonProfiles.azurepolicy
```

The output should resemble the following:
```bash
{
    // ...
    "enabled": true,
    // ...
}
```

### Using Azure Policy for Kubernetes

Azure Policy for Kubernetes may be viewed and managed through the Azure Portal.  Simply open Azure Policy and navigate to the Definitions tab in the Authoring section.

![Azure Policy Menu](../../assets/images/module5/AzurePolicyMenu.png)

Expand the Category filter and clear all the checked items except for Kubernetes.  This will filter the view down to just Azure Polciy for Kubernetes Definitions and Initiatives.  (Simply put, an Initiative is a collection of one or more Definitions applied to a particular scope, like a Resource Group.)

![Azure Policy Definition Filter](../../assets/images/module5/AzurePolicyDefinitionFilter.png)

Azure Policy for Kubernetes provides some pre-defined iniatives.  Let's open and apply one to our infrastructure.  Find the "Kubernetes cluster pod security baseline standards for Linux-based workloads" and click it.

![Azure Policy Initiative](../../assets/images/module5/AzurePolicyInitiative.png)

You will see that the selected Initiative is comprised of five Policy Definitions.  Notice that one is titled, `Kubernetes cluster should not allow privileged containers.`  Let's assign this initiative to our Cluster's Resource Group.  

Click the `Assign` button located at the top of the Initiative tab.

![Assign Azure Policy Initiative](../../assets/images/module5/AzurePolicyAssign.png)

You will be preseted with the Portal's Assign Initiative wizard.  On the Basics Tab, use the Scope control to select your Cluster's Resource Group.

Then, on the Parameters tab, uncheck the `Only show parameters that need input or review` checkbox.  This will update the screen and you will be able to change the value of the `Effect` control.  Change `Effect` from `Audit` to `deny`.

Then, click `Review + create` followed by `Create`.  This will apply the Initiative, however, it may take up to twenty minutes for the changes to be reflected on your Cluster.

Once the Initiative is in place, you can test it out by doing the following:

First, let's create a namespace to hold our work.

```bash
kubectl create namespace azurepolicytest
kubectl config set-context --current --namespace azurepolicytest
```

Next, let's run an interactive bash Pod on the cluster in privileged mode:

```bash
kubectl run test-shell --rm -i --tty --privileged=true --image ubuntu -- bash
```

This previous command should fail due to our new Azure Policy for Kubernetes Initiative.

```bash
Error from server (Forbidden): admission webhook "validation.gatekeeper.sh" denied the request: [azurepolicy-k8sazurev2noprivilege-bba3aab5a745b0e81725] Privileged container is not allowed: test-shell, securityContext: {"privileged": true}
```

Now, let's clean up our cluster:

```bash
kubectl delete namespace azurepolicytest
kubectl config set-context --current --namespace default
```

### Update Bicep Templates

Now that we have enabled Azure Policy for Kubernetes, let's go back and update our Bicep tempaltes in order to make sure our deployment process picks up the changes.

Add the following to your Bicep template:

```bash
// add the following to addonProfiles
"azurepolicy": {
    "enabled": true
},
```

## Defender for Containers
Defender for Containers is a cloud-native solution that may be used to secure your containers, helping you to improve, monitor, and maintain the security of your clusters, containers, and their applications.

Defender for Containers assists you with the three core aspects of container security:
- Environment hardening - protects your Kubernetes clusters whether they're running Azure Kubernetes Service, Kubernetes on-premises/IaaS, or Amazon EKS.  Defender for Contaniners continuously assesses clusters to provide visibility into misconfigurations and guidelines to help mitigate identified threats.
- Vulnerability assessment - tools to manage and assess images stored in container registries and running in Azure Kubernetes Service
- Run-time theat protection for nodes and clusters - threat protection for clusters and Linux nodes generates security alerts for suspicious activities

> You can learn more by reading the [documentation](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction), or watching this video from the Defender for Cloud in the Field video series: [Microsoft Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/episode-three).

### Enable Defender for Containers

To begin, open the Azure Portal and navigate to [Defender for Cloud](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-cloud-introduction) - your Cloud Security Posture (CSPM) and Cloud Workload Protection Platform (CWPP).

<img alt="Defender for Cloud" src="../../assets/images/module5/DefenderForCloud.png" height="200px">

The Defender for Cloud Overview tab will open and you'll find a dashboard describing your security posture, regulatory compliance, and more.  For now, turn your attention to the Management section of the navigation menu and click on Environment Settings.

From within the Environment Settings tab, you will be presented with an expandable list of your Azure Management Groups and their associated resources.  Find and open your Azure Subscription in this list.  You will be taken to a view that shows a list of all the Defender plans available to your subscription and their status.

If the Containers Plan is not enabled, enable it. 

![Defender for Cloud - Toggle Enabled](../../assets/images//module5/DefenderForCloudPlansEnable.png)

A Settings link will appear within the description of your Defender for Containers plan.  Click it.

![Defender for Cloud Settings](../../assets/images/module5/DefenderForCloudPlansSettings.png)

Here, you have the ability to toggle automatic installation/application of Defender for Cloud components, namely, the Defender DaemonSet and Azure Policy for Kubernetes.  (We looked at Azure Policy for Kubernetes in the previous section.) If these items are disabled, enable them.  This will ensure any clusters you create in the future are automatically enrolled in the service.

> Note: the Defender profile uses a default Log Analytics workspace.  If you don't already have a default Log Analytics workspace, Defender for Cloud will create a new resource group and workspace for you when the profile is installed.  The default workspace is created based on your region.
>
> The naming convention for the default Log Analytics workspace and resource group is:
> - Workspace: DefaultWorkspace-[Subscription-ID]-[geo]
> - Resource Group: DefaultResourceGroup-[geo]
>
> See [Assign a custom workspace](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-enable) for instructions on how to change the workspace.

Now, let's use the CLI in order to check and see if our cluster is enrolled in Defender for Containers.  If not, we'll take steps to fix it.  Return to your command line and issue the following commands:

```bash
kubectl get pods -n kube-system
```

Check the output of this command.  You should see two Defender profile components in the output:
- microsoft-defender-collector-ds-* - this is a set of containers that focus on collecting inventory and security events from the Kubernetes environment
- microsoft-defneder-publisher-ds-* - this set of containers publish the collected data to the Microsoft Defender for Containers backend service where the data will be processed and analyzed.

You may also be interested to see the associated deployment:

```bash
kubectl get deployment -n kube-system
```

The above should include:
- microsoft-defender-collector-misc-*

If, for some reason, the above components are not present on your cluster, you may install them by issuing the following command.  Then, return to the previous verification steps to make sure everything is in order.

```bash
az aks update --resource-group $GROUP --name $CLUSTER --enable-defender
```

### Using Defender for Containers

Now that Defender for Containers is enabled in our cluster, let's simulate a security alert.  Run the following command:

```bash
kubectl get pods --namespace=asc-alerttest-662jfi039n
```

The above is a test command that is designed to trigger a test alert.  The following output is expected:

```bash
No resources found in asc-alerttest-662jfi039n namespace.
```

Next, return to the Azure Portal, navigate to the cluster and click on the Defender for Cloud menu option.

<img alt="Defender for Cloud Menu" src="../../assets/images/module5/DefenderForCloudMenu.png" height="480px">

Inside Defender for Cloud you will see a summary of Recommendations and Security Alerts for the Cluster. 

![Defender for Cloud Recommendations and Security Alerts](../../assets/images/module5/DefenderForCloudRecommendationsAndAlertsBig.png)

Near the top of the screen, you will see that there is at least one security alert.  This alert will correspond to the `kubectl get pods` command we ran just a few steps ago.
   
![Defender for Cloud Security Alerts](../../assets/images/module5/DefenderForCloudAlerts.png)

We can also test the Security Alerting facility by executing a test command within a running pod on our cluster.  Return to the command line and execute the following procedure to trigger another test alert:
   
```bash
kubectl create namespace defendertest
kubectl config set-context --current --namespace defendertest
kubectl run test-shell --rm -i --tty --image ubuntu -- bash
```

Within test-shell:

```bash
cp /bin/echo ./asc_alerttest_662jfi039n
./asc_alerttest_662jfi039n testing eicar pipe
exit
```

Original console:

```bash
kubectl delete namespace defendertest
kubectl config set-context --current --namespace default
```

Return to Defender for Cloud in the Azure Portal and monitor Security Alerts.  Within ten minutes, you will find a second alert that corresponds to this most recent test.

![Defender for Cloud Test Alert](../../assets/images/module5/DefenderForCloudPodAlert.png)

### Update Bicep Templates

Finally, let's adjust the cluster's Bicep template in order to reflect these changes:

```bash
// ...
// Enable defender for containers by adding the following to the cluster's properties
// you will need the resource id for the log analytics workspace
securityProfile: {
    defender: {
    logAnalyticsWorkspaceResourceId: '/subscriptions/[SUBSCRIPTION-ID]/resourcegroups/[RESOURCE-GROUP]/providers/microsoft.operationalinsights/workspaces/[WORKSPACE-NAME]'
    securityMonitoring: {
        enabled: true
    }
}
```