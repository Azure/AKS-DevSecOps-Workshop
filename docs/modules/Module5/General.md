---
title: General
parent: Module 5 - Operate and Monitor AKS
has_children: false
nav_order: 1
---

# Module 5: Operate and Monitor AKS
In this module you will learn how to operate and monitor Azure Kubernetes Service.  You will learn about [Container Insights](https://learn.microsoft.com/en-us/azure/azure-monitor/containers/container-insights-overview), [Azure Policy for Kubernetes](https://learn.microsoft.com/en-us/azure/governance/policy/concepts/policy-for-kubernetes), and [Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction).

## Best Practices
Throughout this module, we will implement a subset of the best practices desccribed in [DevSecOps on Azure Kubernetes Service (AKS)](https://learn.microsoft.com/en-us/azure/architecture/guide/devsecops/devsecops-on-aks).  These best practices include items from the Operate phase:

### Best practice – Use Microsoft Defender for cloud to enable automated scanning and monitoring of your production configurations
- Run continual scanning to detect drift in the vulnerability state of your application and implement a process to patch and replace the vulnerable images.
- Implement automated configuration monitoring for operating systems.
  - Use Microsoft Defender for Cloud container recommendations (under the Compute and apps section) to perform baseline scans for your AKS clusters. Get notified in the Microsoft Defender for Cloud dashboard when configuration issues or vulnerabilities are found.
  - Use Microsoft Defender for Cloud and follow its network protection recommendations to help [secure](https://learn.microsoft.com/en-us/azure/defender-for-cloud/protect-network-resources) the network resources being used by your AKS clusters.
- Conduct a vulnerability assessment for images stored in Container Registry.
  - Implement continuous scans for running images in Container Registry by enabling [Defender for Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-vulnerability-assessment-azure).

### Best practice – Use Azure Policy to secure and govern your AKS clusters
- After installing the [Azure Policy Add-on for AKS](https://learn.microsoft.com/en-us/azure/aks/use-azure-policy), you can apply individual policy definitions or groups of policy definitions called initiatives (also called policy sets) to your cluster.
- Use [Built-in Azure policies](https://learn.microsoft.com/en-us/azure/aks/policy-reference) for common scenarios like preventing privileged containers from running or only approving allowlisted external IPs. You can also create custom policies for specific use cases.
- Apply policy definitions to your cluster and verify those assignments are being enforced.
- Use Gatekeeper to configure an admission controller that allows or denies deployments based on rules specified. Azure Policy extends Gatekeeper.
- Secure traffic between workload pods by using network policies in AKS
  - Install the network policy engine and create Kubernetes network policies to control the flow of traffic between pods in AKS. Network policy can be used for Linux-based or Windows-based nodes and pods in AKS.

### Best practice – Use Azure Monitor for Continuous monitoring and alerting
- Use Azure Monitor to collect logs and metrics from AKS. You gain insights on the availability and performance of your application and infrastructure. It also gives you access to signals to monitor your solution's health and spot abnormal activity early.
  - Continuous monitoring with Azure Monitor extends to release pipelines to gate or rollback releases based on monitoring data. Azure Monitor also ingests security logs and can alert on suspicious activity.
  - Onboard your AKS instances to Azure Monitor and configure diagnostic settings for your cluster.
    - See [Azure security baseline for Azure Kubernetes Service](https://learn.microsoft.com/en-us/security/benchmark/azure/baselines/aks-security-baseline).

### Best practice – Use Microsoft Defender for Cloud for active threat monitoring
- Microsoft Defender for Cloud provides active threat monitoring on the AKS at the node level (VM threats) and for internals.
- Defender for DevOps should be used for comprehensive visibility and provides security and operator teams with a centralized dashboard for all your CI/CD pipelines. This functionality is especially useful if you're using multi-pipeline platforms like Azure DevOps and GitHub or are running pipelines across public clouds.
- Defender for Key Vault can be used to detect unusual, suspicious attempts to access key vault accounts and can alert administrators based on configuration.
- Defender for Containers can alert on vulnerabilities found within your container images stored on Container Registry.

### Best Practice – Enable diagnostics on your Azure Resources
- By enabling Azure diagnostics across all of your workload’s resources, you have access to platform logs that provide detailed diagnostic and auditing information for your Azure resources. These logs can be ingested into Log Analytics or a SIEM solution like Microsoft Sentinel for security monitoring and alerting.

## Getting Started
In order to complete the hands-on portion of this module, you will need an Azure Kuberentes Service Cluster as described in the Module 0 Prerequisites.  You will also need a Bash Shell, the Azure CLI and the following environment variables:

```bash
# Set these variables equal to values from your environment
resourceGroupName="rg-aks-gha"
clusterName="devsecops-aks"

# Set workspace name equal to a unique log analytics workspace name
workspaceName="devsecops-logs" 
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