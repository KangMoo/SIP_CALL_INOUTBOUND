package com.signal.inout;

import java.util.HashMap;

public class InboundModels  extends SipLogger {
    private static HashMap<String,BoundModel> InboundModels;
    private InboundModels(){}
    public static HashMap<String,BoundModel> getInstance(){
        if (InboundModels == null) InboundModels = new HashMap<>();
        return InboundModels;
    }
}
