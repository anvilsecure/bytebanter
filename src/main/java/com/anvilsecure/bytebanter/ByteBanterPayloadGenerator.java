package com.anvilsecure.bytebanter;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.intruder.GeneratedPayload;
import burp.api.montoya.intruder.IntruderInsertionPoint;
import burp.api.montoya.intruder.PayloadGenerator;
import com.anvilsecure.bytebanter.AIEngines.AIEngine;
import com.anvilsecure.bytebanter.AIEngines.OobaboogaAIEngine;


public class ByteBanterPayloadGenerator implements PayloadGenerator {

    private final AIEngine[] engines;
    private AIEngine engine;

    public ByteBanterPayloadGenerator(MontoyaApi api) {
        engines = new AIEngine[]{new OobaboogaAIEngine(api)};
        //Stateful engines need to be registered as handlers
        api.http().registerHttpHandler(engines[0]);
    }

    public void setEngine(int index){
        engine = engines[index];
    }

    public AIEngine getEngine(){
        return engine;
    }

    public String[] getEnginesNames(){
        String[] names = new String[engines.length];
        for (int i = 0; i < engines.length; i++) {
            names[i] = engines[i].getName();
        }
        return names;
    }

    @Override
    public GeneratedPayload generatePayloadFor(IntruderInsertionPoint intruderInsertionPoint) {
            return GeneratedPayload.payload(engine.askAi());
    }
}
