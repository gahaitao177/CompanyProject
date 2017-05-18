package com.caiyi.financial.nirvana.discount.tools.bean;

/**
 * Created by zhukai on 2016/12/5.
 */
public class IdfaBean {
    private static final long serialVersionUID = 1L;
    private String appid;//推广App标识
    private String idfa;//用户的IDFA
    private String source;//推广渠道来源
    private String callback;//渠道回调地址及参数
    private String timestamp;//请求时间戳
    private String sign;//请求参数签名

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "IdfaBean{" +
                "appid='" + appid + '\'' +
                ", idfa='" + idfa + '\'' +
                ", source='" + source + '\'' +
                ", callback='" + callback + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
