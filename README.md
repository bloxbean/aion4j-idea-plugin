[![Gitter](https://badges.gitter.im/aion4j/community.svg)](https://gitter.im/aion4j/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
# Aion4j-IDEA-Plugin

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
* $> Get the plugin zip file from build/distributions/aion4j-idea-pugin-{version}.zip 

## Videos

### Deploy and test Java Smart Contract under ~1 min

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/MEaNFQdB1T4/0.jpg)](https://www.youtube.com/watch?v=MEaNFQdB1T4)

### Getting started with Java Smart Contract

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/p9PPL4gN43g/0.jpg)](https://www.youtube.com/watch?v=p9PPL4gN43g)

### Contract to Contract call - All in IntelliJ without a real blockchain setup

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/8qNka4cI3Z0/0.jpg)](https://www.youtube.com/watch?v=8qNka4cI3Z0)

## Blog Posts

* [Aion4j Tips — Testing Contract to Contract call with Embedded AVM](https://medium.com/swlh/aion4j-tips-testing-contract-to-contract-call-with-embedded-avm-3f7acbbca8e5)
* [Aion4j Tips — Debug Java Smart Contract with Embedded AVM and Aion4j Plugins](https://medium.com/@satran004/aion4j-tips-debug-java-smart-contract-with-embedded-avm-and-aion4j-plugins-32cddbab660f)
* [Aion4j Tips —Unit Test your Avm Java Smart Contract with Spock Framework](https://medium.com/@satran004/aion4j-tips-unit-test-your-avm-java-smart-contract-with-spock-framework-a878a0d6fb7a)
* [Aion4j Tips —Exception Stack Trace during method call in Embedded AVM](https://medium.com/@satran004/aion4j-tips-exception-stack-trace-during-method-call-in-embedded-avm-ae2c58a0f6ec)
