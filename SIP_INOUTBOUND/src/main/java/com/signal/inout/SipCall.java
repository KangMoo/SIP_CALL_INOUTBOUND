package com.signal.inout;

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


public class SipCall implements SipListener{

    private SipProvider sipProvider;
    private SipStack sipStack;
    public SipFactory sipFactory;
    public AddressFactory addressFactory;
    public HeaderFactory headerFactory;
    public MessageFactory messageFactory;
    private int port;

    public SipCall(String ip, int port, String protocol){
        this.port = port;
        System.out.println("\n\n====================================================\n\nSipSignal Setting and Start");
        sipFactory = SipFactory.getInstance();
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", "0.0.0.0");
        properties.setProperty("javax.sip.STACK_NAME", "SIG_DEMO");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "debug.log");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "debug.log");

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
            System.out.println("\n\n====================================================\n\nSipStack Create " + sipStack);
            // SipProvider Create
            ListeningPoint listeningPoint = sipStack.createListeningPoint(ip,port,protocol);
            System.out.println("\n\n====================================================\n\nSipListeningPoint Create " + listeningPoint);
            this.sipProvider = sipStack.createSipProvider(listeningPoint);
            System.out.println("\n\n====================================================\n\nSipProvider Create " + sipProvider);
            // SipFactory Create
            this.sipProvider.addSipListener(this);
            sipStack.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SipProvider getSipProvider(){
        return this.sipProvider;
    }
    @Override
    public void processRequest(RequestEvent requestEvent) {
        System.out.println(requestEvent.toString());
        Request request = requestEvent.getRequest();
        System.out.println(request.toString());
        if(request.getMethod().equals(Request.INVITE)) {
            System.out.println("== INVITE ~ ==");
            ProcessRequest.getInstance().processRequestINVITE(requestEvent,
                    messageFactory,
                    addressFactory,
                    headerFactory,
                    sipProvider,
                    port,this);
            System.out.println("== ~ INVITE ==");

        }else if(request.getMethod().equals(Request.ACK)){
            System.out.println("== ACK ~ ==");
            Dialog dialog = requestEvent.getDialog();
            try {
                SipProvider provider = (SipProvider) requestEvent.getSource();
                Request byeRequest = dialog.createRequest(Request.BYE);
                ClientTransaction ct = provider.getNewClientTransaction(byeRequest);
                //dialog.sendRequest(ct);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("== ~ ACK ==");
        }
        else if(request.getMethod().equals(Request.BYE)){
            System.out.println("== BYE ~ ==");
            ServerTransaction sId = requestEvent.getServerTransaction();
            System.out.println("\n\n====================================================\n\nBye Request Inbound ServerTransaction" + sId);

            if (sId == null) {
                System.out.println("\n\n====================================================\n\nServerTransactionId is null");
                try {
                    SipProvider sipProvider;
                    sipProvider = (SipProvider) requestEvent.getSource();
                    sId = sipProvider.getNewServerTransaction(request);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //SendResponse.getInstance().processResponseBye(request,sId,messageFactory);
            System.out.println("== ~ BYE ==");
        }
        else if(request.getMethod().equals(Request.CANCEL)){
            System.out.println("== Cancel ==");
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {


        Response response = responseEvent.getResponse();
        System.out.println("\\n\\n====================================================\n\nFirst Response is : " + response);
        String csq = ((CSeqHeader) response.getHeader("Cseq")).getMethod();
        ProcessResponse sipProcessResponse = new ProcessResponse();

        try {
            if (response.getStatusCode() == Response.OK) {
                if (csq.equals(Request.INVITE)) {
                    sipProcessResponse.processResponseOk(responseEvent);
                }
            }

            else if (response.getStatusCode() == Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST) {
                // Response Bye
                sipProcessResponse.processResponseBye(responseEvent);
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
