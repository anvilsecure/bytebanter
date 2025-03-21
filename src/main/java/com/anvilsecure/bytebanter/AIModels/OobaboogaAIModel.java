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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class OobaboogaAIModel implements AIModel {
    private static final String DEFAULT_MESSAGE = "Generate a new payload";
    private final OobaboogaAIModelUI UI;
    private JSONArray messages;
    private Boolean isStateful = false;
    private final MontoyaApi api;

    public OobaboogaAIModel(MontoyaApi api) {
        UI = new OobaboogaAIModelUI();
        messages = new JSONArray();
        this.api = api;
    }

    @Override
    public String askAi() throws IOException {
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

        JSONObject response = sendPostRequest(params.get("URL") + "chat/completions", data.toString());
        String responseMessage = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

        messages.put(new JSONObject().put("role", "assistant").put("content", responseMessage));

        return responseMessage;
    }

    @Override
    public String getName(){
        return "Oobabooga";
    }

    @Override
    public AIModelUI getUI(){
        return UI;
    }

    private JSONObject sendPostRequest(String urlString, String payload) throws IOException {
        URL url = new URL(urlString);
        api.logging().logToOutput(payload);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return new JSONObject(response.toString());
    }


    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        JSONObject params = UI.getParams();
        if (isStateful) {
            api.logging().logToOutput("got a response!");
            //TODO: add here logic to parse target responses
        }
        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }
}
