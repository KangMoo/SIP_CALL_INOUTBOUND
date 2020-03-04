package com.signal.inout;
import javax.sip.ClientTransaction;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.HashMap;

public class SessionMap {
    private HashMap<String, SessionModel> SessionHmap = new HashMap<>();
    private static SessionMap sessionMap =null;
    private SessionMap(){}
    public static SessionMap getInstance(){
        if (sessionMap==null) sessionMap = new SessionMap();
        return sessionMap;
    }

    public SessionModel makeSession(String callId, String fromip, Integer fromPort, String fromUser, String toip, Integer toPort, String toUser, String type, String sdp, ServerTransaction st, Request request, Long seq){
        SessionModel session = new SessionModel();
        session.setCallId(callId);
        session.setFromip(fromip);
        session.setFromPort(fromPort);
        session.setFromUser(fromUser);
        session.setToip(toip);
        session.setToPort(toPort);
        session.setToUser(toUser);
        session.setType(type);
        session.setSdp(sdp);
        session.setServerTransaction(st);
        session.setRequest(request);
        session.setSeq(111L);

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


        System.out.println("revSessionModel~");
        System.out.println(request);
        System.out.println("~revSessionModel");

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
        //System.out.println("putSession~\n" +session.toString()+"\n~putSession");
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
