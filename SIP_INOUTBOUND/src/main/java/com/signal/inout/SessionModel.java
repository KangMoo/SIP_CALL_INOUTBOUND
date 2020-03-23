package com.signal.inout;


import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;
import java.io.Serializable;


public class SessionModel  extends SipLogger  implements Serializable {
    private Request request;
    private String callId;
    private ServerTransaction serverTransaction;
    private ClientTransaction clientTransaction;
    private String toTag;
    private String fromTag;
    private String toip;
    private String fromip;
    private int toPort;
    private int fromPort;
    private String toUser;
    private String fromUser;
    private String sdp;
    private long seq;



    private String type;

    public void SessionModel(){}
    public void SessionModel(String callId, String fromip, Integer fromPort, String fromUser,String fromTag, String toip, Integer toPort, String toUser,String toTag, String type, String sdp, ServerTransaction st, Request request, Long seq){
        setCallId(callId);
        setFromip(fromip);
        setFromPort(fromPort);
        setFromUser(fromUser);
        setFromTag(fromTag);
        setToip(toip);
        setToPort(toPort);
        setToUser(toUser);
        setToTag(toTag);
        setType(type);
        setSdp(sdp);
        setServerTransaction(st);
        setRequest(request);
        setSeq(seq);
    }

    public void setSession(String callId, String fromip, Integer fromPort, String fromUser,String fromTag, String toip, Integer toPort, String toUser,String toTag, String type, String sdp, ServerTransaction st, Request request, Long seq){
        setCallId(callId);
        setFromip(fromip);
        setFromPort(fromPort);
        setFromUser(fromUser);
        setFromTag(fromTag);
        setToip(toip);
        setToPort(toPort);
        setToUser(toUser);
        setToTag(toTag);
        setType(type);
        setSdp(sdp);
        setServerTransaction(st);
        setRequest(request);
        setSeq(seq);
    }

    public SessionModel getSession(){
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToip() {
        return toip;
    }

    public void setToip(String toip) {
        this.toip = toip;
    }

    public String getFromip() {
        return fromip;
    }

    public void setFromip(String fromip) {
        this.fromip = fromip;
    }

    public int getToPort() {
        return toPort;
    }

    public void setToPort(int toPort) {
        this.toPort = toPort;
    }

    public int getFromPort() {
        return fromPort;
    }

    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getSeq() {
        return seq;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }


    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public ServerTransaction getServerTransaction() {
        return serverTransaction;
    }

    public void setServerTransaction(ServerTransaction serverTransaction) {
        this.serverTransaction = serverTransaction;
    }

    public ClientTransaction getClientTransaction() {
        return clientTransaction;
    }

    public void setClientTransaction(ClientTransaction clientTransaction) {
        this.clientTransaction = clientTransaction;
    }

    public String getToTag() {
        return toTag;
    }

    public String getFromTag() {
        return fromTag;
    }

    public void setToTag(String toTag) {
        this.toTag = toTag;
    }

    public void setFromTag(String fromTag) {
        this.fromTag = fromTag;
    }

    @Override
    public String toString() {
        return "SessionModel{" +
                "request=" + request +
                ", callId='" + callId + '\'' +
                ", serverTransaction=" + serverTransaction +
                ", clientTransaction=" + clientTransaction +
                ", toTag='" + toTag + '\'' +
                ", fromTag='" + fromTag + '\'' +
                ", toip='" + toip + '\'' +
                ", fromip='" + fromip + '\'' +
                ", toPort=" + toPort +
                ", fromPort=" + fromPort +
                ", toUser='" + toUser + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", sdp='" + sdp + '\'' +
                ", seq=" + seq +
                ", type='" + type + '\'' +
                '}';
    }
}