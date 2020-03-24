package com.signal.inout;

import com.signal.inout.outbound.OutSetting;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.CSeqHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.util.Properties;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.message.Response;


public class SipCall extends SipLogger implements SipListener{

    public static SipProvider sipProvider;
    public static SipStack sipStack;
    public static SipFactory sipFactory;
    public static AddressFactory addressFactory;
    public static HeaderFactory headerFactory;
    public static MessageFactory messageFactory;


    public SipCall(String ip, int port, String protocol){
        logger.debug(" : SipSignal Setting and Start");
        sipFactory = SipFactory.getInstance();
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", "0.0.0.0");
        properties.setProperty("javax.sip.STACK_NAME", "SIG_DEMO");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "debug.log");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "debug.log");

        OutSetting outSetting = OutSetting.getInstance();
//        outSetting.setIp("127.0.0.1");
//        outSetting.setPort(5060);
        outSetting.setIp("192.168.7.33");
        outSetting.setPort(5061);

        try {
            sipStack = sipFactory.createSipStack(properties);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            System.exit(0);
        }

        try {
            // SipStack Create
            this.headerFactory = sipFactory.createHeaderFactory();
            this.addressFactory = sipFactory.createAddressFactory();
            this.messageFactory = sipFactory.createMessageFactory();
            sipStack = sipFactory.createSipStack(properties);
            logger.debug(" : SipStack Create " + sipStack);
            // SipProvider Create
            ListeningPoint listeningPoint = sipStack.createListeningPoint(ip,port,protocol);
            logger.debug(" : SipListeningPoint Create " + listeningPoint);
            this.sipProvider = sipStack.createSipProvider(listeningPoint);
            logger.debug(" : SipProvider Create " + sipProvider);
            // SipFactory Create
            this.sipProvider.addSipListener(this);
            sipStack.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        ProcessRequest processRequest = ProcessRequest.getInstance();
        if(request.getMethod().equals(Request.INVITE)) {
            logger.debug("\n== INVITE ~ ==");
            processRequest.processInvite(requestEvent);
            logger.debug("\n== ~ INVITE ==");

        }else if(request.getMethod().equals(Request.ACK)){
            logger.debug("\n== ACK ~ ==");
            processRequest.processACK(requestEvent);
            logger.debug("\n== ~ ACK ==");
        }
        else if(request.getMethod().equals(Request.BYE)){
            logger.debug("\n== BYE ~ ==");
            processRequest.processBye(requestEvent);
            logger.debug("\n== ~ BYE ==");
        }
        else if(request.getMethod().equals(Request.CANCEL)){
            processRequest.processCancel(requestEvent);
            logger.debug("\n== Cancel ==");
        }
        else if(request.getMethod().equals(Request.ACK)){
            logger.debug("\n== ACK ~ ==");
            logger.debug("request = " + request);
            logger.debug("\n== ~ ACK ==");
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

        Response response = responseEvent.getResponse();
        logger.debug("\n\n====================================================\n\nFirst Response is : " + response);
        String csq = ((CSeqHeader) response.getHeader("Cseq")).getMethod();
        ProcessResponse processResponse = ProcessResponse.getInstance();
        try {
            if (response.getStatusCode() == Response.OK) {
                if (csq.equals(Request.INVITE)) {
                    processResponse.processResponseOk(responseEvent);
                }
            }
            else if (response.getStatusCode() == Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST) {
                // Response Bye
                processResponse.processResponseBye(responseEvent);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}
