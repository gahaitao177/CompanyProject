package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;

import com.caiyi.financial.nirvana.ccard.material.bean.ViewBean;

/**
 * Created by lwg
 */
public class Message {
    private int code;
    private String desc;

    public Message(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Message success(String desc) {
        return new Message(1,desc);
    }

    public static Message error(String desc) {
        return new Message(0,desc);
    }

    /**
     * 短信验证码错误
     * @param desc 错误信息
     * @return
     */
    public static Message smsError(String desc) {
        return new Message(-1,desc);
    }

    public static Message errorJson(ViewBean bean, String desc, String... json) {
        String j = "error";
        if (json.length>0){
            j = json[0];
        }
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc(desc);
        bean.setBusiJSON(j);
        return new Message(0,desc);
    }

    public static Message successJson(ViewBean bean, String desc, String... json) {
        String j = "success";
        if (json.length>0){
            j = json[0];
        }
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc(desc);
        bean.setBusiJSON(j);
        return new Message(1,desc);
    }

    public static Message resultJson(ViewBean bean, Message message, String... json) {
        String j = "";
        if (json.length>0){
            j = json[0];
        }else{
            if(message.getCode()==1){
                j="success";
            }else {
                j="error";
            }
        }
        bean.setBusiErrCode(message.getCode());
        bean.setBusiErrDesc(message.getDesc());
        bean.setBusiJSON(j);
        return new Message(message.getCode(),message.getDesc());
    }
}
