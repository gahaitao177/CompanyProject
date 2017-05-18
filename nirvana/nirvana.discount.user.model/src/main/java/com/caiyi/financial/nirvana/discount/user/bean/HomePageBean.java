package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public class HomePageBean extends ViewBean {
    private String adcode;//高德地图code

    private String icityid;//本地tb_area中主键id

    private String hskcityid;//本地tb_area中主键id


    private String ibankids;//银行id集合

    private String homePageType;

    private List<String> bankids;

    public String getHomePageType() {
        return homePageType;
    }

    public void setHomePageType(String homePageType) {
        this.homePageType = homePageType;
    }

    public String getIbankids() {
        return ibankids;
    }

    public void setIbankids(String ibankids) {
        this.ibankids = ibankids;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getIcityid() {
        return icityid;
    }

    public void setIcityid(String icityid) {
        this.icityid = icityid;
    }

    public String getHskcityid() {
        return hskcityid;
    }

    public void setHskcityid(String hskcityid) {
        this.hskcityid = hskcityid;
        setIcityid(hskcityid);
    }

    public List<String> getBankids() {
        return bankids;
    }

    public void setBankids(List<String> bankids) {
        this.bankids = bankids;
    }
}
