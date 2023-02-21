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

## Infrastructure Deployment via GitHub Actions

For this workshop, we will be using GitHub Actions to deploy the infrastructure.  This will require performing the following tasks:

1. Cloning/Importing this repository into your GitHub account 
2. Creating an Azure Resource Group
3. Configuring OpenID Connect in Azure. OpenID Connect (OIDC) allows your GitHub Actions workflows to access resources in Azure, without needing to store the Azure credentials as long-lived GitHub secrets. 
4. Setting Github Actions secrets
5. Triggering the GitHub Actions workflow

### Cloning/Importing this repository into your GitHub account

* Import this repository as a new repository in your GitHub account, by navigating to the following URL: [Import Repo](https://github.com/new/import).  You can also find this by clicking the `Import a repository` link on the create a new repository within GitHub.  
  * Enter `https://github.com/Azure/AKS-DevSecOps-Workshop` in `Your old repository’s clone URL` field
  * Enter the name of your new repository in the `Name your new repository` field
  * Click `Begin import`

### Creating an Azure Resource Group

```bash
az group create --name "rg-aks-gha" --location "eastus"
```

### Configuring OpenID Connect in Azure

```bash
az ad app create --display-name myNewOidcApp
az ad sp create --id $appId
az role assignment create --role contributor --subscription $subscriptionId --assignee-object-id  $assigneeObjectId --assignee-principal-type ServicePrincipal --scope /subscriptions/$subscriptionId/resourceGroups/$resourceGroupName
az ad app federated-credential create --id $appId input.json
```

### Setting Github Actions secrets

1. Open your Github repository and click on the "Settings" tab.
2. In the left-hand menu, expand "Secrets and variables", and click on "Actions".
3. Click on the "New repository secret" button for each of the following secrets:
   * `AZURE_SUBSCRIPTION_ID`
   * `AZURE_TENANT_ID`
   * `AZURE_CLIENT_ID`
   * `AZURE_RESOURCE_GROUP`

### Triggering the GitHub Actions workflow

When you commit these updates to the main branch, GitHub Actions will deploy your AKS cluster by executing [infra-deployment-workflow.yml](../../../.github/workflows/infra-deployment-workflow.yml).

## Infrastructure Deployment Manually

1. Create a Resource Group.

  `az group create --name "rg-aks-gha" --location "eastus"`

1. Deploy the AKS cluster bicep template:

```bash
az deployment group create --template-file ../../../tools/deploy/module0/aks.bicep --resource-group $RG_NAME --parameters location=$LOCATION
```

## Connect to your cluster

* To connect to your cluster:

```bash
az aks get-credentials --name $NAME --resource-group $NAME
kubectl get nodes
```
