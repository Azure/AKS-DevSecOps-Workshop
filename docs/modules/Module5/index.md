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

## Labs
Once you have completed the above steps, you may continue by completing the following labs
- [Lab 1 - Container Insights](Lab01.md)
- [Lab 2 - Azure Policy for Kubernetes](Lab02.md)
- [Lab 3 - Defender for Containers](Lab03.md)