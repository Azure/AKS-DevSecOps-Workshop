---
title: Module 1 - Plan phase

has_children: true

---

## Plan phase

The plan phase generally has the least amount of automation but will have important security implications that significantly impact later DevOps lifecycle stages. This stage involves collaboration between security, development, and operations teams. Including security stakeholders in this phase of designing and planning ensures security requirements and security issues are appropriately accounted for or mitigated. 
Best Practice – AKS Secure application platform design

## Best Practice - Secure Platform Design

Building a secure AKS hosted platform is an important step to ensure security is built into the system at every layer, starting with the platform itself. This can include components both internal to the cluster such as runtime security and policy agents, as well as components that are external to AKS such as network firewalls, and container registries. For in-depth information on these topics, visit the AKS Landing zone accelerator, which contains critical design areas such as security, identity, and network topology.

## Best Practice - Threat Modeling

Threat Modeling is traditionally a manual activity that is a collaboration involving security and development teams.  It is used to model and find threats within a system, allowing vulnerabilities to be addressed prior to any code being developed or changes being made to a system. Threat modeling can occur at different times, triggered by events such as a significant software change, solution architectural change, and security incidents.

Microsoft recommends and uses the [STRIDE threat model](https://learn.microsoft.com/en-us/azure/security/develop/threat-modeling-tool-threats#stride-model), a methodology which starts with a data flow diagram and using the STRIDE mnemonic (Spoofing, Tampering, Info Disclosure, Repudiation, Denial of Service and Elevation of Privilege) threat categories and empowers teams to identify, mitigate, and validate risk. This also includes a [modeling tool](https://www.microsoft.com/en-us/securityengineering/sdl/threatmodeling) to easily notate and visualize system components, data flows and security boundaries. Building threat modeling into your SDLC processes will introduce new processes and additional work to maintain updated threat models but will ensure security is in place early which in turn will reduce the potential cost of dealing with security issues found in later SDLC stages.
