package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by dengh on 2016/8/8.
 */
public class FeedBackBean extends BaseBean {
    private static final long serialVersionUID = 1L;

    // 0:用户纠错 1:优惠上传
    private String type;
    // 错误类型 0:优惠不存在 1:优惠内容错误 2：电话错误 3：地图位置错误 4：商家已关 5：详细报错
    private String wrongType = "";
    // 门店id
    private String storeId ="";
    // 门店名
    private String name = "";
    // 地址
    private String address = "";
    // 银行id
    private String bankId = "";
    // 内容
    private String title = "";
    // 详情
    private String detail = "";
    // 图片地址
    private String picUrl = "";
    // 创建者
    private String addBy = "";
    // 修改者
    private String updateBy = "";
    // 状态
    private int status;
    // 城市id
    private String cityId = "";

    // qq号码
    private String qqAccountId = "";

    // idfa值
    //private String iosIdfa = "";
    private String idfa = "";
    private String mediatype = "";
    private String busiJSON = "";

    public String getMediatype() {
        return mediatype;
    }
    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }
    public String getCityId() {
        return cityId;
    }
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getWrongType() {
        return wrongType;
    }
    public void setWrongType(String wrongType) {
        this.wrongType = wrongType;
    }
    public String getStoreId() {
        return storeId;
    }
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getBankId() {
        return bankId;
    }
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getPicUrl() {
        return picUrl;
    }
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getAddBy() {
        return addBy;
    }
    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }
    public String getUpdateBy() {
        return updateBy;
    }
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getQqAccountId() {
        return qqAccountId;
    }
    public void setQqAccountId(String qqAccountId) {
        this.qqAccountId = qqAccountId;
    }

    public String getIdfa() {
        return idfa;
    }
    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }
    public String getBusiJSON() {
        return busiJSON;
    }
    public void setBusiJSON(String busiJSON) {
        this.busiJSON = busiJSON;
    }

    @Override
    public String toString() {
        if ("json".equals(this.getMediatype())){

            return this.getBusiJSON();
        }
        return super.toString();
    }


}
