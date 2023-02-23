---
title: Lab - Workload Identity
parent: Module 1 - Plan phase
has_children: false
nav_order: 2
---
# Module 1 - Workload Identity

Workload Identity allows pods to access Azure resources using Azure managed identities and removes the need to store any principal secrets.  For example, given a workload that may store files in Azure Storage,  when it needs to access those files, the pod authenticates itself against the resource as an Azure managed identity.

Workload Identity for AKS integrates with the Kubernetes native capabilities to federate with any external identity providers.

The feature sunsets the existing AAD Pod-Managed Identity offering and makes it easier to use and deploy, and overcome several limitations in AAD Pod-Managed Identity.

![Sequence Diagram](../../assets/images/module1/aks-workload-identity-model.png)

## Enabling Workload Identity

> **Note**
> As of Feb. 2023, this feature is in public preview, with expectations that GA is soon, so the following 'Register preview providers' section will not be required once the feature is GA.

Set the following environment variables in your bash session by updating the values and executing in your terminal. Update the values in the <> brackets. The values for these can be pulled from your lab resource group:

```bash
SUBSCRIPTION_ID=<SubscriptionID>
KEYVAULT_NAME=<Keyvault found in RG>
RG_NAME=rg-aks-gha
LOCATION=eastus
CLUSTER_NAME=devsecops-aks
KEYVAULT_SECRET_NAME=mysecret
```

### Register preview providers on your subscription

1. Run the following command to register the preview provider feature for Workload Identity:
```bash
az feature register --namespace "Microsoft.ContainerService" --name "EnableWorkloadIdentityPreview"
```
2. Wait for the feature to be enabled by running this command, the state should show "Registered" when complete. This may take up to 10 minutes
```bash
az feature show --namespace "Microsoft.ContainerService" --name "EnableWorkloadIdentityPreview"
```
3. Once the Workload Identity preview feature is registered, you must register the parent Microsoft.ContainerService resource provider.
```bash
az provider register --namespace Microsoft.ContainerService
```
4. Add and/or update the aks-preview extension with the Azure CLI.
```bash
az extension add --name aks-preview 
az extension update --name aks-preview
```

### Enable ODIC and Workload Identity on the AKS Cluster

<!-- Internal Ref note: Consider changing this to update in the Bicep template instead of running by CLI.
-->

1. Execute the following CLI command to enable oidc-issuer and to enable workload identity on your AKS cluster. This operation will take several minutes.
```bash
az aks update --resource-group $RG_NAME --name $CLUSTER_NAME --enable-oidc-issuer --enable-workload-identity
```
2. Set the ODIC Issuer URL to a variable for usage later. 
```bash
export AKS_OIDC_ISSUER="$(az aks show -n $CLUSTER_NAME -g $RG_NAME --query "oidcIssuerProfile.issuerUrl" -otsv)"
echo $AKS_OIDC_ISSUER
```
3. Verify that you now see a mutating webhook pod on your cluster:
```bash
az aks get-credentials --resource-group $RG_NAME  --name $CLUSTER_NAME --admin
kubectl get pods -n kube-system | grep webhook
```

### Create a managed identity and grant permission to Azure Keyvault

1. Create Managed Identity in your resource group
```bash
export USER_ASSIGNED_IDENTITY_NAME="workshop-Identity"
az identity create --name "${USER_ASSIGNED_IDENTITY_NAME}" --resource-group "${RG_NAME}" --location "${LOCATION}" --subscription "${SUBSCRIPTION_ID}"
```
2. Create Access Policy against Keyvault, allowing the identity to get secrets.
```bash
export USER_ASSIGNED_CLIENT_ID="$(az identity show --resource-group "${RG_NAME}" --name "${USER_ASSIGNED_IDENTITY_NAME}" --query 'clientId' -otsv)"
az keyvault set-policy --name "${KEYVAULT_NAME}" --secret-permissions get --spn "${USER_ASSIGNED_CLIENT_ID}"
```
3. Create a Secret in Keyvault
```bash
export USERID=$(az ad signed-in-user show --query id -o tsv)
az keyvault set-policy --name "${KEYVAULT_NAME}" --secret-permissions list set get --object-id $USERID
az keyvault secret set --vault-name "${KEYVAULT_NAME}" \
       --name "${KEYVAULT_SECRET_NAME}" \
       --value "LevelUp Lab Secret\!"
```

