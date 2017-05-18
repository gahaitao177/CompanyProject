package com.caiyi.financial.nirvana.discount.user.dto;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by wenshiliang on 2016/9/6.
 * 收藏 超市优惠查询dto
 */
public class MarketCheapDto {
    private String icheapid;
    private Long imarketid;
    private String cname;
    private String clogo;
    private String ctitle;
    private String cdiscount;
    private String cimgurl;
    private Date cenddate;
    private int iexpire;
    private Timestamp cadddate;

    public int getIexpire() {
        return iexpire;
    }

    public void setIexpire(int iexpire) {
        this.iexpire = iexpire;
    }

    public Timestamp getCadddate() {
        return cadddate;
    }

    public void setCadddate(Timestamp cadddate) {
        this.cadddate = cadddate;
    }

    public String getIcheapid() {
        return icheapid;
    }

    public void setIcheapid(String icheapid) {
        this.icheapid = icheapid;
    }

    public Long getImarketid() {
        return imarketid;
    }

    public void setImarketid(Long imarketid) {
        this.imarketid = imarketid;
    }

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

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCdiscount() {
        return cdiscount;
    }

    public void setCdiscount(String cdiscount) {
        this.cdiscount = cdiscount;
    }

    public String getCimgurl() {
        return cimgurl;
    }

    public void setCimgurl(String cimgurl) {
        this.cimgurl = cimgurl;
    }

    public Date getCenddate() {
        return cenddate;
    }

    public void setCenddate(Date cenddate) {
        this.cenddate = cenddate;
    }
}
