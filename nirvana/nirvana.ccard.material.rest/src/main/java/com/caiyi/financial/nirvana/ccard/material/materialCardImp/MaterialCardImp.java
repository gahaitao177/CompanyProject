package com.caiyi.financial.nirvana.ccard.material.materialCardImp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.ccard.material.banks.cityBank.CityBankApply;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaException;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.apply.GuangDaApplyUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.GuangFaSubmit;
import com.caiyi.financial.nirvana.ccard.material.banks.jiaotong.JiaoTongH5Helper;
import com.caiyi.financial.nirvana.ccard.material.banks.minsheng.MinShengSubmit;
import com.caiyi.financial.nirvana.ccard.material.banks.pingan.PingAnApplyUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.xingye.XingYeApply;
import com.caiyi.financial.nirvana.ccard.material.banks.zhada.ZhadaUtils;
import com.caiyi.financial.nirvana.ccard.material.banks.zhongXin.ZhongXinUtil;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.danga.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/28.
 */
@MVCComponent
public class MaterialCardImp {
    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient client;

    private  static Logger log= LoggerFactory.getLogger(MaterialCardImp.class);
    /**
     * 办卡手机短信验证码接口（第一步）
     *
     * @param bean
     * @param response
     */
    public int getBankMessage(MaterialBean bean, HttpServletRequest request, HttpServletResponse response, MemCachedClient cc) {
        bean.setBusiJSON("fail");
        MaterialModel model = bean.getModel();
        log.info("测试*****model.getApplyBankCardId="+bean.getApplyBankCardId());
        log.info("测试*****model.getApplyBankCardLevel="+bean.getApplyBankCardLevel());
        log.info("测试*****model.getCphone="+model.getCphone());
        bean.setCphone(model.getCphone());
        if ("16".equals(bean.getIbankid())) {
            return JiaoTongH5Helper.getBankMessage_JiaoTong(bean,cc);
        } else if ("3".equals(bean.getIbankid())) {
            //光大
            try {
                return GuangDaApplyUtil.getBankMessage(bean,cc);
            } catch (GuangDaException e) {
                e.printStackTrace();
                bean.setBusiErrCode(e.getCode());
                bean.setBusiErrDesc(e.getMessage());
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("出错了");
                return 0;
            }
        } else if ("1".equals(bean.getIbankid())) {//广发
            return GuangFaSubmit.sendSmsCode(bean,cc).getCode();
        }else if("7".equals(bean.getIbankid())) {//平安
            return PingAnApplyUtil.getApplyBankMessage(bean,cc);
        }else if("2".equals(bean.getIbankid())) {//中信银行,zhaojie
            ZhongXinUtil zxu = new ZhongXinUtil();
            return zxu.openSession(bean,cc);
        }else if("11".equals(bean.getIbankid())){// 民生 by denghong
            MinShengSubmit mssub = new MinShengSubmit();
            return  mssub.getPhoneCode(bean,cc);  // 成功返回1 失败返回0
        } else if("10".equals(bean.getIbankid())){
            XingYeApply xingye=new XingYeApply();
            return xingye.sendMessage(bean,cc);
        } else if("5".equals(bean.getIbankid())){  // add by lcs 20160616 start
            Map<String,String> map=new HashMap<>();
            map.put("mobileNo",bean.getCphone());
            String z=client.execute(Constant.HSK_CCARD_MATERIAL,new DrpcRequest("material","sendMessage",map));
//            RemoteBeanCallUtil.RemoteBeanCall(bean, context, "2", "find_Material_sendMsg");
            log.info("花旗发送验证码" + bean.getBusiErrDesc() + ","+ bean.getYzm() + ",CODE" + bean.getBusiErrCode());

            if(z!=null&&z.contains("1")){
                return 1;
            }else {
                return 0;
            }
        }else if("6".equals(bean.getIbankid())){
            Map<String,String> map=new HashMap<>();
            map.put("mobileNo",bean.getCphone());
            log.info("渣打发送验证码");
            String z= client.execute(Constant.HSK_CCARD_MATERIAL,new DrpcRequest("material","sendMessage",map));
//            RemoteBeanCallUtil.RemoteBeanCall(bean, context, "2", "find_Material_sendMsg");
            log.info("渣打发送验证码" + bean.getBusiErrDesc() + ","+ bean.getYzm() + ",CODE" + bean.getBusiErrCode());
            if(z!=null&&z.contains("1")){
               return 1;
            }else {
                return 0;
            }

        }
        // add by lcs 20160616 end
        else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该银行暂不支持");
        }
        return 0;
    }
    /**
     * 办卡手机短信验证码检测接口（第二步）
     *
     * @param bean
     * @param response
     */
    public int checkBankMessage(MaterialBean bean,HttpServletRequest request, HttpServletResponse response,MemCachedClient cc) {
        log.info("进入checkBankMessage 接口");
        bean.setBusiJSON("fail");
        if("7".equals(bean.getIbankid())){
            return PingAnApplyUtil.checkBankMessage(bean,cc);
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该银行暂不支持");
        }
        return 0;
    }
    /**
     * 办卡申请提交接口（第二步）
     * @param bean
     * @param response
     */
    public int applyBankCreditCard(MaterialBean bean,HttpServletRequest request, HttpServletResponse response,MemCachedClient cc) {
        bean.setBusiJSON("fail");
        bean.setMediatype("json");
        int ret = 0;
        if ("16".equals(bean.getIbankid())) {
            ret =  JiaoTongH5Helper.applyForTheJiaoTongBank(bean,cc);
        } else if ("3".equals(bean.getIbankid())) {
            //光大
            try {
                ret =  GuangDaApplyUtil.applyBankCreditCard(bean,cc);
            } catch (GuangDaException e) {
                e.printStackTrace();
                bean.setBusiErrCode(e.getCode());
                bean.setBusiErrDesc(e.getMessage());
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("出错了");
                return 0;
            }

        } else if ("1".equals(bean.getIbankid())) {//广发
            int code = GuangFaSubmit.applyForTheGuangFaBank(bean,cc).getCode();
            ret =  code==1?1:0;
        } else if ("7".equals(bean.getIbankid())){
            ret =  PingAnApplyUtil.applyCreditCard(bean,cc);
        }else if("2".equals(bean.getIbankid())){//中信银行,zhaojie
            ZhongXinUtil zx = new ZhongXinUtil();
            ret =  zx.applayCard(bean,cc);
        }else if("6".equals(bean.getIbankid())){//渣打银行,zhaojie
            bean.setOnlyCheckSms("1");
            bean.setCphone(bean.getModel().getCphone());
            log.info("渣打验证验证码" + bean.getPhoneauthcode() + "," + bean.getCphone());
            Map<String,String> para=new HashMap<>();
//            包含手机号 mobileNo  验证码  yzm
            para.put("mobileNo",bean.getCphone());
            para.put("yzm",bean.getPhoneauthcode());
            String s= client.execute(new DrpcRequest("material","findMaterial",para));
            List<Map<String,Object>> list= JSON.toJavaObject(JSON.parseObject(s),List.class);
            log.info("验证验证码" + bean.getBusiErrDesc() + ",code:" +  bean.getBusiErrCode());
            if (list!=null&&list.size()>0){
                ZhadaUtils zd = new ZhadaUtils();
                ret = zd.applayCard(bean);
            }else {
                bean.setBusiErrCode(-1);
            }
        }else if("11".equals(bean.getIbankid())){// 民生 denghong
            MinShengSubmit msSub = new MinShengSubmit();
            ret =  msSub.submit(bean,cc); // 成功1 失败0
        }else if("10".equals(bean.getIbankid())){// 兴业
            XingYeApply xingye=new XingYeApply();
            ret =  xingye.applyForXingYe(bean,request,cc); // 成功1 失败0
        }else if("5".equals(bean.getIbankid())){// 花旗 李传顺
            log.info("花旗验证验证码" + bean.getPhoneauthcode());
            bean.setOnlyCheckSms("1");
            bean.setCphone(bean.getModel().getCphone());

            Map<String,String> para=new HashMap<>();
//            包含手机号 mobileNo  验证码  yzm
            para.put("mobileNo",bean.getCphone());
            para.put("yzm",bean.getPhoneauthcode());
            String s= client.execute(new DrpcRequest("material","findMaterial",para));
            List<Map<String,Object>> list= JSON.toJavaObject(JSON.parseObject(s),List.class);
//            RemoteBeanCallUtil.RemoteBeanCall(bean, context, "2", "find_Material");
            log.info("验证验证码" + bean.getBusiErrDesc() + ",code:" +  bean.getBusiErrCode());
            if (list!=null&&list.size()>0){
                CityBankApply cityBankApply=new CityBankApply();
                ret =  cityBankApply.cardApply(bean); // 成功1 失败0
            } else {
                bean.setBusiErrCode(-1);
            }
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该银行暂不支持");
        }

        //调用stub查询城市是否支持预约人工办卡
        if(ret != 1){
            Map<String,String> para=new HashMap<>();
//            包含手机号 mobileNo  验证码  yzm
            para.put("icityid",bean.getIcityid());
//            para.put("yzm",bean.getPhoneauthcode());
            String s= client.execute(new DrpcRequest("materialCard","o2oApplyCity",para));
            JSONObject o2oJson=JSON.parseObject(s);
            if(o2oJson.get("data")!=null) {
                bean.setBusiJSON(o2oJson.get("data").toString());
            }else {
                bean.setBusiJSON("o2o:false");
            }
//            RemoteBeanCallUtil.RemoteBeanCall(bean, context, "2", "o2oApplyCity");
        }
        return ret;
    }
}
