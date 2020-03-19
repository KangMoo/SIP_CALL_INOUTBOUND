package com.signal.inout;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.header.*;

import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.net.server.Client;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.message.SIPMessage;

import java.util.ArrayList;

public class ProcessRequest {


    //private static HashMap<String, SessionModel> SessionHmap = new HashMap<>();
    private static ProcessRequest processRequest = null;
    private ProcessRequest(){}
    public static ProcessRequest getInstance(){
        if(processRequest == null) processRequest = new ProcessRequest();
        return processRequest;
    }

    public void processRequestINVITE(RequestEvent requestEvent,
                                     MessageFactory messageFactory,
                                     AddressFactory addressFactory,
                                     HeaderFactory headerFactory,
                                     SipProvider sipProvider,
                                     int port, SipCall sipCall){
        Request request = requestEvent.getRequest();
        try{
            ServerTransaction transactionId = requestEvent.getServerTransaction();
            if(transactionId == null)
            {
                transactionId = ((SipProvider)requestEvent.getSource()).getNewServerTransaction(request);
                System.out.println("Create Transaction ID : " + transactionId);
            }

            System.out.println("== Request ~ ==\n" + request + "\n== ~ Request ==");

            System.out.println("== Request Test ~ ==\n" +
                    ((SipURI)request.getRequestURI()).toString() +
                    "\n== ~ Request Test ==");

            // 100 Trying
            SendResponse.getInstance().processResponseTrying(request,transactionId,messageFactory);
            // 180 Ringing
            SendResponse.getInstance().processResponseRing(request,transactionId,messageFactory);

            // inbound Setting ~
            BoundModel boundModel = BoundModel.makeModel(request,sipProvider);
            System.out.println("boundModel.getCallId1() = " + boundModel.getCallId1());
            System.out.println("boundModel.getCallId2() = " + boundModel.getCallId2());
            SessionModel in_session = SessionMap.getInstance().makeSession(
                    boundModel.getCallId2(),
                    boundModel.getFromip(),
                    boundModel.getFromPort(),
                    boundModel.getFromUser(),
                    boundModel.getToip(),
                    boundModel.getToPort(),
                    boundModel.getToUser(),
                    "inbound",
                    boundModel.getSdp(),
                    transactionId,
                    request,20L);
            SessionMap.getInstance().putSession(boundModel.getCallId1(),in_session);
            System.out.println("\n== Inbound Session ~ ==\n"+in_session.toString()+"\n== ~ Inbound Session ==\n");
            // ~ inbound Setting

            // outbound Setting ~
            SessionModel out_session = SessionMap.getInstance().makeSession(
                    boundModel.getCallId1(),
                    boundModel.getToip(),
                    port,
                    boundModel.getFromUser(),
                    boundModel.getToip(),
                    boundModel.getToPort(),
                    boundModel.getToUser(),
                    "outbound",boundModel.getSdp(),transactionId,request,20L);
            SessionMap.getInstance().putSession(boundModel.getCallId2(), out_session);

            Request out_request;
            CallIdHeader callIdHeader = headerFactory.createCallIdHeader(boundModel.getCallId2());

            String sdp = out_session.getSdp();

//            out_request = test(out_session,request,Request.INVITE,boundModel.getCallId2(),sipProvider);
            out_request = SendRequest.getInstance().makePassReq(in_session,Request.INVITE,boundModel.getCallId2(),sipProvider);
//            ClientTransaction clientTransaction;
//            if (SessionMap.getInstance().findSession(boundModel.getCallId2()).getClientTransaction() == null) {
//                clientTransaction = sipProvider.getNewClientTransaction(out_request);
//                SessionMap.getInstance().setClientTransactionCallId(boundModel.getCallId2(),clientTransaction);
//            }
//            else {
//                clientTransaction = SessionMap.getInstance().findClientTransaction(boundModel.getCallId2());
//                clientTransaction.createCancel();
//            }
//            Dialog dialog = clientTransaction.getDialog();
//            dialog.sendRequest(clientTransaction);

            //System.out.println("=Test333\n"+test+"\nTest333=");

            //System.out.println("== OutBound Session ~ ==\n"+out_session+"\n== ~ Outbound Session == ");
            // ~ outbound Setting

            //SessionMap.getInstance().in2outSessionModel(in_session,sipProvider);
//            System.out.println("!!TEST!!~");
//            SIPMessage sipMessage = (SIPMessage)(request.clone());
//            System.out.println("sipMessage = " + sipMessage.getFrom());
//            System.out.println("sipMessage.getFromTag() = " + sipMessage.getFromTag());
//            System.out.println("sipMessage.getSIPVersion() = " + sipMessage.getSIPVersion());
//            System.out.println("sipMessage.getFirstLine() = " + sipMessage.getFirstLine());
//            System.out.println("sipMessage.getHeader(\"Via\") = " + sipMessage.getHeader("Via"));
//            System.out.println("sipMessage.getContent() = " + sipMessage.getContent());
//            System.out.println("sipMessage.getHeaders() = " + sipMessage.getHeaders());
//            System.out.println("sipMessage.getToHeader() = " + sipMessage.getToHeader());
//            System.out.println("sipMessage.getViaHeaders() = " + sipMessage.getViaHeaders());
//            Request temp = SendRequest.getInstance().makePassReq(in_session,Request.INVITE,boundModel.getCallId2(),sipProvider);
//            System.out.println("request = " + request);
//            System.out.println("temp = " + temp);
//
//            System.out.println("~!!TEST!!");
            // Send Invite
            //SendRequest.getInstance().processRequest(request,);

            // 200 OK
            //SendResponse.getInstance().processResponseOk_Temp(request,transactionId,messageFactory,headerFactory,addressFactory);
            System.out.println("transactionId = " + transactionId);
            ClientTransaction clientTransaction;
            if (SessionMap.getInstance().findSession(boundModel.getCallId2()).getClientTransaction() == null) {
                clientTransaction = sipProvider.getNewClientTransaction(out_request);
                SessionMap.getInstance().setClientTransactionCallId(boundModel.getCallId2(),clientTransaction);
            }
            else {
                clientTransaction = SessionMap.getInstance().findClientTransaction(boundModel.getCallId2());
                clientTransaction.createCancel();
            }
            Dialog dialog = clientTransaction.getDialog();
            dialog.sendRequest(clientTransaction);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public Request makeRequest(SessionModel sessionModel,
                               String method,
                               String out_CallId, String fromTags, String toTags, SipProvider sipProvider,
                               MessageFactory messageFactory,
                               AddressFactory addressFactory,
                               HeaderFactory headerFactory){
        Request request=null;
        String sdp = sessionModel.getSdp();

        try{
            SipURI contactURI = addressFactory.createSipURI(sessionModel.getFromUser(), sessionModel.getFromip());
            contactURI.setPort(sessionModel.getFromPort());
            Address contactAddress = addressFactory.createAddress(contactURI);

            contactAddress.setDisplayName(sessionModel.getFromUser());

            ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

            if(sdp !=null){
                byte[] contents = sdp.getBytes();
                ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
                request.setContent(contents, contentTypeHeader);
            }
            request.addHeader(contactHeader);
        }catch (Exception e){
            e.printStackTrace();
        }

        return request;
    }

    public Request test(SessionModel sessionModel,
                        Request in_request,
                        String method,
                        String out_CallId,
                        SipProvider sipProvider){


        Request out_request = null;
        try{
            String sdp=sessionModel.getSdp();
            SipFactory sipFactory = SipFactory.getInstance();
            MessageFactory messageFactory  = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            AddressFactory addressFactory = sipFactory.createAddressFactory();


            ArrayList viaHeaders = new ArrayList();
            String address = sipProvider.getListeningPoint().getIPAddress();
            ViaHeader viaHeader = headerFactory.createViaHeader(address, sipProvider.getListeningPoint("udp").getPort(),"udp",null);
            viaHeaders.add(viaHeader);

            String fromTags = (new Utils()).generateTag();
            String toTags = null;
            //String toTags = (new Utils()).generateTag();
            SipURI toSipUrl = addressFactory.createSipURI(sessionModel.getToUser(),sessionModel.getToip());
            SipURI fromSipUrl=addressFactory.createSipURI(sessionModel.getFromUser(),sessionModel.getFromip());
            CallIdHeader callIdheader =headerFactory.createCallIdHeader(out_CallId);
            SipURI sipURI=((SipURI)addressFactory.createSipURI(sessionModel.getToUser(),sessionModel.getToip()+":"+sessionModel.getFromPort()));
            CSeqHeader cseqHeader = headerFactory.createCSeqHeader(20L,method);
//            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromSipUrl), fromTags);
//            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toSipUrl), toTags);
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(toSipUrl), toTags);
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(fromSipUrl), fromTags);
            MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);



            out_request = messageFactory.createRequest(
                    sipURI,
                    method,
                    callIdheader,
                    cseqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwardsHeader
            );


            SipURI contactURI = addressFactory.createSipURI(sessionModel.getFromUser(), sessionModel.getFromip());
            contactURI.setPort(sessionModel.getFromPort());
            Address contactAddress = addressFactory.createAddress(contactURI);

            contactAddress.setDisplayName(sessionModel.getFromUser());

            ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

            if(sdp !=null){
                byte[] contents = sdp.getBytes();
                ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application","sdp");
                out_request.setContent(contents, contentTypeHeader);
            }
            out_request.addHeader(contactHeader);


        }catch(Exception e){
            e.printStackTrace();
        }



        return out_request;
    }


    public Request in2outReq(String ip, String port, Request in_request){

        Request out_request = in_request;
        try {
            //out_request.getHeader()
        }catch (Exception e){
            e.printStackTrace();
        }


        return out_request;
    }


}
