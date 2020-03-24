package com.signal.inout;

import com.signal.inout.outbound.OutSetting;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.SIPHeaderList;
import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.address.AddressFactory;
import javax.sip.message.MessageFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class SendResponse  extends SipLogger {

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
            SessionModel sessionModel = SessionMap.getInstance().findSession(callId);


            //Request request = SessionMap.getInstance().findRequest(in_callId);

            String sdp = response.toString().substring(response.toString().length() - response.getContentLength().getContentLength(), response.toString().length());;

            response = messageFactory.createResponse(Response.OK,sessionModel.getRequest());
            if(sdp != null){
                byte[] sdpbytes = sdp.getBytes();
                ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
                response.setContent(sdpbytes,contentTypeHeader);
            }


            SIPMessage sipMessage = (SIPMessage) response;
            String fromtag = sessionModel.getToTag();
            SipURI fromSipUrl=addressFactory.createSipURI(sessionModel.getToUser(),sessionModel.getToip());
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromSipUrl), fromtag);

            String totag = sessionModel.getFromTag();
            SipURI toSipUrl=addressFactory.createSipURI(sessionModel.getFromUser(),sessionModel.getFromip());
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toSipUrl), totag);

            sipMessage.setTo(toHeader);
            sipMessage.setFrom(fromHeader);

            Address address = addressFactory.createAddress("<sip:"+sessionModel.getToip()+":"+sessionModel.getToPort()+">");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);

            ServerTransaction st = SessionMap.getInstance().findTransactionId(callId);
            st.sendResponse(response);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendResponseTrying(Request request, ServerTransaction transactionId){
        logger.debug("\n== processResponseTrying ~ ==");
        try {
            logger.debug("\n== Response Trying : \n" +request);
            Response response = SipCall.messageFactory.createResponse( Response.TRYING, request);
            logger.debug("\n== Response Data : \n" + response);
            transactionId.sendResponse(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("\n== ~ processResponseTrying ==\n");
    }
    public void sendResponseRing (Request request, ServerTransaction transactionId) {
        try {
            logger.debug("\n== processResponseRing ~ ==");
            logger.debug("\n== Response Ringing : \n" + request);
            Response response = SipCall.messageFactory.createResponse( Response.RINGING, request);
            logger.debug("\n== SipResponse Ringing : \n" + response);
            transactionId.sendResponse(response);
            logger.debug("\n== ~ processResponseRing ==\n");

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
}
