package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;
import com.anvilsecure.bytebanter.AIModelUIs.OllamaAIModelUI;
import org.json.JSONArray;

public class OllamaAIModel extends AIModel{
    private static final String DEFAULT_MESSAGE = "Generate a new payload";
    private final OllamaAIModelUI UI;
    private JSONArray messages;
    private Boolean isStateful = false;


    public OllamaAIModel(MontoyaApi api) {
        super(api, "Ollama");
        UI = new OllamaAIModelUI(this);
        messages = new JSONArray();
    }

    @Override
    public String askAi() {
        return "";
    }

    @Override
    public String askAi(String prompt, String user_input) {
        return "";
    }

    @Override
    public AIModelUI getUI() {
        return UI;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}
