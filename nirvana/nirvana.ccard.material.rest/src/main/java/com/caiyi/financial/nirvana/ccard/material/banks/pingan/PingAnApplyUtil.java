package com.caiyi.financial.nirvana.ccard.material.banks.pingan;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.discount.Constants;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 平安办卡申请工具
 * @author A-0199
 *
 */
public class PingAnApplyUtil {
	public static Logger logger = LoggerFactory.getLogger("PingAnApplyUtil");
//    @Autowired
//    private  static MemCachedClient client;
	 /**
     * 平安办卡获取图片验证码前置
     * @param bean
     * @return
     * @throws IOException
     */
    public static BufferedImage getApplyImgPre(MaterialBean bean,MemCachedClient client){
    	BufferedImage localBufferedImage = null;
    	String cphone = "";
    	try{  		
        	MaterialModel model = bean.getModel();
            String cidcard = model.getCidcard();
            cphone = model.getCphone();
        	String keyPrefix = cidcard + cphone;
        	String cookieKey = keyPrefix + "pingAn_cookieStore";
        	Object cookieObj = client.get(cookieKey);
        	logger.info("getBankVerifyCode>>>>>>>cookieKey:"+cookieKey);
        	CookieStore pacookieStore;
        	if(cookieObj!=null){
        		pacookieStore = (CookieStore) cookieObj;
        	}else{
        		bean.setBusiErrCode(0);
                bean.setBusiErrDesc("会话过期,请重新请求短信验证码");
                return localBufferedImage;
        	}
        	PingAnUtil paUtil = new PingAnUtil();
        	localBufferedImage = paUtil.getImageRand(pacookieStore);
        	client.set(cookieKey,pacookieStore, Constants.TIME_HOUR);
    	}catch(Exception e){
    		logger.info("pingAnApplyUtil.getApplyImgPre 异常");
    		ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage()+";"+bean.getBusiErrDesc());
    		errBean.setCerrordesc("获取图片验证码异常");
            errBean.setIerrortype(0);
            errBean.setCphone(cphone);
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.img_code, errBean);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取图片验证码错误");
            bean.setBusiJSON("error");
    	}
        return localBufferedImage;
    }
    
    /**
     * 平安办卡短信验证码发送验证
     * @param bean
     * @return
     */
    public static int checkBankMessage(MaterialBean bean,MemCachedClient client){
    	String cphone = "";
    	try{
    
    		if (bean.getBusiErrCode() == 0) {
                return 0;
            }
    		MaterialModel model = bean.getModel();
            String cidcard = model.getCidcard();
            cphone = model.getCphone();
            if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数错误");
                return 0;
            }            
            String key = cidcard + cphone + "_pingan";
            String cookieKey = cidcard + cphone + "pingAn_cookieStore";
            PingAnBean pingAn = new PingAnBean();
            PingAnUtil paUtil = new PingAnUtil();
            CookieStore cookieStore = new BasicCookieStore();
        	String errMsg = paUtil.applyFirst(pingAn,cookieStore);//
        	if(!StringUtils.isEmpty(errMsg)){
        		bean.setBusiErrCode(0);
        		bean.setBusiErrDesc(errMsg);
        		return 0;
        	}
        	errMsg = paUtil.sentPhoneRand(bean,cookieStore);//获取短信验证码        
        	client.set(key,pingAn,Constants.TIME_HOUR);
        	client.set(cookieKey,cookieStore,Constants.TIME_HOUR);
        	logger.info("errMsg====="+errMsg);
        	if(!StringUtils.isEmpty(errMsg)){
        		if(errMsg.contains("picVerify")){
        			bean.setBusiErrCode(-1);//
        			bean.setBusiErrDesc("需要进行图片验证");
        		}else if(errMsg.contains("验证码")||errMsg.contains("请输入验证码")){
            		bean.setBusiErrCode(-1);//验证码错误
            		bean.setBusiErrDesc("需要进行图片验证");
        		}else{//
        			bean.setBusiErrCode(0);
        			bean.setBusiErrDesc(errMsg);
        		}  
                return 0;
        	}else{
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信验证码发送成功!");
                return 1;
        	}	
    	}catch(Exception e){
    		logger.info("pingAnApplyUtil.checkBankMessage 异常");
            try {
                ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
                errBean.setCerrordesc("短信验证码发送检测异常");
                errBean.setIerrortype(0);
                errBean.setCphone(cphone);
                BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.phone_code, errBean);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码发送检测异常");
            bean.setBusiJSON("error");
            return 0;
    	}
    	
    }
    
    /**
     * 平安办卡申请获取短信验证码
     * @param bean
     * @return
     */
    public static int getApplyBankMessage(MaterialBean bean,MemCachedClient client){
    	String cphone = "";
    	String errMsg = "";
    	try{
    		if (bean.getBusiErrCode() == 0) {
                return 0;
            }
    		MaterialModel model = bean.getModel();
            String imgCode = bean.getImgauthcode();         
            String cidcard = model.getCidcard();
            cphone = model.getCphone();
            if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数错误");
                return 0;
            }   
            String cookieKey = cidcard + cphone + "pingAn_cookieStore";
            CookieStore cookieStore;
            Object cookieObj = client.get(cookieKey);
            if(cookieObj!=null){
            	cookieStore = (CookieStore) cookieObj;
            }else{
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("会话过期,请重新请求图片验证码");
                return 0;
            }    
            if (StringUtils.isEmpty(imgCode)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("图片验证码不能为空");
                return 0;
            }   
        	PingAnUtil paUtil = new PingAnUtil();
        	errMsg = paUtil.sentPhoneRand(bean,cookieStore);//获取短信验证码
        	client.set(cookieKey,cookieStore,Constants.TIME_HOUR);
        	logger.info("errMsg====="+errMsg);
        	if(!StringUtils.isEmpty(errMsg)){       		
        		if(errMsg.contains("验证码错误")||errMsg.contains("picVerify")){
            		bean.setBusiErrCode(-1);//验证码错误
            		bean.setBusiErrDesc("验证码错误,请重新刷新验证码");
        		}else{
        			bean.setBusiErrCode(0);
        			bean.setBusiErrDesc("获取短信验证码错误");
        		}
                return 0;
        	}else{  
        		BankApplyListener.sendSucess(BankEnum.pingan,BankApplyStepEnum.phone_code);
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信验证码发送成功!");
                return 1;
        	}
    	}catch(Exception e){
    		logger.error("pingAnApplyUtil.getApplyBankMessage 异常",e);

    		ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
    		errBean.setCerrordesc("获取短信验证码出现异常");
            errBean.setIerrortype(0);
            errBean.setCphone(cphone);
            errBean.setResult(errMsg);
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.phone_code, errBean);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信发送异常");
            bean.setBusiJSON("error");
            return 0;
    	}   
    }
    /**
     * 平安申请信用卡
     * @param bean
     * @return
     */
    public static int applyCreditCard(MaterialBean bean,MemCachedClient client){
    	String cphone = "";
    	try{
    		String phoneRcode = bean.getPhoneauthcode();//短信验证码
        	bean.setBusiJSON("fail");
            if (StringUtils.isEmpty(phoneRcode)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码不能为空");
                return 0;
            }
            MaterialModel model = bean.getModel();
            String cidcard = model.getCidcard();
            cphone = model.getCphone();
            if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数错误");
                return 0;
            }
            String key = cidcard + cphone + "_pingan";
            String cookieKey = cidcard + cphone + "pingAn_cookieStore";
            logger.info("applyCreditCard>>>>>>>>会话key："+key);
            Object obj = client.get(key);
            Object cookieObj = client.get(cookieKey);
            if (obj == null || cookieObj ==null) {
            	bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("会话过期,请重新请求短信验证码");
                return 0;
            }           
            PingAnBean pingAn = (PingAnBean) obj;
            CookieStore cookieStore = (CookieStore) cookieObj;   

        	PingAnUtil paUtil = new PingAnUtil();
        	String errMsg = paUtil.applyPingAnBank(bean,pingAn,cookieStore,phoneRcode,client);
    		logger.info("applyBankCreditCard>>>errMsg=="+errMsg);
        	if(StringUtils.isEmpty(errMsg)){//申卡成功
                BankApplyListener.sendSucess(BankEnum.pingan,BankApplyStepEnum.submit_apply);
        		//成功
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("申卡成功,请耐心等待银行反馈结果");
                bean.setBusiJSON("success");
                return 1;
        	}else{//申卡失败
        		if(errMsg.contains("动态密码不正确")){//短信验证码错误
        			bean.setBusiErrCode(-1);	
        		}else{
            		bean.setBusiErrCode(0);
        		}
        		bean.setBusiErrDesc(errMsg);
        		return 0;
        	}
    	}catch(Exception e){
    		logger.info("pingAnApplyUtil.applyCreditCard 异常");
    		ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
    		errBean.setCerrordesc("申卡异常");
            errBean.setIerrortype(0);
            errBean.setCphone(cphone);
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.submit_apply, errBean);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申卡提交异常");
            bean.setBusiJSON("error");
            return 0;
    	}    	
    }     
}
