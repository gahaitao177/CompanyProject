package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by dengh on 2016/8/24.
 */
public class U_AccountBean {
    private  String cnickid;
    private  String cphone;
    private  String ctinyurl;
    private  String cusername;
    private  int banks;
    private  int stores=0;
    private  int cards;
    private  int  coupons=0;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getCtinyurl() {
        return ctinyurl;
    }

    public void setCtinyurl(String ctinyurl) {
        this.ctinyurl = ctinyurl;
    }

    public String getCusername() {
        return cusername;
    }

    public void setCusername(String cusername) {
        this.cusername = cusername;
    }

    public int getBanks() {
        return banks;
    }

    public void setBanks(int banks) {
        this.banks = banks;
    }

    public int getStores() {
        return stores;
    }

    public void setStores(int stores) {
        this.stores = stores;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getCoupons() {
        return coupons;
    }

    public void setCoupons(int coupons) {
        this.coupons = coupons;
    }
}
