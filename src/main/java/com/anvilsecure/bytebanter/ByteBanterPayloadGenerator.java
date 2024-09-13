package com.anvilsecure.bytebanter;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.intruder.GeneratedPayload;
import burp.api.montoya.intruder.IntruderInsertionPoint;
import burp.api.montoya.intruder.PayloadGenerator;
import com.anvilsecure.bytebanter.AIEngines.AIEngine;
import com.anvilsecure.bytebanter.AIEngines.BurpAIEngine;
import com.anvilsecure.bytebanter.AIEngines.OllamaAIEngine;
import com.anvilsecure.bytebanter.AIEngines.OobaboogaAIEngine;

import java.util.ArrayList;
import java.util.List;


public class ByteBanterPayloadGenerator implements PayloadGenerator {

    private final List<AIEngine> engines;
    private AIEngine engine;

    public ByteBanterPayloadGenerator(MontoyaApi api) {
        engines = new ArrayList<>();
        engines.add(new OobaboogaAIEngine(api));
        engines.add(new OllamaAIEngine(api));
        if(api.ai().isEnabled()){
            engines.add(new BurpAIEngine(api));
        }
    }

    public void setEngine(int index){
        engine = engines.get(index);
    }

    public AIEngine getEngine(){
        return engine;
    }

    public List<AIEngine> getEngines() {
        return engines;
    }

    public String[] getEnginesNames(){
        String[] names = new String[engines.size()];
        for (int i = 0; i < engines.size(); i++) {
            names[i] = engines.get(i).getName();
        }
        return names;
    }

    @Override
    public GeneratedPayload generatePayloadFor(IntruderInsertionPoint intruderInsertionPoint) {
            return GeneratedPayload.payload(engine.askAi());
    }
}
