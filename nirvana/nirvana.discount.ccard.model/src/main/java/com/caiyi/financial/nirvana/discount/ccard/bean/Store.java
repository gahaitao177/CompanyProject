package com.caiyi.financial.nirvana.discount.ccard.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by wenshiliang on 2016/5/5.
 */
public class Store extends BaseBean {
    private Long istoreid;
    private String cstorename;
    private String caddress;
    private String ctel;
    private String clng;
    private String clat;
    private String rn;

    private Long ibusinessid;//坑，数据库字段为ibussinessid
    private Long icityid;

    public Long getIbusinessid() {
        return ibusinessid;
    }

    public void setIbusinessid(Long ibusinessid) {
        this.ibusinessid = ibusinessid;
    }

    public Long getIcityid() {
        return icityid;
    }

    public void setIcityid(Long icityid) {
        this.icityid = icityid;
    }

    public Long getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(Long istoreid) {
        this.istoreid = istoreid;
    }

    public String getCstorename() {
        return cstorename;
    }

    public void setCstorename(String cstorename) {
        this.cstorename = cstorename;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getCtel() {
        return ctel;
    }

    public void setCtel(String ctel) {
        this.ctel = ctel;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getRn() {
        return rn;
    }

    public void setRn(String rn) {
        this.rn = rn;
    }
}
