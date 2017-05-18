package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by dengh on 2016/8/1.
 */
public class PageBean extends BaseBean {
    private static final long serialVersionUID = 7864744287816340664L;

    private int tp = 0;//总页数
    private Integer ps = 5;//页面大小
    private Integer pn = 1;//页码
    private int rc = 0;//总记录数

    public int getTp() {
        return tp;
    }
    public void setTp(int tp) {
        this.tp = tp;
    }
    public Integer getPs() {
        return ps;
    }
    public void setPs(int ps) {
        this.ps = ps;
    }
    public Integer getPn() {
        return pn;
    }
    public void setPn(int pn) {
        this.pn = pn;
    }
    public int getRc() {
        return rc;
    }
    public void setRc(int rc) {
        this.rc = rc;
    }
}
