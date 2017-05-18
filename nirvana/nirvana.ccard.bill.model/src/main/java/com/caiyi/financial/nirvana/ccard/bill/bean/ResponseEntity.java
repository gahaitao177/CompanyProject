package com.caiyi.financial.nirvana.ccard.bill.bean;

/**
 * Created by ljl on 2016/12/1.
 */
public class ResponseEntity {

    private String code;
    private String bankId = "";
    private String method = "";
    private String desc;
    private String isFrist = "";
    private String imgcode;
    private String flag;
    private String phoneCode;
    private String taskId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIsFrist() {
        return isFrist;
    }

    public void setIsFrist(String isFrist) {
        this.isFrist = isFrist;
    }

    public String getImgcode() {
        return imgcode;
    }

    public void setImgcode(String imgcode) {
        this.imgcode = imgcode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
