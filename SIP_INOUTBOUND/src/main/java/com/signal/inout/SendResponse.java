package com.signal.inout;

import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.address.AddressFactory;
import javax.sip.message.MessageFactory;

public class SendResponse {

    public static SendResponse processResponse = null;
    private SendResponse(){}
    public static SendResponse getInstance(){
        if(processResponse == null) processResponse = new SendResponse();
        return processResponse;
    }
    // Trying: 100, Ringing: 180

    public void passResponseOK(ResponseEvent responseEvent){
        try{
            HeaderFactory headerFactory = SipCall.headerFactory;
            MessageFactory messageFactory = SipCall.messageFactory;
            AddressFactory addressFactory = SipCall.addressFactory;
            Response response = responseEvent.getResponse();

            String callId = ((CallIdHeader) response.getHeader( "Call-ID")).getCallId();
            String in_callId = SessionMap.getInstance().findSession(callId).getCallId();

            Request request = SessionMap.getInstance().findRequest(in_callId);

            String sdp = response.toString().substring(response.toString().length() - response.getContentLength().getContentLength(), response.toString().length());;

            response = messageFactory.createResponse(Response.OK, request);
            if(sdp != null){
                byte[] sdpbytes = sdp.getBytes();
                ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
                response.setContent(sdpbytes,contentTypeHeader);
            }

//            String strAddress = "<" + ((SIPMessage)request).getTo().getAddress()+":"+((SIPMessage)request).getLocalPort() + ">";
//            Address address = addressFactory.createAddress(strAddress);
            
            Address address = addressFactory.createAddress("<sip:127.0.0.1:5070>");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);


            ServerTransaction st = SessionMap.getInstance().findTransactionId(in_callId);
            System.out.println("st = " + st);
            st.sendResponse(response);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendResponseTrying(Request request, ServerTransaction transactionId){
        System.out.println("\n== processResponseTrying ~ ==");
        try {
            System.out.println("== Response Trying : \n" +request);
            Response response = SipCall.messageFactory.createResponse( Response.TRYING, request);
            System.out.println("== Response Data : \n" + response);
            transactionId.sendResponse(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("== ~ processResponseTrying ==\n");
    }
    public void sendResponseRing (Request request, ServerTransaction transactionId) {
        try {
            System.out.println("\n== processResponseRing ~ ==");
            System.out.println("== Response Ringing : \n" + request);
            Response response = SipCall.messageFactory.createResponse( Response.RINGING, request);
            System.out.println("== SipResponse Ringing : \n" + response);
            transactionId.sendResponse(response);
            System.out.println("== ~ processResponseRing ==\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendACK(RequestEvent responseEvent) {
        try{
            Request request = responseEvent.getRequest();

            Dialog dialog = responseEvent.getDialog();
            Long cseq = ((SIPMessage)request).getCSeq().getSeqNumber();
            Request ackRequest = dialog.createAck(cseq);
            dialog.sendAck(ackRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendOK(RequestEvent requestEvent){
        try{
            Request request = requestEvent.getRequest();
            Dialog dialog = requestEvent.getDialog();
            Long cseq = ((SIPMessage)request).getCSeq().getSeqNumber();

            /////////////

            /////////////

            //dialog.createRequest();
            //dialog.sendAck();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send487(RequestEvent responseEvent) {
        try{

        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
