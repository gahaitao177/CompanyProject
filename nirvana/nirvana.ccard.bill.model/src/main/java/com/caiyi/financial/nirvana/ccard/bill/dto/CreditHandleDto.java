package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Linxingyu on 2017/2/20.
 */
public class CreditHandleDto {
    private Integer ibankid;
    private String cbankname;
    private String cactivation;
    private String cbankicon;
    private String cprogressaddr;

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public String getCbankname() {
        return cbankname;
    }

    public void setCbankname(String cbankname) {
        this.cbankname = cbankname;
    }

    public String getCactivation() {
        return cactivation;
    }

    public void setCactivation(String cactivation) {
        this.cactivation = cactivation;
    }

    public String getCbankicon() {
        return cbankicon;
    }

    public void setCbankicon(String cbankicon) {
        this.cbankicon = cbankicon;
    }

    public String getCprogressaddr() {
        return cprogressaddr;
    }

    public void setCprogressaddr(String cprogressaddr) {
        this.cprogressaddr = cprogressaddr;
    }

    @Override
    public String toString() {
        return "{" +
                "ibankid=" + ibankid +
                ", cbankname='" + cbankname + '\'' +
                ", cactivation='" + cactivation + '\'' +
                ", cbankicon='" + cbankicon + '\'' +
                ", cprogressaddr='" + cprogressaddr + '\'' +
                '}';
    }
}
