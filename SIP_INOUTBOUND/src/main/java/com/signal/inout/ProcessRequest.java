package com.signal.inout;

import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.*;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class ProcessRequest extends SipLogger  {


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

    }
    public void processCancel(RequestEvent requestEvent){
        Request request = requestEvent.getRequest();
        ServerTransaction st = requestEvent.getServerTransaction();
        try{
            if(st == null){
                logger.debug("ServerTransaction is null");
                return;
            }
            // Reply 200 OK(Cancel)
            Response response = SipCall.messageFactory.createResponse(200, request);
            st.sendResponse(response);

            // Reply 487 Request Terminated
            if(requestEvent.getDialog().getState() != DialogState.CONFIRMED){
                response = SipCall.messageFactory.createResponse(Response.REQUEST_TERMINATED, request);
                st.sendResponse(response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void processBye(RequestEvent requestEvent){
        // Send ACK ~
        Request request = requestEvent.getRequest();
        ServerTransaction st = requestEvent.getServerTransaction();

        try{
            if(st == null){
                logger.debug("ServerTransaction is null");
                return;
            }
            //Dialog dialog = st.getDialog();
            Response response = SipCall.messageFactory.createResponse(200, request);
            st.sendResponse(response);
        } catch(Exception e){
            e.printStackTrace();
        }
        // ~ Send ACK
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
                logger.debug("Create Transaction ID : " + st);
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
                    boundModel.getFromTag(),
                    boundModel.getToip(),
                    boundModel.getToPort(),
                    boundModel.getToUser(),
                    boundModel.getToTag(),
                    "inbound",
                    boundModel.getSdp(),
                    st,null,
                    request,cseq);

            SessionMap.getInstance().putSession(boundModel.getCallId1(),in_session);
            logger.debug("boundModel = " + boundModel);
            logger.debug("\n== Inbound Session ~ ==\n"+in_session.toString()+"\n== ~ Inbound Session ==\n");
            // ~ inbound Setting




        }catch (Exception e){
            e.printStackTrace();
        }
        return st;
    }

    public void processACK(RequestEvent requestEvent) {
        Dialog dialog = requestEvent.getDialog();
        try {
            SipProvider provider = (SipProvider) requestEvent.getSource();
            Request byeRequest = dialog.createRequest(Request.BYE);
            ClientTransaction ct = provider.getNewClientTransaction(byeRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
