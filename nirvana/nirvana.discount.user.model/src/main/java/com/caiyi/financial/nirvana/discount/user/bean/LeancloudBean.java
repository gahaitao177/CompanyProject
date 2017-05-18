package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/7/28.
 */
public class LeancloudBean  {

    private  String cuserid;      // 用户唯一序列id
    private  String  cleancloudid;  // 唯一id
    private  String idevicetype;  // 设备状态
    private  String  cbankids;   //关注银行
    private  Integer icityid;    // 城市id
    private  Date    cadddate;   // 添加时间
    private  Date    cupdate;   // 修改时间
    private  String  ccardids; // 关注银行卡id

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getCleancloudid() {
        return cleancloudid;
    }

    public void setCleancloudid(String cleancloudid) {
        this.cleancloudid = cleancloudid;
    }

    public String getIdevicetype() {
        return idevicetype;
    }

    public void setIdevicetype(String idevicetype) {
        this.idevicetype = idevicetype;
    }

    public String getCbankids() {
        return cbankids;
    }

    public void setCbankids(String cbankids) {
        this.cbankids = cbankids;
    }

    public Integer getIcityid() {
        return icityid;
    }

    public void setIcityid(Integer icityid) {
        this.icityid = icityid;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public Date getCupdate() {
        return cupdate;
    }

    public void setCupdate(Date cupdate) {
        this.cupdate = cupdate;
    }

    public String getCcardids() {
        return ccardids;
    }

    public void setCcardids(String ccardids) {
        this.ccardids = ccardids;
    }
}
