package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by Mario on 2016/7/25 0025.
 * 对应表 TB_CR_REPORTDETAILS
 */
public class CreditReportDetailsDto {
    private Long icrid;
    private String cdetails;
    private Integer itype;

    public Long getIcrid() {
        return icrid;
    }

    public void setIcrid(Long icrid) {
        this.icrid = icrid;
    }

    public String getCdetails() {
        return cdetails;
    }

    public void setCdetails(String cdetails) {
        this.cdetails = cdetails;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }
}
