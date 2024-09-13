package com.anvilsecure.bytebanter.AIEngines;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.anvilsecure.bytebanter.AIEngineUIs.AIEngineUI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AIEngine implements HttpHandler {
    protected final String name ;
    protected final MontoyaApi api;
    protected static final String DEFAULT_MESSAGE = "Generate a new payload";
    protected AIEngineUI UI;
    protected JSONArray messages;
    protected Boolean isStateful = false;

    protected AIEngine(MontoyaApi api, String name) {
        this.api = api;
        this.name = name;
    }

    public AIEngineUI getUI(){
        return UI;
    };

     public String getName(){
         return name;
     };

     public MontoyaApi getApi(){
         return api;
     }

     public String askAi() {
        JSONObject params = UI.getParams();
        JSONObject data = packData(new JSONObject(), params);

        // reset messages on "stateful" change
        messages = isStateful != params.getBoolean("stateful") ? new JSONArray() : messages;
        isStateful = params.getBoolean("stateful");

        if(messages.isEmpty()) {
            messages.put(new JSONObject().put("role", "system").put("content", params.getString("prompt")));
        }

        if(!isStateful) {
            messages.put(new JSONObject().put("role", "user").put("content", DEFAULT_MESSAGE));
        }
        data.remove("messages");
        data.put("messages", messages);
        String responseMessage = sendRequestToAI(data, params);
        messages.put(new JSONObject().put("role", "assistant").put("content", responseMessage));
        return responseMessage;
    }

    // used for other interaction with the AI (i.e.: prompt optimization)
    public String askAi(String prompt, String user_input) {
        JSONObject params = UI.getParams();
        JSONObject data = packData(new JSONObject(), params);;
        JSONArray m = new JSONArray();
        m.put(new JSONObject().put("role", "system").put("content", prompt));
        m.put(new JSONObject().put("role", "user").put("content", user_input));
        data.remove("messages");
        data.put("messages", m);

        return sendRequestToAI(data, params);
    }

     protected JSONObject packData(JSONObject data, JSONObject params) {
        data.put("frequency_penalty", params.getDouble("frequency_penalty")/20);
        data.put("max_tokens", params.getInt("max_tokens"));
        data.put("presence_penalty", params.getDouble("presence_penalty")/20);
        data.put("temperature", params.getDouble("temperature")/100);
        data.put("top_p", params.getDouble("top_p")/20);
        data.put("stream", false);
        data.put("seed", new Random().nextInt() % 10000);
        api.logging().logToOutput("Model Config: "+ data.toString());
        return data;
     }

     protected String sendRequestToAI(JSONObject data, JSONObject params) {
        JSONObject response = sendPostRequest(params.get("URL") + "chat/completions", data.toString(), params.getString("headers"));
        return response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
     }

     JSONObject sendPostRequest(String urlString, String payload, String headers) {
        HttpRequest request = HttpRequest.httpRequestFromUrl(urlString);
        request = request.withMethod("POST");
        if (!headers.isEmpty()) {
            HttpHeader httpHeader = HttpHeader.httpHeader(headers);
            request = request.withAddedHeader(httpHeader);
        }
        request = request.withAddedHeader("Content-Type", "application/json");
        request = request.withBody(payload);
        api.logging().logToOutput(request.toString());
        HttpRequest finalRequest = request;
        HttpRequestResponse response = api.http().sendRequest(finalRequest);
        api.logging().logToOutput("---------------------------- Attacker Response: -------------------------------");
        api.logging().logToOutput(response.response().bodyToString());
        api.logging().logToOutput("-------------------------------------------------------------------------------");
        return new JSONObject(response.response().bodyToString());
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
