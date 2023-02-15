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
* An [SSH public key](https://cda.ms/2nD).
  If you need to create a key, run the following command:

    `ssh-keygen -m PEM -t rsa -b 4096`

  To retrieve the public key:

    `cat ~/.ssh/id_rsa.pub`

## Environment Setup

1. Create a Service Principal for Github Actions'
  Note: Replace <subscription-id> with your subscriptionId

  `az ad sp create-for-rbac --name "sp-aks-gha" --role "Contributor" --scopes /subscriptions/<subscription-id> --sdk-auth> sp.txt` 

  This `sp.txt` file now contains your service principal credentials to login to your Azure account when running GitHub Actions.  Now to add them as secrets within the GitHub Secrets environment variables.

1. Create a Resource Group.

  `az group create --name "rg-aks-gha" --location "westus"`

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

* update [aks.bicep](../../../tools/deploy/module0/aks.bicep) with your SSH public key:
  
  `param sshRSAPublicKey string = < output of cat ~/.ssh/id_rsa.pub >`

* When you commit these updates to the main branch, GitHub Actions will deploy your AKS cluster by executing [infra-deployment-workflow.yml](../../../.github/workflows/infra-deployment-workflow.yml).

* You can also deploy the AKS cluster manually by running the following commands:

```bash
export SSH=
export RG_NAME=
export LOCATION=
az login
az account set --subscription $SUBSCRIPTION
az group create --name $NAME --location $LOCATION
az deployment group create --template-file ../../../tools/deploy/module0/aks.bicep --resource-group $RG_NAME --parameters location=$LOCATION sshRSAPublicKey=$SSH linuxAdminUsername=workshopadmin
```

* To connect to your cluster:

```bash
az aks get-credentials --name $NAME --resource-group $NAME
kubectl get nodes
```
