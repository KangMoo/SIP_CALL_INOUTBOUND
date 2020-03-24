package com.signal.inout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class main {
    private static Logger logger = LoggerFactory.getLogger(main.class);
    public static void main(String[] args){
        logger.debug("\n== Main Start ==");
        SipCall sipCall = new SipCall("192.168.2.157",5070,"udp");
    }
}
