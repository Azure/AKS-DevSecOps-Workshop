---
title: Deploy Best Practices
parent: Module 4 - Deploy Phase
has_children: false
nav_order: 1
---

## Deploy Phase - Best Practices
During the deploy phase, the operations team - in collaboration with the security team - deploys the application to AKS, ensuring that security policies and procedures are applied throughout the deployment process.

By following the AKS DevSecOps practices, organizations can ensure that their containerized applications are secure and reliable, and that the development, security, and operations teams are working together effectively to deliver high-quality software.


### **Best practice – Control the access and workflow of the deployment pipeline**
- By using [environments](https://docs.github.com/actions/deployment/targeting-different-environments/using-environments-for-deployment) for deployment, you can configure environments with protection rules and secrets.
- You can protect important branches by [setting branch protection rules](https://docs.github.com/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches). These rules define whether collaborators can delete or force push to the branch. They also set requirements for any pushes to the branch, such as passing status checks or a linear commit history.
- You can take advantage of the [Approvals](https://docs.github.com/actions/managing-workflow-runs/reviewing-deployments) and [Gates](https://learn.microsoft.com/en-us/azure/devops/pipelines/release/deploy-using-approvals?view=azure-devops) feature to control the workflow of the deployment pipeline. For example, you can require manual approvals from a security or operations team before a deployment to a production environment.

###  **Best practice – Secure deployment credentials**
- [OpenID Connect (OIDC)](https://docs.github.com/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-azure) lets your GitHub Action workflows access resources in Azure without needing to store the Azure credentials as long-lived GitHub secrets.
- By using environments for deployment, you can configure environments with protection rules and secrets.
    - A [pull-based approach to CI/CD with GitOps](https://learn.microsoft.com/en-us/azure/architecture/example-scenario/apps/devops-with-aks) lets you shift security credentials to your Kubernetes cluster, which reduces the security and risk surface by removing credentials from being stored in your external CI tooling. You can also reduce allowed inbound connections and limit admin-level access to your Kubernetes clusters.

### **Best practice – Run dynamic application security tests (DAST) to find vulnerabilities in your running application**
- Use GitHub Actions in deployment workflows to run dynamic application security testing [(DAST)](https://github.com/marketplace?category=testing&type=actions&query=) tests.
Use open-source tools such as OWASP ZAP to do penetration testing for common web application vulnerabilities.

### **Best practice – Deploy container images from trusted registries only**
Use Defender for Containers to enable Azure Policy add-on for Kubernetes.
Enable Azure Policy so that container images can only be deployed from trusted registries.

> There are components like Notary that may simplify the process of verifying images but we'll delegate the decision of using it to you and/or the customers.  




