package com.signal.inout;

import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;

import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

public class BoundModel {
    private String toip;
    private String fromip;
    private int toPort;
    private int fromPort;
    private String toUser;
    private String fromUser;
    private String callId1;
    private String callId2;
    private String sdp;

    public static BoundModel makeModel(Request request, SipProvider sipProvider){
        BoundModel boundModel = new BoundModel();
        boundModel.setModel(request,sipProvider);
        return boundModel;
    }

    public String getSdp() {
        return sdp;
    }

    public String getCallId1() {
        return callId1;
    }

    public String getCallId2() {
        return callId2;
    }

    public String getToip() {
        return toip;
    }

    public String getFromip() {
        return fromip;
    }

    public int getToPort() {
        return toPort;
    }

    public int getFromPort() {
        return fromPort;
    }

    public String getToUser() {
        return toUser;
    }

    public String getFromUser() {
        return fromUser;
    }
    public void setModel(Request request, SipProvider sipProvider){
        try{
        this.toip = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getHost();
        this.fromip = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getHost();
        this.toPort =  Integer.parseInt(((SipURI)request.getRequestURI()).toString().split(":")[2]);
        this.fromPort =  ((ViaHeader) request.getHeader("via")).getPort();
        this.callId1 =  ((CallIdHeader) request.getHeader("Call-Id")).getCallId();
        this.toUser = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getUser();
        this.fromUser = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getUser();

        this.callId2 = sipProvider.getNewCallId().getCallId();

        String sdp = request.toString().substring(request.toString().length() - request.getContentLength().getContentLength(), request.toString().length());
        SDPAnnounceParser parser = new SDPAnnounceParser(sdp);
        SessionDescriptionImpl parsedDescription = parser.parse();
        SessionDescriptionImpl sdpParser = new SessionDescriptionImpl(parsedDescription);
        this.sdp = sdpParser.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setToIp(Request request) {
        this.toip = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getHost();
    }

    public void setFromIp(Request request) {
        this.fromip = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getHost();
    }

    public void setToPort(Request request) {
        this.toPort = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getPort();
    }

    public void setFromPort(Request request) {
        this.fromPort =  ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getPort();
    }

    public void setCallId1(Request request) {
        this.callId1 =  ((CallIdHeader) request.getHeader("Call-Id")).getCallId();
    }

    public void setToUser(Request request) {
        this.toUser = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getUser();
    }

    public void setFromUser(Request request) {
        this.fromUser = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getUser();
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public void setCallId2(String callId2) {
        this.callId2 = callId2;
    }

}
