---
title: Pre-requisites
parent: Module 0 - Introduction and Pre-requisites
has_children: false
nav_order: 2
---

# Pre-requisites

## Basics

* Azure Subscription
* An [SSH public key](https://cda.ms/2nD).
  To create a key:

    `ssh-keygen -m PEM -t rsa -b 4096`

  To retrieve the public key:

    `cat ~/.ssh/id_rsa.pub`

* Create a Service Principal for Github Actions

  `az ad sp create-for-rbac --name "sp-aks-gha" --role "Contributor" --scopes /subscriptions/<subscription-id> --sdk-auth> sp.txt` 

  This `sp.txt` file now contains your service principal credentials to login to your Azure account when running GitHub Actions.  Now to add them as secrets within the GitHub Secrets environment variables.

* Create a Resource Group.

  `az group create --name "rg-aks-gha" --location "westus"`

* Import this repository as a new repository in your GitHub account.  You can do this by clicking the `Import repository` button on the GitHub home page.  
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
az login
az account set --subscription $SUBSCRIPTION
az group create --name $NAME --location $LOCATION
az deployment group create --template-file ../../../tools/deploy/module0/aks.bicep --resource-group $NAME --parameters name=$NAME location=$LOCATION sshRSAPublicKey=$SSH
```

* To connect to your cluster:

```bash
az aks get-credentials --name $NAME --resource-group $NAME
kubectl get nodes
```
