package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.util.List;

/**
 * Created byLinxingyu on 2016/12/19.
 */
public class BillConsumeTypeDto {
    private String month;
    private String money;
    private String repayStatus;
    private List<BillConsumeAnalysisDto> bills;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRepayStatus() {
        return repayStatus;
    }

    public void setRepayStatus(String repayStatus) {
        this.repayStatus = repayStatus;
    }

    public List<BillConsumeAnalysisDto> getBills() {
        return bills;
    }

    public void setBills(List<BillConsumeAnalysisDto> bills) {
        this.bills = bills;
    }

}
