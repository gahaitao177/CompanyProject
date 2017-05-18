package com.caiyi.financial.nirvana.ccard.material.banks.zhongXin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Mario on 2016/2/19 0019.
 * 页面用到的各种DataStore
 */
public class ZhongXinPageDataBean {
    /**
     * 将json字符串转为list
     */
    public void initList() {
        this.listProvince = JSON.parseArray(this.province,Province.class);
        this.listGaverMArrays = JSON.parseArray(this.gaverMArray,GaverMArray.class);
        this.listCountry = JSON.parseArray(this.country,Country.class);
        this.listCreditCardKinds = JSON.parseArray(this.creditCardKinds,CreditCardKinds.class);
        this.listBusinessArray = JSON.parseArray(this.businessArray,BusinessArray.class);
        this.listCity = JSON.parseArray(this.city,City.class);
        this.listJobArray = JSON.parseArray(this.jobArray,JobArray.class);
        if(this.yearMoneyArray!=null&&!this.yearMoneyArray.equals("")){
            this.listYearMoneyArray = JSON.parseArray(this.yearMoneyArray,YearMoneyArray.class);
        }
    }

    /**
     * 省、直辖市
     */
    static class Province{
        public String provenceCode;
        public String provenceName;
        public String code;
    }
    public List<Province> listProvince;
    public String province;

    /**
     * 不明，只有下面这些
     * "[{code:'001',name:'深圳市南山区中信支行'},{code:'002',name:'深圳市宝安区中信支行'},{code:'003',name:'深圳市福田区中信支行'},{code:'004',name:'深圳市罗湖区中信支行'}]"
     */
    static class GaverMArray{
        public String code;
        public String name;
    }
    public List<GaverMArray> listGaverMArrays;
    public String gaverMArray;

    /**
     * 区、县
     */
    static class Country{
        public String countyCode;
        public String countyName;
        public String code;
    }
    public List<Country> listCountry;
    public String country;

    /**
     * 卡种类
     * "[{userid:'1',imageUrl:'https://creditcard.ecitic.com/eshop/appimg/cardshop/card/KPML0014.png',appCardNbr:'KPML0014',name:'魔力银联金卡'},]"
     */
    static class CreditCardKinds{
        public String userid;
        public String imageUrl;
        public String appCardNbr;
        public String name;
    }
    public List<CreditCardKinds> listCreditCardKinds;
    public String creditCardKinds;
    /**
     * 本次请求的Message
     */
    public String message;

    /**
     * 工作单位类型
     * "[{code:'1',name:'机关事业'},{code:'2',name:'国有'},{code:'3',name:'外商独资'},{code:'4',name:'合资/合作'},{code:'5',name:'股份制'},{code:'6',name:'民营'},{code:'7',name:'个体私营'},{code:'8',name:'其他'}]"
     */
    static public class BusinessArray{
        public String code;
        public String name;
    }
    public List<BusinessArray> listBusinessArray;
    public String businessArray;

    /**
     * 市
     */
    static class City{
        public String cityCode;
        public String cityName;
        public String code;
        public String areaCode;
        public String postCode;
    }
    public List<City> listCity;
    public String city;

    /**
     * 工作类型,级别
     * "[{code:'01',name:'单位负责人级'},{code:'02',name:'部门负责人级'},{code:'03',name:'科室负责人级'},{code:'04',name:'一般员工'}]"
     */
    static class JobArray{
        public String code;
        public String name;
    }
    public List<JobArray> listJobArray;
    public String jobArray;
    /**
     * 年费类型,有些卡没有
     * "yearMoneyArray":"[{flagId:'W3',yearFree:'480'},]"
     */
    static class YearMoneyArray{
        public String flagId;
        public String yearFree;
    }
    public List<YearMoneyArray> listYearMoneyArray;
    public String yearMoneyArray;

    /**
     * 本次请求的code
     * 0:成功
     * -1:失败
     */
    public String code;

    /**
     * 根据省id获取省
     */
    public JSONObject getProvinceById(String pid){
        for(Province p : listProvince){
            if (p.code.equals(pid)){
                return JSON.parseObject(JSON.toJSONString(p));
            }
        }
        return null;
    }
    /**
     * 根据市名获取有关信息
     * @param cityName
     * @return
     */
    public JSONObject getCityByCityName(String cityName){
        if(listCity == null || listCity.size() == 0){
            return null;
        }
        for(City item : listCity){
            if(item.cityName.contains(cityName)||cityName.contains(item.cityName)){
                return JSON.parseObject(JSON.toJSONString(item));
            }
        }
        return null;
    }

    /**
     * 根据城市id获取城市名
     * @param cityId
     * @return
     */
    public JSONObject getCityByCityId(String cityId){
        for (City item : listCity){
            if(item.code.equals(cityId)){
                return JSON.parseObject(JSON.toJSONString(item));
            }
        }
        return null;
    }

    /**
     * 根据市code，区县名称,获取区县
     * @param cityCode
     * @param countryName
     * @return
     */
    public JSONObject getCountryByNameAndCityCode(String cityCode,String countryName){
        if(listCountry == null || listCountry.size() == 0){
            return null;
        }
        for(Country item : listCountry){
            if (item.countyCode.equals(cityCode) && (item.countyName.contains(countryName)||countryName.contains(item.countyName))){
                return JSON.parseObject(JSON.toJSONString(item));
            }
        }
        return null;
    }

    /**
     * 根据区县id获取区县
     * @param countryId
     * @return
     */
    public JSONObject getCountryById(String countryId){
        for(Country item : listCountry){
            if (item.code.equals(countryId)){
                return JSON.parseObject(JSON.toJSONString(item));
            }
        }
        return null;
    }
}
