package com.caiyi.nirvana.analyse.monitor.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Authentic extends Authenticator {


    public Authentic(String name, String password) {
        this.setUsername(name);
        this.setPwd(password);
    }


    private String username = "";
    private String pwd = "";

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(getUsername(), getPwd());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
