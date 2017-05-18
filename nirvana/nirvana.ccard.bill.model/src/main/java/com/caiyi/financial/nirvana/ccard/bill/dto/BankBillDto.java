package com.caiyi.financial.nirvana.ccard.bill.dto;
/**
 * Created by lichuanshun on 16/5/18.
 */
public class BankBillDto extends BaseBillDto{
    private Integer isZero;//是否可以提升临额，0能，1不能
    private String zeroQuota;//临额可提升至
    private Integer isFixed;//是否可以提升固额，0能，1不能
    private String fixedQuota;//固额可提升至
    private String nowzeroquota;//临额基准

    public String getNowzeroquota() {
        return nowzeroquota;
    }

    public void setNowzeroquota(String nowzeroquota) {
        this.nowzeroquota = nowzeroquota;
    }

    public Integer getIsZero() {
        return isZero;
    }

    public void setIsZero(Integer isZero) {
        this.isZero = isZero;
    }

    public String getZeroQuota() {
        return zeroQuota;
    }

    public void setZeroQuota(String zeroQuota) {
        this.zeroQuota = zeroQuota;
    }

    public Integer getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Integer isFixed) {
        this.isFixed = isFixed;
    }

    public String getFixedQuota() {
        return fixedQuota;
    }

    public void setFixedQuota(String fixedQuota) {
        this.fixedQuota = fixedQuota;
    }
}

