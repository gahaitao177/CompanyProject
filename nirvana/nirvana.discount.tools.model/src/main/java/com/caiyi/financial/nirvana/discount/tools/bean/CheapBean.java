package com.caiyi.financial.nirvana.discount.tools.bean;

/**
 * Created by dengh on 2016/8/9.
 */
public class CheapBean extends ViewBean {
    private static final long serialVersionUID = 1L;

    private Long id; // 优惠编号
    private Long bankid; // 优惠编号
    private Long businessid; // 优惠编号
    private String title;// 优惠标题
    private String content;// 优惠内容
    private int pv;// 访问量
    private int pv1;// 收藏量
    private int pv2;// 是否限量
    private int pv3;// 是否长期有效
    private int pv4;// 是否限制卡中
    private int cityid;// 城市
    private int week;// 星期
    private int star;// 星级
    private int iopen;// 是否开启

    private int icityid;//城市id

    private int ipareaid;

    private String istoreid;
    private String ibusinessid;

    private String citycode;

    private String query;

    private double clat;
    private double clng;
    private String searchtype = "0";

    public Long getBankid() {
        return bankid;
    }

    public void setBankid(Long bankid) {
        this.bankid = bankid;
    }

    public Long getBusinessid() {
        return businessid;
    }

    public void setBusinessid(Long businessid) {
        this.businessid = businessid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getClat() {
        return clat;
    }
    public void setClat(double clat) {
        this.clat = clat;
    }
    public double getClng() {
        return clng;
    }
    public void setClng(double clng) {
        this.clng = clng;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public int getIpareaid() {
        return ipareaid;
    }
    public void setIpareaid(int ipareaid) {
        this.ipareaid = ipareaid;
    }
    public String getIstoreid() {
        return istoreid;
    }
    public void setIstoreid(String istoreid) {
        this.istoreid = istoreid;
    }
    public String getIbusinessid() {
        return ibusinessid;
    }
    public void setIbusinessid(String ibusinessid) {
        this.ibusinessid = ibusinessid;
    }
    public String getCitycode() {
        return citycode;
    }
    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
    public int getIcityid() {
        return icityid;
    }
    public void setIcityid(int icityid) {
        this.icityid = icityid;
    }

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

}
