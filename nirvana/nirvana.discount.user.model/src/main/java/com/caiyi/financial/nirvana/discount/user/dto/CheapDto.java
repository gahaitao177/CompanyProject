package com.caiyi.financial.nirvana.discount.user.dto;

/**
 * Created by wenshiliang on 2016/9/5.
 * 收藏 门店优惠信息
 * 收藏接口相关(针对收藏接口进行优化)
 */
public class CheapDto {
    private String icheapid;//优惠id
    private String cbankid;//银行id
    private String ctitle;//标题
    private String cptype;//类型
    private String ishortname;//银行名称
    private String ibankids;//门店 优惠银行id
    private Long istoreid;//门店id
    private String iexpire;//优惠过期？？？？
    private String cstorename;//门店名称
    private String clogo;//商户logo

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }

    public String getCstorename() {
        return cstorename;
    }

    public void setCstorename(String cstorename) {
        this.cstorename = cstorename;
    }

    public String getIcheapid() {
        return icheapid;
    }

    public void setIcheapid(String icheapid) {
        this.icheapid = icheapid;
    }

    public String getCbankid() {
        return cbankid;
    }

    public void setCbankid(String cbankid) {
        this.cbankid = cbankid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCptype() {
        return cptype;
    }

    public void setCptype(String cptype) {
        this.cptype = cptype;
    }

    public String getIshortname() {
        return ishortname;
    }

    public void setIshortname(String ishortname) {
        this.ishortname = ishortname;
    }

    public String getIbankids() {
        return ibankids;
    }

    public void setIbankids(String ibankids) {
        this.ibankids = ibankids;
    }

    public Long getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(Long istoreid) {
        this.istoreid = istoreid;
    }

    public String getIexpire() {
        return iexpire;
    }

    public void setIexpire(String iexpire) {
        this.iexpire = iexpire;
    }
}
