# Aion4j-Idea-Plugin

Intellij IDEA plugin which supports smart contract development on Aion Virtual Machine (AVM).

## Features :

* AVM Project (Avm Maven Archetype support)
* Jar Optimizer
* JCL whitelist api check
* ABI type check for parameter and return types
* Integration with Embedded AVM
     * Deploy
     * Debug and Test
     * Method Call
     * Get Balance
     * Create Account
 * Integration with Remote Aion Kernel
     * Deploy
     * Call
     * Contract Txn
     * Get Receipt
     * Get Balance
     * Transfer
     * Unlock
     
## Installation
* Aion4j IDEA plugin is available on IntelliJ Marketplace as "Aion4j AVM Integration". You can directly install it from IntelliJ.

(https://plugins.jetbrains.com/plugin/12047-aion4j-avm-integration)

## Documents

* Aion4j IntelliJ IDEA Plugin documents can be found at Aion Doc page (https://docs.aion.network/docs/intellij-plugin)

## Build From Source
* Clone the repository
* $> ./gradlew clean build
* $> Get the plugin zip file from build/distributions/aion4j-idea-pugin-<version>.zip 
