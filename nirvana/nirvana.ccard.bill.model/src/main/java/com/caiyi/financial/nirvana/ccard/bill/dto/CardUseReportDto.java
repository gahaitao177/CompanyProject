package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Linxingyu on 2017/2/6.
 */
public class CardUseReportDto {
    private String month;
    private Integer isOnTimeRepayment;//是否按时还款， 0 不按时， 1 按时
    private Double repaymentRate;//月还款率
    private Double useRate;//月额度使用率

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getIsOnTimeRepayment() {
        return isOnTimeRepayment;
    }

    public void setIsOnTimeRepayment(Integer isOnTimeRepayment) {
        this.isOnTimeRepayment = isOnTimeRepayment;
    }

    public Double getRepaymentRate() {
        return repaymentRate;
    }

    public void setRepaymentRate(Double repaymentRate) {
        this.repaymentRate = repaymentRate;
    }

    public Double getUseRate() {
        return useRate;
    }

    public void setUseRate(Double useRate) {
        this.useRate = useRate;
    }
}
