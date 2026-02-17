package com.anvilsecure.bytebanter.AIEngineUIs;

import com.anvilsecure.bytebanter.AIEngines.AIEngine;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public abstract class AIEngineUI {
    private JCheckBox statefulCheck;
    private JTextField regexField;
    private JCheckBox b64Check;
    private JTextArea promptField;
    protected AIEngine model;
    protected JTextField urlField;
    protected JTextArea headersField;

    // Request Limit Config
    protected JCheckBox infiniteRequestCheck;
    protected JFormattedTextField requestLimitField;

    protected JSpinner maxTokensSpinner;
    protected JSlider temperatureSlider;
    protected JSlider topPSlider;
    protected JSlider frequencyPenaltySlider;
    protected JSlider presencePenaltySlider;

    public AIEngineUI(AIEngine model) {
        this.model = model;
    }

    public JPanel getPromptPanel(String default_prompt) {
        JPanel promptPanel = new JPanel(new GridBagLayout());
        promptPanel.setBorder(new TitledBorder("Prompt:"));
        promptField = new JTextArea(default_prompt, 50, 50);
        promptField.setMinimumSize(new Dimension(50, 50));
        JScrollPane promptScrollPane = new JScrollPane(promptField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        promptScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        promptField.setLineWrap(true);
        promptScrollPane.setMinimumSize(new Dimension(50, 50));
        promptPanel.add(promptScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        return promptPanel;
    }

    public JPanel getStatePanel() {
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

    public JPanel getAIConfPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("Engine Configuration:"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // --- Model Invocation Parsing (URL & Headers) ---
        urlField = new JTextField("http://localhost:1337/v1/", 40);
        urlField.setBorder(new TitledBorder("URL:"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        configPanel.add(urlField, gbc);

        headersField = new JTextArea("", 4, 40);
        headersField.setBorder(new TitledBorder("Headers (optional):"));
        JScrollPane headersScrollPane = new JScrollPane(headersField);
        headersScrollPane.setPreferredSize(new Dimension(400, 80));
        gbc.gridy = 1;
        configPanel.add(headersScrollPane, gbc);

        // --- Request Limit Configuration ---
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        limitPanel.setBorder(new TitledBorder("Request Limits:"));

        infiniteRequestCheck = new JCheckBox("Infinite Requests");
        limitPanel.add(infiniteRequestCheck);

        // Create formatter for request limit
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(1000000);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        requestLimitField = new JFormattedTextField(formatter);
        requestLimitField.setValue(10); // default
        requestLimitField.setColumns(8);
        limitPanel.add(new JLabel("Limit:"));
        limitPanel.add(requestLimitField);

        infiniteRequestCheck.addActionListener(e -> {
            requestLimitField.setEnabled(!infiniteRequestCheck.isSelected());
        });

        gbc.gridy = 2;
        configPanel.add(limitPanel, gbc);

        return configPanel;
    }

    public JPanel getParamPanel() {
        JPanel paramPanel = new JPanel(new GridLayout(6, 1));

        // Max Tokens
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(512, 1, 4096, 1));
        JPanel maxTokensPanel = new JPanel(new BorderLayout());
        maxTokensPanel.add(new JLabel("Max Tokens:"), BorderLayout.WEST);
        maxTokensPanel.add(maxTokensSpinner, BorderLayout.CENTER);
        paramPanel.add(maxTokensPanel);

        // Temperature
        temperatureSlider = new JSlider(0, 500, 70);
        JLabel tempLabel = new JLabel("Temperature: " + String.format("%.2f", temperatureSlider.getValue() / 100.0));
        temperatureSlider.addChangeListener(
                e -> tempLabel.setText("Temperature: " + String.format("%.2f", temperatureSlider.getValue() / 100.0)));
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(tempLabel, BorderLayout.NORTH);
        tempPanel.add(temperatureSlider, BorderLayout.CENTER);
        paramPanel.add(tempPanel);

        // Top P
        topPSlider = new JSlider(0, 20, 20);
        JLabel topPLabel = new JLabel("Top P: " + String.format("%.2f", topPSlider.getValue() / 20.0));
        topPSlider.addChangeListener(
                e -> topPLabel.setText("Top P: " + String.format("%.2f", topPSlider.getValue() / 20.0)));
        JPanel topPPanel = new JPanel(new BorderLayout());
        topPPanel.add(topPLabel, BorderLayout.NORTH);
        topPPanel.add(topPSlider, BorderLayout.CENTER);
        paramPanel.add(topPPanel);

        // Frequency Penalty
        frequencyPenaltySlider = new JSlider(0, 20, 0);
        JLabel freqLabel = new JLabel(
                "Frequency Penalty: " + String.format("%.2f", frequencyPenaltySlider.getValue() / 20.0));
        frequencyPenaltySlider.addChangeListener(e -> freqLabel
                .setText("Frequency Penalty: " + String.format("%.2f", frequencyPenaltySlider.getValue() / 20.0)));
        JPanel freqPanel = new JPanel(new BorderLayout());
        freqPanel.add(freqLabel, BorderLayout.NORTH);
        freqPanel.add(frequencyPenaltySlider, BorderLayout.CENTER);
        paramPanel.add(freqPanel);

        // Presence Penalty
        presencePenaltySlider = new JSlider(0, 20, 0);
        JLabel presLabel = new JLabel(
                "Presence Penalty: " + String.format("%.2f", presencePenaltySlider.getValue() / 20.0));
        presencePenaltySlider.addChangeListener(e -> presLabel
                .setText("Presence Penalty: " + String.format("%.2f", presencePenaltySlider.getValue() / 20.0)));
        JPanel presPanel = new JPanel(new BorderLayout());
        presPanel.add(presLabel, BorderLayout.NORTH);
        presPanel.add(presencePenaltySlider, BorderLayout.CENTER);
        paramPanel.add(presPanel);

        return paramPanel;
    }

    public JSONObject getParams() {
        JSONObject params = new JSONObject();
        params.put("prompt", promptField.getText());
        params.put("stateful", statefulCheck.isSelected());
        params.put("regex", regexField.getText());
        params.put("b64", b64Check.isSelected());

        // New Request Limit params
        if (infiniteRequestCheck != null) {
            params.put("isInfiniteRequests", infiniteRequestCheck.isSelected());
        }
        if (requestLimitField != null) {
            params.put("requestsLimit", requestLimitField.getValue());
        }

        // Those fields are not mandatory in engines UIs
        if (urlField != null) {
            params.put("URL", urlField.getText());
        }
        if (headersField != null) {
            params.put("headers", headersField.getText());
        }
        if (maxTokensSpinner != null) {
            params.put("max_tokens", maxTokensSpinner.getValue());
        }
        if (temperatureSlider != null) {
            params.put("temperature", temperatureSlider.getValue());
        }
        if (topPSlider != null) {
            params.put("top_p", topPSlider.getValue());
        }
        if (frequencyPenaltySlider != null) {
            params.put("frequency_penalty", frequencyPenaltySlider.getValue());
        }
        if (presencePenaltySlider != null) {
            params.put("presence_penalty", presencePenaltySlider.getValue());
        }

        return params;
    }

    public void loadParams(JSONObject params) {
        promptField.setText(params.getString("prompt"));
        statefulCheck.setSelected(params.getBoolean("stateful"));
        regexField.setText(params.getString("regex"));
        b64Check.setSelected(params.getBoolean("b64"));

        if (infiniteRequestCheck != null) {
            infiniteRequestCheck.setSelected(params.optBoolean("isInfiniteRequests", false));
            requestLimitField.setEnabled(!infiniteRequestCheck.isSelected());
        }
        if (requestLimitField != null) {
            requestLimitField.setValue(params.optInt("requestsLimit", 10));
        }

        // Those fields are not mandatory in engines UIs
        if (urlField != null) {
            urlField.setText(params.getString("URL"));
        }
        if (headersField != null) {
            headersField.setText(params.getString("headers"));
        }
        if (maxTokensSpinner != null) {
            maxTokensSpinner.setValue(params.getInt("max_tokens"));
        }
        if (temperatureSlider != null) {
            temperatureSlider.setValue(params.getInt("temperature"));
        }
        if (topPSlider != null) {
            topPSlider.setValue(params.getInt("top_p"));
        }
        if (frequencyPenaltySlider != null) {
            frequencyPenaltySlider.setValue(params.getInt("frequency_penalty"));
        }
        if (presencePenaltySlider != null) {
            presencePenaltySlider.setValue(params.getInt("presence_penalty"));
        }
    }
}
