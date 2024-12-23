import json
import subprocess

from burp import IBurpExtender, IIntruderPayloadGenerator, IIntruderPayloadGeneratorFactory, ITab
from javax.swing import JLabel, JTextField, JOptionPane, JTabbedPane, JPanel, JButton, JTextArea
from java.awt import GridBagLayout, GridBagConstraints
from java.io import PrintWriter
from random import randint


class Requests:
    @staticmethod
    def post(url, data):
        curl = subprocess.Popen(["/usr/bin/curl", url, "-X", "POST", "-H", "Content-Type: application/json",
                                 "-d", data], stdout=subprocess.PIPE)
        curl.wait()
        message = json.loads(curl.stdout.read().replace("\n", "").replace("\r", ""))
        return message


class AnvilAIModule:
    def __init__(self, prompt, first_message="begin a conversation"):
        self._data = {"messages": [{"role": "system", "content": [{"type": "text", "text": prompt}]},
                                   {"role": "user", "content": [{"type": "text", "text": first_message}]}],
                      "frequency_penalty": 0.0, "max_tokens": 50, "n": 1, "presence_penalty": 0.0,
                      "seed": randint(0, 10000), "stream": False, "temperature": 1.0, "top_p": 1.0}

    def ask_ai(self, question=None):
        if question:
            self._data["messages"].append({"role": "user", "content": [{"type": "text", "text": question}]})
        r = Requests.post("http://anvil-ai:1337/v1/chat/completions", data=json.dumps(self._data))
        message = r["choices"][0]["message"]
        self._data["messages"].append(message)
        return message["content"]

    def save_target_response(self, response):
        self._data["messages"].append({"role": "user", "content": [{"type": "text", "text": response}]})

    def reset(self, prompt, first_message="begin a conversation"):
        self._data = {"messages": [{"role": "system", "content": [{"type": "text", "text": prompt}]},
                                   {"role": "user", "content": [{"type": "text", "text": first_message}]}],
                      "frequency_penalty": 0.0, "max_tokens": 50, "n": 1, "presence_penalty": 0.0,
                      "seed": randint(0, 10000), "stream": False, "temperature": 1.0, "top_p": 1.0}


class BurpExtender(IBurpExtender, IIntruderPayloadGeneratorFactory, ITab):
    _jTabbedPane = JTabbedPane()
    _jPanel = JPanel()
    _jAboutPanel = JPanel()
    _jPanelConstraints = GridBagConstraints()
    _jLabelPrompt = None
    _jTextFieldPrompt = None
    _jLabelMessage = None
    _jTextFieldMessage = None
    _jButtonSave = None
    _jLabelAbout = None
    _GeneratorInstance = None
    aboutText = "A Burp extension to interact with AnvilAI and use it as payload generator"
    _prompt = "You are a fuzzer that's being used during a penetration to validate the security of a web application."
    _message = "Generate an XSS payload, the message should only contain the payload"
    stdout = None
    stderr = None

    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName("AnvilAI Burp Extension")
        callbacks.registerIntruderPayloadGeneratorFactory(self)
        callbacks.addSuiteTab(self)
        self.initPanelConfig()
        self._jTabbedPane.addTab("Configuration", self._jPanel)
        self._jTabbedPane.addTab("About", self._jAboutPanel)
        self.stdout = PrintWriter(callbacks.getStdout(), True)
        self.stderr = PrintWriter(callbacks.getStderr(), True)
        self._GeneratorInstance = AnvilAIIntruderPayloadGenerator(self._prompt, self._message)
        return

    def getGeneratorName(self):
        return "AnvilAI Burp Extension"

    def createNewInstance(self, attack):
        self.stdout.println("Create New Instance")
        self._GeneratorInstance = AnvilAIIntruderPayloadGenerator(self._prompt, self._message)
        return self._GeneratorInstance

    def getUiComponent(self):
        return self._jTabbedPane

    def getTabCaption(self):
        return "AnvilAI"

    def initPanelConfig(self):
        self._jPanel.setBounds(0, 0, 1000, 1000)
        self._jPanel.setLayout(GridBagLayout())

        self._jAboutPanel.setBounds(0, 0, 1000, 1000)
        self._jAboutPanel.setLayout(GridBagLayout())

        self._jLabelPrompt = JLabel("Prompt to use to generate your payload: ")
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 0
        self._jPanelConstraints.gridy = 0
        self._jPanel.add(self._jLabelPrompt, self._jPanelConstraints)

        self._jTextFieldPrompt = JTextArea(text=self._prompt, columns=15, rows=20)
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 1
        self._jPanelConstraints.gridy = 0
        self._jPanel.add(self._jTextFieldPrompt, self._jPanelConstraints)

        self._jLabelMessage = JLabel("The first message:")
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 0
        self._jPanelConstraints.gridy = 1
        self._jPanel.add(self._jLabelMessage, self._jPanelConstraints)

        self._jTextFieldMessage = JTextField(self._message, 15)
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 1
        self._jPanelConstraints.gridy = 1
        self._jPanel.add(self._jTextFieldMessage, self._jPanelConstraints)

        self._jButtonSave = JButton('Save', actionPerformed=self.setAI)
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 0
        self._jPanelConstraints.gridy = 5
        self._jPanelConstraints.gridwidth = 2
        self._jPanel.add(self._jButtonSave, self._jPanelConstraints)

        self._jLabelAbout = JLabel(self.aboutText)
        self._jPanelConstraints.fill = GridBagConstraints.HORIZONTAL
        self._jPanelConstraints.gridx = 0
        self._jPanelConstraints.gridy = 0
        self._jAboutPanel.add(self._jLabelAbout, self._jPanelConstraints)

    def setAI(self, event=None):
        self._prompt = self._jTextFieldPrompt.getText()
        self._message = self._jTextFieldMessage.getText()
        self._GeneratorInstance.set_prompt(self._prompt)
        self._GeneratorInstance.set_question(self._message)
        self._GeneratorInstance.reset()
        JOptionPane.showMessageDialog(None, "Anvil AI configured!")


class AnvilAIIntruderPayloadGenerator(IIntruderPayloadGenerator):
    def __init__(self, prompt, question):
        self._prompt = prompt
        self._anvilAIModule = AnvilAIModule(prompt=prompt)
        self._question = question

    def set_prompt(self, prompt):
        self._prompt = prompt

    def set_question(self, question):
        self._question = question

    def getNextPayload(self, baseValue):
        payload = self._anvilAIModule.ask_ai(self._question)
        return payload

    def hasMorePayloads(self):
        return True

    def reset(self):
        return self._anvilAIModule.reset(self._prompt, self._question)
