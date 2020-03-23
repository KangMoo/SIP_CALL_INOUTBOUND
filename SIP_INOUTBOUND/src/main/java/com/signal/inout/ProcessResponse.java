package com.signal.inout;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;

public class ProcessResponse {
    private static ProcessResponse processResponse;
    private ProcessResponse(){}
    public static ProcessResponse getInstance(){
        if(processResponse == null) processResponse = new ProcessResponse();
        return processResponse;
    }

    public void processResponseOk(ResponseEvent responseEvent) throws ParseException {//, ServerTransaction transactionId) {
        // Send ACK
        SendRequest.getInstance().sendACK(responseEvent);

        // Pass Response
        SendResponse.getInstance().passResponseOK(responseEvent);
    }

    public void processResponseBye(ResponseEvent responseEvent) {

    }

    public void processInvite(ResponseEvent responseEvent) {
        // Send ACK
        SendRequest.getInstance().sendACK(responseEvent);

        // Pass Response
        SendResponse.getInstance().passResponseOK(responseEvent);
    }
}
