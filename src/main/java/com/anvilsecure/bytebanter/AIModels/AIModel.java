package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;
import com.anvilsecure.bytebanter.AIModelUIs.OobaboogaAIModelUI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AIModel implements HttpHandler {
    protected final String name ;
    protected final MontoyaApi api;
    protected static final String DEFAULT_MESSAGE = "Generate a new payload";
    protected AIModelUI UI;
    protected JSONArray messages;
    protected Boolean isStateful = false;

    protected AIModel(MontoyaApi api, String name) {
        this.api = api;
        this.name = name;
    }

    abstract public String askAi();
    abstract public String askAi(String prompt, String user_input);
    abstract public AIModelUI getUI();

     public String getName(){
         return name;
     };

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
