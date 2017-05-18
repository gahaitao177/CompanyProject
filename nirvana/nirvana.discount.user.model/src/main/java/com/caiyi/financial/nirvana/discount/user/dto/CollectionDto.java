package com.caiyi.financial.nirvana.discount.user.dto;

import java.util.List;

/**
 * Created by wenshiliang on 2016/9/6.
 * 收藏接口查询 优惠dto
 */
public class CollectionDto {
    private List<Bank> bank;//关注银行集合
    private List<CheapDto> row;//门店优惠集合
    private List<MarketDto> coupon;//优惠劵集合
    private Count count;//统计

    public List<Bank> getBank() {
        return bank;
    }

    public void setBank(List<Bank> bank) {
        this.bank = bank;
    }

    public List<CheapDto> getRow() {
        return row;
    }

    public void setRow(List<CheapDto> row) {
        this.row = row;
    }

    public List<MarketDto> getCoupon() {
        return coupon;
    }

    public void setCoupon(List<MarketDto> coupon) {
        this.coupon = coupon;
    }

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

    public static class Bank{
        private int ibankid;

        public int getIbankid() {
            return ibankid;
        }

        public void setIbankid(int ibankid) {
            this.ibankid = ibankid;
        }
    }


    public static class CheapDto{
        private Long istoreid;//门店id
        private String cname;//门店名称
        private String clogo;//
        private String bankids;//优惠银行
        private String cheaptitle;//优惠标题
        private String ccheaptype;//优惠类别
        private String iexpire;//是否过期

        public Long getIstoreid() {
            return istoreid;
        }

        public void setIstoreid(Long istoreid) {
            this.istoreid = istoreid;
        }

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public String getClogo() {
            return clogo;
        }

        public void setClogo(String clogo) {
            this.clogo = clogo;
        }

        public String getBankids() {
            return bankids;
        }

        public void setBankids(String bankids) {
            this.bankids = bankids;
        }

        public String getCheaptitle() {
            return cheaptitle;
        }

        public void setCheaptitle(String cheaptitle) {
            this.cheaptitle = cheaptitle;
        }

        public String getCcheaptype() {
            return ccheaptype;
        }

        public void setCcheaptype(String ccheaptype) {
            this.ccheaptype = ccheaptype;
        }

        public String getIexpire() {
            return iexpire;
        }

        public void setIexpire(String iexpire) {
            this.iexpire = iexpire;
        }
    }
    public static class MarketDto{
        private Long id;//
        private String cname;
        private int count;
        private String clogo;
        private List<MarketCheapDto> cheap;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getClogo() {
            return clogo;
        }

        public void setClogo(String clogo) {
            this.clogo = clogo;
        }

        public List<MarketCheapDto> getCheap() {
            return cheap;
        }

        public void setCheap(List<MarketCheapDto> cheap) {
            this.cheap = cheap;
        }
    }

    public static class Count{
        private int banks;
        private int stores;
        private int coupons;
        private int expire;

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

        public int getCoupons() {
            return coupons;
        }

        public void setCoupons(int coupons) {
            this.coupons = coupons;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }
    }

}
