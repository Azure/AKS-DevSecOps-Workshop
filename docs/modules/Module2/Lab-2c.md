---
title: Lab - pre-commit hooks
parent:  Module 2 - Develop phase
has_children: false
nav_order: 3
---

# Module 2: Develop - pre-commit hooks

## (Optional) Lab Module 2c - Install and configure a 'private key' detection pre-commit hook.

Git pre-commit hooks allow you to check for sensitive information within your project and prevent a commit from happening if a security issue is found. The popular [pre-commit framework](https://pre-commit.com/) provides built-in hooks that can be easily configured for a specific project. The pre-commit framework is a client side component and needs to be setup on your local machine before it can be used. 

**Note**: For Windows users, miniconda is a minimum installer package that works well and was tested for this lab exercise, you can find the install packages here -> [windows pre-commit install](https://docs.conda.io/en/latest/miniconda.html)

1. Install pre-commit framework using the links above if you are on Windows, for **WSL/Linux**, you can install pre-commit using the command line below
   ```shell
      sudo apt install pre-commit
   ```
   If you already have pip (Python package installer), you can also use the following command for installation
   ```shell
      pip install pre-commit
   ```

   ```shell
      ##validate the install was successful */
      pre-commit --version
   ```

2. We will now create a simple git project to test the pre-commit hooks in action, start by creating a new project directory and initializing it as a git repo.

   ```shell
      mkdir precommit-test
      cd precommit-test
      
      ## git initialize the project
      git init
   ```
3. Add a simple pre-commit configuration to your git project, this config is using one git hook for detecting private keys.
    
    - create a file called `.pre-commit-config.yaml` within the `precommit-test` root directory, this is a simple configuration to enable secrets detection, use the sample config yaml shown below.
      
   ```yml
        repos:
        -  repo: https://github.com/pre-commit/pre-commit-hooks
           rev: v2.3.0
           hooks:
           - id: detect-private-key
   ```
4. Install the pre-commit hook using the following command, this step is only required when you are configuring a new pre-commit hook or using a new repository
      ```shell
         pre-commit install
      ```

4. We will now trigger the key detection pre-commit hook by adding a private key to our git project, in this example, we are simulating storage of a private key embedded within a kubernetes secret in `secret.yaml` 

   Create a file named `secret.yaml` within your project root directory, the contents of this file can be copied from the sample below.

   ```yml
    apiVersion: v1
    kind: Secret
    metadata:
      name: secret-ssh-auth
    type: kubernetes.io/ssh-auth
    data:
      key: 
       -----BEGIN DSA PRIVATE KEY-----
       MIIBvAIBAAKBgQDaqdgwD3YvYwgbWzs8RQQOm8RmPztSYMUrcM7KQtdJ111sTZ/x
       VAq84frCt/TEupAN5hUFkC+bpJ/diZixQgPvLKo6FVtBKy97HSpuZT8n2pUYZ9/4
       sBTR5YQtP9qExXUYO/yR+fZ+RE9w0TbSAtHW2YZHKnoowJAHdoEGMbaChQIVAK/q
       iXNHCha4xHnIdD2jT0OUs03fAoGBAMnCeTgO09r2GquRAQmGFAT/6IGMhux7KOC8
       QrW7jDaqAYLiuA45E3Ira584RF2rg0VhewxcdEMbqNzqCeSKk9OAmwXpJ1J8vCUR
       dRojGz0DYZHJbcspoGtZF1IF6Z3BoaggRcLX6/KYLbnzFZnBXV/+//gRTbm/V2ie
       BzCWE/qEAoGBANbrGxzVTTdTD8MaVtlOpjU3RqoGFHmFCd4lv0PIt2mjFsXO3Dt/
       6BMtJVREtb74WF0SUGmnpy6FTYoDb05j2LhH1IvCSkFT5hUK0WtAJ3NidJ6ARxxD
       z2QITWI1FTr1K9NbZdR6DoTxeKfV6wWbuLywlwoWYmLe6oAmq21Oft4XAhRcKcLk
       r2R/Rn1uchUL8ru0B2OVkg==
       -----END DSA PRIVATE KEY-----
    ```
5. Git add and commit both your .pre-commit-config and secret.yaml files to your project repository.
   ```shell
      git add .
      git commit -m "initial commit with pre-commit hooks"
   ```

6. The pre-commit hook "detect-private-key" that we configured earlier should get triggered and not allow the commit to proceed, you should see an error explaining the failure as shown below.


   -![pre-commit hook detection](../../assets/images/module2/pre-commit-failure.png)


7. **(Optional)** - Explore additional, 3rd party pre-commit hooks for enhanced secret detection, "git guardian" offers a pre-commit hook that can detect up to 800 common secret configurations. More info can be found here..https://docs.gitguardian.com/ggshield-docs/integrations/git-hooks/pre-commit  A sample configuration is shown below, note: you will require an API key that must be set as an environment variable before git guardian pre-commit hook can be used
   ```yaml
   repos:
   -  repo: https://github.com/pre-commit/pre-commit-hooks
      rev: v2.3.0
      hooks:
      -  id: detect-private-key
   -  repo: https://github.com/gitguardian/ggshield
      rev: v1.14.3
      hooks:
      -  id: ggshield
         language_version: python3
   ```


## pre-commit hook limitations
- The pre-commit framework is a client side framework and as such does not force developers to it's use, developers can also bypass the pre-commit checks by typing `--no-verify` during the actual commit. It is therefore essential to implement post-commit Git hooks to check for secrets, code vulnerabilities, etc. This is where GitHub enhanced security features can be implemented to ensure complete coverage of your build pipelines.
- The built-in 'detect-private-key' hook has limited scope to private keys only, additional hooks for secrets detection such as gitguardian may be used for a more comprehensive secrets and password detection.
- pre-commit hooks have limited scope in general and are nor a replacement for static/dynamic analysis (SAST/DAST) or for component scanning (SCA). Use the GitHub advanced security settings to enable a more comprehensive analysis.





















