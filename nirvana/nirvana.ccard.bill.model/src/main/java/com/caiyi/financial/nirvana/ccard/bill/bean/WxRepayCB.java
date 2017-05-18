package com.caiyi.financial.nirvana.ccard.bill.bean;

/**
 * Created by Mario on 2016/7/8 0008.
 */
public class WxRepayCB {
    private String amount;
    private String bank_type;
    private String card_tail;
    private String partner_id;
    private String repay_time;
    private String pay_time;
    private Integer state;
    private String wx_repay_no;
    private String sign;

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getCard_tail() {
        return card_tail;
    }

    public void setCard_tail(String card_tail) {
        this.card_tail = card_tail;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getRepay_time() {
        return repay_time;
    }

    public void setRepay_time(String repay_time) {
        this.repay_time = repay_time;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getWx_repay_no() {
        return wx_repay_no;
    }

    public void setWx_repay_no(String wx_repay_no) {
        this.wx_repay_no = wx_repay_no;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
