package com.signal.inout.outbound;

import com.signal.inout.SipLogger;

public class OutSetting extends SipLogger {
    public static OutSetting outSetting;
    String ip;
    String port;
    String user;
    private OutSetting(){}
    public static OutSetting getInstance(){
        if (outSetting==null) outSetting = new OutSetting();
        return outSetting;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setPort(int port) { this.port = Integer.toString(port); }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public int getPortByInt(){
        return Integer.parseInt(port);
    }

    public String getUser() {
        return user;
    }
}
