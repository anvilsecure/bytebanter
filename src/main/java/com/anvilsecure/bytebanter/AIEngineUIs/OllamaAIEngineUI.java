package com.anvilsecure.bytebanter.AIEngineUIs;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.anvilsecure.bytebanter.AIEngines.AIEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class OllamaAIEngineUI extends AIEngineUI {
    private JComboBox<String> modelCombo;
    private final DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                new Thread(() -> loadModels(e)).start();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                new Thread(() -> loadModels(e)).start();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                new Thread(() -> loadModels(e)).start();
            }
        };

    public OllamaAIEngineUI(AIEngine engine) {
        super(engine);
    }

    @Override
    public JPanel getURLPanel() {
        JPanel urlPanel = super.getURLPanel();
        urlField.setText("http://localhost:11434/");
        modelCombo = new JComboBox<>(new String[]{"Set the URL to Ollama"});
        urlPanel.add(modelCombo);
        urlField.getDocument().addDocumentListener(documentListener);
        headersField.getDocument().addDocumentListener(documentListener);
        return urlPanel;
    }

    @Override
    public JPanel getParamPanel() {
        JPanel paramPanel = new JPanel(new GridLayout(6, 1));
        temperatureSlider = new JSlider(1,500, 70);
        topPSlider = new JSlider(0,10, 0);
        paramPanel.add(new JLabel("Temperature:"));
        paramPanel.add(temperatureSlider);
        paramPanel.add(new JLabel("Top P:"));
        paramPanel.add(topPSlider);
        return paramPanel;
    }

    @Override
    public JSONObject getParams(){
        JSONObject params = super.getParams();
        params.put("model", modelCombo.getSelectedItem());
        return params;
    }

    private void loadModels(DocumentEvent e) {
        String url = urlField.getText()+"api/tags";
        String headers = headersField.getText();
        MontoyaApi api = this.model.getApi();
        HttpRequestResponse response;

        HttpRequest request = HttpRequest.httpRequestFromUrl(url);
        request = request.withMethod("GET");
        if (!headers.isEmpty()) {
            HttpHeader httpHeader = HttpHeader.httpHeader(headers);
            request = request.withAddedHeader(httpHeader);
        }
        HttpRequest finalRequest = request;
        response = api.http().sendRequest(finalRequest);

        if (response.response().statusCode() == 200) {
            JSONObject body = new JSONObject(response.response().body().toString());
            JSONArray models = body.getJSONArray("models");
            modelCombo.removeAllItems();
            for (int i = 0; i < models.length(); i++) {
                modelCombo.addItem(models.getJSONObject(i).getString("name"));
            }
        }
    }
}
