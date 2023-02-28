---
title: Module 2 - Develop phase

has_children: true

---

## Module 2 - Introduction and Pre-requisites

“Shifting left” is a key tenant of the DevSecOps mindset, and this process begins well before code is even committed into a repository and deployed via a pipeline, by adopting secure coding best practices and using IDE (integrated development environments) tools & plugins for code analysis during the development phase can go a long way with addressing security issues earlier in the development lifecycle when it's much easier to fix.

## Best practice - Use Integrated Development Environment (IDE) plugins 

Most popular IDEs (Integrated Development Environments) like Visual Studio, Visual Studio Code, IntelliJ IDEA, and Eclipse support extensions that developers can use to get immediate feedback and recommendations around potential security issues they may have introduced while writing their application code 

## Best Practice - Use pre-commit hooks

It's a best practice to establish checks into your repositories before and right after commits happen to catch a potential security vulenerabilty, Git pre-commit hooks allow you to check for sensitive information within your application source code and prevent a commit from happening if a security issue is found. The pre-commit frameork provides built-in hooks that can be easily configured for a specific project, for example, there are pre-built hooks to scan for secrets, private keys and credentials and prevent a commit if any of these issues are found. Their are also third party pre-commit hooks that can be used for security use cases.

## Best practice – Perform Static Code Analysis (SAST) & secrets scanning

Use GitHub Advanced Security scanning capabilities for code scanning and CodeQL. Code scanning is a feature that you use to analyze the code in a GitHub repository to find security vulnerabilities and coding errors. Any problems identified by the analysis are shown in GitHub repository dashboard along with details on remidiation

## Prerequistes

The following prerequistes are required to complete the labs in the Develop section

1. Visual Studio Code (VS Code)
2. Java JDK 11
3. Maven
4. GitHub account
5. Git client