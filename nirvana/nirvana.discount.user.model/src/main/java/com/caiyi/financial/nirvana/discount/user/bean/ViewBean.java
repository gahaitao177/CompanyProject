package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by dengh on 2016/8/1.
 */
public class ViewBean extends BaseBean {
    private static final long serialVersionUID = 1L;

    private String mediatype;

    private String busiJSON;
    // iclient 0  安卓  1ios
    private Integer iclient=-1;

    public Integer getIclient() {
        return iclient;
    }

    public void setIclient(int iclient) {
        this.iclient = iclient;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getBusiJSON() {
        return busiJSON;
    }

    public void setBusiJSON(String busiJSON) {
        this.busiJSON = busiJSON;
    }

    @Override
    public String toString() {
        if("json".equals(this.getMediatype()) || busiJSON!=null){
            StringBuilder ret = new StringBuilder();
            ret.append("{");
            ret.append("\"code\":"+this.getBusiErrCode()+",");
            ret.append("\"desc\":\""+this.getBusiErrDesc()+"\",");

            if((null != this.getBusiJSON())&&(this.getBusiJSON().contains("{") || this.getBusiJSON().contains("["))){
                ret.append("\"data\":"+this.getBusiJSON()+"");
            }else{
                ret.append("\"data\":\""+this.getBusiJSON()+"\"");
            }
            ret.append("}");
            return ret.toString();
        }
        return super.toString();
    }
}
