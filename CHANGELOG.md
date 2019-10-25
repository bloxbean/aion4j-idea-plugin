### 0.4.0
  - AVM 1.5
  - Debugging support from editor
  - Added support for value argument during contract deployment
  - Update balance value to Aion instead of nAmp in Create Account
  - Enable deploy in editor context menu (for java source and pom.xml)
  - Fund an Account (Aion4j Faucet support)
  - Account list with balance
  
  **Note:** Debugging and Aion4j Faucet support need Aion4j Maven Plugin 0.8.0 and above.
### 0.3.6.1
  - AVM 1.4+ (d7f5110 - Latest Tooling Jars)
  - Abi version 1
    - BigInteger, BigInteger[]
### 0.3.5
  - AVM 1.3.1
  - Abi Type check added for @Initializable
### 0.3.4
  - AVM 1.3
  - Client side account generation
  - Kernel managed account support removed
  - Avm archetype version changed to 0.20
  - Bug fixes
### 0.3.1
  - AVM 1.1
  - Automatically fetch receipt after contract transaction and transfer
### 0.3.0
  - Multi module maven project support
  - Automatically get receipt during deployment
  - New code inspection rules
  - AVM 1.0
### 0.2.0
  - Fix: JCLWhitelist is taken from avm.jar if available in project's lib folder
  - 2D array support in method args
  - New rules added to code inspection 
       - ABI type check for parameters and return types
       - Method modifier check for @Callable method
  - Support for avmtestnet-2019-04-10 release     
### 0.1.3
  - Avm Archetype version changed to 0.11
### 0.1.2
  - Bug fix
  - Integration with aion4j:maven commands
  - Both remote and embedded AVM support
  - Constructor level JCL whitelist check
### 0.0.3-beta1
  - Method level JCL whitelist check
  
### 0.0.2 (7 March 2019):
  - Avm Archetype version changed to 0.6
  
### 0.0.1 (4 March 2019):
  - Non JCL whitelist api error highlight support
  - Avm Archetype integration (0.5)