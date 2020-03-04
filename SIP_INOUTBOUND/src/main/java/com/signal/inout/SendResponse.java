package com.signal.inout;

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
        if(processResponse==null) processResponse=new SendResponse();
        return processResponse;
    }
    // Trying: 100, Ringing: 180
    public void processResponseOk (Request request, ServerTransaction transactionId, MessageFactory messageFactory,HeaderFactory headerFactory,AddressFactory addressFactory,String sdp) {
        try{
            Response response = messageFactory.createResponse( Response.OK, request );

            byte[] sdpbytes = sdp.getBytes();
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
            response.setContent(sdpbytes,contentTypeHeader);


            Address address = addressFactory.createAddress("<"+((SipURI)request.getRequestURI()).toString()+">");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);

            transactionId.sendResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void processResponseBye (ResponseEvent responseEvent) {

    }

    public void processResponseTrying(Request request, ServerTransaction transactionId, MessageFactory messageFactory){
        System.out.println("\n== processResponseTrying ~ ==");
        try {
            System.out.println("== Response Trying : \n" +request);
            Response response = messageFactory.createResponse( Response.TRYING, request);
            System.out.println("== Response Data : \n" + response);
            transactionId.sendResponse(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("== ~ processResponseTrying ==\n");
    }
    public void processResponseRing (Request request, ServerTransaction transactionId, MessageFactory messageFactory) {
        try {
            System.out.println("\n== processResponseRing ~ ==");
            System.out.println("== Response Ringing : \n" + request);
            Response response = messageFactory.createResponse( Response.RINGING, request);
            System.out.println("== SipResponse Ringing : \n" + response);
            transactionId.sendResponse(response);
            System.out.println("== ~ processResponseRing ==\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processResponseBye (Request request, ServerTransaction transactionId, MessageFactory messageFactory) {
        System.out.println("\n== processResponseBye ~ ==");
        try {
            Response response = messageFactory.createResponse( Response.OK, request);
            System.out.println("== Incomming Response Is : \n" +response);
            transactionId.sendResponse(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("== ~ processResponseBye ==\n");
    }

    public void processResponseOk_Temp (Request request, ServerTransaction transactionId, MessageFactory messageFactory,HeaderFactory headerFactory,AddressFactory addressFactory) {
        String sdp="";
        sdp+="v=0\r\n";
        sdp+="o=heokangmoo 1906 3217 IN IP4 127.0.0.1\r\n";
        sdp+="s=Talk\r\n";
        sdp+="c=IN IP4 127.0.0.1\r\n";
        sdp+="t=0 0\r\n";
        sdp+="a=rtcp-xr:rcvr-rtt=all:10000 stat-summary=loss,dup,jitt,TTL voip-metrics\r\n";
        sdp+="m=audio 7078 RTP/AVP 96 97 101 98\r\n";
        sdp+="a=rtpmap:96 AMR/8000\r\n";
        sdp+="a=fmtp:96 octet-align=1\r\n";

        try{
            Response response = messageFactory.createResponse( Response.OK, request );

            byte[] sdpbytes = sdp.getBytes();
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
            response.setContent(sdpbytes,contentTypeHeader);


            Address address = addressFactory.createAddress("<"+((SipURI)request.getRequestURI()).toString()+">");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);

            transactionId.sendResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
