package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AIModel implements HttpHandler {
    private final String name ;
    final MontoyaApi api;

    protected AIModel(MontoyaApi api, String name) {
        this.api = api;
        this.name = name;
    }

    abstract public String askAi() throws IOException;
    abstract public AIModelUI getUI();

     public String getName(){
         return name;
     };

     JSONObject sendPostRequest(String urlString, String payload) {
        HttpRequest request = HttpRequest.httpRequestFromUrl(urlString);
        request = request.withMethod("POST");
        request = request.withAddedHeader("Content-Type", "application/json");
        request = request.withBody(payload);
        api.logging().logToOutput(payload);
        HttpRequest finalRequest = request;
        HttpRequestResponse response = api.http().sendRequest(finalRequest);
        api.logging().logToOutput(response.response().bodyToString());
        return new JSONObject(response.response().bodyToString());
    }

}
