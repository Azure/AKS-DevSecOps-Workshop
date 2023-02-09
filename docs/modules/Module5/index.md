# Module 5: Operate and Monitor AKS
In this module you will learn how to operate and monitor Azure Kubernetes Service.  You will learn about [Container Insights](https://learn.microsoft.com/en-us/azure/azure-monitor/containers/container-insights-overview), [Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction), and [Sentinel](https://learn.microsoft.com/en-us/azure/sentinel/overview).

## Container Insights
Container insights is a feature designed to monitor the performance of container workloads deployed to the cloud. It gives you performance visibility by collecting memory and processor metrics from controllers, nodes, and containers that are available in Kubernetes through the Metrics API. After you enable monitoring from Kubernetes clusters, metrics and Container logs are automatically collected for you through a containerized version of the Log Analytics agent for Linux. Metrics are sent to the [metrics database in Azure Monitor](https://learn.microsoft.com/en-us/azure/azure-monitor/essentials/data-platform-metrics). Log data is sent to your [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).

### Enable Container Insights
Container insights is designed to store its data in a [Log Analytics workspace](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-workspace-overview).  You can let the enablement process create a Log Analytics workspace for this purpose, or if you already have a workspace, you can use that one.  See [Designing your Azure Monitor Logs deployment](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/workspace-design) to learn more about best practices for Log Analytics.

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

Now that Container Insights has been enabled, we can turn our attention to the Azure Portal and see the results of our labor.  (It may take a few minutes for data to flow into the Log Analytics workspace.)

Within the Portal, navigate to the cluster.  Once inside the cluster, check out the Monitoring section of the menu system and open the Insights tab.  Here, you will be presented with a nice visualization of your cluster, showing node count, CPU, and memory utilization.  You'll also a graph showing the active pod count.  These views are dynamic.  You can change the time range, or even look at live data from the cluster.

![Container Insights Dashboard](../../assets/images/module5/ContainerInsightsCluster.png)

Aside from the Cluster view within the Insights tab, you will find lists that desscribe your cluster's nodes, controllers and containers.  You will also find a tab dedicated to reports.  These data driven reports provide additional insight into your cluster nodes, resource utilization, networking, and billing.

## Defender for Containers

## Sentinel