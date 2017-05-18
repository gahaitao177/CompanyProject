package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by dengh on 2016/8/3.
 */
//首页推荐商户列表
public class RecommendShopBean {
    private Integer irecommendid;
    private Integer ibusid;
    private String  clogo;
    private String  cbusname;
    private Integer iorder;
    private Integer Istate;
    private  String  type;

    public Integer getIrecommendid() {
        return irecommendid;
    }

    public void setIrecommendid(Integer irecommendid) {
        this.irecommendid = irecommendid;
    }

    public Integer getIbusid() {
        return ibusid;
    }

    public void setIbusid(Integer ibusid) {
        this.ibusid = ibusid;
    }

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }

    public String getCbusname() {
        return cbusname;
    }

    public void setCbusname(String cbusname) {
        this.cbusname = cbusname;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }

    public Integer getIstate() {
        return Istate;
    }

    public void setIstate(Integer istate) {
        Istate = istate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
