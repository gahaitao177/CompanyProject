package com.caiyi.financial.nirvana.discount.ccard.dto;


import com.caiyi.financial.nirvana.discount.ccard.bean.Store;

import java.util.List;

/**
 * 优惠详情接口 dto
 * Created by wenshiliang on 2016/5/6.
 */
public class CheapDetailDto {

    private String istoreid;
    private String cstorename;
    private String caddress;
    private String clogo;
    private String ctel;
    private String cbus_info;
    private String clat;
    private String clng;

    private String cpicurl1;
    private String cpicurl2;
    private String ibussinessid;//
    private String ipraisetimes;//点赞数
    private String isavetimes;//收藏数
    private String isave;//是否已收藏
    private String ipraise;//是否已点赞
    private String endtime;
    private String ctitle;
    private String count;

    private List<CheapDto> row;
    private List<Store> store;

    public List<CheapDto> getRow() {
        return row;
    }

    public void setRow(List<CheapDto> row) {
        this.row = row;
    }

    public List<Store> getStore() {
        return store;
    }

    public void setStore(List<Store> store) {
        this.store = store;
    }

    public String getCpicurl2() {
        return cpicurl2;
    }

    public void setCpicurl2(String cpicurl2) {
        this.cpicurl2 = cpicurl2;
    }

    public String getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(String istoreid) {
        this.istoreid = istoreid;
    }

    public String getCstorename() {
        return cstorename;
    }

    public void setCstorename(String cstorename) {
        this.cstorename = cstorename;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }

    public String getCtel() {
        return ctel;
    }

    public void setCtel(String ctel) {
        this.ctel = ctel;
    }

    public String getCbus_info() {
        return cbus_info;
    }

    public void setCbus_info(String cbus_info) {
        this.cbus_info = cbus_info;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public String getCpicurl1() {
        return cpicurl1;
    }

    public void setCpicurl1(String cpicurl1) {
        this.cpicurl1 = cpicurl1;
    }

    public String getIbussinessid() {
        return ibussinessid;
    }

    public void setIbussinessid(String ibussinessid) {
        this.ibussinessid = ibussinessid;
    }

    public String getIpraisetimes() {
        return ipraisetimes;
    }

    public void setIpraisetimes(String ipraisetimes) {
        this.ipraisetimes = ipraisetimes;
    }

    public String getIsavetimes() {
        return isavetimes;
    }

    public void setIsavetimes(String isavetimes) {
        this.isavetimes = isavetimes;
    }

    public String getIsave() {
        return isave;
    }

    public void setIsave(String isave) {
        this.isave = isave;
    }

    public String getIpraise() {
        return ipraise;
    }

    public void setIpraise(String ipraise) {
        this.ipraise = ipraise;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
