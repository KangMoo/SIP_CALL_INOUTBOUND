package com.signal.inout;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.address.AddressFactory;
import javax.sip.message.MessageFactory;

public class SendRequest {
    private static SendRequest sendRequest = null;

    private SendRequest(){};
    public static SendRequest getInstance(){
        if(sendRequest == null) sendRequest = new SendRequest();
        return sendRequest;
    }

    public void processRequest(Request request, ClientTransaction clientTransaction, SipProvider sipProvider){
        Dialog dialog = clientTransaction.getDialog();
        try{
            if(request.getMethod().equals(Request.INVITE)){
                dialog.sendRequest(clientTransaction);
            }
            else if(request.getMethod().equals(Request.BYE)){
                //if(.....) TODO 나중에 수정
                ClientTransaction byeClient = sipProvider.getNewClientTransaction(request);
                dialog.sendRequest(byeClient);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
