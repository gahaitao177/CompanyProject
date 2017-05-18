package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public class CommodityBean extends PageBean{
    private int icommid;
    private String ibankid;
    private String ccommcode;
    private String cname;
    private String ctitle;
    private String coriginprice;
    private String cmincash;
    private String cmaxscore;
    private String cminscore;
    private String ccash1;
    private String ccash2;
    private String cattr;
    private String cdetailimg;
    private String clistimg;
    private String cfetchurl;

    private List<String> cimgs;
    // other
    private int icateid;
    private String ccategory;
    private String cfetchid;
    // 图片
    private int ipicid;
    private String cpicurl;

    public int getIcommid() {
        return icommid;
    }

    public void setIcommid(int icommid) {
        this.icommid = icommid;
    }

    public String getIbankid() {
        return ibankid;
    }

    public void setIbankid(String ibankid) {
        this.ibankid = ibankid;
    }

    public String getCcommcode() {
        return ccommcode;
    }

    public void setCcommcode(String ccommcode) {
        this.ccommcode = ccommcode;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCoriginprice() {
        return coriginprice;
    }

    public void setCoriginprice(String coriginprice) {
        this.coriginprice = coriginprice;
    }

    public String getCmincash() {
        return cmincash;
    }

    public void setCmincash(String cmincash) {
        this.cmincash = cmincash;
    }

    public String getCmaxscore() {
        return cmaxscore;
    }

    public void setCmaxscore(String cmaxscore) {
        this.cmaxscore = cmaxscore;
    }

    public String getCcash1() {
        return ccash1;
    }

    public void setCcash1(String ccash1) {
        this.ccash1 = ccash1;
    }

    public String getCcash2() {
        return ccash2;
    }

    public void setCcash2(String ccash2) {
        this.ccash2 = ccash2;
    }

    public String getCattr() {
        return cattr;
    }

    public void setCattr(String cattr) {
        this.cattr = cattr;
    }

    public String getCdetailimg() {
        return cdetailimg;
    }

    public void setCdetailimg(String cdetailimg) {
        this.cdetailimg = cdetailimg;
    }

    public String getClistimg() {
        return clistimg;
    }

    public void setClistimg(String clistimg) {
        this.clistimg = clistimg;
    }

    public String getCfetchurl() {
        return cfetchurl;
    }

    public void setCfetchurl(String cfetchurl) {
        this.cfetchurl = cfetchurl;
    }

    public List<String> getCimgs() {
        return cimgs;
    }

    public void setCimgs(List<String> cimgs) {
        this.cimgs = cimgs;
    }

    public int getIcateid() {
        return icateid;
    }

    public void setIcateid(int icateid) {
        this.icateid = icateid;
    }

    public String getCcategory() {
        return ccategory;
    }

    public void setCcategory(String ccategory) {
        this.ccategory = ccategory;
    }

    public int getIpicid() {
        return ipicid;
    }

    public void setIpicid(int ipicid) {
        this.ipicid = ipicid;
    }

    public String getCpicurl() {
        return cpicurl;
    }

    public void setCpicurl(String cpicurl) {
        this.cpicurl = cpicurl;
    }

    public String getCfetchid() {
        return cfetchid;
    }

    public void setCfetchid(String cfetchid) {
        this.cfetchid = cfetchid;
    }

    public String getCminscore() {
        return cminscore;
    }

    public void setCminscore(String cminscore) {
        this.cminscore = cminscore;
    }
}
