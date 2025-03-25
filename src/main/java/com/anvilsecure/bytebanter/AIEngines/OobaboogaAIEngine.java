package com.anvilsecure.bytebanter.AIEngines;

import burp.api.montoya.MontoyaApi;
import com.anvilsecure.bytebanter.AIEngineUIs.OobaboogaAIEngineUI;
import org.json.*;

public class OobaboogaAIEngine extends AIEngine {

    public OobaboogaAIEngine(MontoyaApi api) {
        super(api, "Oobabooga");
        super.UI = new OobaboogaAIEngineUI(this);
        super.messages = new JSONArray();
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
}
