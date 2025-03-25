package com.anvilsecure.bytebanter.AIEngineUIs;

import com.anvilsecure.bytebanter.AIEngines.AIEngine;

import javax.swing.*;

public class OllamaAIEngineUI extends AIEngineUI {
    public OllamaAIEngineUI(AIEngine engine) {
        super(engine);
    }

    @Override
    public JPanel getURLPanel() {
        return null;
    }

    @Override
    public JPanel getParamPanel() {
        return null;
    }
}
