package com.anvilsecure.bytebanter.AIEngines;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ai.chat.Message;
import burp.api.montoya.ai.chat.PromptException;
import burp.api.montoya.ai.chat.PromptOptions;
import burp.api.montoya.ai.chat.PromptResponse;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import com.anvilsecure.bytebanter.AIEngineUIs.BurpAIEngineUI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BurpAIEngine extends AIEngine {

    public BurpAIEngine(MontoyaApi api) {
        super(api, "BurpAI");
        super.UI = new BurpAIEngineUI(this);
        super.messages = new JSONArray();
    }

    @Override
    public String askAi() {
        JSONObject params = UI.getParams();
        JSONObject data = packData(new JSONObject(), params);


        // reset messages on "stateful" change
        messages = isStateful != params.getBoolean("stateful") ? new JSONArray() : messages;
        isStateful = params.getBoolean("stateful");

        if(messages.isEmpty()) {
            messages.put(Message.systemMessage(params.getString("prompt")));
        }

        if(!isStateful) {
            messages.put(Message.userMessage(DEFAULT_MESSAGE));
        }
        data.remove("messages");
        data.put("messages", messages);
        String responseMessage = sendRequestToAI(data, params);
        api.logging().logToOutput("--------------******* AI Payload: *******-----------------");
        api.logging().logToOutput(responseMessage);
        api.logging().logToOutput("----------------------------------------------------------");
        messages.put(Message.assistantMessage(responseMessage));
        return responseMessage;
    }

    // used for other interaction with the AI (i.e.: prompt optimization)
    @Override
    public String askAi(String prompt, String user_input) {
        JSONObject params = UI.getParams();
        JSONObject data = packData(new JSONObject(), params);;
        JSONArray m = new JSONArray();
        m.put(Message.systemMessage(prompt));
        m.put(Message.userMessage(user_input));
        data.remove("messages");
        data.put("messages", m);

        return sendRequestToAI(data, params);
    }

    @Override
    protected JSONObject packData(JSONObject data, JSONObject params) {
        data.put("temperature", params.getDouble("temperature")/100);
        api.logging().logToOutput("Model Config: "+ data.toString());
        return data;
    }

    @Override
    protected String sendRequestToAI(JSONObject data, JSONObject params) {
        try {
            // set the temperature with PromptOptions
            PromptOptions options = PromptOptions.promptOptions().withTemperature(data.getDouble("temperature"));

            // convert from JSONArray to BurpAI Message Array
            JSONArray messages = data.getJSONArray("messages");
            Message[] context = new Message[messages.length()];
            for (int i = 0; i < messages.length(); i++) {
                context[i] = (Message) messages.get(i);
            }

            // Execute Prompt
            PromptResponse response = api.ai().prompt().execute(options, context);
            return response.content();
        }catch (PromptException e) {
            api.logging().logToError("An error occurred while processing the prompt: " + e.getMessage());
            api.logging().logToError("Unable to retrieve AI response. Please try again later. " +
                    "(Mostly this error could be related to BurpAI disabled or AI credit exhausted)");
            // Exception thrown anyway, to stop payload generation.
            throw new PromptException(e.getMessage());
        }
    }

     @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        JSONObject params = UI.getParams();
        if (isStateful) {
            Matcher matcher = Pattern.compile(params.getString("regex")).matcher(httpResponseReceived.bodyToString());
            if (matcher.find()) {
                String rxp = params.getBoolean("b64") ?
                        Arrays.toString(Base64.getDecoder().decode(matcher.group(1))) : matcher.group(1);
                api.logging().logToOutput("----------------********** Target Response: **********-----------------");
                api.logging().logToOutput(rxp);
                api.logging().logToOutput("-----------------------------------------------------------------------");
                messages.put(Message.userMessage(rxp));
            }
        }
        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }

}
