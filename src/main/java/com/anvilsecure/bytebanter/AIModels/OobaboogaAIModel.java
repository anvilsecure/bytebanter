package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;
import com.anvilsecure.bytebanter.AIModelUIs.OobaboogaAIModelUI;
import org.json.*;
import java.util.Random;

public class OobaboogaAIModel extends AIModel {

    public OobaboogaAIModel(MontoyaApi api) {
        super(api, "Oobabooga");
        super.UI = new OobaboogaAIModelUI(this);
        super.messages = new JSONArray();
    }

    private String sendRequestToAI(JSONObject data, JSONObject params) {
        data.put("frequency_penalty", params.getDouble("frequency_penalty"));
        data.put("max_tokens", params.getInt("max_tokens"));
        data.put("presence_penalty", params.getDouble("presence_penalty"));
        data.put("temperature", params.getDouble("temperature"));
        data.put("top_p", params.getDouble("top_p"));
        data.put("stream", false);
        data.put("seed", new Random().nextInt() % 10000);
        JSONObject response = super.sendPostRequest(params.get("URL") + "chat/completions", data.toString(), params.getString("headers"));
        return response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    }

    @Override
    public String askAi() {
        JSONObject params = UI.getParams();
        JSONObject data = new JSONObject();

        // reset messages on "stateful" change
        messages = isStateful != params.getBoolean("stateful") ? new JSONArray() : messages;
        isStateful = params.getBoolean("stateful");

        if(messages.isEmpty()) {
            messages.put(new JSONObject().put("role", "system").put("content", params.getString("prompt")));
        }

        if(!isStateful) {
            messages.put(new JSONObject().put("role", "user").put("content", DEFAULT_MESSAGE));
        }
        data.put("messages", messages);
        String responseMessage = sendRequestToAI(data, params);
        messages.put(new JSONObject().put("role", "assistant").put("content", responseMessage));
        return responseMessage;
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

        return sendRequestToAI(data, params);
    }

    @Override
    public AIModelUI getUI(){
        return UI;
    }
}
