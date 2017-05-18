package com.caiyi.financial.nirvana.ccard.bill.dto;


import java.sql.Timestamp;

/**
 * Created by Mario on 2016/7/8 0008.
 * 对应表 TB_REPAYMENT_ORDER
 */
public class RepaymentOrderDto {
    private String cpartner_id;
    private String cchannel_id;
    private String cbank_alias;
    private String ccard_tail;
    private Integer ibill_id;
    private String cuser_id;
    private String ccard_name;
    private Double iamount;
    private String time_stamp;
    private String csign;
    private Integer istate = 0;
    private String cbank_type;
    private java.sql.Timestamp dapply_time;
    private java.sql.Timestamp drepay_time;
    private java.sql.Timestamp dpay_time;
    private String cconfirm_msg;
    private Integer ipay_type;
    private String ccomments;
    private Double icommission;
    private String cwx_repay_no;
    private Double iwx_amount;
    private String cwx_sign;

    public String getCpartner_id() {
        return cpartner_id;
    }

    public void setCpartner_id(String cpartner_id) {
        this.cpartner_id = cpartner_id;
    }

    public String getCchannel_id() {
        return cchannel_id;
    }

    public void setCchannel_id(String cchannel_id) {
        this.cchannel_id = cchannel_id;
    }

    public String getCbank_alias() {
        return cbank_alias;
    }

    public void setCbank_alias(String cbank_alias) {
        this.cbank_alias = cbank_alias;
    }

    public String getCcard_tail() {
        return ccard_tail;
    }

    public void setCcard_tail(String ccard_tail) {
        this.ccard_tail = ccard_tail;
    }

    public Integer getIbill_id() {
        return ibill_id;
    }

    public void setIbill_id(Integer ibill_id) {
        this.ibill_id = ibill_id;
    }

    public String getCuser_id() {
        return cuser_id;
    }

    public void setCuser_id(String cuser_id) {
        this.cuser_id = cuser_id;
    }

    public String getCcard_name() {
        return ccard_name;
    }

    public void setCcard_name(String ccard_name) {
        this.ccard_name = ccard_name;
    }

    public Double getIamount() {
        return iamount;
    }

    public void setIamount(Double iamount) {
        this.iamount = iamount;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getCsign() {
        return csign;
    }

    public void setCsign(String csign) {
        this.csign = csign;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public String getCbank_type() {
        return cbank_type;
    }

    public void setCbank_type(String cbank_type) {
        this.cbank_type = cbank_type;
    }

    public java.sql.Timestamp getDapply_time() {
        return dapply_time;
    }

    public void setDapply_time(java.sql.Timestamp dapply_time) {
        this.dapply_time = dapply_time;
    }

    public java.sql.Timestamp getDrepay_time() {
        return drepay_time;
    }

    public void setDrepay_time(java.sql.Timestamp drepay_time) {
        this.drepay_time = drepay_time;
    }

    public Timestamp getDpay_time() {
        return dpay_time;
    }

    public void setDpay_time(Timestamp dpay_time) {
        this.dpay_time = dpay_time;
    }

    public String getCconfirm_msg() {
        return cconfirm_msg;
    }

    public void setCconfirm_msg(String cconfirm_msg) {
        this.cconfirm_msg = cconfirm_msg;
    }

    public Integer getIpay_type() {
        return ipay_type;
    }

    public void setIpay_type(Integer ipay_type) {
        this.ipay_type = ipay_type;
    }

    public String getCcomments() {
        return ccomments;
    }

    public void setCcomments(String ccomments) {
        this.ccomments = ccomments;
    }

    public Double getIcommission() {
        return icommission;
    }

    public void setIcommission(Double icommission) {
        this.icommission = icommission;
    }

    public String getCwx_repay_no() {
        return cwx_repay_no;
    }

    public void setCwx_repay_no(String cwx_repay_no) {
        this.cwx_repay_no = cwx_repay_no;
    }

    public Double getIwx_amount() {
        return iwx_amount;
    }

    public void setIwx_amount(Double iwx_amount) {
        this.iwx_amount = iwx_amount;
    }

    public String getCwx_sign() {
        return cwx_sign;
    }

    public void setCwx_sign(String cwx_sign) {
        this.cwx_sign = cwx_sign;
    }
}
