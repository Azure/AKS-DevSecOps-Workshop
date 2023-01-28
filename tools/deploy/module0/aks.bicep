// mandatory params
@description('The unique DNS prefix for your cluster, such as myakscluster. This cannot be updated once the Managed Cluster has been created.')
param dnsPrefix string = resourceGroup().name // name is obtained from env

@description('The administrator username to use for Linux VMs.')
param linuxAdminUsername string

@description('Certificate public key used to authenticate with VMs through SSH. The certificate must be in PEM format with or without headers.')
param sshRSAPublicKey string = 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDQAuD+1JUFJiE5P+Nz3/EHdBNT3n1P6HKWscW6RuL8fWJQsWfQ0ocrmPvNUmRoPpJlR0V1U+eP02PDV8/0gr6olfloVgsaD1gq2mffVNQ6/E1ep5H4iCE+8hVm4KK8xdzeYPqnz6YWagUDYPCrlCc4BROL+kMM0ev0et1vxYhF5a3jP2Fg0SJ0VzzjrvBWbxfylnB1aG6CWlhQ7fZYXfT0T54kyMWErIHvnhQTXnaf8bZchq1NYUYiRAKKLI8cCFfZjbw62D0CJqGSh3xKDVwC5HEuBYEBz6vKUQ+EKbt7w+LnbK6JVURlzOGRS8NiHLVXZBiJn9vJDHGwJ/pRxZRp5tEdT+H12n3x6N9SUCheHpHP+q5Km1kzID3Vn56G+LlQxEg+5tSjHM0VmqLNujb4hrXQmeOKgs+dHmuZvxvWpYMjxmH9f8FOnVR4giC9TpZ1tgD98bxl49kOJ7MaDLFBupqbZ5TB4ndQ4ZaHkHPxWep2Rg5K/mWpbOmMofif4VZ0/Zhfc8XegcoiSH/3rm1Ej1NP4Wu4nq5Ocun5Rwjz6mmtH637bpWD0hCKPVt5omARtCBf1EysSkfyD5MW8/F79EHDrd7NzbnPPsBFSryuMIXPGVktdn4h1dOvUimHb4mOD7og7N9JMpawOUB+i0sQjiZ7ikiMluYLGYHJYG/fZQ== ahmedbham@microsoft.com'

// @description('The ID for the service principal.')
// param servicePrincipalClientId string

@description('The unique name for the AKS cluster, such as myAKSCluster.')
param uniqueclustername string = 'aksbicep'

// @secure()
// @description('The secret password associated with the service principal.')
// param servicePrincipalClientSecret string

// Optional params
@description('The region to deploy the cluster. By default this will use the same region as the resource group.')
param location string = resourceGroup().location

@minValue(0)
@maxValue(1023)
@description('OS Disk Size in GB to be used to specify the disk size for every machine in the master/agent pool. If you specify 0, it will apply the default osDisk size according to the vmSize specified.')
param osDiskSizeGB int = 30

@minValue(1)
@maxValue(50)
@description('Number of agents (VMs) to host docker containers. Allowed values must be in the range of 0 to 1000 (inclusive) for user pools and in the range of 1 to 1000 (inclusive) for system pools. The default value is 1.')
param agentCount int = 3

@description('VM size availability varies by region. If a node contains insufficient compute resources (memory, cpu, etc) pods might fail to run correctly. For more details on restricted VM sizes, see: https://docs.microsoft.com/azure/aks/quotas-skus-regions')
param agentVMSize string = 'Standard_DS2_v2'

resource aks 'Microsoft.ContainerService/managedClusters@2022-09-02-preview' = {
  name: uniqueclustername
  location: location
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    dnsPrefix: dnsPrefix
    agentPoolProfiles: [
      {
        name: 'agentpool'
        osDiskSizeGB: osDiskSizeGB
        count: agentCount
        vmSize: agentVMSize
        osType: 'Linux'
        mode: 'System'
      }
    ]    
    aadProfile: {
      managed: true
      enableAzureRBAC: true
    }    
    linuxProfile: {
      adminUsername: linuxAdminUsername
      ssh: {
        publicKeys: [
          {
            keyData: sshRSAPublicKey
          }
        ]
      }
    }

    // servicePrincipalProfile: {
    //   clientId: servicePrincipalClientId
    //   secret: servicePrincipalClientSecret
    // }
  }
}

output controlPlaneFQDN string = aks.properties.fqdn
