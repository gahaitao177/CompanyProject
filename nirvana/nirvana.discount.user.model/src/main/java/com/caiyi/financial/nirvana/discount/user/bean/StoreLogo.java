package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by dengh on 2016/7/21.
 */
public class StoreLogo extends BaseBean {
   // ts.istoreid, tbs.clogo
    private  Integer istoreid;
    private  String clogo;
    private  String ibankids;

    public String getIbankids() {
        return ibankids;
    }

    public void setIbankids(String ibankids) {
        this.ibankids = ibankids;
    }

    public Integer getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(Integer istoreid) {
        this.istoreid = istoreid;
    }

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }
}
