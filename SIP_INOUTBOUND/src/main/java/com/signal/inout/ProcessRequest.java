package com.signal.inout;

import ch.qos.logback.core.net.server.Client;
import com.signal.inout.outbound.OutSetting;
import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

public class ProcessRequest {


    //private static HashMap<String, SessionModel> SessionHmap = new HashMap<>();
    private static ProcessRequest processRequest = null;
    private ProcessRequest(){}
    public static ProcessRequest getInstance(){
        if(processRequest == null) processRequest = new ProcessRequest();
        return processRequest;
    }
    public void processInvite(RequestEvent requestEvent){

        // Save Info
        ServerTransaction st = saveCallInfo(requestEvent);

        // reply 100, 180
        reply100_180(requestEvent, st);

        // Pass INVITE to Server
        SendRequest.getInstance().passRequest(requestEvent, st);
        System.out.println("st1 = " + st);

    }
    public void processCancel(RequestEvent requestEvent){
        // Send 200 OK(Cancel)

        // Send 487 Request Terminated

    }
    public ServerTransaction reply100_180(RequestEvent requestEvent, ServerTransaction transaction){
        Request request = requestEvent.getRequest();

        try {
            // 100 Trying
            SendResponse.getInstance().sendResponseTrying(request,transaction);
            // 180 Ringing
            SendResponse.getInstance().sendResponseRing(request,transaction);
        }catch (Exception e){
            e.printStackTrace();
        }
        return transaction;
    }

    public ServerTransaction saveCallInfo(RequestEvent requestEvent){
        Request request = requestEvent.getRequest();

        ServerTransaction st = requestEvent.getServerTransaction();
        try{
            if(st == null)
            {
                st = ((SipProvider)requestEvent.getSource()).getNewServerTransaction(request);
                System.out.println("Create Transaction ID : " + st);
            }
            // inbound Setting ~
            BoundModel boundModel = BoundModel.makeModel(request);
            String address = ((SIPMessage)request).getFrom().getAddress().toString();
            InboundModels.getInstance().put(address,boundModel);
            Long cseq = ((SIPMessage)request).getCSeq().getSeqNumber();

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
                    st,null,
                    request,cseq);
            SessionMap.getInstance().putSession(boundModel.getCallId1(),in_session);
            System.out.println("\n== Inbound Session ~ ==\n"+in_session.toString()+"\n== ~ Inbound Session ==\n");
            // ~ inbound Setting




        }catch (Exception e){
            e.printStackTrace();
        }
        return st;
    }
}
