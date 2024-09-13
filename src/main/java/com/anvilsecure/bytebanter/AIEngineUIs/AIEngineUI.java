package com.anvilsecure.bytebanter.AIEngineUIs;

import com.anvilsecure.bytebanter.AIEngines.AIEngine;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public abstract class AIEngineUI {
    private JCheckBox statefulCheck;
    private JTextField regexField;
    private JCheckBox b64Check;
    private JTextArea promptField;
    protected AIEngine model;
    protected JTextField urlField;
    protected JTextArea headersField;

    protected JSpinner maxTokensSpinner;
    protected JSlider temperatureSlider;
    protected JSlider topPSlider;
    protected JSlider frequencyPenaltySlider;
    protected JSlider presencePenaltySlider;

    public AIEngineUI(AIEngine model) {
        this.model = model;
    }

    public JPanel getPromptPanel(String default_prompt){
        JPanel promptPanel = new JPanel(new GridBagLayout());
        promptPanel.setBorder(new TitledBorder("Prompt:"));
        promptField = new JTextArea(default_prompt, 50, 50);
        promptField.setMinimumSize(new Dimension(50,50));
        JScrollPane promptScrollPane = new JScrollPane(promptField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        promptScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        promptField.setLineWrap(true);
        promptScrollPane.setMinimumSize(new Dimension(50,50));
        promptPanel.add(promptScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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

    public JTextArea getPromptField() {
        return promptField;
    }


    public JPanel getURLPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("Engine URL:"));
        urlField = new JTextField("http://localhost:1337/v1/", 40);
        urlField.setBorder(new TitledBorder("URL:"));
        configPanel.add(urlField, new GridBagConstraints(0, 0, 2, 1, 0.001, 0.001,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        headersField = new JTextArea("",10, 40);
        headersField.setBorder(new TitledBorder("Headers (optional):"));
        configPanel.add(headersField, new GridBagConstraints(0, 2, 2, 1, 1.00, 1.00,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return configPanel;
    }

    public JPanel getParamPanel() {
        JPanel paramPanel = new JPanel(new GridLayout(6, 1));
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(512, 1, 4096, 1));
        temperatureSlider = new JSlider(0,500, 70);
        topPSlider = new JSlider(0,20, 20);
        frequencyPenaltySlider = new JSlider(0,20, 0);
        presencePenaltySlider = new JSlider(0,20, 0);
        paramPanel.add(new JLabel("Max Tokens:"));
        paramPanel.add(maxTokensSpinner);
        paramPanel.add(new JLabel("Temperature:"));
        paramPanel.add(temperatureSlider);
        paramPanel.add(new JLabel("Top P:"));
        paramPanel.add(topPSlider);
        paramPanel.add(new JLabel("Frequency Penalty:"));
        paramPanel.add(frequencyPenaltySlider);
        paramPanel.add(new JLabel("Presence Penalty:"));
        paramPanel.add(presencePenaltySlider);
        return paramPanel;
    }

    public JSONObject getParams() {
        JSONObject params = new JSONObject();
        params.put("prompt", promptField.getText());
        params.put("stateful", statefulCheck.isSelected());
        params.put("regex", regexField.getText());
        params.put("b64", b64Check.isSelected());

        //Those fields are not mandatory in engines UIs
        if(urlField != null){params.put("URL", urlField.getText());}
        if(headersField != null){params.put("headers", headersField.getText());}
        if (maxTokensSpinner != null) {params.put("max_tokens", maxTokensSpinner.getValue());}
        if (temperatureSlider != null) {params.put("temperature", temperatureSlider.getValue());}
        if (topPSlider != null) {params.put("top_p", topPSlider.getValue());}
        if (frequencyPenaltySlider != null) {params.put("frequency_penalty", frequencyPenaltySlider.getValue());}
        if (presencePenaltySlider != null) {params.put("presence_penalty", presencePenaltySlider.getValue());}

        return params;
    }

    public void loadParams(JSONObject params) {
        promptField.setText(params.getString("prompt"));
        statefulCheck.setSelected(params.getBoolean("stateful"));
        regexField.setText(params.getString("regex"));
        b64Check.setSelected(params.getBoolean("b64"));

        //Those fields are not mandatory in engines UIs
        if(urlField != null){urlField.setText(params.getString("URL"));}
        if(headersField != null){headersField.setText(params.getString("headers"));}
        if(maxTokensSpinner != null){maxTokensSpinner.setValue(params.getInt("max_tokens"));}
        if(temperatureSlider != null){temperatureSlider.setValue(params.getInt("temperature"));}
        if(topPSlider != null){topPSlider.setValue(params.getInt("top_p"));}
        if(frequencyPenaltySlider != null){frequencyPenaltySlider.setValue(params.getInt("frequency_penalty"));}
        if(presencePenaltySlider != null){presencePenaltySlider.setValue(params.getInt("presence_penalty"));}
    }
}
