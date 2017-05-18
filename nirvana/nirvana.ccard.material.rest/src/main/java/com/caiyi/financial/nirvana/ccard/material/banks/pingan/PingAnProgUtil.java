package com.caiyi.financial.nirvana.ccard.material.banks.pingan;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.discount.Constants;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 平安办卡进度查询工具类
 * @author A-0199
 *
 */
public class PingAnProgUtil {
	public static Logger logger = LoggerFactory.getLogger("PingAnProgUtil");
//	@Autowired
//	private  static MemCachedClient client;
    /**
     * 平安办卡进度查询方法
     * @param bean
     * @return
     */
	public static int pingAnProgressQuery(MaterialBean bean,MemCachedClient client) {
		try {
			if (bean.getBusiErrCode() == 0) {
                return 0;
            }
			String idcardid = bean.getIdcardid();// 身份证
			String smsCode = bean.getPhoneauthcode();//短信验证码
			bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
			if (StringUtils.isEmpty(idcardid)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("参数错误");
				return 0;
			}
			String key = idcardid + "schedule_pingAn_cookieStore";
			String redirect_key = bean.getIdcardid() + "redirectFlag_pingAn_cookieStore";
            String cardKey = idcardid + "_schedule_pingAn_cookieStore_cardType";
			Object cookieObj = client.get(key);
			Object redirectObj = client.get(redirect_key);
			Object cardObj = client.get(cardKey);
			CookieStore cookieStore;
			String redirectFlag = "";
			String cardType = "";
			if (cookieObj != null && cardObj!=null) {
				cookieStore = (CookieStore) cookieObj;
				cardType = (String) cardObj;
			} else {
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("会话过期,请重新刷新验证码");
				return 0;
			}
			if(redirectObj!=null){
				redirectFlag = (String) redirectObj;
			}
			PingAnUtil paUtil = new PingAnUtil();
			int code = 0;
			if(StringUtils.isEmpty(redirectFlag)){
				int ret = PingAnProgUtil.apply_credit_sms(bean,cookieStore,paUtil,client);
				if(ret==0){
					return 0;
				}else{
					code = bean.getBusiErrCode();
					if(code==2){
						return 1;
					}
				}
			}else if(redirectFlag.equals("have_sms")){
				client.delete(redirect_key);//清楚key
				if (StringUtils.isEmpty(smsCode)) {
					bean.setBusiErrCode(0);
					bean.setBusiErrDesc("参数错误");
					return 0;
				}
				String errMsg = paUtil.checkSmsCodeOfProgress(cookieStore, bean);
				if (!errMsg.equals("success")) {
					bean.setBusiErrCode(0);
					bean.setBusiErrDesc(errMsg);
					return 0;
				}
			}			
			JSONObject restJobj = paUtil.getCardProgress(cookieStore,bean,redirectFlag,cardType);		
			String resultcode = "0"; // 0:处理中 1:通过 2:不通过 3:没有记录 -1:系统异常
			if (!restJobj.isNull("resultcode")) {
				resultcode = restJobj.get("resultcode").toString();
			}
			if(resultcode.equals("-1")){//系统异常
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("系统异常,请稍后再试!");
				logger.info("PingAn申卡进度查询异常");
				return 0;
			}else if(resultcode.equals("0")){
				bean.setBusiErrDesc("处理中");
			}else if(resultcode.equals("1")){
				bean.setBusiErrDesc("已通过");
			}else if(resultcode.equals("2")){
				bean.setBusiErrDesc("未通过");
			}else{
				bean.setBusiErrDesc("处理中");
			}
			bean.setBusiErrCode(1);
			bean.setCstatus(resultcode);				
			bean.setBusiJSON(restJobj.toString());
			logger.info("PingAn申卡进度查询成功>>>>>>>申卡结果是:"+restJobj.toString());
			BankApplyListener.sendSucess(BankEnum.pingan, BankApplyStepEnum.query_apply);
			return 1;
		} catch (Exception e) {
			ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.query_apply, errBean);
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("查询进度错误，请联系惠刷卡客服咨询");
			bean.setBusiJSON("fail");
			logger.error(bean.getIapplyid() + " pingAnProgressQuery 异常", e);
		}
		return 0;
	}
	
	public static int smsCodeForQueryApply(MaterialBean bean,MemCachedClient client) {
		try {
			String idcardid = bean.getIdcardid();// 身份证
			if (StringUtils.isEmpty(idcardid)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("参数错误");
				return 0;
			}
			String key = idcardid + "schedule_pingAn_cookieStore";
			Object cookieObj = client.get(key);
			CookieStore cookieStore;
			if (cookieObj != null) {
				cookieStore = (CookieStore) cookieObj;
			} else {
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("会话过期,请重新刷新验证码");
				return 0;
			}
			PingAnUtil paUtil = new PingAnUtil();
			int result = paUtil.smsCodeForQueryApply(cookieStore,bean);
			client.set(key, cookieStore, Constants.TIME_HOUR);
			return result;
		} catch (Exception e) {
			ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.phone_code, errBean);
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("查询进度错误，请联系惠刷卡客服咨询");
			bean.setBusiJSON("fail");
			logger.error(bean.getIapplyid() + " smsCodeForQueryApply 异常", e);
		}
		return 0;
	}
	/**
     * 平安申卡进度查询短信验证码发送检测
     * @param bean
     * @return
     * @throws IOException
     */
	public static int apply_credit_sms(MaterialBean bean,CookieStore cookieStore,PingAnUtil paUtil,MemCachedClient client) {
		try {
			String imgCode = bean.getImgauthcode();// 图片验证码
			String idcardid = bean.getIdcardid();// 身份证
			if (StringUtils.isEmpty(imgCode) || StringUtils.isEmpty(idcardid)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("参数错误");				
				return 0;
			}
			String errMsg = paUtil.checkImgCodeOfProgress(cookieStore, imgCode);
			String key = idcardid + "schedule_pingAn_cookieStore";
			if (errMsg.equals("success")) {// 图片验证码正确
				int rest = paUtil.apply_credit_sms(cookieStore, bean,client);//检测是否需要输入短信验证码
				client.set(key, cookieStore);
				return rest;
			} else {
				if ("checkNo".equals(errMsg)) {
					errMsg = "请输入验证码";
				} else if ("checkNoTimeOut".equals(errMsg)) {
					errMsg = "验证码已失效，请重新输入";
				} else if ("checkNoError".equals(errMsg)) {
					errMsg = "验证码错误，请重新输入";
				} else {
					errMsg = "验证码错误，请重新输入";
				}
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc(errMsg);
				return 0;
			}

		} catch (Exception e) {
			ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.phone_code, errBean);
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("查询进度错误，请联系惠刷卡客服咨询");
			bean.setBusiJSON("fail");
			logger.error(bean.getIapplyid() + " apply_credit_sms 异常", e);
		}
		return 0;
	}
    
	/**
     * 平安办卡获取图片验证码前置
     * @param bean
     * @return
     * @throws IOException
     */
    public static BufferedImage getProgQueryImg(MaterialBean bean,MemCachedClient client){
    	BufferedImage localBufferedImage = null;
    	try{   		
            String idcardid = bean.getIdcardid();//身份证
            if(StringUtils.isEmpty(idcardid)){
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数错误");
                return localBufferedImage;
            }
            CookieStore cookieStore = new BasicCookieStore();
            String key = idcardid + "schedule_pingAn_cookieStore";
            PingAnUtil paUtil = new PingAnUtil();
        	localBufferedImage = paUtil.getImageRandOfProgress(cookieStore);
			client.set(key, cookieStore,Constants.TIME_HOUR);
    	}catch(Exception e){
    		ErrorRequestBean errBean = new ErrorRequestBean(bean, e.getMessage());
            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.img_code, errBean);
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("办卡进度查询,获取图片验证码错误");
			bean.setBusiJSON("fail");
			logger.error(bean.getIapplyid() + " apply_credit_sms 异常", e);
    	}    	
        return localBufferedImage;
    }
    /**
     * 根据传入的卡信息,从平安申卡进度查询结果中匹配信息
     */
    public static int applyQuerySelCardIndex(JSONArray resultList,String cardType){
    	String[] cardCodes = cardType.split("\\@")[0].split("\\|");
		String cardLogo = cardCodes[1];
		String mainCardface = cardCodes[3];
		String apply_data = cardType.split("\\@")[1].replaceAll("[^0-9]", "");//2014-04-11  					
		int cardIndex = -1;
		for(int i=0;i<resultList.length();i++){
			JSONObject cardResult = resultList.getJSONObject(i);				
			int logo = 0;
			if(!cardResult.isNull("logo")){
				logo =  Integer.valueOf(cardResult.get("logo").toString());				
			}
			String photoCardFlag = "";
			if(!cardResult.isNull("photoCardFlag")){
				photoCardFlag =  cardResult.get("photoCardFlag").toString();				
			}
			String applyDate = "";
			if(!cardResult.isNull("applyDate")){
				applyDate = cardResult.get("applyDate").toString().replaceAll("[^0-9]", "");    					
			}
			if(Integer.valueOf(cardLogo)==logo){
				if(mainCardface.equals(photoCardFlag)){
					if(apply_data.equals(applyDate)){
						cardIndex = i;
						break;
					}
					cardIndex = i;
				}
				cardIndex = i;
			}else{  					
				continue;
			}
		}
		return cardIndex;
    }
	/**
	 * 封装平安官网返回信息,获取申卡记录结果
	 * @param cardResult
	 * @return
	 */
	public static JSONObject getProcessRecord(JSONObject cardResult) {
		JSONObject restJobj = new JSONObject();
		StringBuilder sb = new StringBuilder("");
		int logo = 0;
		if (!cardResult.isNull("logo")) {
			logo = Integer.valueOf(cardResult.get("logo").toString());
		}
		String applyDate = "";
		if (!cardResult.isNull("applyDate")) {
			applyDate = cardResult.get("applyDate").toString()
					.replaceAll("[^0-9]", "");
		}
		if (!cardResult.isNull("cardFaceDes")) {
			sb.append("申请卡面:" + cardResult.get("cardFaceDes").toString());
		}
		if (!cardResult.isNull("cardName")) {
			sb.append(",申请卡类别:" + cardResult.get("cardName").toString());
		}
		sb.append(",申卡时间:" + applyDate);
		sb.append(";申卡结果:");
		int processState = 0;
		if (!cardResult.isNull("processState")) {
			processState = cardResult.getInt("processState");
		}
		if (logo == 121 || logo == 122 || logo == 123 || logo == 124
				|| logo == 125 || logo == 126 || logo == 110 || logo == 111
				|| logo == 112 || logo == 113 || logo == 114 || logo == 115
				|| logo == 116 || logo == 118 || logo == 119) {
			if (processState == 0) {
				sb.append("目前尚无您的资料，请在填写后第二天查询");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 1) {
				sb.append("您已填写了网络申请，但我行尚未收到您的实体资料");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 2) {
				sb.append("您申请的信用卡已经在审核中，请耐心等待，感谢您的支持");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 3) {
				sb.append("审核通过，我行会尽快为您寄送卡片，请耐心等待");
				restJobj.put("resultcode", 1);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 4) {
				sb.append("非常遗憾，您申请的信用卡未审核通过，感谢您的支持");
				restJobj.put("resultcode", 2);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 5) {
				sb.append("您的申请正在办理中，但需要补件信息，请致电客服热线95511-2咨询");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else {
				sb.append("您申请的信用卡已经在审核中，请耐心等待，感谢您的支持");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			}
		} else {
			if (processState == 1 || processState == 2) {
				sb.append("您好！已经收到您的信用卡申请表，敬请等待审批");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 0) {
				sb.append("目前尚无您的资料，请在填写后第二天查询");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 4) {
				sb.append("非常遗憾，您申请的信用卡未审核通过，感谢您对平安银行的支持");
				restJobj.put("resultcode", 2);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 5 || processState == 6) {
				sb.append("您好！您申请的信用卡正在审核中，敬请耐心等待");
				restJobj.put("resultcode", 0);
				restJobj.put("resultdesc", sb.toString());
			} else if (processState == 3) {
				String sendDate = "";// 寄卡时间
				String manufactureDate = "";// 制卡时间
				String decisionDate = "";// 审核通过时间
				String registerCompany = "";// 物流公司
				String registerNo = "";// 快递单号
				if (!cardResult.isNull("sendDate")) {
					sendDate = cardResult.get("sendDate").toString();
				}
				if (!cardResult.isNull("manufactureDate")) {
					manufactureDate = cardResult.get("manufactureDate")
							.toString();
				}
				if (!cardResult.isNull("decisionDate")) {
					decisionDate = cardResult.get("decisionDate").toString();
				}
				if (!cardResult.isNull("registerCompany")) {
					registerCompany = cardResult.get("registerCompany")
							.toString();
				}
				if (!cardResult.isNull("registerNo")) {
					registerNo = cardResult.get("registerNo").toString();
				}
				if (!StringUtils.isEmpty(sendDate)) {
					if (!StringUtils.isEmpty(registerCompany)) {// 已寄卡
						String year = sendDate.substring(0, 4) + "年";
						String month = sendDate.substring(5, 7) + "月";
						String day = sendDate.substring(8, 10) + "日";
						String dateStr = year + month + day;
						sb.append("恭喜您！您申请的信用卡已于" + dateStr + "通过快递寄出，敬请留意查收");
						sb.append(";查询物流:" + registerCompany + "快递单号:"
								+ registerNo);
					} else {// 寄卡中
						String year = manufactureDate.substring(0, 4) + "年";
						String month = manufactureDate.substring(5, 7) + "月";
						String day = manufactureDate.substring(8, 10) + "日";
						String dateStr = year + month + day;
						sb.append("恭喜您！您申请的信用卡已于" + dateStr + "完成制卡，敬请等待寄卡");
					}
				} else if (!StringUtils.isEmpty(manufactureDate)) {// 寄卡中
					String year = manufactureDate.substring(0, 4) + "年";
					String month = manufactureDate.substring(5, 7) + "月";
					String day = manufactureDate.substring(8, 10) + "日";
					String dateStr = year + month + day;
					sb.append("恭喜您！您申请的信用卡已于" + dateStr + "完成制卡，敬请等待寄卡");
				} else {// 正在制卡中
					sb.append("您好！您申请的信用卡正在制卡中，敬请耐心等待;审核通过时间:" + decisionDate);
				}
				restJobj.put("resultcode", 1);
				restJobj.put("resultdesc", sb.toString());
			}
		}
		return restJobj;
	}
}
