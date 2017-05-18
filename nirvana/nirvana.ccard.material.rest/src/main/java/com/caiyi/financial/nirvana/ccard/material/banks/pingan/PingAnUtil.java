package com.caiyi.financial.nirvana.ccard.material.banks.pingan;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.discount.Constants;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HttpClientHelper;
import com.hsk.cardUtil.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PingAnUtil {
	public static Logger logger = LoggerFactory.getLogger("PingAnUtil");
	private Map<String, String> headers;
	private HttpClientHelper hc = new HttpClientHelper();
	private String errHtml;
	private String errJsMsg;// 网页错误匹配信息
	private String cardInfo;
	//平安合作链接
	private String firstUrl = "https://c.pingan.com/apply/mobile/apply/index.html?scc=920000180&ccp=1a2a3a4a5a8a9a10a11a12a13&showt=0";
	public HttpClientHelper getHc() {
		return hc;
	}

	public void setHc(HttpClientHelper hc) {
		this.hc = hc;
	}

	public String transCardInfo(MaterialBean bean, PingAnBean pingAn, MemCachedClient client) {
		String errMsg = "";
        String cidcard = bean.getModel().getCidcard();
        String cphone = bean.getModel().getCphone();
        String cardKey = cidcard + cphone + "_pingAn_cardtype";
        Object obj = client.get(cardKey);
        logger.info("cardKey>>>>>>>>>>>>>>"+obj);
        if(obj==null){
        	errMsg = "cardKey会话过期";
			return errMsg;
        }
        String cardIdMsg = (String) obj;	   
		// 选卡信息
	    if(StringUtils.isEmpty(cardIdMsg)){
	    	errMsg = "申卡id为空";
			return errMsg;
	    }
		String[] cardCodes = cardIdMsg.split("\\|");// creditcardTypeName;cardLogo;cardOrganization;mainCardface;
		// mainCardName;creditcardCategory;creditcardType;affinityCode;currencyFlag
		for (int i = 0; i < cardCodes.length; i++) {// 1,608,0
			String cardMsg = cardCodes[i];
			if (i == 0) {
				pingAn.setCreditcardTypeName(cardMsg);
			}
			if (i == 1) {
				pingAn.setCardLogo(cardMsg);
			}
			if (i == 2) {
				pingAn.setCardOrganization(cardMsg);
			}
			if (i == 3) {
				pingAn.setMainCardface(cardMsg);
			}
			if (i == 4) {
				pingAn.setMainCardName(cardMsg);
			}
			if (i == 5) {
				pingAn.setCreditcardCategory(cardMsg);
			}
			if (i == 6) {
				pingAn.setCreditcardType(cardMsg);
			}
			if (i == 7) {
				pingAn.setAffinityCode(cardMsg);
			}
			if (i == 8) {
				pingAn.setCurrencyFlag(cardMsg);
			}
		}

		String credit_cardType = pingAn.getCreditcardTypeName();// 传入的卡类型值
		String incard_logo = pingAn.getCardLogo();// 传入的卡面logo
/*		if("3".equals(credit_cardType)||"5".equals(credit_cardType)){//车主卡 携程联名卡
			errMsg = "车主卡或携程联名卡暂时不能选,请选其他信用卡";
			return errMsg;
		}*/
		if (!StringUtils.isEmpty(credit_cardType)
				&& !StringUtils.isEmpty(incard_logo)) {
			if (!StringUtils.isEmpty(cardInfo)) {
				JSONObject infoJobj = new JSONObject(cardInfo);
				if (!infoJobj.isNull("cardInfo")) {
					JSONObject cardsJobj = infoJobj.getJSONObject("cardInfo");
					if(cardsJobj.has(credit_cardType)){
						JSONObject typeJobj = cardsJobj
								.getJSONObject(credit_cardType);
						if(!typeJobj.isNull("cardOrgList")){
							JSONArray cardOrgList = typeJobj
									.getJSONArray("cardOrgList");
							loop: for (int i = 0; i < cardOrgList.length(); i++) {
								String cardOrganization = String.valueOf(i);
								/*if (StringUtils.isEmpty(pingAn.getCardOrganization())) {
									pingAn.setCardOrganization(cardOrganization);
								}*/						
								JSONObject cardOrg = cardOrgList.getJSONObject(i);
								String typeFlag = "";
								String remark1 = "";
								String remark2 = "";
								String cardLogo = "";
								String face = "";
								String descripName = "";
								String sortFlag = "";
								if (!cardOrg.isNull("cardFaceList")) {
									JSONArray cardFaceList = cardOrg
											.getJSONArray("cardFaceList");
									for (int j = 0; j < cardFaceList.length(); j++) {
										JSONObject cardFace = cardFaceList
												.getJSONObject(j);
										if (!cardFace.isNull("cardLogo")) {
											cardLogo = cardFace.get("cardLogo")
													.toString();
										}
										if (incard_logo.equals(cardLogo)) {
											pingAn.setCardOrganization(cardOrganization);
											if (!cardOrg.isNull("typeFlag")) {
												typeFlag = cardOrg.get("typeFlag").toString();
												pingAn.setCreditcardType(typeFlag);
											}									
											if (!cardOrg.isNull("remark1")) {
												remark1 = cardOrg.get("remark1").toString();
												pingAn.setCurrencyFlag(remark1);
											}									
											if (!cardOrg.isNull("remark2")) {
												remark2 = cardOrg.get("remark2").toString();
												pingAn.setAffinityCode(remark2);
											}
											String inface = pingAn.getMainCardface();
											if (!cardFace.isNull("face")) {
												face = cardFace.get("face").toString();
												if (face.equals(inface)) {
													if (!cardFace.isNull("descripName")) {
														descripName = cardFace.get(
																"descripName")
																.toString();
														pingAn.setMainCardName(descripName);
													}
													if (!cardFace.isNull("sortFlag")) {
														sortFlag = cardFace.get(
																"sortFlag").toString();
														pingAn.setCreditcardCategory(sortFlag);
													}
													break loop;
												}
											}
										}
									}
								}
							}
						}	
					}									 									
				}
			}
		}
		return errMsg;
	}
	/**
	 * 住宅，公司地址信息转换
	 * @param bean
	 * @param pingAn
	 * @return
	 */
	public String transLiveInfo(MaterialBean bean, PingAnBean pingAn,MemCachedClient client) {
		String errMsg = "";
        String cidcard = bean.getModel().getCidcard();
        String cphone = bean.getModel().getCphone();
        String city_key = cidcard + cphone + "_pingAn_citydata";
        String id_addressKey = cidcard + cphone + "_pingan_idAddress";
        Object obj = client.get(city_key);
        Object fzjgObj = client.get(id_addressKey);
	    logger.info("city_key>>>>>>>>>>>>>>"+obj);
	    logger.info("id_addressKey>>>>>>>>>>>>"+fzjgObj);
        if(obj==null){
        	errMsg = "会话已过期";
			return errMsg;
        }
        if(fzjgObj==null){
        	errMsg += "会话已过期";       	
        	return errMsg;
        }       
	    String city_data = (String) obj;
	    //发证机关
	    String fzjg_data = (String) fzjgObj;	    	  
	    if(StringUtils.isEmpty(city_data)){
	    	errMsg = "地址信息为空";
			return errMsg;
	    }
	    if(StringUtils.isEmpty(fzjg_data)){
	    	errMsg = "身份证件信息为空";
			return errMsg;
	    }
	    pingAn.setFzjg(fzjg_data);
	    String[] citys = city_data.split("\\@");
	    //住宅地址
	    String home_cityStr = citys[0];
	     
	    String[] home_citys = home_cityStr.split("\\,");	
	    if(home_citys.length==6){
	    	pingAn.setIhome_pid(home_citys[0]);
	    	pingAn.setChome_pname(home_citys[1]);
	    	String home_cityid = home_citys[2];
		    if(!StringUtils.isEmpty(home_cityid)){
		    	String[] home_cids = home_cityid.split("\\|");
		    	if(home_cids.length==2){
		    	    pingAn.setIhome_cid(home_cids[1]);
		    	}else{
		    		logger.info("PingAnUtil.transLiveInfo>>>>>>住宅城市对应银行id参数有误");
		    		errMsg = "住宅地址参数有误,请确认!";
		    		return errMsg;
		    	}
		    }
	 	    pingAn.setChome_cname(home_citys[3].replaceAll("市辖区", ""));
	 	    String home_disid = home_citys[4];
	 	    if(!StringUtils.isEmpty(home_disid)){
		    	String[] home_dids = home_disid.split("\\|");
		    	if(home_dids.length==2){
		    	    pingAn.setIhome_did(home_dids[1]);
		    	}else{
		    		logger.info("PingAnUtil.transLiveInfo>>>>>>住宅城市县区对应银行id参数有误");
		    	}
		    }
	 	    pingAn.setChome_dname(home_citys[5]);	 	    
	    }else{
	    	errMsg = "住宅地址参数有误,请确认!";
    		return errMsg;
	    }
	    
	    //公司地址
	    String company_cityStr = citys[1];	 
	    String[] company_citys = company_cityStr.split("\\,");
	    if(company_citys.length==6){
	    	pingAn.setIcompany_pid(company_citys[0]);
	    	pingAn.setCcompany_pname(company_citys[1]);
	    	String company_cityid = company_citys[2];
		    if(!StringUtils.isEmpty(company_cityid)){
		    	String[] company_cids = company_cityid.split("\\|");
		    	if(company_cids.length==2){
		    	    pingAn.setIcompany_cid(company_cids[1]);
		    	}else{
		    		logger.info("PingAnUtil.transLiveInfo>>>>>>住宅城市对应银行id参数有误");
		    		errMsg = "住宅地址参数有误,请确认!";
		    		return errMsg;
		    	}
		    }
	 	    pingAn.setCcompany_cname(company_citys[3].replaceAll("市辖区", ""));
	 	    String company_disid = company_citys[4];
	 	    if(!StringUtils.isEmpty(company_disid)){
		    	String[] company_dids = company_disid.split("\\|");
		    	if(company_dids.length==2){
		    	    pingAn.setIcompany_did(company_dids[1]);
		    	}else{
		    		logger.info("PingAnUtil.transLiveInfo>>>>>>住宅城市县区对应银行id参数有误");
		    	}
		    }
	 	    pingAn.setCcompany_dname(company_citys[5]);	 	    
	    }else{
	    	errMsg = "住宅地址参数有误,请确认!";
    		return errMsg;
	    }
	    //申卡地址
	    String apply_cityStr = citys[2].replaceAll("市辖区", "");
	    pingAn.setCity(apply_cityStr);	
		return errMsg;
	}

	/**
     * 用户信息转为对应的平安办卡信息。
     * @param bean
     * @return
     */
    public String transferMsg(MaterialBean bean,PingAnBean pingAn){ 
    	
   	    /**
         * 基本信息
         */
		MaterialModel model = bean.getModel();
		String errMsg = "";
        pingAn.setUname(model.getCname());//姓名      
        pingAn.setPingyin(model.getCenglishname());//姓名拼音      
        pingAn.setIdCardNo(model.getCidcard());//证件号        
        pingAn.setBegin_date(pingAn.getIdCardNo().substring(6, 14));//出生日期     
        String cidexpirationtime = model.getCidexpirationtime();//起始时间,终止时间
        if(cidexpirationtime.contains("-1")){//证件有效期
        	pingAn.setCidexpirationtime("20991231");
        }else{
            pingAn.setCidexpirationtime(cidexpirationtime.split("\\,")[1]);//身份证有效日期    
        }
        pingAn.setMobileNo(model.getCphone());//手机号        
        if ("1".equals(model.getIsex())){//性别
        	pingAn.setSex("M");
        }else{
        	pingAn.setSex("F");
        }
        
       /**
         * 个人信息
         */
        pingAn.setEmail(model.getCemail());//邮箱       
        String maritalstatus = model.getMaritalstatus();
        String marital = "";//1、未婚 2、已婚 3、其它
        switch (maritalstatus) {
		case "1":
			marital = "未婚";
			break;
		case "2":
			marital = "已婚";
			break;
		case "3":
			marital = "其他";
			break;
		default:
			break;
		}       
        pingAn.setMarital_status(marital);//婚姻状况
        String idegree = model.getIdegree();//1、博士及以上 2、硕士 3、本科 4、大专 5、高中、中专一下 ;本地库
        String education = "";//教育状况  02：博士及以上 03:硕士 04:本科 05:大专 07:高中及中专 08:初中及以下 ;平安银行
        switch (idegree) {
		case "1":
			education = "博士及以上";
			break;
		case "2":
			education = "硕士";
			break;
		case "3":
			education = "本科";
			break;
		case "4":
			education = "大专";
			break;
		case "5":
			education = "高中及中专";
			break;
		default:
			break;
		}
        pingAn.setEducation(education);//教育程度
        pingAn.setEmer_contact(model.getFamilyname());//亲属姓名
        pingAn.setEmer_phone(model.getCfamilyphonenum());//紧急联系人电话
        String icemergencyties = model.getIfamilyties();//亲属关系1.配偶 2.父母 3.子女 4.兄弟姐妹
        String emergencyties = "";//紧急联系人关系 1:配偶 2:父母 3:子女 4:亲戚 5:朋友 6:同学 7:同事 9:其他
        switch (icemergencyties) {
		case "1":
			emergencyties = "配偶";
			break;
		case "2":
			emergencyties = "父母";
			break;
		case "3":
			emergencyties = "子女";
			break;
		case "4":
			emergencyties = "亲戚";
			break;
		case "5":
			emergencyties = "朋友";
			break;
		default:
			emergencyties = "其他";
			break;
		}
        pingAn.setGuardian_relat(emergencyties);
        /**
         * 工作信息
         */
        pingAn.setCompany(model.getCcompanyname());//单位名
        pingAn.setDepartment(model.getCdepartmentname());//部门
        String idepartment = model.getIdepartment();//1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级 
        String position = "";//职务
        switch (idepartment) {
		case "1":
			position = "一般员工";
			break;
		case "2":
			position = "部门经理";
			break;
		case "3":
			position = "总经理";
			break;
		case "4":
			position = "主管 ";
			break;
		default:
			position = "一般员工";
			break;
		}
        pingAn.setPosition(position);
        pingAn.setWorkage(model.getItimeinjob());//工作年限      
        pingAn.setJzip_code(model.getCcompany_postcode());//公司邮编
        pingAn.setCompany_addr(model.getCcompany_detailaddress());//公司详细地址

        //电话  公司电话和住宅电话必填一个
        String company_number = model.getCcompany_telnum();//公司电话 区号-电话号-分机号
        String home_number = model.getChome_telnum();//住宅电话 区号-电话号
        if(!StringUtils.isEmpty(company_number) && !company_number.equals("0")){
        	String[] company_numbers = company_number.split("\\-");
        	if(company_numbers.length==3){
        		pingAn.setCompany_pzone(company_numbers[0]);//公司电话区号
        		pingAn.setCompany_phone(company_numbers[1]);//电话号
                pingAn.setCompany_pextension(company_numbers[2]);//分机号
        	}else if(company_numbers.length==2){
        		pingAn.setCompany_pzone(company_numbers[0]);//公司电话区号
        		pingAn.setCompany_phone(company_numbers[1]);//电话号
        	}else{
        		errMsg = "公司电话格式不正确";
        	}
        }
        if(!StringUtils.isEmpty(home_number) && !home_number.equals("0")){
        	String[] home_numbers = home_number.split("\\-");
			if(home_numbers.length>=2){
				pingAn.setHome_pzone(home_numbers[0]);
				pingAn.setHome_phone(home_numbers[1]);
				errMsg = "";
			}else{
				errMsg = "住宅电话格式不正确";
			}
        }

        /**
         * 居住信息
         */
        pingAn.setHome_addr(model.getChome_detailaddress());//家庭详细地址
        pingAn.setHome_acode(model.getChome_postcode());//住宅邮编
        String residence = model.getResidencestatus();//1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它
        String home_status;
        switch (residence) {
		case "1":
			home_status = "自购有贷款";
			break;
		case "2":
		    home_status = "自购无贷款";
		    break;
		case "3":
		    home_status = "租用";
		    break;
		case "4":
		    home_status = "亲属住房";
		    break;
		default:
			home_status = "其他";
			break;
		}
        if(residence.equals("1")){
        	if(StringUtils.isEmpty(model.getCmonthlypayment())){
        		errMsg = errMsg+";月还贷金额不能为空";
        	}
            pingAn.setMonth_pay(model.getCmonthlypayment());
        }
        pingAn.setHome_statu(home_status);
        pingAn.setLive_year(model.getIlivelen());
        
        /**
         * 补充信息
         */
        String jk_flag = model.getIpostaddress();//数据库中寄卡地址标识1、单位地址  2、住宅地址
        if("1".equals(jk_flag)){//官网寄卡标识 1:住宅地址 2:单位地址
        	pingAn.setJk_addr("2");
        }else{
        	pingAn.setJk_addr("1");
        }
        return errMsg;
    }

    //平安申卡方法
    public String applyPingAnBank(MaterialBean bean,PingAnBean pingAn,CookieStore cookieStore,String optRand,MemCachedClient client){
    	hc.setBasicCookieStore(cookieStore);
    	//获取信用卡信息
	    String url = "https://c.pingan.com/apply/mobile/apply/data/cardInfo.json";
	    headers = getBasicHeader();
	    headers.put("Accept", "application/json");
	    headers.put("Referer", "https://c.pingan.com/apply/mobile/apply/index.html?scc=920000180&ccp=1a2a3a4a5a8a9a10a11a12a13&showt=0");
		HttpResult hr = hc.get(url, headers);
		cardInfo = hr.getHtml();
		String cphone = bean.getModel().getCphone();
        String errMsg = transCardInfo(bean,pingAn,client);//卡信息转换
		if(!StringUtils.isEmpty(errMsg)){
        	return errMsg;
        }
		errMsg = transLiveInfo(bean,pingAn,client);//住宅，公司地址信息转换
		if(!StringUtils.isEmpty(errMsg)){
        	return errMsg;
        }
		//参数检测
		errMsg = checkMaterial(bean,pingAn);
		if(!StringUtils.isEmpty(errMsg)){
			return errMsg;
		}
        pingAn.setOtpRand(optRand);
        //申卡首页
        try {			
	        //下一步
	        errMsg = applyNext(pingAn,optRand);	        
	        if(!StringUtils.isEmpty(errMsg)){
	        	ErrorRequestBean errBean = new ErrorRequestBean(pingAn, "申卡第一步提交资料失败:"+errMsg);
	        	errBean.setCerrordesc("申卡资料第一步验证不通过");
                errBean.setIerrortype(1);
                errBean.setCphone(cphone);
                errBean.setUrl(url);
	            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.submit_apply, errBean);
	        	return errMsg;
	        }
	        //最后提交
	        errMsg = applySubmit(pingAn);
	        if(!StringUtils.isEmpty(errMsg)){
	        	ErrorRequestBean errBean = new ErrorRequestBean(pingAn, "申卡最后一步失败:"+errMsg);
        		errBean.setCerrordesc("申卡资料最后一步验证不通过");
                errBean.setIerrortype(2);
                errBean.setCphone(cphone);
                errBean.setUrl(url);
	            BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.submit_apply, errBean);
	        	return errMsg;
	        } 
		} catch (Exception e) {
			logger.info("PingAnUtil.applyPingAnBank exception>>>>>【"+errHtml+"】",e);
			ErrorRequestBean errBean = new ErrorRequestBean(pingAn, "申卡异常:"+e.getMessage());
			errBean.setCerrordesc("申卡异常");
            errBean.setIerrortype(0);
            errBean.setCphone(cphone);
			BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.submit_apply, errBean);
			return "申卡出现问题,请稍候.....";
		}
        return errMsg;
    }
    //申卡首页
	public String applyFirst(PingAnBean pingAn,CookieStore cookieStore){
		try{
			String scc = PingAnBean.scc;
			String ccp = PingAnBean.ccp;
			String sccId = "";
			String errMsg = "";
			// 请求办卡链接
			headers = getBasicHeader();		
			hc.setBasicCookieStore(cookieStore);
			HttpResult hr = hc.get(firstUrl, headers);
			errHtml = hr.getHtml("GBK");
			// 进入首页
			String url = "https://c.pingan.com/apply/mobile/apply/index.html?scc="+scc+"&ccp="+ccp+"&showt=0";
			headers.put("Referer", firstUrl);
			hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");
			// 获取办卡合作标识序列
			url = "https://c.pingan.com/apply/txtloginw/changeSCC.do?scc="+scc+"&ccp="+ccp+"&showt=0";
			headers.put("Accept", "application/json");
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			headers.put(
					"Referer",
					"https://c.pingan.com/apply/mobile/apply/index.html?scc="+scc+"&ccp="+ccp+"&showt=0");
			hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");
			if (errHtml.contains("ret_message")) {
				JSONObject sccJObj = new JSONObject(errHtml);
				if (!sccJObj.isNull("scc")) {
					scc = sccJObj.get("scc").toString();
				}
				if (!sccJObj.isNull("ccp")) {
					ccp = sccJObj.get("ccp").toString();
				}
			}

			url = "https://c.pingan.com/apply/tianxiatong/restApplyCardInit.do";
			hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");
			if(errHtml.contains("ret_message")){
				JSONObject restJobj = new JSONObject(errHtml);
				if (!restJobj.isNull("scc")) {
					scc = restJobj.get("scc").toString();
				}
				if (!restJobj.isNull("sccId")) {
					sccId = restJobj.get("sccId").toString();
				}
			}			
			logger.info("applyFirst>>>>sccId=="+sccId);
			pingAn.setSccId(sccId);
			url = "https://c.pingan.com/apply/mobile/apply/partials/mainPage.html";
			hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");
			url = "https://c.pingan.com/apply/mobile/apply/partials/baseInfo.html";
			hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");
			return errMsg;
		}catch(Exception e){
			logger.error("PingAnUtil.applyFirst error"+"【"+errHtml+"】",e);	
			return "网络错误,请稍后在试";
		}	       
	}
	//平安申卡下一步
	public String applyNext(PingAnBean pingAn,String otpRand) throws Exception{
        
	    logger.info("applyNext>>>>sccId=="+pingAn.getSccId());
		String uname = pingAn.getUname();
		String pingyin = pingAn.getPingyin().replaceAll("\\s*", "");
		// 获取姓名拼音
		String url = "https://c.pingan.com/apply/tianxiatong/getPinyin.do?xm="
				+ URLEncoder.encode(uname, "utf-8");
		headers.put("Accept", "*/*");
		HttpResult hr = hc.get(url, headers);
		errHtml = hr.getHtml("GBK");
		JSONObject pyJObj = new JSONObject(errHtml);
		String pa_pingyin = "";
		if (!pyJObj.isNull("pinyin")) {
			pa_pingyin = pyJObj.get("pinyin").toString();
		}
		if(pa_pingyin.contains(",")){
			pingAn.setPingyin(pingyin);
		}else{
			pingAn.setPingyin(pa_pingyin);
		}
		pingyin = pingAn.getPingyin().toUpperCase();
		String sex = pingAn.getSex();
		String begin_date = pingAn.getBegin_date().replace("-", "").trim();
		String begin_year = begin_date.substring(0,4);
		String begin_month = begin_date.substring(4,6);
		String begin_day = begin_date.substring(6);
		String fzjg = pingAn.getFzjg();
		String idCardNo = pingAn.getIdCardNo();
		String mobileNo = pingAn.getMobileNo();
		String apply_city = pingAn.getCity();
		String scc = PingAnBean.scc;
		String sccId = pingAn.getSccId();
		String creditcardTypeName = pingAn.getCreditcardTypeName();		
		String cardLogo = pingAn.getCardLogo();
		String cardOrganization = pingAn.getCardOrganization();
		String mainCardface = pingAn.getMainCardface();		
		String cidexpirationtime = pingAn.getCidexpirationtime();
	    logger.info("pingAnUtil.applyNext>>>>申卡下一步参数====="+pingAn.toString());
		// 下一步
		url = "https://c.pingan.com/apply/tianxiatong/restApplyCardInput.do";
		Map<String, String> data = new HashMap<String, String>();						
		data.put("mainEnglishName", pingyin);	
		data.put("mainChineseName", uname);// 姓名
		data.put("mainGender", sex);// 性别
		data.put("mainBirthdayYear", begin_year);
		data.put("mainBirthdayMonth", begin_month);
		data.put("mainBirthdayDay", begin_day);
		data.put("nationality", "中国");// 国家
		data.put("nationalityCode", "156");// 国家编码
		data.put("issuingAuthority", fzjg);// 身份证发证机关
		data.put("mainCertificationNo", idCardNo);// 身份证号
		data.put("mainCertificationType", "1");// 证件类型
		data.put("mainMobilePhoneNo", mobileNo);	
		data.put("giveCardCity", apply_city);// 所在城市
		data.put("relationShipMgm", "0");
		data.put("mainCardface", mainCardface);//卡面
		data.put("cardLogo", cardLogo);// 信用卡logo标识	
		data.put("certificationExpiryDate",cidexpirationtime);// 身份有效期
		data.put("cardOrganization", cardOrganization);// 卡组织
		data.put("creditcardTypeName", creditcardTypeName);//卡种类
		if("9".equals(creditcardTypeName)){//平安由你卡
			data.put("cardCatenaNo", "01");//卡系列编号
			data.put("cardFaceNo", "01");//卡面编号
			data.put("atCardUrl", "tuijian101.jpg");//卡面图片
		}else{
			data.put("cardCatenaNo", "");
			data.put("cardFaceNo", "");
		}	
		data.put("certificateType", "1");
		data.put("mobileNo", mobileNo);
		data.put("idNo", idCardNo);
		data.put("otp", otpRand);
		data.put("recommendChineseName", "");
		data.put("recommendPhone", "");
		data.put("scc", scc);
		data.put("sccFirstTwo", scc.substring(0, 2));
		data.put("suTxnSeqId", sccId);
		data.put("ifIsMGMPath", "");
		data.put("ifIsSCCPath", "");
				
		headers = getBasicHeader();
		headers.put(
				"Referer",
				"https://c.pingan.com/apply/mobile/apply/index.html?scc="+PingAnBean.scc+"&ccp="+PingAnBean.ccp+"&showt=0");
		headers.put("Accept", "application/json");
		hr = hc.post(url, data, headers);
		errHtml = hr.getHtml();

		JSONObject restObj = new JSONObject(errHtml);		
		String ret_message = "";
		if (!restObj.isNull("ret_message")) {
			ret_message = restObj.get("ret_message").toString();
		}
		//验证错误js
	    headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");	    
	    url = "https://c.pingan.com/apply/mobile/apply/scripts/views/errorinfo.js";
	    hr = hc.get(url,headers);
	    errHtml = hr.getHtml();	 
	    errJsMsg = errHtml;
	    String errMsg = "";
        if(!StringUtils.isEmpty(ret_message) && !ret_message.equalsIgnoreCase("success")){
        	if(errHtml.contains(ret_message)){
        		String errSub = errHtml.substring(errHtml.indexOf(ret_message));
        		errSub = errSub.substring(0,errSub.indexOf(","));
        		errMsg = errSub.split(":")[1].replace("'", "");
        	}else{
        		errMsg = ret_message;
        	}	    	
	    }       
        return errMsg;
	}
	private String applySubmit(PingAnBean pingAn) throws Exception{
		headers = getBasicHeader();
		//个人信息
		String url = "https://c.pingan.com/apply/mobile/apply/partials/persInfo.html";
		headers.put("Referer","https://c.pingan.com/apply/mobile/apply/index.html?scc="+PingAnBean.scc+"&ccp="+PingAnBean.ccp+"&showt=0");
	    headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
	    HttpResult hr = hc.get(url, headers);
	    errHtml = hr.getHtml();
	    Document personDoc = Jsoup.parse(errHtml);
	    //婚姻状况
	    String marriageStr = pingAn.getMarital_status();
	    Element marriageState = personDoc.getElementById("marriageState");
	    Elements marriageEles = marriageState.children();
	    String imarital_status = getSelState(marriageEles,marriageStr);	
	    pingAn.setImarital_status(imarital_status);
	    //教育状况
	    String education = pingAn.getEducation();
	    Elements educationEles = personDoc.getElementById("educationState").children();
	    String ieducation = getSelState(educationEles, education);
	    pingAn.setIeducation(ieducation);
	    //紧急联系人关系
	    String guardian_relation = pingAn.getGuardian_relat();
	    Elements guardianEles = personDoc.getElementById("guardianRelation").children();
	    String iguardian_relat = getSelState(guardianEles, guardian_relation);
	    pingAn.setIguardian_relat(iguardian_relat);
	    int relationShip1 = Integer.valueOf(iguardian_relat);	    
	    int nonRelationShip = 0;
	    if(relationShip1>3&&relationShip1<9){
            nonRelationShip=relationShip1-3;
        } else if(relationShip1>0&&relationShip1<4){
        	iguardian_relat=String.valueOf(relationShip1);
        } else {
            nonRelationShip=9;
        }
	    //工作信息
	    url = "https://c.pingan.com/apply/mobile/apply/partials/workInfo.html";
	    hr = hc.get(url, headers);
	    errHtml = hr.getHtml();
	    //居住信息
	    url = "https://c.pingan.com/apply/mobile/apply/partials/liveInfo.html";
	    hr = hc.get(url, headers);
	    errHtml = hr.getHtml();
	    Document liveDoc = Jsoup.parse(errHtml);
	    String home_state = pingAn.getHome_statu();
	    Elements houseEles = liveDoc.getElementById("houseState").children();
	    String ihome_state = getSelState(houseEles, home_state);//房产状况
	    pingAn.setIhome_statu(ihome_state);
	    //补充信息
	    url = "https://c.pingan.com/apply/mobile/apply/partials/additionInfo.html";
	    hr = hc.get(url, headers);
	    errHtml = hr.getHtml();
	    logger.info("pingAnUtil.applySubmit>>>>申卡补充信息====="+errHtml);
	    //提交信息
	    url = "https://c.pingan.com/apply/mobile/apply/partials/submit.html";
	    hr = hc.get(url, headers);
	    errHtml = hr.getHtml();
	    
	    String icompany_pid = pingAn.getIcompany_pid();
	    String icompany_cid = pingAn.getIcompany_cid();
	    String icompany_did = pingAn.getIcompany_did();	 
	    String cpostCode = "";//工作邮编
	    if(StringUtils.isNumeric(icompany_pid) && StringUtils.isNumeric(icompany_cid) && StringUtils.isNumeric(icompany_did)){
	    	//获取工作邮编
		    headers.put("Accept", "application/json");
		    url = "https://c.pingan.com/apply/newpublic/getPostCode.do?pCode="+icompany_pid+"&cCode="+icompany_cid+"&dCode"+icompany_did;
		    hr = hc.get(url, headers);
		    errHtml = hr.getHtml();
		    if(errHtml.contains("postCode")){
		    	JSONObject postJson = new JSONObject(errHtml);		
				if (!postJson.isNull("postCode")) {
					cpostCode = postJson.get("postCode").toString();
				}
		    }
	    }
	    if(!StringUtils.isEmpty(cpostCode)){
			pingAn.setJzip_code(cpostCode);
		}
	    //获取住宅邮编
	    String ihome_pid = pingAn.getIhome_pid();
	    String ihome_cid = pingAn.getIhome_cid();
	    String ihome_did = pingAn.getIhome_did();	
	    String hpostCode = "";//住宅邮编
	    if(StringUtils.isNumeric(ihome_pid) && StringUtils.isNumeric(ihome_cid) && StringUtils.isNumeric(ihome_did)){
	    	url = "https://c.pingan.com/apply/newpublic/getPostCode.do?pCode="+ihome_pid+"&cCode="+ihome_cid+"&dCode"+ihome_did;
	 	    hr = hc.get(url, headers);
	 	    errHtml = hr.getHtml();
	 	    if(errHtml.contains("postCode")){
	 	    	JSONObject postJson = new JSONObject(errHtml);			 			
	 			if (!postJson.isNull("postCode")) {
	 				hpostCode = postJson.get("postCode").toString();
	 			}
	 	    }
	    }	   
	    if(!StringUtils.isEmpty(hpostCode)){
			pingAn.setHome_acode(cpostCode);
		}
	    logger.info("pingAnUtil.applySubmit>>>>申卡参数====="+pingAn.toString());
	    String card_logo= pingAn.getCardLogo();//卡标识
	    String card_org = pingAn.getCardOrganization();//卡组织
	    String card_type = pingAn.getCreditcardTypeName();//卡类型
	    String card_face = pingAn.getMainCardface();//卡面
	    String pingyin = pingAn.getPingyin().toUpperCase();
	    String uname = pingAn.getUname();
	    String sex = pingAn.getSex();
		String birth_date = pingAn.getBegin_date().replace("-", "").trim();
		String birth_year = birth_date.substring(0,4);
		String birth_month = birth_date.substring(4,6);
		String birth_day = birth_date.substring(6);
		String fzjg = pingAn.getFzjg();
		String idCardNo = pingAn.getIdCardNo();
		String mobileNo = pingAn.getMobileNo();
		String apply_city = pingAn.getCity();
		String cidexpirationtime = pingAn.getCidexpirationtime();
		String email = pingAn.getEmail();
		String linealRelativeName = pingAn.getEmer_contact();
		String linealRelativeMp = pingAn.getEmer_phone();
		String nonSuppleRelativeName = pingAn.getEmer_contact();
		String nonSuppleRelativeMp = pingAn.getEmer_phone();
		String mainCompanyName = pingAn.getCompany();
		String jobDepartMent = pingAn.getDepartment();
		String jobPosition = pingAn.getPosition();
		String optRand = pingAn.getOtpRand();//短信验证码
		String presentServiceYears = pingAn.getWorkage();
		String mainCompanyAddress10 = pingAn.getCcompany_pname();//**省
		String mainCompanyAddress11 = pingAn.getCcompany_cname();//**市
		String mainCompanyAddress2 = pingAn.getCcompany_dname();//**区,县
		String mainCompanyAddress3 = pingAn.getCompany_addr();//公司详细地址
		String mainCompanyCityCode = pingAn.getIcompany_cid();//工作城市sCode
		String mainCompanyZipCode = pingAn.getJzip_code();//公司邮编		
		String mainCompanyPhoneZone = pingAn.getCompany_pzone();//公司区号
	    String mainCompanyPhoneNo = pingAn.getCompany_phone();//公司电话
	    String mainCompanyPhoneExtension = pingAn.getCompany_pextension();//公司分机号			
		
		String mainHomeAddress10 = pingAn.getChome_pname();//住宅省份
		String mainHomeAddress11 = pingAn.getChome_cname();//住宅城市
		String mainHomeAddress2 = pingAn.getChome_dname();//住宅区县
		String mainHomeAddress3 = pingAn.getHome_addr();//住宅详细地址
		String mainHomePhoneZone = pingAn.getHome_pzone();//住宅区号
		String mainHomePhoneNo = pingAn.getHome_phone();//住宅电话
		String resideYears = pingAn.getLive_year();//居住年限
		String mainHomeZipCode = pingAn.getHome_acode();//住宅邮编
		String monthlyMortgagePayment = pingAn.getMonth_pay();//月还贷额
		String mainHomeCityCode = pingAn.getIhome_cid();//住宅城市code
		String mainBillingAddressType = pingAn.getJk_addr();//寄卡地址   住宅地址:1; 单位地址：2
		String mainBillingPostType = pingAn.getBillingPostType();
		String autoPayOff = pingAn.getAutoPayOff(); //默认不自动还款
	    String paymentLimitType = pingAn.getPaymentLimitType();//默认全额还款
	    String exchangeFlag = pingAn.getExchangeFlag();//默认全额购汇
	    String bankAccount1 = pingAn.getBankAccount1();//自扣关联帐号
	    String tradePassedFlag = pingAn.getTradePassedFlag();//N默认仅使用签名确认交易 Y使用密码确认交易
	    String logFlag = pingAn.getLogFlag();
	    String mainCardName = pingAn.getMainCardface();//descripName
	    String creditcardCategory = pingAn.getCreditcardCategory();//sortFlag
	    String creditcardType = pingAn.getCreditcardType();
	    String affinityCode = pingAn.getAffinityCode();
	    String currencyFlag = pingAn.getCurrencyFlag();
	    String scc = pingAn.scc;
	    String suTxnSeqId = pingAn.getSccId();
	    String sccFirstTwo = scc.substring(0,2);
	    Map<String,String> datas = new HashMap<String,String>();
	    datas.put("cardFace", card_face);
	    datas.put("cardLogo", card_logo);
	    datas.put("holdCardFlag", card_type);
	    datas.put("mainEnglishName", pingyin);
	    datas.put("mainChineseName", uname);
	    datas.put("mainGender", sex);
	    datas.put("mainBirthdayYear", birth_year);
	    datas.put("mainBirthdayMonth", birth_month);
	    datas.put("mainBirthdayDay", birth_day);
	    datas.put("nationality", "中国");
	    datas.put("nationalityCode", "156");
	    datas.put("issuingAuthority", fzjg);
	    datas.put("mainCertificationNo", idCardNo);
	    datas.put("mainCertificationType", "1");
	    datas.put("mainMobilePhoneNo", mobileNo);
	    datas.put("giveCardCity", apply_city);
	    datas.put("relationShipMgm", "0");
	    datas.put("mainCardface", card_face);
	   
	    datas.put("certificationExpiryDate", cidexpirationtime);
	    datas.put("cardOrganization", card_org);
	    datas.put("creditcardTypeName", card_type);
	    datas.put("mobileNo", mobileNo);
	    datas.put("idNo", idCardNo);
	    datas.put("otp", optRand);
	    //datas.put("fingerprintID", value);
	    datas.put("recommendChineseName", "");
	    datas.put("recommendPhone", "");
	    datas.put("email", email);
	    datas.put("mainMaritalStatus", imarital_status);
	    datas.put("educationDegree", ieducation);
	    datas.put("linealRelativeName", linealRelativeName);
	    datas.put("linealRelativeMp", linealRelativeMp);
	    datas.put("nonSuppleRelativeName", nonSuppleRelativeName);
	    datas.put("nonSuppleRelativeMp", nonSuppleRelativeMp);
	    datas.put("relationShip1", iguardian_relat);
	    if(nonRelationShip!=0){
	    	datas.put("nonRelationShip", String.valueOf(nonRelationShip));
	    }
	    datas.put("mainCompanyName", mainCompanyName);
	    datas.put("jobDepartMent", jobDepartMent);
	    datas.put("jobPosition", jobPosition);
	    datas.put("presentServiceYears", presentServiceYears);
	    datas.put("mainCompanyAddress10", mainCompanyAddress10);
	    datas.put("mainCompanyAddress11", mainCompanyAddress11);
	    datas.put("mainCompanyAddress2", mainCompanyAddress2);
	    datas.put("mainCompanyAddress3", mainCompanyAddress3);
	    datas.put("mainCompanyZipCode", mainCompanyZipCode);
	    datas.put("mainCompanyPhoneZone", mainCompanyPhoneZone);
	    datas.put("mainCompanyPhoneNo", mainCompanyPhoneNo);
	    datas.put("mainCompanyCityCode", mainCompanyCityCode);
	    datas.put("mainCompanyPhoneExtension", mainCompanyPhoneExtension);
	    datas.put("mainHomeAddress10", mainHomeAddress10);
	    datas.put("mainHomeAddress11", mainHomeAddress11);
	    datas.put("mainHomeAddress2", mainHomeAddress2);
	    datas.put("mainHomeAddress3", mainHomeAddress3);
	    datas.put("mainHomePhoneZone", mainHomePhoneZone);
	    datas.put("mainHomePhoneNo", mainHomePhoneNo);
	    datas.put("resideYears", resideYears);
	    datas.put("mainHomeZipCode", mainHomeZipCode);
	    datas.put("mainResidentialType", ihome_state);
	    datas.put("monthlyMortgagePayment", monthlyMortgagePayment);
	    datas.put("mainHomeCityCode", mainHomeCityCode);
	    datas.put("mainBillingAddressType", mainBillingAddressType);
	    datas.put("mainHomeCityCode", mainHomeCityCode);
	    datas.put("mainBillingPostType", mainBillingPostType);
	    datas.put("autoPayOff", autoPayOff);
	    datas.put("paymentLimitType", paymentLimitType);
	    datas.put("exchangeFlag", exchangeFlag);
	    datas.put("bankAccount1", bankAccount1);
	    datas.put("tradePassedFlag", tradePassedFlag);
	    datas.put("logFlag", logFlag);
	    datas.put("operatingNode", "N004");
	    datas.put("mainCardName", mainCardName);
	    datas.put("creditcardCategory", creditcardCategory);
	    datas.put("creditcardType", creditcardType);
	    datas.put("affinityCode", affinityCode);
	    datas.put("currencyFlag", currencyFlag);
	    datas.put("scc", scc);
	    datas.put("suTxnSeqId", suTxnSeqId);
	    datas.put("sccFirstTwo", sccFirstTwo);
	    
	    //最后提交
	    url = "https://c.pingan.com/apply/tianxiatong/restApplyCardCommit.do";
	    headers.put("Accept", "application/json");
	    headers.put("Content-Type", "application/x-www-form-urlencoded");
	    headers.put("X-Requested-With", "XMLHttpRequest");
	    hr = hc.post(url, datas, headers);
	    errHtml = hr.getHtml();
	    logger.info("pingAnUtil.applySubmit>>>>申卡结果页====="+errHtml);
	    int index = 5;
	    while (!errHtml.contains("ret_message") && index>0){
	    	hr = hc.post(url, datas, headers);
	 	    errHtml = hr.getHtml();
	 	    index = index-1;
	    }
	    String errMsg = "";
	    if(!errHtml.contains("ret_message")){
	    	errMsg = "网页加载失败,请稍后再试";
	    	return errMsg;
	    }	    
	    JSONObject restObj = new JSONObject(errHtml);		
		String ret_message = "";
		if (!restObj.isNull("ret_message")) {
			ret_message = restObj.get("ret_message").toString();
		}		    
        if(!StringUtils.isEmpty(ret_message) && !ret_message.equalsIgnoreCase("success")){
        	if(errJsMsg.contains(ret_message)){
        		String errSub = errJsMsg.substring(errJsMsg.indexOf(ret_message));
        		errSub = errSub.substring(0,errSub.indexOf(","));
        		errMsg = errSub.split(":")[1].replace("'", "");
        	}else{
        		errMsg = "未知错误";
        	}	    	
	    }
        return errMsg;
	}
	//匹配选择标签value
	private String getSelState(Elements states,String stateKey){
		String stateVal = "";
		for(Element ele:states){
	    	String text = ele.text();
	    	String value = ele.attr("value");
	    	if(text.contains(stateKey)){
	    		stateVal = value;
	    		return stateVal;
	    	}
	    }			
		return stateVal;
	}
	 
	/**
	 * 平安银行办卡获取图片验证码
	 */
    public BufferedImage getImageRand(CookieStore cookieStore){ 
    	hc.setBasicCookieStore(cookieStore);
    	String cookieStr = hc.getAllCookie();
		headers = getBasicHeader();
		headers.put("Cookie", cookieStr);
		headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
		headers.put(
				"Referer",
				"https://c.pingan.com/apply/mobile/apply/index.html?scc="+PingAnBean.scc+"&ccp="+PingAnBean.ccp+"&showt=0");
		String url = "https://c.pingan.com/apply/paic/common/vcode.do?rd="+Math.random(); 
		BufferedImage image = hc.getRandomImageOfJPEG("GET", url, null, headers);
		return image;
    }
    
    /**
     * 平安银行获取短信验证码
     */
	public String sentPhoneRand(MaterialBean bean,CookieStore cookieStore) {		
		String errMsg = "";
		MaterialModel model = bean.getModel();
		String idCardNo = model.getCidcard().trim();
		String mobileNo = model.getCphone().trim();
		String img_code = bean.getImgauthcode();
		if(!StringUtils.isEmpty(img_code)){
			img_code = img_code.replaceAll("\\s*", "");
		}
		hc.setBasicCookieStore(cookieStore);
		headers = getBasicHeader();
		headers.put("Accept", "*/*");
		headers.put("Referer",
				"https://c.pingan.com/apply/mobile/apply/index.html?scc="
						+ PingAnBean.scc + "&ccp=" + PingAnBean.ccp + "&showt=0");
		// 验证图片验证码,成功后发送短信验证码
		String url;
		try {    
			url = "https://c.pingan.com/apply/newpublic/generateOTPNew.do?certificateType=1&idNo"
					+ "="+ idCardNo+ "&mobileNo="+ mobileNo
					+ "&otpOption=1&content="
					+ URLEncoder.encode("登陆外信用卡申请", "utf-8")+ "&checkNo="+ img_code;
			HttpResult hr = hc.get(url, headers);
			errHtml = hr.getHtml("GBK");		
			if (!errHtml.contains("success")) {
				errMsg = errHtml;
				return errMsg;// 验证码错误
			}
		} catch (Exception e) {
			logger.error("平安银行获取短信验证码请求异常【"+errHtml+"】",e);
			errMsg = "获取短信验证码错误";
			return errMsg;
		}
		return errMsg;
	}

	/*public String checkPhoneRand(MaterialBean bean,CookieStore cookieStore){
		String errMsg = "";
		MaterialModel model = bean.getModel();
		String idCardNo = model.getCidcard().trim();
		String mobileNo = model.getCphone().trim();
		String otpRand = bean.getPhoneauthcode().trim();//短信验证码
		hc.setBasicCookieStore(cookieStore);
		headers = getBasicHeader();*/
		//headers.put("Accept", "*/*");
		/*headers.put("Referer",
				"https://c.pingan.com/apply/mobile/apply/index.html?scc="
						+ PingAnBean.scc + "&ccp=" + PingAnBean.ccp + "&show=0");
		String url = "https://c.pingan.com/apply/newpublic/verifyOTPNew.do?certificateType=1&mobileNo="+mobileNo+"&idNo="+ idCardNo+ "&otp="+ otpRand;
		HttpResult hr = hc.get(url, headers);
		errHtml = hr.getHtml("GBK");
		if (!errHtml.contains("success")) {
			errMsg = errHtml;
			return errMsg;// 验证码错误
		}
		return errMsg;
	}*/
	//办卡进度查询首页
	public void cardProgressFirstIndex(CookieStore cookieStore){
		hc.setBasicCookieStore(cookieStore);
		headers = getBasicHeader();
		headers.put("Host", "wap-ebank.pingan.com");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
		
		//办卡首页
		String url = "https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html#queryf";
		HttpResult hr = hc.get(url, headers);
		errHtml = hr.getHtml();
		//跳转页
		url = "https://wap-ebank.pingan.com/weixin/modules/queryApp/partials/queryfirst.html";
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		hr = hc.get(url, headers);
		errHtml = hr.getHtml();
	}
	/**
	 * 平安银行办卡进度查询获取图片验证码
	 */
    public BufferedImage getImageRandOfProgress(CookieStore cookieStore){ 
    	cardProgressFirstIndex(cookieStore);
    	hc.setBasicCookieStore(cookieStore);
		String cookieStr = hc.getAllCookie();
		System.out.println("getImageRandOfProgress>>>>"+cookieStr);
		headers = getBasicHeader();
		headers.put("Host", "wap-ebank.pingan.com");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");				
		headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("Cookie", cookieStr);
    	String url = "https://wap-ebank.pingan.com/xinyongka/newWebVcode.do?cid=undefined&random="+Math.random();
		BufferedImage image = hc.getRandomImageOfJPEG("GET", url, null, headers);
		return image;
    }
    public String checkSmsCodeOfProgress(CookieStore cookieStore,MaterialBean bean){
    	String idCardNo = bean.getIdcardid().trim();
		String otpRand = bean.getPhoneauthcode().trim();//短信验证码
    	hc.setBasicCookieStore(cookieStore);   			
		String cookieStr = hc.getAllCookie();
    	headers = getBasicHeader();
    	headers.put("Cookie", cookieStr);
    	headers.put("Host", "wap-ebank.pingan.com");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		String url = "https://wap-ebank.pingan.com/xinyongka/public.do?&operationType=verifyOTPNew&cid=undefined&random="+Math.random();
		Map<String,String> datas = new HashMap<String,String>();
		datas.put("otp", otpRand);
		datas.put("certificateType", "1");
		datas.put("idNo", idCardNo);
		HttpResult hr = hc.post(url,datas,headers);
		errHtml = hr.getHtml();
		logger.info("PingAnUtil.checkSmsCodeOfProgress>>>>>>"+errHtml);
		if(errHtml.contains("success")){
			return "success";
		}else{
			return "动态密码不正确,请重新输入";
		}
    }
    /**
	 * 平安银行办卡进度查询图片验证码检测
	 */
    public String checkImgCodeOfProgress(CookieStore cookieStore,String imgCode){
    	hc.setBasicCookieStore(cookieStore);   			
		String cookieStr = hc.getAllCookie();
    	headers = getBasicHeader();
    	headers.put("Cookie", cookieStr);
    	headers.put("Host", "wap-ebank.pingan.com");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		String url = "https://wap-ebank.pingan.com/xinyongka/newWebVcodeValidate.do?cid=undefined&random="+Math.random()+"&checkNo="+imgCode+"&scene=public";
		HttpResult hr = hc.get(url, headers);
		errHtml = hr.getHtml();
		JSONObject checkObj = new JSONObject(errHtml);		
		String ret_message = "";
		String ret_code = "";
		if (!checkObj.isNull("ret_message")) {
			ret_message = checkObj.get("ret_message").toString();
		} 
		if (!checkObj.isNull("ret_code")) {
			ret_code = checkObj.get("ret_code").toString();
		}
		if("验证成功".equals(ret_message)||"000".equals(ret_code)){
			return "success";
		}else{
			return ret_message;
		}
    }
    
    
    /**
     * 平安进度查询下一步,检测是否需要短信验证码
     * @param cookieStore
     * @param bean
     * @return
     */
    public int apply_credit_sms(CookieStore cookieStore,MaterialBean bean,MemCachedClient client) throws Exception{
    	hc.setBasicCookieStore(cookieStore);
    	String idCardNo = bean.getIdcardid().trim();
    	headers = getBasicHeader();
    	headers.put("Host", "wap-ebank.pingan.com");
		headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		//查询结果
		String url = "https://wap-ebank.pingan.com/xinyongka/public.do?operationType=queryApp&cid=undefined";
		Map<String,String> datas = new HashMap<String,String>();
		datas.put("cardType", "1");
		datas.put("cardNum", idCardNo);
		datas.put("nationalityFlag", "1");
		HttpResult hr = hc.post(url, datas, headers);
		errHtml = hr.getHtml();
		logger.info("平安申卡进度查询下一步结果页>>>>>>>>>>>>>>>"+errHtml);
		JSONObject restObj = new JSONObject(errHtml);		
		String ret_msg = "";
		String ret_code = "";
		String mobileNo = "";//申卡填写的手机号
		if (!restObj.isNull("ret_msg")) {//结果
			ret_msg = restObj.get("ret_msg").toString();
		} 
		if (!restObj.isNull("ret_code")) {
			ret_code = restObj.get("ret_code").toString();
		}
		if (!restObj.isNull("mobileNo")) {
			mobileNo = restObj.get("mobileNo").toString();
		}
//		CacheClient cc = CacheClient.getInstance();
		String redirect_key = bean.getIdcardid() + "redirectFlag_pingAn_cookieStore";
		if("success".equalsIgnoreCase(ret_msg) && "000".equals(ret_code)){
			url = "https://wap-ebank.pingan.com/weixin/modules/queryApp/partials/queryver.html";
			hr = hc.get(url, headers);
			errHtml = hr.getHtml();
			client.set(redirect_key, "have_sms",Constants.TIME_HOUR);//标识接口跳转
			bean.setBusiErrCode(2);
			bean.setBusiErrDesc("需要短信验证码");
            bean.setBusiJSON("{\"mobileNo\":\""+mobileNo+"\"}");
			return 1;
		}else{
			client.set(redirect_key, "no_sms",Constants.TIME_HOUR);
			bean.setBusiErrCode(1);
			bean.setBusiErrDesc("不需要短信验证");
			return 1;
		}
    }
    
    public int smsCodeForQueryApply(CookieStore cookieStore,MaterialBean bean)throws Exception{
    	hc.setBasicCookieStore(cookieStore);
    	String idCardNo = bean.getIdcardid().trim();
    	headers = getBasicHeader();
    	headers.put("Host", "wap-ebank.pingan.com");
		headers.put("Accept", "*/*");
		headers.put("Referer"," https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		//查询结果
		String url = "https://wap-ebank.pingan.com/xinyongka/public.do?operationType=generateOTPNew&cid=undefined&random="+Math.random()+
				"&certificateType=1&idNo="+idCardNo+"&partyNo=&content=%E7%94%B3%E8%AF%B7%E8%BF%9B%E5%BA%A6%E6%9F%A5%E8%AF%A2&otpOption=1";
		HttpResult hr = hc.get(url,headers);
		errHtml = hr.getHtml();
		if(errHtml.contains("success")){
			bean.setBusiErrCode(1);
            bean.setBusiErrDesc("短信验证码发送成功");
            return 1;	
		}else{
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("短信验证码发送失败,请重新获取");
			return 0;					 
		}
    }
    /**
	 * 平安银行办卡获取进度查询结果
	 */
    public JSONObject getCardProgress(CookieStore cookieStore,MaterialBean bean,String redirectFlag,String cardType)throws Exception{
    	String idCardNo = bean.getIdcardid().trim();
    	hc.setBasicCookieStore(cookieStore);
    	headers = getBasicHeader();
    	headers.put("Host", "wap-ebank.pingan.com");
		headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
		headers.put("Referer","https://wap-ebank.pingan.com/weixin/modules/queryApp/index.html");
		headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		//查询结果集 				
    	JSONObject restJobj = new JSONObject();        
        if("have_sms".equals(redirectFlag)){//有申卡记录
        	String url = "https://wap-ebank.pingan.com/weixin/modules/queryApp/partials/querysecond.html";
        	HttpResult hr = hc.get(url, headers);
        	errHtml = hr.getHtml();
        	headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        	url = "https://wap-ebank.pingan.com/xinyongka/public.do?operationType=queryApp&cid=undefined";
        	Map<String,String> datas = new HashMap<String,String>();
        	datas.put("cardType", "1");
        	datas.put("cardNum", idCardNo);
        	datas.put("nationalityFlag", "2");
        	hr = hc.post(url, datas, headers);
        	errHtml = hr.getHtml();
        	JSONObject checkObj = new JSONObject(errHtml);		
    		if(!checkObj.isNull("resultList")){ 
    			JSONArray resultList = checkObj.getJSONArray("resultList");
    			int cardIndex = PingAnProgUtil.applyQuerySelCardIndex(resultList,cardType);
    			if(cardIndex==-1){//未匹配到申卡结果
    				restJobj.put("resultcode",0);
    			    restJobj.put("resultdesc","我行还没有处理您的申请,可能您的申请寄出尚未满7个工作日,请耐心等待");
    			}else{
    				JSONObject cardResult = resultList.getJSONObject(cardIndex);
        			restJobj = PingAnProgUtil.getProcessRecord(cardResult);
    			}   			
    			restJobj.put("resean", "");	
    		}else{//查询错误
				ErrorRequestBean errBean = new ErrorRequestBean(bean, errHtml);
				errBean.setCerrordesc("平安进度查询网站异常");
				errBean.setCphone(bean.getCphone());
				errBean.setResult("平安进度查询系统服务升级");
				BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.query_apply, errBean);
    			restJobj.put("resultcode",-1);
			    restJobj.put("resultdesc","系统异常，请您稍后再试");			    
    		}
        }else{//没有记录
			restJobj.put("resultcode",0);
		    restJobj.put("resultdesc","我行还没有处理您的申请,可能您的申请寄出尚未满7个工作日,请耐心等待");
		    restJobj.put("resean", "您的申请书寄出尚未满7个工作日或者您输入的证件号码有误");	 
        }				
		return restJobj;
    }
    
	//请求消息头
	public Map<String, String> getBasicHeader() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		headers.put("Connection", "Keep-Alive");
		headers.put("Host", "c.pingan.com");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put(
				"User-Agent",
				"Opera/9.80 (Android 2.3.7; Linux; Opera Tablet/46154) Presto/2.11.355 Version/12.10");
		return headers;
	}
	//平安银行申卡参数检查(根据平安申卡网站前端检测方法改写)
	public String checkMaterial(MaterialBean bean,PingAnBean pingAn) {
		MaterialModel model = bean.getModel();
		String errMsg = "";
		String cidcard = model.getCidcard();//身份证号
		if(StringUtils.isEmpty(cidcard)){
			errMsg += "请填写您的身份证号;";
		}else{
			String begin_date = cidcard.substring(6, 14);//出生日期
			pingAn.setIdCardNo(cidcard);
			pingAn.setBegin_date(begin_date);
		}
		String cphone = model.getCphone();//手机号
		if(StringUtils.isEmpty(cphone)){
			errMsg += "请填写您的手机号;";
		}else{
			pingAn.setMobileNo(cphone);
		}
		/**
		 * 基本信息
		 */
		String cname = model.getCname();//姓名
		if(StringUtils.isEmpty(cname)){
			errMsg +="请填写您的姓名;";
		}else{
			pingAn.setUname(cname);
		}
		String englishname = model.getCenglishname();//姓名拼音
		pingAn.setPingyin(englishname);

		String cidexpirationtime = model.getCidexpirationtime();//起始时间,终止时间
		if(StringUtils.isEmpty(cidexpirationtime)){
			errMsg +="请填写身份证有效期;";
		}else if(cidexpirationtime.contains("-1")){//无限期
			pingAn.setCidexpirationtime("20991231");
		}else{
			String[] cidexpirationtimeAry = cidexpirationtime.split("\\,");
			if(cidexpirationtimeAry.length!=2){
				errMsg +="身份证有效期格式错误;";
			}else{
				pingAn.setCidexpirationtime(cidexpirationtime.split("\\,")[1]);//身份证有效日期
			}
		}

		String isex = model.getIsex();//性别
		if(StringUtils.isEmpty(isex)){
			errMsg +="请填写您的性别;";
		}else{
			if ("1".equals(isex)){//性别
				pingAn.setSex("M");
			}else{
				pingAn.setSex("F");
			}
		}

		/**
		 * 个人信息
		 */
		String cemail = model.getCemail();//邮箱
		String email_regex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";//"^([a-zA-Z0-9_\\.-]+)@([\\da-zA-Z\\.-]+)\\.([a-zA-Z\\.]{2,6})$";
		if(StringUtils.isEmpty(cemail)){
			errMsg += "请填写您的E-mail地址;";
		}else if(!cemail.matches(email_regex)){
			errMsg += "E-mail格式不正确;";
		}else{
			pingAn.setEmail(model.getCemail());//邮箱
		}

		String maritalstatus = model.getMaritalstatus();
		if(StringUtils.isEmpty(maritalstatus)){
			errMsg +="请选择您的婚姻状况;";
		}else{
			String marital = "";//1、未婚 2、已婚 3、其它
			switch (maritalstatus) {
				case "1":
					marital = "未婚";
					break;
				case "2":
					marital = "已婚";
					break;
				case "3":
					marital = "其他";
					break;
				default:
					break;
			}
			pingAn.setMarital_status(marital);//婚姻状况
		}

		String educationState = model.getIdegree();//1、博士及以上 2、硕士 3、本科 4、大专 5、高中、中专一下 ;本地库
		if(StringUtils.isEmpty(educationState)){
			errMsg+="请选择受教育程度;";
		}else{
			String education = "";//教育状况  02：博士及以上 03:硕士 04:本科 05:大专 07:高中及中专 08:初中及以下 ;平安银行
			switch (educationState) {
				case "1":
					education = "博士及以上";
					break;
				case "2":
					education = "硕士";
					break;
				case "3":
					education = "本科";
					break;
				case "4":
					education = "大专";
					break;
				case "5":
					education = "高中及中专";
					break;
				default:
					break;
			}
			pingAn.setEducation(education);
		}
		String family_name = model.getFamilyname();//亲属姓名
		String family_regex = "^[\u4e00-\u9fa5]{2,19}$";
		if(StringUtils.isEmpty(family_name)){
			errMsg += "请填写亲属姓名;";
		}else if(!family_name.matches(family_regex)){
			errMsg += "亲属姓名需为中文;";
		}else{
			pingAn.setEmer_contact(family_name);
		}
		String guardianRelation =  model.getIfamilyties();//亲属关系1.配偶 2.父母 3.子女 4.兄弟姐妹
		if(StringUtils.isEmpty(guardianRelation)){
			errMsg += "请选择与您的关系;";
		}else{
			String emergencyties = "";//紧急联系人关系 1:配偶 2:父母 3:子女 4:亲戚 5:朋友 6:同学 7:同事 9:其他
			if(StringUtils.isEmpty(guardianRelation)){
				emergencyties = "其他";
			}else{
				switch (guardianRelation) {
					case "1":
						emergencyties = "配偶";
						break;
					case "2":
						emergencyties = "父母";
						break;
					case "3":
						emergencyties = "子女";
						break;
					case "4":
						emergencyties = "亲戚";
						break;
					case "5":
						emergencyties = "朋友";
						break;
					default:
						emergencyties = "其他";
						break;
				}
			}
			pingAn.setGuardian_relat(emergencyties);
		}
		String family_phone = model.getCfamilyphonenum();//亲属电话
		String fphone_regex = "^1\\d{10}$";
		if(StringUtils.isEmpty(family_phone)){
			errMsg += "请填写亲属手机号;";
		}else if(!family_phone.matches(fphone_regex)){
			errMsg += "手机号格式不正确;";
		}else if(cphone.equals(family_phone)){
			errMsg += "亲属移动电话与申请人移动电话不能重复;";
		}else{
			pingAn.setEmer_phone(family_phone);
		}


		/**
		 * 工作信息
		 */
		String company = model.getCcompanyname();//单位名称
		String company_regex = "^[\u4e00-\u9fa5a-zA-Z0-9]{2,19}$";
		if(StringUtils.isEmpty(company)){
			errMsg+="请填写公司名称;";
		}else if(!company.matches(company_regex)){
			errMsg+="公司名称格式错误;";
		}else{
			pingAn.setCompany(company);
		}

		String department = model.getCdepartmentname();//部门名称
		String department_regex = "^[\u4e00-\u9fa5a-zA-Z0-9]{2,9}$";
		if(StringUtils.isEmpty(department)){
			errMsg+="请填写部门名称;";
		}else if(!department.matches(department_regex)){
			errMsg+="部门名称格式错误;";
		}else{
			pingAn.setDepartment(department);
		}

		String idepartment = model.getIdepartment();//1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级
		if(StringUtils.isEmpty(idepartment)){
			errMsg+="请填写职务名称";
		}else{
			String position = "";//职务
			switch (idepartment) {
				case "1":
					position = "一般员工";
					break;
				case "2":
					position = "部门经理";
					break;
				case "3":
					position = "总经理";
					break;
				case "4":
					position = "主管 ";
					break;
				default:
					position = "一般员工";
					break;
			}
			pingAn.setPosition(position);
		}

		String workage = model.getItimeinjob();
		String workage_regex = "^[1-9][0-9]{0,1}$|^0$";
		if(StringUtils.isEmpty(workage)){
			errMsg+="请填写任职年限;";
		}else if(!workage.matches(workage_regex)){
			errMsg+="请输入您在该单位工作的年限(数字)即可;";
		}else{
			pingAn.setWorkage(workage);
		}
		String company_postcode = model.getCcompany_postcode();//公司邮编
		String compost_regex = "^\\d{6}$";
		if(StringUtils.isEmpty(company_postcode)){
			errMsg+="请填写公司邮编;";
		}else if(!company_postcode.matches(compost_regex)){
			errMsg+="邮编格式不正确;";
		}else{
			pingAn.setJzip_code(company_postcode);
		}
		String company_addr = model.getCcompany_detailaddress();//公司详细地址
		//String comaddr_regex = "^[\u4e00-\u9fa5a-zA-Z0-9\\(\\)-\\#\\（\\）\\＃]{1,38}$";
		if(StringUtils.isEmpty(company_addr)){
			errMsg+="请填写详细地址;";
		}else if(company_addr.length()<4){
			errMsg+="您输入的地址过短，填写真实详细地址将有助审批;";
		}else{
			pingAn.setCompany_addr(company_addr);
		}

		//电话  公司电话和住宅电话必填一个
		String company_number = model.getCcompany_telnum();//公司电话 区号-电话号-分机号
		String home_number = model.getChome_telnum();//住宅电话 区号-电话号
		String company_pzone = "";
		String company_phone = "";
		String company_pextension = "";
		if(!StringUtils.isEmpty(company_number)){
			String[] company_numbers = company_number.split("\\-");
			if(company_numbers.length==2){
				company_pzone = company_numbers[0];
				company_phone = company_numbers[1];
			}else if(company_numbers.length>=3){
				company_pzone = company_numbers[0];
				company_phone = company_numbers[1];
				company_pextension = company_numbers[2];
			}else{
				errMsg += "填写的公司电话不正确;";
			}
			String pzone_regex = "^\\s*$|^\\d{3,4}$";
			if(!company_pzone.matches(pzone_regex)){
				errMsg += "公司电话区号格式不正确;";
			}else{
				pingAn.setCompany_pzone(company_pzone);
			}
			String phone_regex = "^\\s*$|^\\d{7,8}$";
			if(!company_phone.matches(phone_regex)){
				errMsg += "公司电话格式不正确;";
			}else{
				pingAn.setCompany_phone(company_phone);
			}
			if(!StringUtils.isEmpty(company_pextension)){
				String fenji_regex = "^\\s*$|^\\d{0,5}$";
				if(!company_pextension.matches(fenji_regex)){
					errMsg += "请填写正确的分机号或不填;";
				}else{
					pingAn.setCompany_pextension(company_pextension);
				}
			}
		}else{
			if(StringUtils.isEmpty(home_number)){
				errMsg +="工作、住宅电话至少填写一个;";
			}else{
				String[] home_numbers = home_number.split("\\-");
				if(home_numbers.length>=2){
					String houseZoneCode = home_numbers[0];
					String houseTel = home_numbers[1];
					String zone_regex = "^\\s*$|^\\d{3,4}$";
					if(!houseZoneCode.matches(zone_regex)){
						errMsg+="住宅电话区号格式不正确;";
					}else{
						pingAn.setHome_pzone(houseZoneCode);
					}
					String tel_regex = "^\\s*$|^\\d{7,8}$";
					if(!houseTel.matches(tel_regex)){
						errMsg+="住宅电话号码格式不正确;";
					}else{
						pingAn.setHome_phone(houseTel);
					}
					if(houseTel.equals(company_phone)){
						errMsg+="工作、住宅电话不能相同;";
					}
				}else{
					errMsg = "填写的住宅电话不正确;";
				}
			}
		}


		/**
		 * 居住信息
		 */
		String home_addr = model.getChome_detailaddress();//家庭详细地址
		// String haddr_regex = "^[\u4e00-\u9fa5a-zA-Z0-9\\(\\)-\\#\\（\\）\\＃]{1,38}$";
		if(StringUtils.isEmpty(home_addr)){
			errMsg+="填写住宅详细地址;";
		}else if(home_addr.length()<4){
			errMsg+="您输入的地址过短，填写真实详细地址将有助审批;";
		}else{
			pingAn.setHome_addr(home_addr);
		}

		String home_acode = model.getChome_postcode();//住宅邮编
		String hacode_regex = "^\\d{6}$";
		if(StringUtils.isEmpty(home_acode)){
			errMsg+="填写住宅邮编;";
		}else if(!home_acode.matches(hacode_regex)){
			errMsg+="住宅邮编格式不正确;";
		}else{
			pingAn.setHome_acode(home_acode);
		}

		String residence = model.getResidencestatus();//1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它
		String monthPay = model.getCmonthlypayment();//月还贷
		if(StringUtils.isEmpty(residence)){
			errMsg+="请选择房产状况;";
		}else{
			String home_status;
			switch (residence) {
				case "1":
					home_status = "自购有贷款";
					break;
				case "2":
					home_status = "自购无贷款";
					break;
				case "3":
					home_status = "租用";
					break;
				case "4":
					home_status = "亲属住房";
					break;
				default:
					home_status = "其他";
					break;
			}
			pingAn.setHome_statu(home_status);
		}
		if("1".equals(residence)){
			String monthpay_regex = "^[1-9]\\d{0,6}$";
			if(StringUtils.isEmpty(monthPay)){
				errMsg +="月还贷金额不能为空;";
			}else if(!monthPay.matches(monthpay_regex)){
				errMsg +="月还款额输入不正确，请重新输入;";
			}else{
				pingAn.setMonth_pay(monthPay);
			}
		}

		String live_year = model.getIlivelen();//居住年限
		String houseAge_regex = "^[1-9][0-9]{0,1}$|^0$";
		if(StringUtils.isEmpty(live_year)){
			errMsg+="请填写居住年限;";
		}else if(!live_year.matches(houseAge_regex)){
			errMsg+="请输入您在此地居住的年限(数字)即可;";
		}else{
			pingAn.setLive_year(live_year);
		}

		/**
		 * 补充信息
		 */
		String jk_flag = model.getIpostaddress();//数据库中寄卡地址标识1、单位地址  2、住宅地址
		if("1".equals(jk_flag)){//官网寄卡标识 1:住宅地址 2:单位地址
			pingAn.setJk_addr("2");
		}else{
			pingAn.setJk_addr("1");
		}
		return errMsg;
	}
	/*private static void saveErrHtml(MaterialModel md, Map<String, String> htmls) {
		try {
			List<ErrorRequestBean> beans=new ArrayList<>();
			long times=System.currentTimeMillis();
			for (String key : htmls.keySet()) {
				String filename=md.getCphone()+"-"+md.getCidcard()+"-"+key+"-"+times+ ".html";
				ErrorRequestBean bean=new ErrorRequestBean(filename, null, htmls.get(key), null);
				beans.add(bean);
			}
			BankApplyListener.sendError(BankEnum.pingan, BankApplyStepEnum.submit_apply, beans);
		} catch (Exception e) {
			for (String key: htmls.keySet()) {
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] "+key+" content["+htmls.get(key)+"]");
			}
		}
	}*/
}