### Create a Service Account and Establish Federated Identity

1. Create/Deploy Service Account K8S YAML. Note the annotations and labels required for this service account to leverage Workload Identity.
   
```
export SERVICE_ACCOUNT_NAME="workload-identity-sa"
export SERVICE_ACCOUNT_NAMESPACE="default"

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  annotations:
    azure.workload.identity/client-id: "${USER_ASSIGNED_CLIENT_ID}"
  labels:
    azure.workload.identity/use: "true"
  name: "${SERVICE_ACCOUNT_NAME}"
  namespace: "${SERVICE_ACCOUNT_NAMESPACE}"
EOF
```

2. Establish Federated Identity. The namespace and service account name are used to create the subject identifier in the federation. Once this is setup, this application will now trust tokens coming from our Kubernetes cluster.
```bash
az identity federated-credential create --name myfederatedIdentity --identity-name "${USER_ASSIGNED_IDENTITY_NAME}" --resource-group "${RG_NAME}" --issuer "${AKS_OIDC_ISSUER}" --subject system:serviceaccount:"${SERVICE_ACCOUNT_NAMESPACE}":"${SERVICE_ACCOUNT_NAME}"
```

After the federation is setup, navigate to your cluster resource group, and you will now see an identity. Click the Identity resource and select the "Federated credentials" blade under Settings.

![Managed Identity](../../assets/images/module1/ManagedIdentity.png)

### Deploy Sample workload & Test

The following YAML to deploy a sample .net application that writes to the log the content of the secret inside keyvault. The Go application expects two environment variables for the Kevault URL and the Keyvault secret name references. You can find source code for different programming languages that implement MSAL and KeyVault integration [here](https://github.com/Azure/azure-workload-identity/tree/main/examples).

Note the following required annotations on the K8S YAML configuration:

- azure.workload.identity/use: "true"
- serviceAccountName: ${SERVICE_ACCOUNT_NAME}

1. Execute: 
   
```
export KEYVAULT_URL="$(az keyvault show -g ${RG_NAME} -n ${KEYVAULT_NAME} --query properties.vaultUri -o tsv)"

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: quick-start
  namespace: ${SERVICE_ACCOUNT_NAMESPACE}
  labels:
    azure.workload.identity/use: "true"
spec:
  serviceAccountName: ${SERVICE_ACCOUNT_NAME}
  containers:
    - image: ghcr.io/azure/azure-workload-identity/msal-net
      name: oidc
      env:
      - name: KEYVAULT_URL
        value: ${KEYVAULT_URL}
      - name: SECRET_NAME
        value: ${KEYVAULT_SECRET_NAME}
EOF
```

2. Once the pod is running, ensure the pod is showing the KeyVault secret:
```bash
kubectl logs quick-start
```
If the pod communication to the KeyVault was successful, you will see the following message:
![Pod Logs](../../assets/images/module1/podlogs.png)

3. Inspect the additional environment variables and volumeMounts created:
```bash
kubectl get pod quick-start -o yaml
```
You should notice the new environment variables and volume mount, as shown in this screenshot.
![Pod YAML](../../assets/images/module1/podyaml.png)
4. Additionally, you can inspect the token mounted to the pod by executing into the quick-start container. The path here is found in the volumeMount. You can further copy this token and paste it into [jwt.io](https://jwt.io) to inspect the content of the token.
```bash
kubectl exec quick-start -- cat /var/run/secrets/azure/tokens/azure-identity-token
```

This completes the hands-on lab.

## Troubleshooting

In the case you do not see the above logs, validate the environment variables and delete/re-deploy the application.

For troubleshooting the azure-workload-identity components, you can view and isolate errors from logs with the following command:

```bash
kubectl logs -n kube-system -l azure-workload-identity.io/system=true  --since=1h | grep ^E
```

<!--
## Optional - Update Bicep template

- TBD
--->
