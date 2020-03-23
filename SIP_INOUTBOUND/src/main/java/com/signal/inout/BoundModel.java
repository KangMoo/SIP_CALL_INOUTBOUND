package com.signal.inout;

import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;
import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.util.ArrayList;

public class BoundModel  extends SipLogger {
    private String toip;
    private String fromip;



    private String toTag;
    private String fromTag;
    private int toPort;
    private int fromPort;
    private String toUser;
    private String fromUser;
    private String callId1;
    private String callId2;
    private String sdp;

    @Override
    public String toString() {
        return "BoundModel{" +
                "toip='" + toip + '\'' +
                ", fromip='" + fromip + '\'' +
                ", toTag='" + toTag + '\'' +
                ", fromTag='" + fromTag + '\'' +
                ", toPort=" + toPort +
                ", fromPort=" + fromPort +
                ", toUser='" + toUser + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", callId1='" + callId1 + '\'' +
                ", callId2='" + callId2 + '\'' +
                ", sdp='" + sdp + '\'' +
                '}';
    }

    public static BoundModel makeModel(Request request){
        BoundModel boundModel = new BoundModel();
        boundModel.setModel(request);
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
    public Request makeRequest(SipProvider sipProvider){
        Request request = null;
        try{
            SipFactory sipFactory = SipFactory.getInstance();
            MessageFactory messageFactory  = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            AddressFactory addressFactory = sipFactory.createAddressFactory();

            //make Via
            ArrayList viaHeaders = new ArrayList();
            String address = sipProvider.getListeningPoint().getIPAddress();
            ViaHeader viaHeader = headerFactory.createViaHeader(address, sipProvider.getListeningPoint("udp").getPort(),"udp",null);
            viaHeaders.add(viaHeader);



        } catch(Exception e){
            e.printStackTrace();
        }



        return request;
    }
    public String getFromUser() {
        return fromUser;
    }
    public void setModel(Request request){

        try{
            this.toip = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getHost();
            this.fromip = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getHost();
            this.toPort =  Integer.parseInt(((SipURI)request.getRequestURI()).toString().split(":")[2]);
            this.fromPort =  ((ViaHeader) request.getHeader("via")).getPort();
            this.callId1 =  ((CallIdHeader) request.getHeader("Call-Id")).getCallId();
            this.toUser = ((SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI()).getUser();
            this.fromUser = ((SipURI) ((FromHeader) request.getHeader("From")).getAddress().getURI()).getUser();
            this.callId2 = SipCall.sipProvider.getNewCallId().getCallId();
            this.toTag = ((SIPMessage) request).getToTag();
            this.fromTag = ((SIPMessage) request).getFromTag();
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
    public void setToTag(String toTag) {
        this.toTag = toTag;
    }

    public void setFromTag(String fromTag) {
        this.fromTag = fromTag;
    }

    public String getToTag() {
        return toTag;
    }

    public String getFromTag() {
        return fromTag;
    }
}
