# Module 0 - PreRequisites

This lab will leverage Codespaces to perform the modules. To learn more about Codespaces, go to [GitHub Codespaces Documentation - GitHub Docs](https://docs.github.com/en/codespaces).

**Note:** If you cannot use Codespaces, you can use WSL2 with the following tools installed: [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli), [helm](https://helm.sh/docs/intro/install/), [Bicep Tools](https://docs.microsoft.com/en-us/azure/azure-resource-manager/bicep/install), [kubectl](https://kubernetes.io/docs/tasks/tools/), [jq](https://stedolan.github.io/jq/download/)

## Running the Labs in Github Codespace

- Go to the GitHub repository for this Lab: [aks-advanced-autoscaling](https://github.com/Azure/aks-advanced-autoscaling)
- Click the `Code` button on this repo
  - Select `Codespaces` tab

![Create Codespace](../../assets/images/0-CodespacesTab.png)  
  - If you don't see `Codespaces` tab, you will need to first [link your Microsoft alias to your GitHub account](https://docs.opensource.microsoft.com/github/accounts/linking/) 

![Create Codespace](../../assets/images/0-OpenWithCodespaces.jpg)
- Click `New codespace`
- Choose the `2 core` option
- Log in to Azure from a bash or zsh terminal via: `az login --use-device-code`
- Proceed to [Module1!](../Module1/index.md)

