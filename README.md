# ByteBanter
**AnvilSecure** presents **ByteBanter**! **ByteBanter** is a Burp Suite extension that leverages Large Language Models (LLMs) to generate intelligent and context-aware payloads for Burp Intruder. By integrating AI capabilities, ByteBanter enhances automated security testing by producing diverse and adaptive payloads tailored to specific testing scenarios.

## Features
* **LLM-Driven Payload Generation:** Utilizes LLMs to create dynamic payloads based on user-defined prompts and contexts.
* **Seamless Burp Integration:** Registers as a custom payload generator within Burp Intruder, allowing easy selection and use.
* **Configurable Prompts:** Users can define and customize prompts to guide the LLM in generating desired payloads.

This version of ByteBanter is meant to comply with BurpSuite BApp store standards, hence it supports interaction only with BurpAI engine. You can find on the main branch the version with capabilities to interact with other LLM engines. 

## Installation
You can find this version of ByteBanter in the official BurpSuite BApp store. But If you prefer you can compile the code by yourself according to the following instructions.

* Clone the Repository and checkout `bapp` branch:

```bash
git clone https://github.com/anvilsecure/bytebanter/
git checkout bapp
```
* Build the Extension: Navigate to the project directory and build the JAR file using your preferred build tool (e.g., Maven or Gradle).

```bash
mvn clean package
```

```bash
gradle build
```

* Load into Burp Suite:
  * Open Burp Suite.
  * Go to the **Extender** tab. 
  * Click on **Add**. 
  * Select the built JAR file (the `uber` one) to load the extension.

## Usage
* Configure LLM Settings:
  * Navigate to the newly added ByteBanter tab in Burp Suite. 
  * Enter your LLM API credentials and configure any necessary settings. 
* Set Up Intruder Attack:
  * Go to the Intruder tab. 
  * Configure your target and positions as usual. 
* Select ByteBanter as Payload Source:
  * In the Payloads tab:
  * Set Payload type to Extension-generated.
  * Choose ByteBanter from the list of available generators. 
* Define Prompts:
  * Within the ByteBanter tab, create and customize prompts that will guide the LLM in generating payloads.
* Start the Attack:
  * Run the Intruder attack. 
  * ByteBanter will generate and supply payloads dynamically using the configured LLM.

## Configuration
In the **ByteBanter** extension tab: select the framework you want to use from the combo box on th top right corner.
Configure the endpoint details (i.e.: model URL, parameters). Set whether you want the extension to keep track of target responses and 
specify the regular expression to extract them form the HTTP body. Write your prompt to instruct the model for generating payloads.
Eventually use the "Optimize!" button to ask AI to optimize your prompt. And you are ready to go! Settings are 
automatically saved and used by the generator and Burp also persists them.

## Development
### Prerequisites
* Java Development Kit (JDK) 11 or higher
* Build tool (Maven or Gradle)
* Burp Suite (Community or Professional Edition)

### Contributing
Contributions are welcome!

## License
This project is licensed under the [MIT License](https://opensource.org/license/mit).

## Acknowledgments
* [PortSwigger](https://portswigger.net/) for Burp Suite and its extensibility.
