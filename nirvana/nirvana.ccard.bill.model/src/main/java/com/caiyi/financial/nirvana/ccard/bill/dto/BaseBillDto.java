package com.caiyi.financial.nirvana.ccard.bill.dto;


import java.sql.Timestamp;

/**
 * Created by lichuanshun on 16/7/12.
 */
public class BaseBillDto {
    private Integer ibillid;
    private String icard4num;
    private String cname;
    private Integer icreditid;
    private Integer ibankid;
    private String cuserid;
    private String cstartperioddate;
    private String cendperioddate;
    private Double ishouldrepayment;
    private Double ilowestrepayment;
    private Double inobillamount;
    private Double itotalquota;
    private Double iavailablequota;
    private Double icashamount;
    private String cbilldate;
    private String crepaymentdate;
    private Double iflowamount;
    private Double iflowoutamount;
    private Double itotalconsume;
    private Double ibalance;
    private Timestamp cadddate;
    private Timestamp cupdate;
    private Integer isdel;
    private String ioutsideid;
    private Integer iswebormail;
    private Integer iskeep;
    private Double ipoint;
    private String cexpiredate;
    private Double ipoint1;
    private Integer imailbilltype;
    private Integer irepayment;
    private String uname;

    public Integer getIrepayment() {
        return irepayment;
    }

    public void setIrepayment(Integer irepayment) {
        this.irepayment = irepayment;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Integer getIbillid() {
        return ibillid;
    }

    public void setIbillid(Integer ibillid) {
        this.ibillid = ibillid;
    }

    public String getIcard4num() {
        return icard4num;
    }

    public void setIcard4num(String icard4num) {
        this.icard4num = icard4num;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public Integer getIcreditid() {
        return icreditid;
    }

    public void setIcreditid(Integer icreditid) {
        this.icreditid = icreditid;
    }

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getCstartperioddate() {
        return cstartperioddate;
    }

    public void setCstartperioddate(String cstartperioddate) {
        this.cstartperioddate = cstartperioddate;
    }

    public String getCendperioddate() {
        return cendperioddate;
    }

    public void setCendperioddate(String cendperioddate) {
        this.cendperioddate = cendperioddate;
    }

    public Double getIshouldrepayment() {
        return ishouldrepayment;
    }

    public void setIshouldrepayment(Double ishouldrepayment) {
        this.ishouldrepayment = ishouldrepayment;
    }

    public Double getIlowestrepayment() {
        return ilowestrepayment;
    }

    public void setIlowestrepayment(Double ilowestrepayment) {
        this.ilowestrepayment = ilowestrepayment;
    }

    public Double getInobillamount() {
        return inobillamount;
    }

    public void setInobillamount(Double inobillamount) {
        this.inobillamount = inobillamount;
    }

    public Double getItotalquota() {
        return itotalquota;
    }

    public void setItotalquota(Double itotalquota) {
        this.itotalquota = itotalquota;
    }

    public Double getIavailablequota() {
        return iavailablequota;
    }

    public void setIavailablequota(Double iavailablequota) {
        this.iavailablequota = iavailablequota;
    }

    public Double getIcashamount() {
        return icashamount;
    }

    public void setIcashamount(Double icashamount) {
        this.icashamount = icashamount;
    }

    public String getCbilldate() {
        return cbilldate;
    }

    public void setCbilldate(String cbilldate) {
        this.cbilldate = cbilldate;
    }

    public String getCrepaymentdate() {
        return crepaymentdate;
    }

    public void setCrepaymentdate(String crepaymentdate) {
        this.crepaymentdate = crepaymentdate;
    }

    public Double getIflowamount() {
        return iflowamount;
    }

    public void setIflowamount(Double iflowamount) {
        this.iflowamount = iflowamount;
    }

    public Double getIflowoutamount() {
        return iflowoutamount;
    }

    public void setIflowoutamount(Double iflowoutamount) {
        this.iflowoutamount = iflowoutamount;
    }

    public Double getItotalconsume() {
        return itotalconsume;
    }

    public void setItotalconsume(Double itotalconsume) {
        this.itotalconsume = itotalconsume;
    }

    public Double getIbalance() {
        return ibalance;
    }

    public void setIbalance(Double ibalance) {
        this.ibalance = ibalance;
    }

    public Timestamp getCadddate() {
        return cadddate;
    }

    public void setCadddate(Timestamp cadddate) {
        this.cadddate = cadddate;
    }

    public Timestamp getCupdate() {
        return cupdate;
    }

    public void setCupdate(Timestamp cupdate) {
        this.cupdate = cupdate;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    public String getIoutsideid() {
        return ioutsideid;
    }

    public void setIoutsideid(String ioutsideid) {
        this.ioutsideid = ioutsideid;
    }

    public Integer getIswebormail() {
        return iswebormail;
    }

    public void setIswebormail(Integer iswebormail) {
        this.iswebormail = iswebormail;
    }

    public Integer getIskeep() {
        return iskeep;
    }

    public void setIskeep(Integer iskeep) {
        this.iskeep = iskeep;
    }

    public Double getIpoint() {
        return ipoint;
    }

    public void setIpoint(Double ipoint) {
        this.ipoint = ipoint;
    }

    public String getCexpiredate() {
        return cexpiredate;
    }

    public void setCexpiredate(String cexpiredate) {
        this.cexpiredate = cexpiredate;
    }

    public Double getIpoint1() {
        return ipoint1;
    }

    public void setIpoint1(Double ipoint1) {
        this.ipoint1 = ipoint1;
    }

    public Integer getImailbilltype() {
        return imailbilltype;
    }

    public void setImailbilltype(Integer imailbilltype) {
        this.imailbilltype = imailbilltype;
    }
}
