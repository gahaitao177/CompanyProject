package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.Date;

/**
 * Created by Linxingyu on 2016/12/23.
 * TB_YOUYU_NEWS
 */
public class YouYuNews extends BaseBean {
    private Integer inewsid;//资讯id
    private Integer iitemid;//元素id
    private Integer ifrom;//0 资讯自发布 1卡神攻略 2 特惠快讯 3:banner
    private Integer iprime;//是否是精品 0:否  1:是
    private String ctype;//xyk:信用卡 dk:贷款 lc:理财 hot 热门 zx:征信 gjj:公积金 fd:房贷
    private Integer iorder;//排序
    private Integer ishow;//显示 0 不显示  1:显示
    private Date cadddate;//添加时间
    private String caddby;//添加者
    private Date cupdate;
    private String cupdateby;
    private Integer idel;//删除标记 1 删除
    private String ctitle;//标题
    private String csource;//来源
    private String clogourl;//logo_url
    private Integer ishowtype;//showtype:0(正常模式) | 1(大图模式) | 2(banner模式)
    private String ctarget;//target:如果ishowType是0 1 值为链接 如果是2则显示banner配置的json数据


    public Integer getInewsid() {
        return inewsid;
    }

    public void setInewsid(Integer inewsid) {
        this.inewsid = inewsid;
    }

    public Integer getIitemid() {
        return iitemid;
    }

    public void setIitemid(Integer iitemid) {
        this.iitemid = iitemid;
    }

    public Integer getIfrom() {
        return ifrom;
    }

    public void setIfrom(Integer ifrom) {
        this.ifrom = ifrom;
    }

    public Integer getIprime() {
        return iprime;
    }

    public void setIprime(Integer iprime) {
        this.iprime = iprime;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }

    public Integer getIshow() {
        return ishow;
    }

    public void setIshow(Integer ishow) {
        this.ishow = ishow;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCaddby() {
        return caddby;
    }

    public void setCaddby(String caddby) {
        this.caddby = caddby;
    }

    public Date getCupdate() {
        return cupdate;
    }

    public void setCupdate(Date cupdate) {
        this.cupdate = cupdate;
    }

    public String getCupdateby() {
        return cupdateby;
    }

    public void setCupdateby(String cupdateby) {
        this.cupdateby = cupdateby;
    }

    public Integer getIdel() {
        return idel;
    }

    public void setIdel(Integer idel) {
        this.idel = idel;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCsource() {
        return csource;
    }

    public void setCsource(String csource) {
        this.csource = csource;
    }

    public String getClogourl() {
        return clogourl;
    }

    public void setClogourl(String clogourl) {
        this.clogourl = clogourl;
    }

    public Integer getIshowtype() {
        return ishowtype;
    }

    public void setIshowtype(Integer ishowtype) {
        this.ishowtype = ishowtype;
    }

    public String getCtarget() {
        return ctarget;
    }

    public void setCtarget(String ctarget) {
        this.ctarget = ctarget;
    }
}
