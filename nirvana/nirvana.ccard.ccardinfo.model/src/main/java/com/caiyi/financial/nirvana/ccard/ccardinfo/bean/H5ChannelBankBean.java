package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.Date;

public class H5ChannelBankBean  extends BaseBean{
    private Long ibankid;

    private Long ichannelid;

    private String cbankname;

    private String curl;

    private Long iclicknum;

    private Short istatus;

    private Short iorder;

    private String cadduser;

    private Date cadddate;

    private String cupdateuser;

    private Date cupdatedate;

    public Long getIchannelid() {
        return ichannelid;
    }

    public void setIchannelid(Long ichannelid) {
        this.ichannelid = ichannelid;
    }

    public Long getIbankid() {
        return ibankid;
    }

    public void setIbankid(Long ibankid) {
        this.ibankid = ibankid;
    }

    public String getCbankname() {
        return cbankname;
    }

    public void setCbankname(String cbankname) {
        this.cbankname = cbankname == null ? null : cbankname.trim();
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl == null ? null : curl.trim();
    }

    public Long getIclicknum() {
        return iclicknum;
    }

    public void setIclicknum(Long iclicknum) {
        this.iclicknum = iclicknum;
    }

    public Short getIstatus() {
        return istatus;
    }

    public void setIstatus(Short istatus) {
        this.istatus = istatus;
    }

    public Short getIorder() {
        return iorder;
    }

    public void setIorder(Short iorder) {
        this.iorder = iorder;
    }

    public String getCadduser() {
        return cadduser;
    }

    public void setCadduser(String cadduser) {
        this.cadduser = cadduser == null ? null : cadduser.trim();
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCupdateuser() {
        return cupdateuser;
    }

    public void setCupdateuser(String cupdateuser) {
        this.cupdateuser = cupdateuser == null ? null : cupdateuser.trim();
    }

    public Date getCupdatedate() {
        return cupdatedate;
    }

    public void setCupdatedate(Date cupdatedate) {
        this.cupdatedate = cupdatedate;
    }
}