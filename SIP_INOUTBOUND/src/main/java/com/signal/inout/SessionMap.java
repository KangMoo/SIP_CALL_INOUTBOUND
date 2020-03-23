package com.signal.inout;
import javax.sip.ClientTransaction;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.HashMap;

public class SessionMap  extends SipLogger {
    private HashMap<String, SessionModel> SessionHmap = new HashMap<>();
    private static SessionMap sessionMap =null;
    private SessionMap(){}
    public static SessionMap getInstance(){
        if (sessionMap==null) sessionMap = new SessionMap();
        return sessionMap;
    }

    public SessionModel makeSession(String callId, String fromip, Integer fromPort, String fromUser,String fromTag, String toip, Integer toPort, String toUser,String toTag, String type, String sdp, ServerTransaction st,ClientTransaction ct, Request request, Long seq){
        SessionModel session = new SessionModel();
        session.setCallId(callId);
        session.setFromip(fromip);
        session.setFromPort(fromPort);
        session.setFromUser(fromUser);
        session.setFromTag(fromTag);
        session.setToip(toip);
        session.setToPort(toPort);
        session.setToUser(toUser);
        session.setToTag(toTag);
        session.setType(type);
        session.setSdp(sdp);
        session.setServerTransaction(st);
        session.setClientTransaction(ct);
        session.setRequest(request);
        session.setSeq(seq);

        return session;
    }

    public SessionModel makeOutgoingSession(SessionModel session, SipProvider sipProvider)
    {
        ClientTransaction clientTransaction;
        try{
            if (session.getClientTransaction() == null) {
                clientTransaction = sipProvider.getNewClientTransaction(session.getRequest());
                session.setClientTransaction(clientTransaction);
            }
            else {
                clientTransaction = session.getClientTransaction();
                clientTransaction.createCancel();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return session;
    }

    public void in2outSessionModel(SessionModel session, SipProvider sipProvider)
    {

        Request request = session.getRequest();



        ClientTransaction clientTransaction = null;
        try{
            if (session.getClientTransaction() == null) {
                clientTransaction = sipProvider.getNewClientTransaction(session.getRequest());
                session.setClientTransaction(clientTransaction);
            }
            else {
                clientTransaction = session.getClientTransaction();
                clientTransaction.createCancel();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        session.setClientTransaction(clientTransaction);




    }

    public void putSession(String key, SessionModel session){
        //logger.debug("putSession~\n" +session.toString()+"\n~putSession");
        SessionHmap.put(key,session);
    }
    public void deleteSessionModel(String key){
        SessionHmap.remove(key);
    }
    public SessionModel findSession(String callId){
        return SessionHmap.get(callId);
    }
    public Request findRequest(String callId){
        return SessionHmap.get(callId).getRequest();
    }

    public void processResponseTrying(Request request, ServerTransaction transactionId, MessageFactory messageFactory){
        logger.debug("\n== processResponseTrying ~ ==");
        try {
            logger.debug("== Response Trying : \n" +request);
            Response response = messageFactory.createResponse( Response.TRYING, request);
            logger.debug("== Response Data : \n" + response);
            transactionId.sendResponse(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("== ~ processResponseTrying ==\n");
    }
    public void processResponseRing (Request request, ServerTransaction transactionId, MessageFactory messageFactory) {
        try {
            logger.debug("\n== processResponseRing ~ ==");
            logger.debug("== Response Ringing : \n" + request);
            Response response = messageFactory.createResponse( Response.RINGING, request);
            logger.debug("== SipResponse Ringing : \n" + response);
            transactionId.sendResponse(response);
            logger.debug("== ~ processResponseRing ==\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientTransaction findClientTransaction (String callId) {
        HashMap<String, SessionModel> map = SessionHmap;
        SessionModel sessionModel = map.get(callId);
        return sessionModel.getClientTransaction();
    }

    public ServerTransaction findTransactionId (String callId) {
        ServerTransaction st = SessionHmap.get(callId).getServerTransaction();
        return st;
    }

    public void setClientTransactionCallId(String callId1, ClientTransaction clientTransaction) {
        HashMap<String, SessionModel> map = SessionHmap;
        SessionModel sessionModel = map.get(callId1);
        sessionModel.setClientTransaction(clientTransaction);
    }


}
