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

* Create a Service Principal for Github Actions

`az ad sp create-for-rbac --name "sp-aks-gha" --role "Contributor" --scopes /subscriptions/<subscription-id> --sdk-auth> sp.txt` 

This `sp.txt` file now contains your service principal credentials to login to your Azure account when running GitHub Actions.  Now to add them as secrets within the GitHub Secrets environment variables.

* Create a Resource Group.

To create a Resource Group:

`az group create --name "rg-aks-gha" --location "westus"`

* Clone this repository.
* Set the following Github Actions secrets:

```bash
AZURE_CREDENTIALS: <is the output of `sp.txt`>
AZURE_SUBSCRIPTION_ID: <subscription-id>
AZURE_TENANT_ID: <tenant-id>
AZURE_RESOURCE_GROUP: <resource-group>
CLUSTER_NAME: <cluster-name>
```

* update ![aks.bicep](../../.github/workflows/aks.bicep) with your SSH public key and name of the cluster.
* When you commit to the main branch, it will kick off a build.  You'll get an AKS cluster
* To connect to your cluster:

```bash
az aks get-credentials --name $NAME --resource-group $NAME
kubectl get nodes
```
