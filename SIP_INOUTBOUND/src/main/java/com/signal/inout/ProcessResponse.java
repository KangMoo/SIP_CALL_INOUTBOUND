package com.signal.inout;

import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

public class ProcessResponse {



    public void processResponseOk(ResponseEvent responseEvent) throws ParseException {//, ServerTransaction transactionId) {

        try{
            SIPMessage sipMessage = ((SIPMessage)(responseEvent.getResponse().clone()));
            HeaderFactory headerFactory = SipFactory.getInstance().createHeaderFactory();
            FromHeader fromHeader = headerFactory.createFromHeader(sipMessage.getToHeader().getAddress(), sipMessage.getToTag());
            ToHeader toHeader = headerFactory.createToHeader(sipMessage.getFromHeader().getAddress(), sipMessage.getFromTag());

            MessageFactory messageFactory = SipFactory.getInstance().createMessageFactory();
            AddressFactory addressFactory = SipFactory.getInstance().createAddressFactory();
            Response response = responseEvent.getResponse();

            String callId = ((CallIdHeader) response.getHeader( "Call-ID")).getCallId();


            sdp = response.toString().substring(response.toString().length() - response.getContentLength().getContentLength(), response.toString().length());;

            Request request = SessionMap.getInstance().findRequest(callId);
            response = messageFactory.createResponse(Response.OK, request);

            byte[] sdpbytes = sdp.getBytes();
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
            response.setContent(sdpbytes,contentTypeHeader);


            Address address = addressFactory.createAddress("<sip:127.0.0.1:5070>");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);


            String in_callId = SessionMap.getInstance().findSession(callId).getCallId();
            ServerTransaction st = SessionMap.getInstance().findTransactionId(in_callId);
            System.out.println("st = " + st);


            st.sendResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*
        SIPMessage sipMessage = ((SIPMessage)(responseEvent.getResponse().clone()));


        Response response = responseEvent.getResponse();
        String sdp = response.toString().substring(response.toString().length() - response.getContentLength().getContentLength(), response.toString().length());;

        String callId = ((CallIdHeader) response.getHeader( "Call-ID")).getCallId();
        String in_callId = SessionMap.getInstance().findSession(callId).getCallId();
        ServerTransaction st = SessionMap.getInstance().findTransactionId(in_callId);


        sipMessage.setFromTag("");
        sipMessage.setCallId(in_callId);
        try{
            HeaderFactory headerFactory = SipFactory.getInstance().createHeaderFactory();
            FromHeader fromHeader = headerFactory.createFromHeader(sipMessage.getToHeader().getAddress(), sipMessage.getToTag());
            ToHeader toHeader = headerFactory.createToHeader(sipMessage.getFromHeader().getAddress(), sipMessage.getFromTag());

            sipMessage.setTo(toHeader);
            sipMessage.setFrom(fromHeader);

            response = (Response)sipMessage;

            System.out.println("st = " + st);

            st.sendResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    public void processResponseBye(ResponseEvent responseEvent) {
    }

}
