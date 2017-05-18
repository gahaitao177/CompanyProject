package com.caiyi.financial.nirvana.discount.tools.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by heshaohua on 2016/8/24.
 */
public class MarketDto {
    private String imarketid;//连锁店id
    private String cname;//连锁名
    private String clogo;//首页logo
    private String clogolist;//列表logo
    private String clogogray;//灰色logo
    private String num;

    private String icheapid;
    private String ctitle;
    private Timestamp cenddate;
    private String cdiscount;
    private String cimgurl;
    private String collectflag;

    private Timestamp cstartdate;
    private Timestamp cendtdate;

    List<MarketDto> child;
    List<MarketDto> img;

    private String itype;
    private String istate;
    private Timestamp cadddate;
    private Timestamp ceditdate;
    private String cedituser;
    private String cslogan;
    private String iorder;

    private String imgid;

    public List<MarketDto> getChild() {
        return child;
    }
    public void setChild(List<MarketDto> child) {
        this.child = child;
    }

    public List<MarketDto> getImg() {
        return img;
    }

    public void setImg(List<MarketDto> img) {
        this.img = img;
    }

    public String getItype() {
        return itype;
    }

    public void setItype(String itype) {
        this.itype = itype;
    }

    public String getIstate() {
        return istate;
    }

    public void setIstate(String istate) {
        this.istate = istate;
    }

    public Timestamp getCadddate() {
        return cadddate;
    }

    public void setCadddate(Timestamp cadddate) {
        this.cadddate = cadddate;
    }

    public Timestamp getCeditdate() {
        return ceditdate;
    }

    public void setCeditdate(Timestamp ceditdate) {
        this.ceditdate = ceditdate;
    }

    public String getCedituser() {
        return cedituser;
    }

    public void setCedituser(String cedituser) {
        this.cedituser = cedituser;
    }

    public String getCslogan() {
        return cslogan;
    }

    public void setCslogan(String cslogan) {
        this.cslogan = cslogan;
    }

    public String getIorder() {
        return iorder;
    }

    public void setIorder(String iorder) {
        this.iorder = iorder;
    }

    public String getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

    public Timestamp getCstartdate() {
        return cstartdate;
    }

    public void setCstartdate(Timestamp cstartdate) {
        this.cstartdate = cstartdate;
    }

    public Timestamp getCendtdate() {
        return cendtdate;
    }

    public void setCendtdate(Timestamp cendtdate) {
        this.cendtdate = cendtdate;
    }

    public String getIcheapid() {
        return icheapid;
    }

    public void setIcheapid(String icheapid) {
        this.icheapid = icheapid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public Timestamp getCenddate() {
        return cenddate;
    }

    public void setCenddate(Timestamp cenddate) {
        this.cenddate = cenddate;
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

    public String getCollectflag() {
        return collectflag;
    }

    public void setCollectflag(String collectflag) {
        this.collectflag = collectflag;
    }

    public String getImarketid() {
        return imarketid;
    }

    public void setImarketid(String imarketid) {
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

    public String getClogolist() {
        return clogolist;
    }

    public void setClogolist(String clogolist) {
        this.clogolist = clogolist;
    }

    public String getClogogray() {
        return clogogray;
    }

    public void setClogogray(String clogogray) {
        this.clogogray = clogogray;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
