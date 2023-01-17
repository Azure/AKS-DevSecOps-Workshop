# Module 1 - Deploy Infrastructure

## Deploy Infrastructure

To deploy the infrastructure for this workshop you can choose from one of the two options below.  This will deploy your resources into a single resource group.  Once the lab is complete, you can clean up the resources by deleting the resource group that is created upon deployment.

Use the following parameter references when deploying infrastructure:

#### Parameters Reference

Parameter Name | Description 
-------------- | ----------- 
 rgname | this parameter is the name of the resource group - the template is a subscription scope which deploys a resource group with all of the necessary resources.  Record this value for use in the subsequent lab modules.
 alias | this parameter is used to create a unique name for your resources and dns prefix for your aks cluster. **This must be unique** As a recommendation, use the first 4 characters of your alias along with 4 random numbers. Example: tedd2022. Record this value for use in the subsequent lab modules.
 location | this parameter is the azure datacenter that the resource group and resources will be created in - with the exception of the azure load testing resource which is further limited.  the allowed values for this parameter are datacenters that support aks 
 loadTestingLocation | the datacenter for load testing - allowed values are datacenters that support the load testing resource 

### Option 1 - Deploy using button

Open the button below in a new window and provide the parameters in the portal - see parameters below for an explanation of the template parameters

[![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FAzure%2Faks-advanced-autoscaling%2Fmain%2Ftools%2Fdeploy%2Fmodule1%2Fdeployrg.json)


## Validate & Check Deployment

Visit the new resource group that was created in the deployment and ensure that the following resources are deployed - 
![azure resources screenshot](../../assets/images/module1/lvlupresources.png)

The following table lists the resources that should be created:

| Resource Type | Resource Name |
|------------------------|-------------------------|
| Kubernetes Service | akscluster |
| Network Security Group | **alias**AKSNSG |
| Container Registry | **alias**lvlupacr |
| Key Vault | **alias**lvlupkeyvault |
| Azure Load Testing | **alias**lvluploadtesting |
| Virtual Network | **alias**lvlupvnet |
| Service Bus Namespace | **alias**servicebus |

Click on the link for the virtual network, and then click on subnets from the left hand navigation.  Ensure there are 2 subnets listed, one called aksSubnet and a second one called aciSubnet Look for \<alias>AKSNSG resource to be associated to the aksSubnet. note: the aciSubnet will likely also have an associated NSG that is created by policy if deploying to a corporate subscription - 
![subnets view of vnet](../../assets/images/module1/nsgassociation.png)

Click on the link for the security group and make sure that there is an inbound rule with priority 1000 to allow http such as - 
![nsg rule 1000 showing open port 80](../../assets/images/module1/port80rule.png)

Go back to the resource group, click on the Service Bus resource, and choose Queues from the left hand navigation.  Ensure there is a queue named orders such as - 
![view of Queues in service bus](../../assets/images/module1/ServiceBusCheck.png)

Finally, go back to the resource group, click on the Azure Container Registry resource, on the left hand navigation, choose Access control (IAM) - choose the Role assignments tab from the top context menu, and check that your akscluster-agentpool has been given AcrPull permissions to the container registry -
![container registry permissions view](../../assets/images/module1/acr_permissions.png)

Once finished, proceed to [Module2!](../Module2/index.md)
