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
}
