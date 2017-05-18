package com.caiyi.financial.nirvana.ccard.material.banks.cityBank;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.hsk.cardUtil.HttpClientHelper;
import com.hsk.cardUtil.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 花旗银行模拟登录
 * Created by lichuanshun on 16/6/13.
 * update by lcs 20160719 add error log 信息
 *
 */
public class CityBankApply extends CityBankUtils{
    // 提交数据
    public int cardApply(MaterialBean bean){
        logger.info("cardApply:" + JSON.toJSONString(bean));
        return submitData(bean);
    }

    // 进度查询
    public int cardProgress(MaterialBean bean){
        logger.info("cardProgress:" + JSON.toJSONString(bean));

        return  1;
    }
    // 提交数据
    private int submitData(MaterialBean bean){
        try {
            //  设置请求数据
            setSubmitData(bean);
            //   不符合条件 预审失败
            if(!eligible){
                logger.info("eligible:" + eligible);
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("非常抱歉，您未通过花旗银行的预审。");
                bean.setCstatus("2");
                bean.setBusiJSON("success");
                ErrorRequestBean errorRequestBean = new ErrorRequestBean(System.currentTimeMillis() + ".html",data,"非常抱歉，您未通过花旗银行的预审。",CITYBANK_SUBMIT_URL,1,"未通过花旗银行的预审",data.get("phone"));
                BankApplyListener.sendError(BankEnum.huaqi, BankApplyStepEnum.submit_apply,errorRequestBean);
                return 1;
            }
            //true 表示使用https方式
            HttpClientHelper hc = new HttpClientHelper(1);
            // 请求头信息
            initSubmitHeader();
            hc.get(CITYBANK_APPLY_ADDRESS,hearders);
            // 请求数据初始化
            initSubmitData();
            System.out.println(data);
            hearders.put("Referer", CITYBANK_APPLY_ADDRESS);
            HttpResult hr = hc.post(CITYBANK_SUBMIT_URL,data,hearders);
            String nextUrl = "";
            for (Header header :hr.getHeaders()){
                if ("Location".toLowerCase().equals(header.getName().toLowerCase())){
                    nextUrl =  header.getValue();
                }
                hearders.put(header.getName(),header.getValue());
            }
            System.out.println(nextUrl);
            if(StringUtils.isEmpty(nextUrl)){
                bean.setBusiErrCode(1003);
                bean.setBusiErrDesc("提交失败");
                ErrorRequestBean errorRequestBean = new ErrorRequestBean(System.currentTimeMillis() + ".html",data,hr.getHeaders().toString(),CITYBANK_SUBMIT_URL,2,"提交失败",data.get("phone"));
                BankApplyListener.sendError(BankEnum.huaqi, BankApplyStepEnum.submit_apply,errorRequestBean);
                return 0;
            }
            HashMap<String,String> newHeader = new HashMap<>();
            hearders.put("Referer", CITYBANK_APPLY_ADDRESS);
            hr = hc.get(nextUrl,newHeader);

            String resultHtml = hr.getHtml();
            if (resultHtml.indexOf("非常感谢您的参与") > 0){
                System.out.println("提交成功");
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("提交成功");
                BankApplyListener.sendSucess(BankEnum.huaqi,BankApplyStepEnum.submit_apply);
                return 1;
            } else {
                bean.setBusiErrCode(1001);
                bean.setBusiErrDesc("提交失败");
                ErrorRequestBean errorRequestBean = new ErrorRequestBean(System.currentTimeMillis() + ".html",data,resultHtml,CITYBANK_SUBMIT_URL,2,"获取提交结果失败",data.get("phone"));
                BankApplyListener.sendError(BankEnum.huaqi, BankApplyStepEnum.submit_apply,errorRequestBean);
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            bean.setBusiErrCode(1002);
            bean.setBusiErrDesc("数据异常");
            ErrorRequestBean errorRequestBean = new ErrorRequestBean(System.currentTimeMillis() + ".html",data,e.toString(),CITYBANK_SUBMIT_URL,0,"异常",data.get("phone"));
            BankApplyListener.sendError(BankEnum.huaqi, BankApplyStepEnum.submit_apply,errorRequestBean);
            return 0;
        }
    }

    // 设置提交数据
    private void setSubmitData(MaterialBean bean){
        MaterialModel model = bean.getModel();
        //  住宅所在城市
        String homecity = model.getIhome_cid();
        // 住宅区县
        String homedistrict = model.getChomedistrictname();
        // 公司城市
        String compantcity = model.getIcompany_cid();
        // 公司区县
        String compantdistrict = model.getIcompany_did();
        //  寄卡地址 1、单位地址 2、住宅地址
        String postAddr = model.getIpostaddress();
        String city = compantcity;
        String district = model.getCcompanydistrictname();
        if ("2".equals(postAddr)){
            city = homecity;
            district = model.getChomedistrictname();
        }
        //  设置城市
        if (CITYBANK_CITY_MAP.containsKey(city)){
            data.put("city", CITYBANK_CITY_MAP.get(city));
            data.put("district", district);
        } else {
            eligible = false;
            return;
        }
        // 设置职业
        // 单位性质：1、机关/事业 2、国有 3、股份制 4、外商独资 5、中外合作企业 6、私营/集体 7、个体
        String natureofunit = model.getInatureofunit();
        // 1、机关/事业 2、国有  -->> 事业单位
        if ("1".equals(natureofunit) || "2".equals(natureofunit)){
            data.put("occupation","事业单位");
        } else if ("3".equals(natureofunit) || "4".equals(natureofunit) || "5".equals(natureofunit) || "6".equals(natureofunit)){
            //股份制、外商独资、中外合作企业、私营/集体 -->> 办公室白领
            data.put("occupation","办公室白领");
        } else {
            eligible = false;
            return;
        }
        // 月薪
        String iannualsalary = model.getIannualsalary();
        if (model.isNotNull(iannualsalary)){
            int yearsalary = 0;
            try {
                yearsalary = Integer.valueOf(iannualsalary);
            }catch (Exception e){
                e.printStackTrace();
            }
           int monthSalary = yearsalary*10000/12;
            System.out.println("monthSalary:" + monthSalary);
            if (monthSalary >= 10000){
                data.put("salary", "above 10,000");
            } else if(monthSalary >= 7000){
                data.put("salary", "7,000-10,000");
            } else {
                eligible = false;
                return;
            }
        } else {
            eligible = false;
            return;
        }

        //  是否有其他银行信用卡
        String holdcard = model.getCstartinfo();
        if ("2".equals(holdcard)){
            eligible = false;
            return;
        } else {
            data.put("is_card", "12月及以上");
        }

        //  姓名
        data.put("name", model.getCname());

        // 性别
        String cidcard = model.getCidcard();
        if (model.isNotNull(cidcard) && cidcard.length() ==18){
            int sex = Integer.valueOf(cidcard.substring(16,17));
            if (sex%2 == 0){
                data.put("title", "ms");
            } else {
                data.put("title", "mr");
            }
        } else {
            data.put("title", "mr");
        }

        // 手机
        data.put("phone", model.getCphone());

        //  邮箱
        data.put("email", model.getCemail());
    }
    // 获取hearders
    private void initSubmitHeader(){
        Map<String, String> requestHeaderMap = new HashMap<String, String>();
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestHeaderMap.put("Connection", "Keep-Alive");
        requestHeaderMap.put("Host", "www.citibank.com.cn");
        this.hearders = requestHeaderMap;
    }

    //  请求数据初始化
    private void initSubmitData(){
        data.put("action_url",CITYBANK_SUBMIT_URL);
        data.put("_paramOrder","code,icid,ecid,lid,media,card,city,district,name,title,phone,occupation,specific_job,salary,is_card");
        data.put("_paramAlias","Code,icid,ecid,lid,media,card,city,district,name,title,phone,occupation,specific_job,salary,is_card");
        data.put("subject", "Credit Card SF");
        data.put("code", COOPERATION_CODE);
        data.put("isThankYouPage", "true");
        data.put("hasRecap","false");
        data.put("ecid", COOPERATION_CODE);
        data.put("icid", "undefined");
        data.put("accept_selected", "1");
    }


    public static void main(String[] args){
        CityBankApply cityBankApply = new CityBankApply();
        MaterialBean bean = new MaterialBean();
        MaterialModel model = new MaterialModel();
        model.setCname("赵飞");
        model.setCphone("18772838321");
        model.setCidcard("370283790911703");
        model.setIhome_cid("310100");
        model.setIcompany_cid("310100");
        model.setIpostaddress("2");
        model.setInatureofunit("1");
        model.setIannualsalary("15");
        model.setCemail("22323@qq.com");
        model.setChomedistrictname("徐汇区");
        model.setCcompanydistrictname("徐汇区");
        bean.setModel(model);
        cityBankApply.cardApply(bean);
//        String id = "372926198903172512";
//        System.out.print(id.length());
//        String sex = id.substring(16,17);
//        System.out.print(sex);
    }

}
