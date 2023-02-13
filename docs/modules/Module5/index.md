# Module 5: Operate and Monitor AKS
In this module you will learn how to operate and monitor Azure Kubernetes Service.  You will learn about [Container Insights](https://learn.microsoft.com/en-us/azure/azure-monitor/containers/container-insights-overview), [Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction), and [Sentinel](https://learn.microsoft.com/en-us/azure/sentinel/overview).

## Container Insights
Container Insights is a feature designed to monitor the performance of container workloads deployed to the cloud. It gives you performance visibility by collecting memory and processor metrics from controllers, nodes, and containers that are available in Kubernetes through the Metrics API. After you enable monitoring from Kubernetes clusters, metrics and Container logs are automatically collected for you through a containerized version of the Log Analytics agent for Linux. Metrics are sent to the [metrics database in Azure Monitor](https://learn.microsoft.com/en-us/azure/azure-monitor/essentials/data-platform-metrics). Log data is sent to your [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).

### Enable Container Insights
Container Insights is designed to store its data in a [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).  You can let the enablement process create a Log Analytics workspace for this purpose, or if you already have a workspace, you can use that one.  See [Designing your Azure Monitor Logs deployment](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/workspace-design) to learn more about best practices for Log Analytics.

Here, let's begin by creating a Log Analytics workspace in order to support Container Insights.  Right now, we will do this using the Azure CLI.  Later, we will augment our Bicep templates in order to perform this same work.

```
az monitor log-analytics workspace create --resource-group $GROUP --workspace-name $WORKSPACE
WORKSPACEID=$(az monitor log-analytics workspace show --resource-group $GROUP --workspace-name $WORKSPACE --query id -o tsv)
```

Now, let's augment our cluster and enable Container Insights.

```
az aks enable-addons -a monitoring --resource-group $GROUP --name $CLUSTER --workspace-resource-id $WORKSPACEID
```

Let's verify that the Container Insights agent and solution were successfully deployed.  First, we'll verify the daemonset was deployed:

```
kubectl get daemonset ama-logs --namespace=kube-system
```

The output should resemble the following:

```
NAME       DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
ama-logs   3         3         3       3            3           <none>          45m
```

Next, we'll verify that the deployment was created:

```
kubectl get deployment ama-logs-rs --namespace-kube-system
```

The output should resemble the following:

```
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
ama-logs-rs   1/1     1            1           47m
```

### Using Container Insights

Now that Container Insights has been enabled, we can turn our attention to the Azure Portal and see the results of our labor.  (It may take a few minutes for data to flow into the Log Analytics workspace.)

Within the Portal, navigate to the cluster.  Once inside the cluster, check out the Monitoring section of the menu system and open the Insights tab.  Here, you will be presented with a nice visualization of your cluster, showing node count, CPU, and memory utilization.  You'll also a graph showing the active pod count.  These views are dynamic.  You can change the time range, or even look at live data from the cluster.

![Container Insights Dashboard](../../assets/images/module5/ContainerInsightsCluster.png)

 Aside from the Cluster view within the Insights tab, you will find lists that desscribe your cluster's nodes, controllers and containers.  You will also find a tab dedicated to reports.  These data driven reports provide additional insight into your cluster nodes, resource utilization, networking, and billing.

Now, let's take a moment and test Container Insights by applying some load to our cluster.

First, let's create a namespace to hold our work.

```
kubectl create namespace containerinsightstest
kubectl config set-context --current --namespace containerinsightstest
```

Next, let's run an interactive bash Pod on the cluster:

```
kubectl run test-shell --rm -i --tty --image ubuntu -- bash
```

Now, within the test-shell Pod, update, install and run stress:

```
apt update
apt install stress
stress -c 10
```

The above commands will generate a sustained CPU spike in the cluster.  Return to Container Insights and view the Cluster tab.  Turn on Live updates and you should see the Node CPU Utilization graph jump as a result of the stress command.

![Container Insights Dasboard](../../assets/images/module5/ContainerInsightsClusterNodeCPU.png)

Next, change the view by clicking on the Nodes tab.  Here, you will see a summary of what's happening inside the cluster.  Notice that one of your nodes (The one running stress) should be much more busy than the others.

![Container Insights Nodes Tab](../../assets//images/module5/ContainerInsightsClusterNodes.png)

Find the node that appears to be the most busy in your cluster and expand its line item.  Here, you will see a list of the processes running on that node.  You should see our test-shell pod running stress at the top of this list.

![Container Insights Node Details](../../assets/images/module5/ContainerInsightsClusterNodesProcesses.png)

Next, change the view by clicking on the Containers tab.  Here, you will be presented with a list of containers running on the cluster.  Notice that our test-shell pod is at the top of the list.

![Container Insights Containers Tab](../../assets/images/module5/ContainerInsightsClusterContainers.png)

Select the test-shell container and you'll get a description of the container.

![Container Insights Container Overview](../../assets/images/module5/ContainerInsightsClusterContainersOverview.png)

Here, you can also see a live stream of the container console and events.

![Container Insights Container Events](../../assets/images/module5/ContainerInsightsClusterContainersLiveEvents.png)

Return to test-shell and type ctrl-c to terminate stress.  Then, exit the pod.

```
exit
```

Now, let's clean our cluster:

```
kubectl delete namespace containerinsightstest
kubectl config set-context --current --namespace default
```

### Additional Diagnostics 

Container Insights provides excellent visibility within our Kubernetes Clusters.  However, we can get even more visibility by streaming diagnostics data into the Azure Log Analytics workspace we just created.  AKS offers you the ability to stream many types of diagnostic data, including log data from various sources as well as performance metrics.

Use the following CLI command to turn begin streaming select diagnostics data into Log Analytics

```
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

```
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

```
// Inside Cluster Definition; add the following to properties

    addonProfiles: {
      omsAgent: {
        enabled: true
        config: {
          logAnalyticsWorkspaceResourceID: workspace.id
        }
      }
```

Finally, add in Diagnostics at the end of the template:

```
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

## Defender for Containers
Defender for Containers is a cloud-native solution that may be used to secure your containers, helping you to improve, monitor, and maintain the security of your clusters, containers, and their applications.

Defender for Containers assists you with the three core aspects of container security:
- Environment hardening - protects your Kubernetes clusters whether they're running Azure Kubernetes Service, Kubernetes on-premises/IaaS, or Amazon EKS.  Defender for Contaniners continuously assesses clusters to provide visibility into misconfigurations and guidelines to help mitigate identified threats.
- Vulnerability assessment - tools to manage and assess images stored in container registries and running in Azure Kubernetes Service
- Run-time theat protection for nodes and clusters - threat protection for clusters and Linux nodes generates security alerts for suspicious activities

You can learn more by reading the [documentation](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction), or watching this video from the Defender for Cloud in the Field video series: [Microsoft Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/episode-three).

### Enable Defender for Containers

To begin, open the Azure Portal and navigate to [Defender for Cloud](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-cloud-introduction) - your Cloud Security Posture (CSPM) and Cloud Workload Protection Platform (CWPP).

![Defender for Cloud](../../assets/images/module5/DefenderForCloud.png)

The Defender for Cloud Overview tab will open and you'll find a dashboard describing your security posture, regulatory compliance, and more.  For now, turn your attention to the Management section of the navigation menu and click on Environment Settings.

From within the Environment Settings tab, you will be presented with an expandable list of your Azure Management Groups and their associated resources.  Find and open your Azure Subscription in this list.  You will be taken to a view that shows a list of all the Defender plans available to your subscription and their status.

If the Containers Plan is not enabled, enable it. 

![Defender for Cloud - Toggle Enabled](../../assets/images//module5/DefenderForCloudPlansEnable.png)

A Settings link will appear within the description of your Defender for Containers plan.  Click it.

![Defender for Cloud Settings](../../assets/images/module5/DefenderForCloudPlansSettings.png)

Here, you have the ability to toggle automatic installation/application of Defender for Cloud components, namely, the Defender DaemonSet and Azure Policy for Kubernetes.  If these items are disabled, enable them.  This will ensure any clusters you create in the future are automatically enrolled in the service.

Note: the Defender profile uses a default Log Analytics workspace.  If you don't already have a default Log Analytics workspace, Defender for Cloud will create a new resource group and workspace for you when the profile is installed.  The default workspace is created based on your region.

The naming convention for the default Log Analytics workspace and resource group is:
- Workspace: DefaultWorkspace-[Subscription-ID]-[geo]
- Resource Group: DefaultResourceGroup-[geo]

See [Assign a custom workspace](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-enable) for instructions on how to change the workspace.

Now, let's use the CLI in order to check and see if our cluster is enrolled in Defender for Containers.  If not, we'll take steps to fix it.  Return to your command line and issue the following commands:

```
kubectl get pods -n kube-system
```

Check the output of this command for the Defender for Containers pods.  If everything is well, you should see a pod called `microsoft-defender-XXXX` in `Running` state.  If not, issue the following command and repeat the previous step to check the result:

```
az aks update --resource-group $GROUP --name $CLUSTER --enable-defender
```

Next, let's check Azure Policy for Kubernetes.  We'll start by checking our cluster for the presence of required pods:

 ```
# check for azure-policy
kubectl get pods -n kube-system

# check for gatekeeper 
kubectl get pods -n gatekeeper-system
```

The above output should show both Azure Policy and Gatekeeper pods are running.

Next, verify that the Azure Policy add-on is installed by running this command:

```
az aks show --resource-group $GROUP --name $CLUSTER --query addonProfiles.azurepolicy 
```

If the previous command does not show Azure Policy, then issue the following command, wait a few minutes and then return to step 9 to make sure everything is well.

```
az aks enable-addons --resource-group $GROUP --name $CLUSTER --addons azure-policy 
```

### Using Defender for Containers

Now that Defender for Containers is enabled in our cluster, let's simulate a security alert.  Run the following command:

```
kubectl get pods --namespace=asc-alerttest-662jfi039n
```

The above is a test command that is designed to trigger a test alert.  The following output is expected:

```
No resources found in asc-alerttest-662jfi039n namespace.
```

Next, return to the Azure Portal, navigate to the cluster and click on the Defender for Cloud menu option.

![Defender for Cloud Menu](../../assets/images/module5/DefenderForCloudMenu.png)

Inside Defender for Cloud you will see a summary of Recommendations and Security Alerts for the Cluster. 

![Defender for Cloud Recommendations and Security Alerts](../../assets/images/module5/DefenderForCloudRecommendationsAndAlertsBig.png)

Near the top of the screen, you will see that there is at least 1 security alert.  This alert will correspond to the `kubectl get pods` command we ran just a few steps ago.
   
![Defender for Cloud Security Alerts](../../assets/images/module5/DefenderForCloudAlerts.png)

We can also test the Security Alerting facility by executing a test command within a running pod on our cluster.  Return to the command line and execute the following procedure to trigger another test alert:
   
```
kubectl create namespace defendertest
kubectl config set-context --current --namespace defendertest
kubectl run test-shell --rm -i --tty --image ubuntu -- bash
```

Within test-shell:

```
cp /bin/echo ./asc_alerttest_662jfi039n
./asc_alerttest_662jfi039n testing eicar pipe
exit
```

Original console:

```
kubectl delete namespace defendertest
kubectl config set-context --current --namespace default
```

Return to Defender for Cloud in the Azure Portal and monitor Security Alerts.  Within ten minutes, you will find a second alert that corresponds to this most recent test.

![Defender for Cloud Test Alert](../../assets/images/module5/DefenderForCloudPodAlert.png)

Separate from the Security Alerting capabilities within Defender for Cloud, you can also check out the Recommendations Section.  Here, you will find an actionable list of security recommendations for the cluster.  Click on one of the recommendations and you will be presented a view showing a description of the recommendation and instructions on how you can fix the associated issue.

![Defender for Cloud Recommendation Details](../../assets/images/module5/DefenderForCloudRecommendationDetails.png)

## Sentinel