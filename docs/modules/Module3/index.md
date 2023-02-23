---
title: Module 3 - Build Phase

has_children: false

---


## Module 3 - Build Phase

During the build phase, developers work with the site reliability
engineers and security teams to integrate automated scans of their
application source within their CI build pipelines. The pipelines are
configured to enable security practices such as SAST, SCA, and secrets
scanning by using the CI/CD platform's security tools and extensions.

### **Best practice -- Perform Static Code Analysis (SAST) to find potential vulnerabilities in your application source code**

-   Use GitHub Advanced Security scanning capabilities for code scanning
    and CodeQL.

    -   [Code
        scanning](https://docs.github.com/enterprise-cloud@latest/code-security/code-scanning/automatically-scanning-your-code-for-vulnerabilities-and-errors/about-code-scanning)
        is a feature that you use to analyze the code in a GitHub
        repository to find security vulnerabilities and coding errors.
        Any problems identified by the analysis are shown in GitHub
        Enterprise Cloud.

    -   If code scanning finds a potential vulnerability or error in
        your code, GitHub displays an alert in the repository.

    -   You can also configure branch rules for [required status
        checks](https://docs.github.com/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/troubleshooting-required-status-checks),
        for example, to enforce that a feature branch is up to date with
        the base branch before merging any new code. This practice
        ensures that your branch has always been tested with the latest
        code.

-   Use tools like [kube-score](https://kube-score.com/) to analyze your
    Kubernetes deployment objects.

    -   kube-score is a tool that does static code analysis of your
        Kubernetes object definitions.

    -   The output is a list of recommendations of what you can improve
        to help make your application more secure and resilient.

### **Best practice -- Perform secret scanning to prevent the fraudulent use of secrets that were committed accidentally to a repository**

-   When [secret
    scanning](https://docs.github.com/enterprise-cloud@latest/code-security/secret-scanning/about-secret-scanning#about-secret-scanning-for-advanced-security)
    is enabled for a repository, GitHub scans the code for patterns that
    match secrets used by many service providers.

-   GitHub also periodically runs a full git history scan of existing
    content in repositories and sends alert notifications.

    -   For Azure DevOps, [Defender for
        Cloud](https://learn.microsoft.com/en-us/azure/defender-for-cloud/detect-credential-leaks)
        uses secret scanning to detect credentials, secrets,
        certificates, and other sensitive content in your source code
        and your build output.

    -   Secret scanning can be run as part of the Microsoft Security
        DevOps for Azure DevOps extension.

### **Best practice -- Use software composition analysis (SCA) tools to track open-source components in the codebase and detect any vulnerabilities in dependencies**

-   [Dependency
    review](https://docs.github.com/code-security/supply-chain-security/understanding-your-software-supply-chain/about-dependency-review)
    lets you catch insecure dependencies before you introduce them to
    your environment, and provides information on license, dependents,
    and age of dependencies. It provides an easily understandable
    visualization of dependency changes with a rich diff on the \"Files
    Changed\" tab of a pull request.

-   [Dependabot](https://docs.github.com/enterprise-cloud@latest/code-security/dependabot/dependabot-alerts/about-dependabot-alerts)
    performs a scan to detect insecure dependencies and sends Dependabot
    alerts when a new advisory is added to the GitHub Advisory Database
    or when dependency graph for a repository changes.

### **Best practice -- Enable security scans of Infrastructure as Code (IaC) templates to minimize cloud misconfigurations reaching production environments**

-   Proactively monitor cloud resource configurations throughout the
    development lifecycle.

-   [Microsoft
    Defender](https://learn.microsoft.com/en-us/azure/defender-for-cloud/iac-vulnerabilities)
    for DevOps supports both GitHub and Azure DevOps repositories.

### **Best practice -- Scan your workload images in container registries to identify known vulnerabilities**

-   [Defender for
    Containers](https://learn.microsoft.com/en-us/azure/defender-for-cloud/defender-for-containers-introduction#hardening)
    scans the containers in Container Registry and Amazon AWS Elastic
    Container Registry (ECR) to notify you if there are known
    vulnerabilities in your images.

-   [Azure
    Policy](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-azure-policy)
    can be enabled to do a vulnerability assessment on all images stored
    in Container Registry and provide detailed information on each
    finding.

### **Best practice -- Automatically build new images on base image update**

-   [Azure Container Registry
    Tasks](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-tasks-base-images)
    dynamically discovers base image dependencies when it builds a
    container image. As a result, it can detect when an application
    image\'s base image is updated. With one preconfigured build task,
    Container Registry tasks can automatically rebuild every application
    image that references the base image.

### **Best practice -- Use Container Registry, Azure Key Vault and notation to digitally sign your container images and configure AKS cluster to only allow validated images**

-   Azure Key Vault stores a signing key that can be used by
    [notation](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-tutorial-sign-build-push)
    with the notation Key Vault plugin (azure-kv) to
    [sign](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-tutorial-sign-build-push)
    and verify container images and other artifacts. Container Registry
    lets you attach these signatures by using the Azure CLI commands.

-   The signed containers let users make sure that deployments are built
    from a trusted entity and verify an artifact hasn\'t been tampered
    with since its creation. The signed artifact ensures integrity and
    authenticity before the user pulls an artifact into any environment,
    which helps avoid attacks.

    -   [Ratify](https://github.com/deislabs/ratify/blob/main/README.md)
        lets Kubernetes clusters verify artifact security metadata prior
        to deployment and admit for deployment only those that comply
        with an admission policy that you create.
