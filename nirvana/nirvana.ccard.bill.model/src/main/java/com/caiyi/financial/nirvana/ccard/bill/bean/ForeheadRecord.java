package com.caiyi.financial.nirvana.ccard.bill.bean;

import java.util.Date;

/**
 * Created by ZhouPingHua
 * Date：2017/2/16.
 * 信用卡提额记录表
 */
public class ForeheadRecord {

    private String frid;       //主键id
    private String ibillid; //tb_bank_bill 主键 关联字段
    private Integer isprofix;  // 固额提额or临额提额 0固额1临额
    private Double oldlimit; //信用卡提额前额度
    private Double setlimit;  //信用卡希望提额额度
    private Integer isstate;  //成功或者失败 0成功 1失败
    private Date createtime;  //提额时间

    public String getFrid() {
        return frid;
    }

    public void setFrid(String frid) {
        this.frid = frid;
    }

    public String getIbillid() {
        return ibillid;
    }

    public void setIbillid(String ibillid) {
        this.ibillid = ibillid;
    }

    public Integer getIsprofix() {
        return isprofix;
    }

    public void setIsprofix(Integer isprofix) {
        this.isprofix = isprofix;
    }

    public Double getOldlimit() {
        return oldlimit;
    }

    public void setOldlimit(Double oldlimit) {
        this.oldlimit = oldlimit;
    }

    public Double getSetlimit() {
        return setlimit;
    }

    public void setSetlimit(Double setlimit) {
        this.setlimit = setlimit;
    }

    public Integer getIsstate() {
        return isstate;
    }

    public void setIsstate(Integer isstate) {
        this.isstate = isstate;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
