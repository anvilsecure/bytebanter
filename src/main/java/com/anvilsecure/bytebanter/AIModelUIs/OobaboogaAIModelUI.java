package com.anvilsecure.bytebanter.AIModelUIs;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class OobaboogaAIModelUI extends AIModelUI {
    private JTextField urlField;
    private JTextArea headersField;

    private JSpinner maxTokensSpinner;
    private JSlider temperatureSlider;
    private JSlider topPSlider;
    private JSlider frequencyPenaltySlider;
    private JSlider presencePenaltySlider;

    @Override
    public JPanel getURLPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("Model URL:"));
        urlField = new JTextField("http://localhost:5000/v1/", 40);
        urlField.setBorder(new TitledBorder("URL:"));
        configPanel.add(urlField, new GridBagConstraints(0, 0, 2, 1, 0.001, 0.001,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        headersField = new JTextArea("",10, 40);
        headersField.setBorder(new TitledBorder("Headers (optional):"));
        configPanel.add(headersField, new GridBagConstraints(0, 2, 2, 1, 1.00, 1.00,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return configPanel;
    }

    @Override
    public JPanel getParamPanel() {
        JPanel paramPanel = new JPanel(new GridLayout(6, 1));
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(512, 1, 4096, 1));
        temperatureSlider = new JSlider(1,500, 70);
        topPSlider = new JSlider(0,10, 0);
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

    @Override
    public JSONObject getParams() {
        JSONObject params = super.getParams();
        params.put("URL", urlField.getText());
        params.put("headers", headersField.getText());
        params.put("max_tokens", maxTokensSpinner.getValue());
        params.put("temperature", temperatureSlider.getValue());
        params.put("top_p", topPSlider.getValue());
        params.put("frequency_penalty", frequencyPenaltySlider.getValue());
        params.put("presence_penalty", presencePenaltySlider.getValue());

        return params;
    }
}
