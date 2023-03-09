---
title: Lab Instructions
parent: Module 3 - Build Phase
has_children: false
nav_order: 2
---

# Build Phase - Lab Instructions

## Lab 1 - Github DependaBot

### Enable Dependabot security and version updates for your repository

* Enable Dependabot alerts for your repository
  * Navigate to the **Settings** tab of your repository
  * Select **Codes security & analysis** from the left-hand menu
  * Click **Enable** next to **Dependabot alerts**
* Enable Dependabot security updates for your repository
  * Click **Enable** next to **Dependabot security updates**
* Enable Dependabot version updates for your repository
  * Click **Enable** next to **Dependabot version updates** to open a basic `dependabot.yml` configuration file in the `.github` directory of your repository
  * set `- package-ecosystem` to `"maven"` and `directory` to `"/tools/deploy/module3"` in the `dependabot.yml` file

  ```yaml
  - package-ecosystem: "maven" # See documentation for possible values
    directory: "/tools/deploy/module3" # Location of package manifests
  ```

  * Click **Commit changes** to commit the file to the `main` branch

### Viewing Dependabot alerts and security updates

* Navigate to the **Security** tab of your repository
* Click **Dependabot alerts** to view the alerts
![Dependabot Alerts Screenshot](../../assets/images/module3/dependabot-alerts.png)
* Click on the alert to view the details of the alert, including initiating a pull request for **security version update** 
* After you enable version updates, the **Dependabot** tab in the **dependency graph** under **Insights** for the repository is populated (it may take few minutes to complete). 
![Dependabot Tab Screenshot](../../assets/images/module3/dependabot-dependency-graph.png)
* Click on **Last Checked 4 minutes ago** to see details.
* Scroll down to see summary of the **Security updates Pull Requests** details.
![Dependabot Security Updates Screenshot](../../assets/images/module3/dependabot-security-updates-details.png)
* Navigate to the **Pull requests** tab of your repository to view the pull request initiated by Dependabot
![Dependabot Pull Request Screenshot](../../assets/images/module3/dependabot-pull-requests.png)

## Lab 2 - Github Secret Scanning

### Enable Secret Scanning for your repository

* Navigate to the **Settings** tab of your repository
* Select **Codes security & analysis** from the left-hand menu
* Click **Enable** next to **Secret scanning**

### Viewing Secret Scanning results

* Navigate to the **Security** tab of your repository
* Click **Secret scanning** to view the result one file with a Google Key
![Secret Scanning Alerts Screenshot](../../assets/images/module3/secret-scaning-alert.png)
* Click on the file name to view the details of the alert, including the remediation steps.
![Secret ScanningAlerts Detail Screenshot](../../assets/images/module3/secret-scanning-alert-details.png)