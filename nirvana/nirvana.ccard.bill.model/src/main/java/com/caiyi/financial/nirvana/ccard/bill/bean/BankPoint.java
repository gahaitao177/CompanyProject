package com.caiyi.financial.nirvana.ccard.bill.bean;

import java.util.Date;

/**
 * Created by Linxingyu on 2017/1/10.
 * TB_BANK_POINT
 */
public class BankPoint {

    private Integer ibankid;//银行ID
    private Integer icityid;//城市ID(tb_area中id)
    private Integer idistrictid;//区ID
    private String inetpointid;//银行网点编号
    private String cnetpointname;//银行网点名称
    private String caddr;//网点地址
    private String cphone;//电话
    private String clng;//经度（高德坐标）
    private String clat;//纬度
    private Integer itype = 1;//类型(1营业厅，2ATM)
    private String cnetpointtime;//营业时间
    private Date caddtime;//添加时间
    private Integer istatus = 1;//状态:1正常；2删除

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public Integer getIcityid() {
        return icityid;
    }

    public void setIcityid(Integer icityid) {
        this.icityid = icityid;
    }

    public Integer getIdistrictid() {
        return idistrictid;
    }

    public void setIdistrictid(Integer idistrictid) {
        this.idistrictid = idistrictid;
    }

    public String getInetpointid() {
        return inetpointid;
    }

    public void setInetpointid(String inetpointid) {
        this.inetpointid = inetpointid;
    }

    public String getCnetpointname() {
        return cnetpointname;
    }

    public void setCnetpointname(String cnetpointname) {
        this.cnetpointname = cnetpointname;
    }

    public String getCaddr() {
        return caddr;
    }

    public void setCaddr(String caddr) {
        this.caddr = caddr;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
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

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public String getCnetpointtime() {
        return cnetpointtime;
    }

    public void setCnetpointtime(String cnetpointtime) {
        this.cnetpointtime = cnetpointtime;
    }

    public Date getCaddtime() {
        return caddtime;
    }

    public void setCaddtime(Date caddtime) {
        this.caddtime = caddtime;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

}
