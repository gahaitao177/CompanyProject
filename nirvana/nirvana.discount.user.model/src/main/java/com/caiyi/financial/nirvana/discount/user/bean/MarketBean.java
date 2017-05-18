package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/7/26.
 */
public class MarketBean  {
    private  Integer imarketid;
    private   Date   cadddate;
    private  String  cname;
    private  String  clogo;
    private  String  clogolist;
    private  String  ctitle;
    private  String   ctitle_sub;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }

    public String getClogolist() {
        return clogolist;
    }

    public void setClogolist(String clogolist) {
        this.clogolist = clogolist;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCtitle_sub() {
        return ctitle_sub;
    }

    public void setCtitle_sub(String ctitle_sub) {
        this.ctitle_sub = ctitle_sub;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public Integer getImarketid() {
        return imarketid;
    }

    public void setImarketid(Integer imarketid) {
        this.imarketid = imarketid;
    }
}
