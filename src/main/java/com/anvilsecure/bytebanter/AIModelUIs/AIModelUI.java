package com.anvilsecure.bytebanter.AIModelUIs;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public abstract class AIModelUI {
    private JCheckBox statefulCheck;
    private JTextField regexField;
    private JCheckBox b64Check;
    private JTextArea promptField;

    public JPanel getPromptPanel(String default_prompt){
        JPanel promptPanel = new JPanel(new GridBagLayout());
        promptPanel.setBorder(new TitledBorder("Prompt:"));
        promptField = new JTextArea(default_prompt);
        promptPanel.add(promptField);
        return promptPanel;
    }

    public JPanel getStatePanel(){
        JPanel statePanel = new JPanel(new GridLayout(3, 1));
        statefulCheck = new JCheckBox("Stateful Interaction", false);
        regexField = new JTextField("^\\{.*\\\"answer\\\":\\\"(.*)\\\",\\\"defender\\\".*\\}$");
        b64Check = new JCheckBox("Base64 decoding", false);
        statePanel.add(statefulCheck);
        statePanel.add(regexField);
        statePanel.add(b64Check);

        return statePanel;
    }

    public JSONObject getParams(){
        JSONObject params = new JSONObject();
        params.put("prompt", promptField.getText());
        params.put("stateful", statefulCheck.isSelected());
        params.put("regex", regexField.getText());
        params.put("b64", b64Check.isSelected());
        return params;
    }

    public abstract JPanel getURLPanel();
    public abstract JPanel getParamPanel();
}
