# Module 4: Deploy - Lab
AKS (Azure Kubernetes Service) DevSecOps Deployment refers to the process of deploying containerized applications on AKS while ensuring security and collaboration between development, security, and operations teams.

The operations team deploys the application to AKS, ensuring that security policies and procedures are applied throughout the deployment process.

By following the AKS DevSecOps Deployment process, organizations can ensure that their containerized applications are secure and reliable, and that the development, security, and operations teams are working together effectively to deliver high-quality software.

In this module you will learn a set of foundational methods to secure your app and/or solution to AKS. We invite you to run labs in the sections below to get familiar with the tools and concepts involved in this process.  

## Environments
Environments are used to describe a general deployment target like production, staging, or development. When a GitHub Actions workflow deploys to an environment, the environment is displayed on the main page of the repository. 
You can configure environments with protection rules and secrets. When a workflow job references an environment, the job won't start until all of the environment's protection rules pass. A job also cannot access secrets that are defined in an environment until all the environment protection rules pass.
### **Lab 1 - Creating enviroments, rules and secrets**

1. On GitHub.com, navigate to the main page of your forked repository.
2. Under your repository name, click  Settings. If you cannot see the "Settings" tab, select the "[...]"  dropdown menu, then click Settings.
![Settings](../../assets/images/module4/repo-settings.webp "Settings")
3. In the left sidebar, click Environments.
4. Click New environment.
5. Enter a name for the environment (e.g.: "test"), then click Configure environment. Environment names are not case sensitive. An environment name may not exceed 255 characters and must be unique within the repository.
6. Specify your github handle/username in the list of "Required reviewers". This will set you as user that must review and approve workflow jobs that use this environment.
7. Click Save protection rules.
8. Add 3 enviroment secrets by following the next steps:
    - Under Environment secrets, click "Add secret".
        - Enter "TEST_AZURE_CLIENT_ID"
        - Enter the value of <i>$appId</i> retrieved from Module 0.
        - Click Add secret.
        > Note: if you don't have this value saved, you can always retrieve it from the ClientID property of the Managed Identity (default:"workshop-Identity") object deployed in your resource group.  
    - Under Environment secrets, click "Add secret" again.
        - Enter "TEST_AZURE_TENANT_ID"
        - Enter the value of your tenant id.
        - Click Add secret.
    - Under Environment secrets, click "Add secret" again.
        - Enter "TEST_AZURE_SUBSCRIPTION_ID"
        - Enter the value of your subscription id.
        - Click Add secret.

        The result should be similar to this:<br>
        ![Environment secrets](../../assets/images/module4/environment-secrets.webp)

9. Add 5 environment variables to store the values needed by the workflow to execute its deployment to the test enviroment:
    - Under Environment variables, click Add variable.
        - Enter "TEST_AZURE_CONTAINER_REGISTRY"
        - Enter the name of the Azure Container Registry deployed in your resource group(e.g.: devsecopsacr1        
        - Click Add variable. 
    - Repeat the previous steps to add the following enviroment variables:
        - TEST_CONTAINER_NAME: name of your acr repo (e.g.: testdevsecops) 
        - TEST_RESOURCE_GROUP: name of your resource group
        - TEST_CLUSTER_NAME: name of your aks cluster 
        - TEST_DEPLOYMENT_MANIFEST_PATH: which is the path to the app manifest present in this lab. Therefore, please use   "tools/deploy/module4/devsecops-demo.yaml" (or whichever manifest you'd like to use) 

10. In [test-credential.json](../../../tools/deploy/module4/test-credential.json) file, replace `<your-github-username>` with your GitHub username (in your local repo).

   `"subject": "repo:<your-github-username>/AKS-DevSecOps-Workshop:environment:test",`

   >If you have named your new repository something other than `AKS-DevSecOps-Workshop`, you will need to replace `AKS-DevSecOps-Workshop` above with the name of your repository. Also, if your deployment branch is not `main`, you will need to replace `main` with the name of your deployment branch.

11. Setup additional federated identity credentials specifically for the test enviroment by:
- retrieving the appId created in module 0. <br>If not saved in your session, you can retrieve the appid specific of this lab by looking at your App Registrations in the Azure Portal (Azure Active Directory section) or by az command. The following command will retrieve a list of all your registered applications (list of displayName + appId).  
```bash 
az ad app list --show-mine --query "[].{displayName:displayName appId:appId createdDateTime:createdDateTime}" -o tsv
```
> please identify in the list the app registration related to this lab (the one created in Module 0) by recognizing its displayName or its createdDateTime displayed in the output of the previous command. Then use the appId (guid only) to run the following command: 
```
appId=<appId from previous command>
```
> the appId is a guid with a format similar to this: 00000000-0000-0000-0000-000000000000
- in the same bash session, run the following command:
```bash
az ad app federated-credential create --id $appId --parameters tools/deploy/module4/test-credential.json
```
12. Copy [mod4-lab1-deploy-test.yml](../../../tools/tools/deploy/module4/mod4-lab1-deploy-test.yml) to [github/workflow](../../../.github/workflows/) folder.
13. Modify [text.txt](../../../tools/deploy/module4/text.txt) file and commit & push all changes to trigger the Actions workflow.
> The workflow is pre-configured to be triggered when any change is pushed to that specific pattern. 
14. Please monitor the execution of the workflow by navigating to "Actions":
![Actions](../../assets/images/module4/actions-workflow-execution.webp)
15. Click on the workflow currently executing:
![Actions](../../assets/images/module4/worflow-run-executing.webp)
> Note:
> 1. The label of the workflow run depends on the Commit message you provided when pushing the changes. 
> 2. You may notice the status Waiting. This depends on the Environment rule we set in step 6. In fact, you will have to review and approve the workflow execution to allow the workflow to run and have permissions to the environment secrets and variables vital to the execution of the workflow. 
> 3. If he trigger doesn't start the workflow, you can run the workflow manually by selecting it and click on "Run workflow":
![Manual dispatch](../../assets/images/module4/workflow-run-manually.webp)

16. Click on the executing workflow 
