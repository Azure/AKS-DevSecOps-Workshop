---
title: Pre-requisites
parent: Module 0 - Introduction and Pre-requisites
has_children: false
nav_order: 2
---

# Pre-requisites

## Requirements

* Azure Subscription
* WSL or Linux Terminal
* Azure CLI

## Infrastructure Deployment via GitHub Actions using OpenID Connect

For this workshop, we will be using GitHub Actions using OpenID Connect to deploy the infrastructure.  This will require performing the following tasks:

1. Forking this repository into your GitHub account 
2. Creating an Azure Resource Group
3. Configuring OpenID Connect in Azure. OpenID Connect (OIDC) allows your GitHub Actions workflows to access resources in Azure, without needing to store the Azure credentials as long-lived GitHub secrets. 
4. Setting Github Actions secrets
5. Triggering the GitHub Actions workflow

### Forking this repository into your GitHub account

* Fork this repository into your GitHub account by clicking on the "Fork" button at the top right of this page.
* Clone your newly forked repository to your local machine.


### Creating an Azure Resource Group

```bash
az login
resourceGroupName="rg-aks-gha"
location="eastus"
az group create --name $resourceGroupName --location $location
```

### Configuring OpenID Connect in Azure

1. Create an Active Directory application

   ```bash
   appId=$(az ad app create --display-name myOidcApp --query appId --output tsv)
   echo $appId
   ```

2. Create a service principal.

   ```bash
   assigneeObjectId=$(az ad sp create --id $appId --query id --output tsv)
   echo $assigneeObjectId 
   ```

3. Create a role assignment.

   ```bash
   subscriptionId=$(az account show --query id --output tsv)
   az role assignment create --role contributor --subscription $subscriptionId --assignee-object-id  $assigneeObjectId --assignee-principal-type ServicePrincipal --scope /subscriptions/$subscriptionId/resourceGroups/$resourceGroupName
   ```

4. Configure a federated identity credential on the Azure AD app created in step 1. 

   You use workload identity federation to configure an Azure AD app registration to trust tokens from an external identity provider (IdP), such as GitHub.

   In [credential.json](../../../tools/deploy/module0/credential.json) file, replace `<your-github-username>` with your GitHub username (in your local repo).

   `"subject": "repo:<your-github-username>/AKS-DevSecOps-Workshop:ref:refs/heads/main",`

   If you name your new repository something other than `AKS-DevSecOps-Workshop`, you will need to replace `AKS-DevSecOps-Workshop` above with the name of your repository. Also, if your deployment branch is not `main`, you will need to replace `main` with the name of your deployment branch.

   Then run the following command to create a federated credential for the Azure AD app.

   ```bash
   az ad app federated-credential create --id $appId --parameters tools/deploy/module0/credential.json
   ```

### Setting Github Actions secrets

1. Open your newly imported Github repository and click on the "Settings" tab.
2. In the left-hand menu, expand "Secrets and variables", and click on "Actions".
3. Click on the "New repository secret" button for each of the following secrets:
   * `AZURE_SUBSCRIPTION_ID`(this is the `subscriptionId`from the previous step)
   * `AZURE_TENANT_ID` (run `az account show --query tenantId --output tsv` to get the value)
   * `AZURE_CLIENT_ID` (this is the appId from the JSON output of the `az ad app create` command)
   * `CLUSTER_RESOURCE_GROUP` (this is the `resourceGroupName` from earlier step)

### Triggering the GitHub Actions workflow

* To trigger the GitHub Actions workflow, you will need to make a change to the [aks.bicep](../../../tools/deploy/module0/aks.bicep) file. You can change the `clusterName` parameter to something unique.
* Alternatively, you can manually start the workflow by:
  * clicking on the "Actions" tab.
  * Select `.github/workflows/infra-deployment-workflow.yml` from the list of workflows on the left.
  * Click on the `Run workflow` button.

## Infrastructure Deployment Manually

1. Create a Resource Group.

  ```bash
  az login
  resourceGroupName="rg-aks-gha"
  location="eastus"
  az group create --name $resourceGroupName --location $location

1. Deploy the AKS cluster bicep template:

   ```bash
   az deployment group create --template-file tools/deploy/module0/aks.bicep --resource-group $resourceGroupName --parameters location=$location
   ```

## Connect to your cluster

* To connect to your cluster:

   ```bash
   clusterName=devsecops-aks
   az aks get-credentials --name $clusterName --resource-group $resourceGroupName --admin
   kubectl get nodes
   ```
