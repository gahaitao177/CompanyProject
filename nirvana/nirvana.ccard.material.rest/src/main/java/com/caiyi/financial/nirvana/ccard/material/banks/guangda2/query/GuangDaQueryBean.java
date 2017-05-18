package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.query;


import org.apache.http.client.CookieStore;

import java.io.Serializable;

/**
 * Created by wsl on 2016/3/2.
 */
public class GuangDaQueryBean implements Serializable {
    private CookieStore cookieStore;

    private String chphone;
    private String name;
    private String id_no;
    private String verify_code;
    private String dynPasswd;

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public String getChphone() {
        return chphone;
    }

    public void setChphone(String chphone) {
        this.chphone = chphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDynPasswd() {
        return dynPasswd;
    }

    public void setDynPasswd(String dynPasswd) {
        this.dynPasswd = dynPasswd;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

}
