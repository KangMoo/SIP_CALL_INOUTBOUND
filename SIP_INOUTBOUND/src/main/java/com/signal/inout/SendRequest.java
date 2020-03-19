package com.signal.inout;

import gov.nist.javax.sip.Utils;
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
            CSeqHeader cseqHeader = headerFactory.createCSeqHeader(19L,method);
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
        return out_request;
    }

}
