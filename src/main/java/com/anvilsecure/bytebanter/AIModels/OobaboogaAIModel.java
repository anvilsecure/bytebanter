package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;
import com.anvilsecure.bytebanter.AIModelUIs.OobaboogaAIModelUI;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OobaboogaAIModel extends AIModel {
    private static final String DEFAULT_MESSAGE = "Generate a new payload";
    private final OobaboogaAIModelUI UI;
    private JSONArray messages;
    private Boolean isStateful = false;

    public OobaboogaAIModel(MontoyaApi api) {
        super(api, "Oobabooga");
        UI = new OobaboogaAIModelUI();
        messages = new JSONArray();
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
        data.put("frequency_penalty", params.getDouble("frequency_penalty"));
        data.put("max_tokens", params.getInt("max_tokens"));
        data.put("presence_penalty", params.getDouble("presence_penalty"));
        data.put("temperature", params.getDouble("temperature"));
        data.put("top_p", params.getDouble("top_p"));
        data.put("stream", false);
        data.put("seed", new Random().nextInt() % 10000);

        JSONObject response = super.sendPostRequest(params.get("URL") + "chat/completions", data.toString());
        String responseMessage = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

        messages.put(new JSONObject().put("role", "assistant").put("content", responseMessage));

        return responseMessage;
    }

    @Override
    public AIModelUI getUI(){
        return UI;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        JSONObject params = UI.getParams();
        if (isStateful) {
            Matcher matcher = Pattern.compile(params.getString("regex")).matcher(httpResponseReceived.bodyToString());
            if (matcher.find()) {
                String rxp = params.getBoolean("b64") ?
                        Arrays.toString(Base64.getDecoder().decode(matcher.group(1))) : matcher.group(1);
                api.logging().logToOutput("------------------------********** Target Response: **********-------------");
                api.logging().logToOutput(rxp);
                api.logging().logToOutput("-----------------------------------------------------------------------");
                messages.put(new JSONObject().put("role", "user").put("content", rxp));
            }
        }
        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }
}
