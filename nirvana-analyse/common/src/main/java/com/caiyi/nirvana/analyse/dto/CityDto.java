package com.caiyi.nirvana.analyse.dto;

import com.datastax.driver.mapping.annotations.Column;

/**
 * Created by wenshiliang on 2017/1/20.
 */
public class CityDto {
    private String province;

    private String city;

    private String cityCode;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
