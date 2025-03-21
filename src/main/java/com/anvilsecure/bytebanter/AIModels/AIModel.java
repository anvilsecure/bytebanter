package com.anvilsecure.bytebanter.AIModels;

import burp.api.montoya.http.handler.HttpHandler;
import com.anvilsecure.bytebanter.AIModelUIs.AIModelUI;

import java.io.IOException;

public interface AIModel extends HttpHandler {
    String askAi() throws IOException;
    String getName();
    AIModelUI getUI();
}
