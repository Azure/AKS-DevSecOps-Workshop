---
title: Lab - GitHub security code scanning
parent:  Module 2 - Develop phase
has_children: false
nav_order: 2
---

# Module 2: Develop - GitHub Security Code scanning using CodeQL

### Perform Static Code Analysis (SAST) 

Use GitHub Security scanning capabilities for code scanning using CodeQL which is the code analysis engine developed by GitHub. Code scanning is a feature that you use to analyze the code in a GitHub repository to find security vulnerabilities and coding errors. After you enable CodeQL, GitHub Actions will execute workflow runs to scan your code and display the results as code scanning alerts. The alerts provide detailed information on the source of the issue and along with details on remediation and fixes.

## Lab Module 2b - Enable GitHub code scanning on your source repository  
&nbsp;

We can use the sample vulnerable app from the earlier lab (2a) for this lab exercise as well, while the Snyk extension in the earlier lab was used for doing client side analysis, GitHub code scanning will be used for server side checks to ensure all code committed to the remote repository is scanned for security vulnerabilities.

1. In the GitHub console, enable code scanning within your git repository settings, **note** for forked repositories, its important that you first enable GitHub actions before security code scanning can be enabled as shown below.

   ![GitHub actions Enable](../../assets/images/module2/ghub-action-enable.png)
   
    After GitHub actions is enabled, you can click on the Security menu option and setup code scanning as shown below

   ![code scanning enable](../../assets/images/module2/code-scanning-enable.png)

    Click on the setup CodeQL and select the "advanced" option as shown below

   ![secrets scanning enable](../../assets/images/module2/code-scan-advanced.png)


2. The previous step will auto generate a starter GitHub action `codeql.yml` file, we will make a small change to the `codeql.yml` to configure scanning for our sample app. Please make the following change as shown in the section below, we will comment out the Autobuild section and introduce a custom build command instead

   ```yaml
       - name: Build Java
         run: |
           mvn clean package -f tools/deploy/module2/pom.xml
   ```
       The completed codeql.yml file should then look as follows, we will keep all the other default values.

   ![secrets scanning enable](../../assets/images/module2/codeql-change.png)

3. Commit the `codeql.yml` file to the repository, this should automatically trigger the github code scanning action to analyze your application code.

   ![codeql github action](../../assets/images/module2/github-action-codeql.png)
 

4. Following the GitHub action run, you should be able to view details of the security analysis and get details and severity level of each issue with specific instructions on remediation.

   ![codeql alerts](../../assets/images/module2/codescan-results.png)

   Clicking on a specific alert displays the security severity details and remediation steps.

   ![codeql alert details](../../assets/images/module2/codescan-details.png)