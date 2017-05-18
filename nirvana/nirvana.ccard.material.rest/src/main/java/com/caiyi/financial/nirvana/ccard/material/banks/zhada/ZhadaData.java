package com.caiyi.financial.nirvana.ccard.material.banks.zhada;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mario on 2016/6/13 0013.
 */
public class ZhadaData {

    public ZhadaData(String name, String tel, String income, String campId, String formId, String city) {
        this.name = name;
        this.tel = tel;
        this.income = income;
        this.campId = campId;
        this.formId = formId;
        this.city = city;
        this.createFormXml();
    }

    public String formXML;
    public String city;
    public String Submission_date = "";
    public String form2_gender = "";
    public String form2_email_address_user = "";
    public String form2_email_address_host = "";
    public String form2_email_address = "";
    public String form2_mobile_countrycode = "";
    public String site = "";
    public String camp_id = "";
    public String source_id = "";

    private String name;
    private String tel;
    private String income;
    private String campId;
    private String formId;
    private String subDate = new SimpleDateFormat().format(new Date());

    /**
     * 生成FORM_XML
     */
    private void createFormXml() {
        this.formXML = "<?xml version='1.0' encoding='UTF-8'?>";
        this.formXML += "<eform FID='" + this.formId + "'>";
        this.formXML += "<model><instance id='savemodel'><sigfields><sigchar><sigchar1>";
        this.formXML += "<![CDATA[" + this.name + "]]>";
        this.formXML += "</sigchar1><sigchar2><![CDATA[+86" + this.tel + "]]>";
        this.formXML += "</sigchar2></sigchar><signum/><sigdate/></sigfields></instance>";
        this.formXML += "<instance id='outputmodel'><Submission_date>";
        this.formXML += "<![CDATA[" + this.subDate + "]]></Submission_date>";
        this.formXML += "<form2_name><![CDATA[" + this.name + "]]></form2_name>";
        this.formXML += "<form2_gender><![CDATA[NA]]></form2_gender>";
        this.formXML += "<form2_monthly_income><![CDATA[" + this.income + "]]></form2_monthly_income>";
        this.formXML += "<form2_email_address_user><![CDATA[NA]]></form2_email_address_user>";
        this.formXML += "<form2_email_address_host><![CDATA[NA]]></form2_email_address_host>";
        this.formXML += "<form2_email_address><![CDATA[NA]]></form2_email_address>";
        this.formXML += "<form2_mobile_countrycode><![CDATA[NA]]></form2_mobile_countrycode>";
        this.formXML += "<form2_mobile><![CDATA[+86" + this.tel + "]]></form2_mobile>";
        this.formXML += "<form2_city><![CDATA[" + this.city + "]]></form2_city>";
        this.formXML += "<site><![CDATA[NA]]></site>";
        this.formXML += "<camp_id><![CDATA[" + this.campId + "]]></camp_id>";
        this.formXML += "<source_id><![CDATA[null]]></source_id>";
        this.formXML += "</instance></model></eform>";
    }

    /**
     * 生成param字符串
     */
    public String toParams() {
        String params = "formXML=" + this.formXML;
        params += "&income=" + this.income;
        params += "&city=" + this.city;
        params += "&Submission_date=";
        params += "&form2_gender=";
        params += "&form2_email_address_user=";
        params += "&form2_email_address_host=";
        params += "&form2_email_address=";
        params += "&form2_mobile_countrycode=";
        params += "&site=";
        params += "&camp_id=";
        params += "&source_id=";
        return params;
    }
}
