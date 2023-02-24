---
title: Pre-commit hooks
parent:  Module 2 - Develop phase
has_children: false
nav_order: 2
---

# Module 2: Develop - Pre-commit hooks

## Lab Module 2b - Install and configure a 'private key' detection pre-commit hook.

The pre-commit framework is a client side copmonent and needs to be setup on your local machine before it can be used. 

Note: For Windows users, miniconda is a minimum installer package that works well and was used for this lab exercise, you can find the install packages here -> [windows pre-commit install](https://docs.conda.io/en/latest/miniconda.html)

1. Install pre-commit frameowrk using the links above if you are on Windows, for WSL/Linux, you can install pre-commit using the command line below
```
   sudo apt install pre-commit
```

```
   /* validate the install was succesful */

   pre-commit --version
```

2. Add a pre-commit configuration to your git project
    1. create a file called .pre-commit-config.yaml, this is a simple, minimal config to enable secrets detection.
      ```
        repos:
          repo: https://github.com/pre-commit/pre-commit-hooks
        rev: v2.3.0
          hooks:
           - id: detect-private-key
      ```
      2. Commit the config file and install the pre-commit git hooks using the following command, this is only required when you are installing/using a new hook.
      ```
         pre-commit install
      ```

3. (Optional step) This is for an initial setup only but it's usually a good idea to run the hooks against all of your project files when adding a new hooks, usually a pre-commit hook will only run on the changed files during a git commit
   ```
     pre-commit run --all-files
   ```
4. Simulate a pre-commit hook check by introducing a private key within your source project, in this example, we are using a private key stored as a kubernetes secret in secret.yaml

   ```
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
5. Commit your secret file to your project's git repository
   ```
      git commit -m "created k8s secret"
   ```

6. The pre-commit hook -detect-private-key' should be triggered and should not allow the commit to happen, you should see an error as displayed below.

   -![pre-commit hook detection](../../assets/images/module2/pre-commit-detect.png)

7. (Optional), add additional pre-commit hooks for enhanced secret detection, git guardian offers a pre-commit hook that can detect upto 800 common secret configurations. More info can be found here..https://docs.gitguardian.com/ggshield-docs/integrations/git-hooks/pre-commit  A sample configuration is shown below, note: you will require an API key that must be set as an environment varaible before git guardian pre-commit hook can be used
```
   repos:
-   repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v2.3.0
    hooks:
    -   id: detect-private-key
-   repo: https://github.com/gitguardian/ggshield
    rev: v1.14.3
    hooks:
      - id: ggshield
        language_version: python3
```


## pre-commit hook limitations
- Pre-commit framework is a client side framework and as such is not required for developers to commit their code to a remote repository
    - developers can also bypass the pre-commit hooks by typing --no-verify during the commit. It is therfore essential to implement post commit checks, server-side checks such as enabling secrets-detection in a GitHub repository.
- The built-in 'detect-private-key' hook has limited scope to private keys only, additional hooks for secrets detection such as gitguardian may be used for a more comprehensive detection
- pre-commit hooks have limited scope in general and are nor a replacment for static/dynamic analysis (SAST/DAST) or for component scanning (SCA). Use the GitHub advanced security settings to enable a comprehensive analysis.





















