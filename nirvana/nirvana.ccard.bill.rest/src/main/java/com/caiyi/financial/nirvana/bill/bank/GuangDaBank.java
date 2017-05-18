package com.caiyi.financial.nirvana.bill.bank;

import cn.aofeng.common4j.lang.StringUtil;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Bill;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ResponseEntity;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GuangDaBank extends AbstractHttpService{
	public static final String smsUrl = "https://wap.cebbank.com/pwap/WPSendSMS.do";
	//获取图片验证码url
	public static final String imgUrl = "https://wap.cebbank.com/pwap/MpGenTokenImg.do";

	public static final String loginUrl = "https://wap.cebbank.com/pwap/WPLoginToken.do";

	/**
	 * 设置图片验证码
	 * 本地测试保存图片到硬盘,测试或线上环境返回base64码
	 * @param bean
	 */
	public String setYzm(Channel bean, MemCachedClient cc) {
		logger.info("光大银行获取图片验证码>>>>");
		LoginContext loginContext = null;
		try {
			Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore");
			if (cookieStore==null){
				cookieStore = new BasicCookieStore();
			}
			loginContext = createLoginContext((BasicCookieStore) cookieStore);
			Map<String, String> headers = loginContext.getHeaders();
			headers.put("Host","wap.cebbank.com");
			headers.put("Accept","image/webp,image/*,*/*;q=0.8");
			String yzm = getYzm(imgUrl, bean.getCuserId(), loginContext);
			cc.set(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore",loginContext.getCookieStore());
			return yzm;
		} catch (Exception e) {
			logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---", e);
		} finally {
			if(loginContext!=null){
				loginContext.close();
			}
		}
		return null;
	}

	/**
	 * 获取短信验证码
	 * @param bean
	 * @param cc
	 * @return
	 */
	public int getSms(Channel bean,MemCachedClient cc){
		LoginContext loginContext = null;
		try {
			Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore");
			Object paramsObj =  cc.get(bean.getCuserId() + bean.getBankId() + "_guangdaParam");
			if (cookieStore==null || paramsObj==null){
				logger.info(bean.getCuserId()+bean.getBankId()+"guangda_cookieStore"+"="+cookieStore);
				bean.setBusiErrCode(BillConstant.fail);
				bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
				return 0;
			}else{
				loginContext = createLoginContext((BasicCookieStore) cookieStore);
                String paramsStr = (String) paramsObj;
				String[] paramStrs = paramsStr.split("\\@");
				bean.setDencryIdcard(paramStrs[0]);
				bean.setBankRand(paramStrs[1]);
			}
			Map<String, String> headers = loginContext.getHeaders();
			headers.put("Host","wap.cebbank.com");
			headers.put("Accept", "*/*");
			Map<String, String> params = buildSMSParams(bean);
			String result = httpPost(smsUrl, params, loginContext);
			logger.info("动态码获取结果 \n" + result);
			Document smsDoc = Jsoup.parse(result);
			Elements peaEle = smsDoc.select("span.er");
			String errMsg = "";
			if (peaEle != null && peaEle.size() > 0) {
				errMsg = peaEle.first().text();
			}
			if (!StringUtils.isEmpty(errMsg)) {
				logger.info("cuserId"+bean.getCuserId()+"光大短信验证码获取失败>>>>>>" + errMsg);
				bean.setBusiErrCode(BillConstant.fail);
				bean.setBusiErrDesc(errMsg);
				return 0;
			} else {
				logger.info("cuserId"+bean.getCuserId()+"光大短信验证码发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
				bean.setBusiErrCode(BillConstant.success);
				bean.setBusiErrDesc("短信验证码发送成功");
				String userPhone = "";
				if (!StringUtils.isEmpty(bean.getIdCardNo())) {
					userPhone = bean.getIdCardNo();
				} else {
					userPhone = "********";
				}
				bean.setPhoneCode(userPhone);
				cc.set(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore",loginContext.getCookieStore());
				return 1;
			}
		} catch (Exception e) {
			logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---", e);
			bean.setBusiErrCode(BillConstant.fail);
			bean.setBusiErrDesc("环境异常,稍后重试");
		} finally {
			if(loginContext!=null){
				loginContext.close();
			}
		}
		return 0;
	}

	public Map<String, String> buildSMSParams(Channel bean) {
		Map<String, String> params = new HashMap<>();
		//卡号
		params.put("WPLoginName", bean.getDencryIdcard());
		//验证码
		params.put("_vTokenName", bean.getBankRand());
		return params;
	}

	/**
	 * 光大网银登陆
	 * @param bean
	 * @param cc
	 * @return
	 */
	public int checkSms(Channel bean, MemCachedClient cc) {
		logger.info("cuserId"+bean.getCuserId()+"光大银行进行短信验证登陆>>>");
		LoginContext loginContext = null;
		String result = "";
		try {
			Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore");
			if (cookieStore==null){
				logger.info(bean.getCuserId()+bean.getBankId()+"guangda_cookieStore"+"="+cookieStore);
				bean.setBusiErrCode(BillConstant.fail);
				bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
				return 0;
			}else{
				loginContext = createLoginContext((BasicCookieStore) cookieStore);
			}
			Map<String, String> headers = loginContext.getHeaders();
			headers.put("Host","wap.cebbank.com");
			headers.put("Accept", "*/*");
			Map<String, String> params = buildLoginParams(bean);
			result = httpPost(loginUrl,params,loginContext);
			String errText = "";
			Document doc = Jsoup.parse(result);
			Elements spanErs = doc.select("span.er");
			if (spanErs != null && spanErs.size() > 0) {
				errText = spanErs.get(0).text();
				errText = errText.replaceAll("\\s*", "");
			}
			logger.info("登陆结果 \n" + errText);
			if(StringUtils.isEmpty(errText)){//登陆成功
				bean.setBusiErrCode(BillConstant.success);
				bean.setBusiErrDesc("登录成功,开始解析账单");
				String cookieStr = loginContext.getCookieStr();
				logger.info("bankSessionId=="+cookieStr);
				bean.setBankSessionId(cookieStr);
				cc.set(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore",loginContext.getCookieStore());
				return 1;
			}else{
				bean.setBusiErrCode(BillConstant.fail);
				bean.setBusiErrDesc(errText);
				return 0;
			}
		} catch (Exception e) {
			logger.info(result);
			logger.error("cuserId="+bean.getCuserId()+":"+getClass().getSimpleName() + "---异常", e);
			bean.setBusiErrCode(BillConstant.fail);
			bean.setBusiErrDesc("环境异常,稍后重试");
		} finally {
			if(loginContext!=null){
				loginContext.close();
			}
		}
		return 0;
	}

	private Map<String, String> buildLoginParams(Channel bean) {
		Map<String, String> params = new HashMap<>();
		params.put("_viewReferer", "webpage/WPLogin");
		params.put("LoginType", "H");
		params.put("Channel", "2");
		params.put("BankId", "9999");
		params.put("_locale", "zh_CN");
		//账号或手机号
		params.put("WPLoginName", bean.getDencryIdcard());
		//手机银行登陆密码
		params.put("Password", bean.getDencryBankPwd());
		//短信验证码
		params.put("OTPPassword", bean.getOptRand());
		//图片验证码
		params.put("_vTokenName", bean.getBankRand());
		params.put("CEBPARAM", "f2b5ae8539968c4f29642c445ae95f434839fb6ca86f413e");
		return params;
	}

	/**
	 * task方法执行
	 * @param bean bean对象
	 * @param client drpc对象
	 * @return 执行结果 0:失败 1:成功
	 */
	public int taskReceve(Channel bean,IDrpcClient client,MemCachedClient cc){
		//保存的短信验证码
		Object optObj = cc.get(bean.getCuserId() + bean.getBankId() + "guangda_optRand");
		cc.delete(bean.getCuserId() + bean.getBankId() + "guangda_optRand");
		if (optObj==null){
			client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("bank", "billTaskConsume", bean));
		}
		int ret = dencrypt_data(bean);//参数解密
		if (ret==0){
			return ret;
		}
		int code = 0;
		if(optObj==null){//初次调用task接口,调用短信验证码发送接口
			bean.setCode("3");
			bean.setBusiErrCode(BillConstant.needmsg);//需要短信验证码
			bean.setBusiErrDesc("需要短信验证");
			bean.setPhoneCode("true");
			String idCardNo = bean.getDencryIdcard();
			cc.set(bean.getCuserId() + bean.getBankId() + "_guangdaParam", idCardNo + "@" + bean.getBankRand(),  3600000 );
		}else{//调用验证码检测接口
			String optRand = (String)optObj;
			bean.setOptRand(optRand);
			code = checkSms(bean,cc);
			if (code==0){
				bean.setCode("0");
			}else {
				bean.setCode("1");
			}
		}
		changeCode(bean,client);
		return code;
	}

    public int verifyMsgGD(Channel bean,MemCachedClient cc) throws Exception {
        logger.info("cuserId"+bean.getCuserId()+"光大银行需要短信验证>>>>");
        String img_rand = bean.getBankRand();
        int ret = dencrypt_data(bean);
        if(ret==1){//解析参数成功
            String idCardNo = bean.getDencryIdcard();
            cc.set(bean.getCuserId() + bean.getBankId() + "_guangdaParam", idCardNo + "@" + img_rand,  3600000 );
            bean.setBusiErrCode(BillConstant.needmsg);
            bean.setBusiErrDesc("需要短信验证");
            bean.setPhoneCode("true");
            return 0;
        }else{
            return ret;
        }
    }
}