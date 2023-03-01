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
        - TEST_DEPLOYMENT_MANIFEST_PATH: which is the path to the app manifest present in this lab. Therefore, please use   "../../tools/deploy/module4/devsecops-demo.yaml" (or whichever manifest you'd like to use) 


## Managing a branch protection rule
In GitHub, you can create a [branch protection rule](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/managing-a-branch-protection-rule) to enforce certain workflows for one or more branches, such as requiring an approving review or passing status checks for all pull requests merged into the protected branch.

> There is a large number of rules you can set up in the Branch settings. In this exercise, we are going to focus on a limited set due to timelines and intentional simplicity of the lab but we encourage you to explore all the options present in the settings.  

### Lab 2 - Creating a branch protection rule
1. On GitHub.com, navigate to the main page of your repository.
2. Under your repository name, click Settings. If you cannot see the "Settings" tab, select the  dropdown menu, then click Settings. <br>
![Settings](../../assets/images/module4/repo-actions-settings.webp "Settings")
3. In the "Code and automation" section of the sidebar, click "Branches".
4. Next to "Branch protection rules", click Add rule.
5. Under "Branch name pattern", you can type the branch name or pattern you want to protect. For this lab, we will type "*" (pattern for <i>any</i> branch) in the textbox in order to apply this set of rules to any branch in your repository. 
![Branch name pattern](../../assets/images/module4/branch-name-pattern.webp "Branch name pattern") 
6. Under "Protect matching branches", select Require a pull request before merging.<br>
![Require pull request](../../assets/images/module4/PR-reviews-required-updated.webp "Require pull request")





