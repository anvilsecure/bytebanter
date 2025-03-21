package com.anvilsecure.bytebanter;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.intruder.GeneratedPayload;
import burp.api.montoya.intruder.IntruderInsertionPoint;
import burp.api.montoya.intruder.PayloadGenerator;
import com.anvilsecure.bytebanter.AIModels.AIModel;
import com.anvilsecure.bytebanter.AIModels.OobaboogaAIModel;

import java.io.IOException;

public class ByteBanterPayloadGenerator implements PayloadGenerator {

    private final AIModel[] models;
    private AIModel model;
    private final MontoyaApi api;

    public ByteBanterPayloadGenerator(MontoyaApi api) {
        models = new AIModel[]{new OobaboogaAIModel(api)};
        this.api = api;
        //Stateful models need to be registered as handlers
        api.http().registerHttpHandler(models[0]);
    }

    public void setModel(int index){
        model = models[index];
    }

    public AIModel getModel(){
        return model;
    }

    public String[] getModelNames(){
        String[] names = new String[models.length];
        for (int i = 0; i < models.length; i++) {
            names[i] = models[i].getName();
        }
        return names;
    }

    @Override
    public GeneratedPayload generatePayloadFor(IntruderInsertionPoint intruderInsertionPoint) {
        try {
            return GeneratedPayload.payload(model.askAi());
        }catch (IOException e){
            api.logging().logToError("Error generating payload", e);
            return GeneratedPayload.end();
        }

    }
}
