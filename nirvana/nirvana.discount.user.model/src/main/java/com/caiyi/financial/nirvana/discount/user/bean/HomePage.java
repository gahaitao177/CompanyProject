package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/8/1.
 */
public class HomePage {
    private     Integer     pageid;
    private     String      type;
    private     String      title;
    private     String      sub_title;
    private     String      pic_url;
    private     Integer     action_type;
    private     String      param01;
    private     String      param02;
    private     Date        cadddate;
    private     String      cadduser;
    private     Date        cupdatedate;
    private     String      cupdateuser;
    private     Integer     is_hodden;
    private     Integer     is_del;
    private     String      city_code;
    private     Integer     iorder;

    public Integer getPageid() {
        return pageid;
    }

    public void setPageid(Integer pageid) {
        this.pageid = pageid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public Integer getAction_type() {
        return action_type;
    }

    public void setAction_type(Integer action_type) {
        this.action_type = action_type;
    }

    public String getParam01() {
        return param01;
    }

    public void setParam01(String param01) {
        this.param01 = param01;
    }

    public String getParam02() {
        return param02;
    }

    public void setParam02(String param02) {
        this.param02 = param02;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCadduser() {
        return cadduser;
    }

    public void setCadduser(String cadduser) {
        this.cadduser = cadduser;
    }

    public Date getCupdatedate() {
        return cupdatedate;
    }

    public void setCupdatedate(Date cupdatedate) {
        this.cupdatedate = cupdatedate;
    }

    public String getCupdateuser() {
        return cupdateuser;
    }

    public void setCupdateuser(String cupdateuser) {
        this.cupdateuser = cupdateuser;
    }

    public Integer getIs_hodden() {
        return is_hodden;
    }

    public void setIs_hodden(Integer is_hodden) {
        this.is_hodden = is_hodden;
    }

    public Integer getIs_del() {
        return is_del;
    }

    public void setIs_del(Integer is_del) {
        this.is_del = is_del;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }
}
