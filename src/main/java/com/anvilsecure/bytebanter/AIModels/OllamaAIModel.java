package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import com.anvilsecure.bytebanter.AIModelUIs.OllamaAIModelUI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

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
    protected String sendRequestToAI(JSONObject data, JSONObject params) {
        data.put("frequency_penalty", params.getDouble("frequency_penalty"));
        data.put("max_tokens", params.getInt("max_tokens"));
        data.put("presence_penalty", params.getDouble("presence_penalty"));
        data.put("temperature", params.getDouble("temperature"));
        data.put("top_p", params.getDouble("top_p"));
        data.put("stream", false);
        data.put("seed", new Random().nextInt() % 10000);
        data.put("model", params.getString("model"));
        JSONObject response = sendPostRequest(params.get("URL") + "chat/completions", data.toString(), params.getString("headers"));
        return response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    }

    // used for other interaction with the AI (i.e.: prompt optimization)
    @Override
    public String askAi(String prompt, String user_input) {
        JSONObject params = UI.getParams();
        JSONObject data = new JSONObject();
        JSONArray m = new JSONArray();
        m.put(new JSONObject().put("role", "system").put("content", prompt));
        m.put(new JSONObject().put("role", "user").put("content", user_input));
        data.put("messages", m);
        data.put("model", params.getString("model"));

        return sendRequestToAI(data, params);
    }
}
