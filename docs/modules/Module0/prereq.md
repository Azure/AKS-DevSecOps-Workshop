---
title: Pre-requisites
parent: Module 0 - Introduction and Pre-requisites
has_children: false
nav_order: 2
---

# Pre-requisites

## Requirements

<<<<<<< HEAD
* Azure Subscription
* WSL or Linux Terminal
* Azure CLI
* An [SSH public key](https://cda.ms/2nD).
  If you need to create a key, run the following command:
=======
* Azure Subscription (if you don't have one, you can create a free account [here](https://azure.microsoft.com/en-us/free/))
* Azure CLI (if you don't have one, you can install it [here](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli))
* Github Account (if you don't have one, you can create one [here](https://github.com)
>>>>>>> e466fa426800d0f5778a06efda4207ef25d6fc33

## AKS Cluster Deployment via GitHub Actions using OpenID Connect and Bicep (IaC)

For this workshop, we will be using GitHub Actions using OpenID Connect and Infrastructure-as-Code (IaC) using Bicep to deploy the AKS cluster, to derive following benefits:

* Infrastructure-as-Code (IaC) - Infrastructure is defined as code, and can be version controlled and reviewed. 
* OpenID Connect - OpenID Connect is an authentication protocol that allows you to connect securely to Azure resources using your GitHub account.
* GitHub Actions - GitHub Actions is a feature of GitHub that allows you to automate your software development workflows.
* Bicep - Bicep is a Domain Specific Language (DSL) for deploying Azure resources declaratively. It aims to drastically simplify the authoring experience with a cleaner syntax, improved type safety, and better support for modularity and code re-use.

<<<<<<< HEAD
## Environment Setup

1. Create a Service Principal for Github Actions'
  Note: Replace <subscription-id> with your subscriptionId
=======
This will require performing the following tasks:
>>>>>>> e466fa426800d0f5778a06efda4207ef25d6fc33

1. Forking this repository into your GitHub account 
2. Creating an Azure Resource Group
3. Configuring OpenID Connect in Azure.
4. Setting Github Actions secrets
5. Triggering the GitHub Actions workflow

### Forking this repository into your GitHub account

<<<<<<< HEAD
1. Create a Resource Group.
=======
* Fork this repository into your GitHub account by clicking on the "Fork" button at the top right of this page.
* Clone your newly forked repository to your local machine.
>>>>>>> e466fa426800d0f5778a06efda4207ef25d6fc33


<<<<<<< HEAD
   1. 

   * Import this repository as a new repository in your GitHub account, by navigating to the following URL: [Import Repo](https://github.com/new/import).  You can also find this by clicking the `Import a repository` link on the create a new repository within GitHub.  
     * Enter `https://github.com/Azure/AKS-DevSecOps-Workshop` in `Your old repository’s clone URL` field
     * Enter the name of your new repository in the `Name your new repository` field
     * Click `Begin import`
   * Once the import is complete, set the following Github Actions secrets:

     `AZURE_CREDENTIALS: <is the output of sp.txt>`

     `AZURE_SUBSCRIPTION_ID: <subscription-id>`

     `AZURE_TENANT_ID: <tenant-id>`

     `AZURE_RESOURCE_GROUP: <resource-group>`

     `CLUSTER_NAME: <cluster-name>`
=======
### Creating an Azure Resource Group

```bash
az login
resourceGroupName="rg-aks-gha"
location="eastus"
az group create --name $resourceGroupName --location $location
```

### Configuring OpenID Connect in Azure

1. Create an Azure AD application

   ```bash
   appId=$(az ad app create --display-name myOidcApp --query appId --output tsv)
   echo $appId
   ```

2. Create a service principal for the Azure AD app.
>>>>>>> e466fa426800d0f5778a06efda4207ef25d6fc33

   ```bash
   assigneeObjectId=$(az ad sp create --id $appId --query id --output tsv)
   echo $assigneeObjectId 
   ```

3. Create a role assignment for the Azure AD app.

   ```bash
   subscriptionId=$(az account show --query id --output tsv)
   echo $subscriptionId
   az role assignment create --role contributor --subscription $subscriptionId --assignee-object-id  $assigneeObjectId --assignee-principal-type ServicePrincipal --scope /subscriptions/$subscriptionId/resourceGroups/$resourceGroupName
   ```

<<<<<<< HEAD
```bash
export SSH=
export RG_NAME=
export LOCATION=
az login
az account set --subscription $SUBSCRIPTION
az group create --name $NAME --location $LOCATION
az deployment group create --template-file ../../../tools/deploy/module0/aks.bicep --resource-group $RG_NAME --parameters location=$LOCATION sshRSAPublicKey=$SSH linuxAdminUsername=workshopadmin
```
=======
4. Configure a federated identity credential on the Azure AD app.

   You use workload identity federation to configure an Azure AD app registration to trust tokens from an external identity provider (IdP), such as GitHub.

   In [credential.json](../../../tools/deploy/module0/credential.json) file, replace `<your-github-username>` with your GitHub username (in your local repo).

   `"subject": "repo:<your-github-username>/AKS-DevSecOps-Workshop:ref:refs/heads/main",`

   If you name your new repository something other than `AKS-DevSecOps-Workshop`, you will need to replace `AKS-DevSecOps-Workshop` above with the name of your repository. Also, if your deployment branch is not `main`, you will need to replace `main` with the name of your deployment branch.

   Then run the following command to create a federated credential for the Azure AD app.

   ```bash
   az ad app federated-credential create --id $appId --parameters tools/deploy/module0/credential.json
   ```

### Setting Github Actions secrets

1. Open your forked Github repository and click on the `Settings` tab.
2. In the left-hand menu, expand `Secrets and variables`, and click on `Actions`.
3. Click on the `New repository secret` button for each of the following secrets:
   * `AZURE_SUBSCRIPTION_ID`(this is the `subscriptionId`from the previous step)
   * `AZURE_TENANT_ID` (run `az account show --query tenantId --output tsv` to get the value)
   * `AZURE_CLIENT_ID` (this is the `appId` from the JSON output of the `az ad app create` command)
   * `CLUSTER_RESOURCE_GROUP` (this is the `resourceGroupName` from earlier step)

### Triggering the GitHub Actions workflow

* Enable GitHub Actions for your repository by clicking on the "Actions" tab, and clicking on the `I understand my workflows, go ahead and enable them` button.
* To trigger the AKS deployment workflow manually:
  * click on the `Actions` tab.
  * Select `.github/workflows/infra-deployment-workflow.yml`.
  * Click on the `Run workflow` button.
* Alternatively, you can make a change to the [aks.bicep](../../../tools/deploy/module0/aks.bicep) file (e.g. change the `clusterName` parameter), and `push` the change to your Github repo. This will trigger the GitHub Actions workflow.

## Alternative AKS Cluster Deployment - via Azure CLI

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
>>>>>>> e466fa426800d0f5778a06efda4207ef25d6fc33

* To connect to your cluster:

   ```bash
   clusterName=devsecops-aks
   az aks get-credentials --name $clusterName --resource-group $resourceGroupName --admin
   kubectl get nodes
   ```
