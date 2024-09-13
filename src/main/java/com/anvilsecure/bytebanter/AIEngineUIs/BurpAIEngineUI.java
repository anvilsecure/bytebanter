package com.anvilsecure.bytebanter.AIEngineUIs;

import com.anvilsecure.bytebanter.AIEngines.AIEngine;

import javax.swing.*;
import java.awt.*;

public class BurpAIEngineUI extends AIEngineUI {

    public BurpAIEngineUI(AIEngine engine) {
        super(engine);
    }

    @Override
    public JPanel getURLPanel() {
        return null;
    }

    @Override
    public JPanel getParamPanel() {
        JPanel paramPanel = new JPanel(new GridLayout(6, 1));
        temperatureSlider = new JSlider(1,200, 50);
        paramPanel.add(new JLabel("Temperature:"));
        paramPanel.add(temperatureSlider);
        return paramPanel;
    }
}
