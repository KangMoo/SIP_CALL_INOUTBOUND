package com.signal.inout;

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

            Request out_request = SendRequest.getInstance().makePassReq(in_session,Request.INVITE,boundModel.getCallId2(),sipProvider);

            // Pass INVITE
            {
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
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }



}
