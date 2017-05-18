package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by A-0106 on 2016/5/20.
 */
public class TopicBussiDto {
    String ibusinessid;
    String istoreid;
    String cname;
    String clogo;
    String distance;
    String ccheaptype;
    String bankids;
    String cheaptitle;
    String salelevel;
    String tag="row";
    public String getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(String istoreid) {
        this.istoreid = istoreid;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCcheaptype() {
        return ccheaptype;
    }

    public void setCcheaptype(String ccheaptype) {
        this.ccheaptype = ccheaptype;
    }

    public String getBankids() {
        return bankids;
    }

    public void setBankids(String bankids) {
        this.bankids = bankids;
    }

    public String getCheaptitle() {
        return cheaptitle;
    }

    public void setCheaptitle(String cheaptitle) {
        this.cheaptitle = cheaptitle;
    }

    public String getSalelevel() {
        return salelevel;
    }

    public void setSalelevel(String salelevel) {
        this.salelevel = salelevel;
    }

    public String getIbusinessid() {
        return ibusinessid;
    }

    public void setIbusinessid(String ibusinessid) {
        this.ibusinessid = ibusinessid;
    }

    public String getTag() {return tag; }

    public void setTag(String tag) {this.tag = tag;}

    @Override
    public  String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("<"+tag+"");
        if(getIbusinessid()!=null)
        sb.append(" ibusinessid="+getIbusinessid());
        if(getIstoreid()!=null)
        sb.append(" istoreid="+getIstoreid());
        if(getCname()!=null)
        sb.append(" cname="+getCname());
        if(getClogo()!=null)
        sb.append(" clogo="+getClogo());
        if(getDistance()!=null)
        sb.append(" distance="+getDistance());
        if(getCcheaptype()!=null)
        sb.append(" ccheaptype="+getCcheaptype());
        if(getBankids()!=null)
        sb.append(" bankids="+getBankids());
        if(getCheaptitle()!=null)
        sb.append(" cheaptitle="+getCheaptitle());
        if(getSalelevel()!=null)
        sb.append(" salelevel="+getSalelevel());
        sb.append("</"+tag+">");
        return sb.toString();
    }
}
