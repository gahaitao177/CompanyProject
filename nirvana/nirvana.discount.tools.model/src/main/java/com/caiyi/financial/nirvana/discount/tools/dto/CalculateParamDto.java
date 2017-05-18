package com.caiyi.financial.nirvana.discount.tools.dto;

import com.caiyi.financial.nirvana.discount.tools.bean.ViewBean;

import java.util.Date;

/**
 * Created by lizhijie on 2016/8/11.
 */
public class CalculateParamDto extends ViewBean {
    private static final long serialVersionUID = -3422332087758295532L;
    private String itypeid;
    private String ccityname;
    private String ctitle;
    private String crate;
    private String cvalue;
    private Integer itype;
    private Date cupdate;
    private int version;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getItypeid() {
        return itypeid;
    }

    public void setItypeid(String itypeid) {
        this.itypeid = itypeid;
    }

    public String getCcityname() {
        return ccityname;
    }

    public void setCcityname(String ccityname) {
        this.ccityname = ccityname;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public String getCvalue() {
        return cvalue;
    }

    public void setCvalue(String cvalue) {
        this.cvalue = cvalue;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(int itype) {
        this.itype = itype;
    }

    public Date getCupdate() {
        return cupdate;
    }

    public void setCupdate(Date cupdate) {
        this.cupdate = cupdate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
