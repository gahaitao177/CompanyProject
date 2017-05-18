package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.Date;

/**
 * h5渠道推广 card bean
 */
public class H5ChannelCardBean extends BaseBean {
    private Long icardid; //卡id
    private Long ichannelid; // 渠道id  卡id和渠道id 构成主键

    private String picurl;//图片地址
    private String cname;  //名字


    private String curl; //办卡地址

    private Long ibankid; //银行id

    private Short iorder; //排序

    private Long iclicknum; //点击人数

    private Short istatus;// 状态 1 正常

    private String cadduser;

    private Date cadddate;

    private String cupdateuser;

    private Date cupdatedate;

    private Short ishot;//是否热门

    private String iuseids;//主题

    private String cprivilege;//权益


    private String iuseid;//查询参数，主题id

    public String getIuseid() {
        return iuseid;
    }

    public void setIuseid(String iuseid) {
        this.iuseid = iuseid;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public Long getIcardid() {
        return icardid;
    }

    public void setIcardid(Long icardid) {
        this.icardid = icardid;
    }

    public Long getIchannelid() {
        return ichannelid;
    }

    public void setIchannelid(Long ichannelid) {
        this.ichannelid = ichannelid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname == null ? null : cname.trim();
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl == null ? null : curl.trim();
    }

    public Long getIbankid() {
        return ibankid;
    }

    public void setIbankid(Long ibankid) {
        this.ibankid = ibankid;
    }

    public Short getIorder() {
        return iorder;
    }

    public void setIorder(Short iorder) {
        this.iorder = iorder;
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

    public Short getIshot() {
        return ishot;
    }

    public void setIshot(Short ishot) {
        this.ishot = ishot;
    }

    public String getIuseids() {
        return iuseids;
    }

    public void setIuseids(String iuseids) {
        this.iuseids = iuseids == null ? null : iuseids.trim();
    }

    public String getCprivilege() {
        return cprivilege;
    }

    public void setCprivilege(String cprivilege) {
        this.cprivilege = cprivilege == null ? null : cprivilege.trim();
    }
}