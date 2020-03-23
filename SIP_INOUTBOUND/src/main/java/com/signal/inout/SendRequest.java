package com.signal.inout;

import com.signal.inout.outbound.OutSetting;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.Via;
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


    public Request makePassReq(SessionModel sessionModel,
                               String method,
                               String out_CallId){

        Request out_request = null;
        try{
            String sdp=sessionModel.getSdp();
            SipFactory sipFactory = SipFactory.getInstance();
            MessageFactory messageFactory  = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            AddressFactory addressFactory = sipFactory.createAddressFactory();


            ArrayList viaHeaders = new ArrayList();
            SipProvider sipProvider = SipCall.sipProvider;
            String address = sipProvider.getListeningPoint().getIPAddress();
            ViaHeader viaHeader = headerFactory.createViaHeader(address, sipProvider.getListeningPoint("udp").getPort(),"udp",null);

            //ViaHeader viaHeader = headerFactory.createViaHeader(sessionModel.getToUser(), sessionModel.getToPort(),"udp",null);
            viaHeaders.add(viaHeader);

            String fromTags = (new Utils()).generateTag();
            String toTags = null;
            //String toTags = (new Utils()).generateTag();
            SipURI toSipUrl = addressFactory.createSipURI(sessionModel.getToUser(),sessionModel.getToip());// OutSetting.getInstance().getIp());
            SipURI fromSipUrl=addressFactory.createSipURI(sessionModel.getFromUser(),sessionModel.getFromip());
            CallIdHeader callIdheader = headerFactory.createCallIdHeader(out_CallId);
            SipURI sipURI=((SipURI)addressFactory.createSipURI(sessionModel.getToUser(),OutSetting.getInstance().getIp()+":"+OutSetting.getInstance().getPort()));
            CSeqHeader cseqHeader = headerFactory.createCSeqHeader(sessionModel.getSeq(),method);
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromSipUrl), fromTags);
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toSipUrl), toTags);
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
        System.out.println("out_request = " + out_request);
        return out_request;
    }

    public void passRequest(RequestEvent requestEvent,ServerTransaction st) {
        Request request = requestEvent.getRequest();

        String address = ((SIPMessage)request).getFrom().getAddress().toString();
        BoundModel boundModel = InboundModels.getInstance().get(address);

        SipProvider sipProvider = SipCall.sipProvider;
        SessionModel in_session = SessionMap.getInstance().findSession(boundModel.getCallId1());
        
        Request temp = (Request)request.clone();
        SIPMessage temp2 = (SIPMessage)temp;

        Request out_request = makePassReq(in_session, Request.INVITE, boundModel.getCallId2());
        System.out.println("out_request = " + out_request);

        // outbound Setting ~
        SessionModel out_session = SessionMap.getInstance().makeSession(
                boundModel.getCallId1(),
                boundModel.getToip(),
                boundModel.getToPort(),
                boundModel.getFromUser(),
                OutSetting.getInstance().getIp(),
                OutSetting.getInstance().getPortByInt(),
                boundModel.getToUser(),
                "outbound",boundModel.getSdp(),st, null, out_request,((SIPMessage)request).getCSeq().getSeqNumber());
        SessionMap.getInstance().putSession(boundModel.getCallId2(), out_session);
        System.out.println("\n== Outbound Session ~ ==\n"+out_session.toString()+"\n== ~ Outbound Session ==\n");
        // ~ outbound Setting

        try{
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

    public void sendACK(ResponseEvent responseEvent) {
        try{
            Response response = responseEvent.getResponse();

            Dialog dialog = responseEvent.getDialog();
            Long cseq = ((SIPMessage)response).getCSeq().getSeqNumber();
            Request ackRequest = dialog.createAck(cseq);
            dialog.sendAck(ackRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
