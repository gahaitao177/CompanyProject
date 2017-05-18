package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lwg
 * 广发银行信用卡提交
 */
public class GuangFaSubmit {
    /**广发合作链接*/
    private static String hezuoURL = "http://95508.com/8gngEu";
    /**发送短信验证码*/
    private static String smsCode = "https://wap.cgbchina.com.cn/creditCardApplySmsCode.do?a="+System.currentTimeMillis();
    /**第一步提交*/
    private static String setp1 = "https://wap.cgbchina.com.cn/saveUnComplete.do";
    /**最后一步提交*/
    private static String saveComplete = "https://wap.cgbchina.com.cn/saveComplete.do";
    /**申卡进度查询页面*/
    private static String queryApplyInfo = "https://wap.cgbchina.com.cn/queryApply.do";
    /**办卡进度短信验证码*/
    private static String smsCodeForQueryApplyStatus = "https://wap.cgbchina.com.cn/getSmsCodeForQueryApplyStatus.do";
    /**查询办卡进度接口*/
    private static String queryApply = "https://wap.cgbchina.com.cn/queryApplyValue.do";

    public static String userAgent = "Mozilla/5.0 (Linux; Android 4.4.4; iToolsVM Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 MicroMessenger/6.3.13.49_r4080b63.740 NetType/WIFI Language/zh_CN";

    public static Logger logger = LoggerFactory.getLogger("GuangFaSubmit");


    /**广发合作链接*/
    private static Map<String,String> heZuoUrlMap = new HashMap<String,String>();
//    @Autowired
//    private  static MemCachedClient client;
    static {
        heZuoUrlMap.put("1025","http://95508.com/t2gjTC");//广发DIY卡
        heZuoUrlMap.put("1026","http://95508.com/j6wTT2");//广发新聪明卡
        heZuoUrlMap.put("1037","http://95508.com/GUVUT1");//广发易车联名卡
        heZuoUrlMap.put("1002","http://95508.com/53BWT4");//广发南航明珠卡
        heZuoUrlMap.put("1017","http://95508.com/KxGxMH");//广发真情卡(女性专属)
        heZuoUrlMap.put("1015","http://95508.com/Z2TAe6");//广发携程卡
    }
    /**
     * 提交资料到广发银行
     * @param bean
     * @return 1成功，0失败，-1短信验证码错误
     */
    public static Message applyForTheGuangFaBank(MaterialBean bean, MemCachedClient client){
        String phone = bean.getModel().getCphone();
        MaterialBean materialBean = (MaterialBean)client.get(phone + "_materialBean");
        if(null == materialBean){
            return Message.errorJson(bean, "获取用户申卡资料失败");
        }
        materialBean.setPhoneauthcode(bean.getPhoneauthcode());
        MaterialModel md = materialBean.getModel();
        md.setIbankid(bean.getIbankid());
        GuangFaEntity creditEntity = GuangFaEntity.initByCardId(materialBean.getApplyBankCardId(),materialBean.getApplyBankCardLevel());
        if(null==creditEntity){
            return Message.errorJson(bean, "不支持申请该信用卡");
        }
        GuangFaModel guangFaModel = GuangFaModel.transfer(materialBean);
        if ("M".equals(guangFaModel.getCbsSex())&&creditEntity.getCapTypeNo().equals("1017")){
            return Message.errorJson(bean,"广发真情卡(女性专属)仅限女性申请");
        }
        try {
            int iclient = bean.getIclient();
//            String capAccessNo = "002816";//广发合作代码
            if (0==iclient){
                logger.info("安卓客户端模拟广发申卡。。");
//                capAccessNo = "002816";
                // TODO: 2016/4/27 添加广发合作代码
            }else if (1==iclient){
                logger.info("IOS客户端模拟广发申卡。。");
//                capAccessNo = "002816";
            }else {
                logger.info("未知客户端模拟广发申卡。。");
//                capAccessNo = "002816";
            }
            Message message = setp1(creditEntity, guangFaModel,client);
            if (message.getCode()!=1){
                return Message.resultJson(bean,message);
            }
            return Message.resultJson(bean, saveComplete(creditEntity, guangFaModel,client));
         } catch (Exception e) {
            e.printStackTrace();
            return Message.errorJson(bean, e.getMessage());
        }
    }

    /**
     * 发送验证码并获取基础信息
     * @param bean
     * @return 1成功，0失败
     */
    public static Message sendSmsCode(MaterialBean bean, MemCachedClient client){
        Message verify = verify(bean);
        if (verify.getCode()==0){
            return Message.resultJson(bean, verify);
        }
        if (bean.getBusiErrCode()==0) {
            return Message.errorJson(bean, bean.getBusiErrDesc());
        }
        MaterialModel md = bean.getModel();
        String mobileNo = md.getCphone();
        if(BankUtils.isEmpty(bean.getApplyBankCardId())){
            return Message.errorJson(bean,"没有获取到银行映射ID");
        }
        if(BankUtils.isEmpty(bean.getApplyBankCardLevel())){
            return Message.errorJson(bean,"没有获取到申请的卡等级");
        }
        String result = null;
        HashMap<String, String> params = new HashMap<String, String>();
        try {
            String url = heZuoUrlMap.get(bean.getApplyBankCardId());
            if (StringUtils.isEmpty(url)) {
                return Message.errorJson(bean, "该卡暂不支持模拟申卡");
            }
            logger.info("广发申卡使用合作链接="+url);
            HttpUtil http = new HttpUtil(url , "utf-8", null, null);
            http.getResponseString();

            //302重定向
            if(http.getConnection().getResponseCode()==302){
                String original = http.getHeaderField("Location");
                http = new HttpUtil(original, "utf-8", null, null);
            }
            result = http.getResponseString();

            //获取sendTradeNo和seqno
            String optFlag = getHtmlContent(result, "optFlag");
            String capAccessNo = getHtmlContent(result, "capAccessNo");
            String sendTradeNo = getHtmlContent(result, "sendTradeNo");
            String seqno = getHtmlContent(result, "seqno");
            client.set(mobileNo+"_optFlag", optFlag);
            client.set(mobileNo+"_capAccessNo", capAccessNo);
            client.set(mobileNo+"_sendTradeNo", sendTradeNo);
            client.set(mobileNo+"_seqno", seqno);

            String session = http.getHeaderField("Set-Cookie");
            client.set(mobileNo+"_session", session);
            client.set(mobileNo+"_materialBean",bean);

            //发送验证码
            params.put("mobileNo", mobileNo);//手机号
            HashMap<String, String> pros = new HashMap<String, String>();
            pros.put("Origin", "https://wap.cgbchina.com.cn");//固定
            pros.put("Cookie", session);
            pros.put("Referer", "https://wap.cgbchina.com.cn/baseInfoIn.do");//固定
            pros.put("User-Agent", userAgent);

            http = new HttpUtil(smsCode, "utf-8", params, pros);
            result = http.getResponseString();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis()+".html",params,result,smsCode,-1,result,mobileNo);
            BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.phone_code, errBean);
            logger.info("广发银行申卡短信发送异常！手机号："+mobileNo+";"+e.getMessage());
            return  Message.errorJson(bean, "短信发送异常");
        }
        if(JSONObject.parseObject(result).get("hostReturnCode").equals("0")){
            logger.info("广发银行申卡短信发送成功！手机号码："+mobileNo);
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.phone_code);
            return Message.successJson(bean, "短信发送成功");
        }else{
            logger.info("广发银行申卡短信发送失败！手机号码："+mobileNo);
            logger.info("错误信息："+result);
            return Message.errorJson(bean, "短信发送失败");
        }
    }

    /**
     * 提交第一步
     * @param creditEntity 广发信用卡资料
     * @param guangFaModel 广发客户资料
     * @return
     * @throws Exception
     */
    private static Message setp1(GuangFaEntity creditEntity, GuangFaModel guangFaModel, MemCachedClient client) throws Exception{
        String optFlag = (String) client.get(guangFaModel.getCbsMobile()+"_optFlag");
        String capAccessNo = (String) client.get(guangFaModel.getCbsMobile()+"_capAccessNo");
        String sendTradeNo = (String) client.get(guangFaModel.getCbsMobile()+"_sendTradeNo");
        String seqno = (String) client.get(guangFaModel.getCbsMobile()+"_seqno");
        String session = (String) client.get(guangFaModel.getCbsMobile()+"_session");
        logger.info("广发申卡，从缓存中获取，optFlag="+optFlag+",capAccessNo="+capAccessNo+",sendTradeNo="+sendTradeNo+",seqno="+seqno);
        if(StringUtils.isEmpty(sendTradeNo)){
            logger.info("sendTradeNo获取错误！");
        }
        if(StringUtils.isEmpty(seqno)){
            logger.info("seqno获取错误！");
        }
        if(StringUtils.isEmpty(session)){
            logger.info("session获取错误！");
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("capTypeNo", creditEntity.getCapTypeNo());//卡号
        params.put("tempMessage", "0000");//固定

        params.put("capAccessNo", capAccessNo);//固定代码，合作代码。
        params.put("sendTradeNo", sendTradeNo);//--交易流水号
        params.put("shortUrl", "");//空
        params.put("optFlag", optFlag);//未知，根据返回页面取值
        params.put("capApplyStatus", "1");//未知，根据返回页面取值

        params.put("capApplyUnion", "123456789");//未知，根据第一步提交返回页面取值
        params.put("capApplyNo", "123456789");//未知，根据第一步提交返回页面取值
        params.put("cdfApplyNo", "123456789");//未知，根据返回页面取值
        params.put("cohOtherNo", "123456789");//未知，根据返回页面取值

        params.put("cbsHaveCard", "N");//是否有本行信用卡

        params.put("seqno", seqno);//每次不同

        params.put("cardNo", "");//未知，空
        params.put("staffId", "");//未知，空
        params.put("cbsCompanyTel", "000-0000000");//单位电话
        params.put("csmRecomCardNo", "");//未知，空
        params.put("cardFormat", creditEntity.getCardFormat());//未知，根据返回页面取值
        params.put("totalTimes", "");//未知，根据返回页面取值，空
        params.put("temp", "");//未知，根据返回页面取值，空
        params.put("passageCode", "");//通路代码，空
        params.put("staffNum", "");//电营营销员代号，空
        params.put("uniqueCode", "");//电营唯一标识，空
        params.put("cardName", creditEntity.getCardName());//--申请卡名


        params.put("capBrand", creditEntity.getCapBrand());//*品牌:C银联UnionPay，M万事达MasterCard，V维萨VISA
        params.put("capLevel", creditEntity.getCapLevel());//卡种：P普卡，J金卡，B白金卡
        params.put("capAptCommon", creditEntity.getCapAptCommon());//0接受普卡，1不接受普卡
        params.put("capClass", creditEntity.getCapClass());//卡别：Z主卡，F附属卡，B主卡+附属卡
        params.put("capStyle", creditEntity.getCapStyle());//*卡版：S竖版，H横版

        //有特殊选项的卡
        if(creditEntity.getCapTypeNo().equals("1025")){//广发DIY
            //广发DIY卡，三倍积分优惠商户类型选择:	(可多选，多一项29元)
            params.put("integral1", creditEntity.getIntegral1());//【A】餐饮娱乐类（包括各项餐饮，娱乐等各类商户）
            params.put("integral2", creditEntity.getIntegral2());//【B】购物类（包括百货、超市、服饰鞋包等各类商户）
            params.put("integral3", creditEntity.getIntegral3());//【C】旅行类（包括机票、酒店、度假、旅游等各类商户）
        }else if (creditEntity.getCapTypeNo().equals("1014")){//广发淘宝潮女卡
            params.put("cdfSelectNo", "s00023");
            params.put("cdfCstInput", creditEntity.getCdfCstInput());
        }else if (creditEntity.getCapTypeNo().equals("1008")){//广发淘宝型男卡
            params.put("cdfSelectNo", "s00019");
            params.put("cdfCstInput", creditEntity.getCdfCstInput());
        }else if (creditEntity.getCapTypeNo().equals("1030")){//广发臻尚白金卡
            params.put("warmTips", "Y");
        }else if(creditEntity.getCapTypeNo().equals("1002")){//广发南航明珠卡
            params.put("northSelectNo", creditEntity.getNorthSelectNo());//--是否南航明珠俱乐部会员：0是，1否
            params.put("s00014", creditEntity.getS00014());//南航明珠俱乐部会员号
        }else if(creditEntity.getCapTypeNo().equals("1003")){//广发东航卡
            params.put("eastSelectNo", creditEntity.getEastSelectNo());//--是否东航卡会员：0是，1否
            params.put("s00016", creditEntity.getS00016());//会员号
        }

        params.put("optionFlag", "1");//我已经阅读并同意《广发银行信用卡客户协议》

        params.put("cbsProvince", guangFaModel.getCbsProvince());//*常驻城市-省份
        params.put("cbsCity", guangFaModel.getCbsCity());//*常驻城市-城市
        params.put("cbsNameCn", guangFaModel.getCbsNameCn());//*姓名
        params.put("cbsNameSpell", guangFaModel.getCbsNameSpell());//*拼音

        params.put("cbsIdCardNo", guangFaModel.getCbsIdCardNo());//*身份证
        params.put("cbsSex", guangFaModel.getCbsSex());//*性别--根据身份证判断
        params.put("cbsBirthday", guangFaModel.getCbsBirthday());//*生日--根据身份证判断
        params.put("cbsEffectDate", guangFaModel.getCbsEffectDate());//身份证有效期，格式yyyyMMdd，留空代表永久有效
        params.put("cbsMobile", guangFaModel.getCbsMobile());//*手机号
        params.put("smsPassword",guangFaModel.getSmsPassword());//*短信验证码


        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Origin", "https://wap.cgbchina.com.cn");//固定
        pros.put("Cookie", session);
        pros.put("Referer", "https://wap.cgbchina.com.cn/baseInfoIn.do");//固定
        pros.put("User-Agent", userAgent);

        printParams(params);
        HttpUtil http = new HttpUtil(setp1, "utf-8",params,pros);
        String result = http.getResponseString();
        if(result.contains("mainWrap")){
            logger.info("广发第一步提交失败");
            logger.info(result);
            //错误代码:PA040304;提示信息:短信验证码错误，请输入正确的短信验证码
            //错误代码：PP010110;提示信息：短信验证码错误，请重新输入
            //错误代码：PP010109;提示信息：短信验证码不存在或已超时
            String errorMsg = getErrorMsg(result);
            if (errorMsg.contains("PA040304")||errorMsg.contains("PP010110")||errorMsg.contains("PP010109")){
                return Message.smsError(errorMsg);
            }else {
                ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis() + ".html", params, result, setp1, -1, result, guangFaModel.getCbsMobile());
                BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.submit_apply, errBean);
                //提示代码：CL001001；提示信息：您在三个月内已申请过该类型主卡或一天内申请过该类型
                return Message.error(errorMsg);
            }
        }else {
            String capApplyUnion = getHtmlContent(result, "capApplyUnion");
            String capApplyNo = getHtmlContent(result, "capApplyNo");
            client.set(guangFaModel.getCbsMobile()+"_capApplyUnion", capApplyUnion);
            client.set(guangFaModel.getCbsMobile()+"_capApplyNo", capApplyNo);
            logger.info("第一步提交成功！");
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.submit_apply);
            return Message.success("第一步提交成功");
        }
    }

    /**
     * 完全提交信息
     * @param creditEntity 广发信用卡资料
     * @param guangFaModel 广发客户资料
     * @throws Exception
     */
    private static Message saveComplete(GuangFaEntity creditEntity, GuangFaModel guangFaModel, MemCachedClient client) throws Exception{
        String optFlag = (String) client.get(guangFaModel.getCbsMobile()+"_optFlag");
        String capAccessNo = (String) client.get(guangFaModel.getCbsMobile()+"_capAccessNo");
        String sendTradeNo = (String)client.get(guangFaModel.getCbsMobile()+"_sendTradeNo");
        String seqno = (String)client.get(guangFaModel.getCbsMobile()+"_seqno");
        String capApplyUnion = (String)client.get(guangFaModel.getCbsMobile()+"_capApplyUnion");
        String capApplyNo = (String)client.get(guangFaModel.getCbsMobile()+"_capApplyNo");
        String session = (String)client.get(guangFaModel.getCbsMobile()+"_session");
        if(StringUtils.isEmpty(sendTradeNo)){
            logger.info("sendTradeNo获取错误！");
        }
        if(StringUtils.isEmpty(seqno)){
            logger.info("seqno获取错误！");
        }
        if(StringUtils.isEmpty(capApplyUnion)){
            logger.info("capApplyUnion获取错误！");
        }
        if(StringUtils.isEmpty(capApplyNo)){
            logger.info("capApplyNo获取错误！");
        }
        if(StringUtils.isEmpty(session)){
            logger.info("session获取错误！");
        }

        HashMap<String, String> params = new HashMap<String, String>();

        //第三步隐藏内容
        params.put("iCollCdf", "<iColl id=\"iCollCdf\" append=\"false\"> </iColl>");
        params.put("cwkHaveInsurence", "Y");//未知参数，根据第二页提交后返回
        params.put("capApplyStatus", "2");
        params.put("cwkCaste", "N");//未知参数，根据第二页提交后返回
        params.put("cpsNationality", "1");//国籍

        //淘宝型男卡&淘宝潮女卡
        params.put("cdfSelectNo", creditEntity.getCdfSelectNo());//型男：s00019，潮女：s00023
        params.put("cdfSelectName", "");
        params.put("cdfOptionNo", creditEntity.getCdfOptionNo());//1
        params.put("cdfOptionName", "");
        params.put("cdfCstInput", creditEntity.getCdfCstInput());//支付宝账号

        params.put("cdfSelectNo1", "");
        params.put("cdfSelectName1", "");
        params.put("cdfOptionNo1", "");
        params.put("cdfOptionName1", "");
        params.put("cdfCstInput1", "");

        params.put("cdfSelectNo2", "");
        params.put("cdfSelectName2", "");
        params.put("cdfOptionNo2", "");
        params.put("cdfOptionName2", "");
        params.put("cdfCstInput2", "");

        params.put("cpsIDCardProv", "");//身份证省份
        params.put("cpsIDCardCity", "");//身份证城市
        params.put("cpsIDCardAddr", "");//身份证地址

        //第一步数据：
        //第一步隐藏信息
        params.put("tempMessage", "0000");//固定
        params.put("shortUrl", "");//固定
        params.put("optFlag", optFlag);//固定
        params.put("optionFlag", "1");//*我已经阅读并同意《广发银行信用卡客户协议》
        params.put("sendTradeNo", sendTradeNo);//*交易流水号

        params.put("capAccessNo", capAccessNo);//固定代码，合作代码。
        params.put("cbsHaveCard", "N");//是否有本行信用卡，N没有，Y有。手机版固定为N。

        params.put("capApplyUnion", capApplyUnion);//根据第二页取值
        params.put("capApplyNo", capApplyNo);//根据第二页取值
        params.put("cdfApplyNo", "123456789");
        params.put("cohOtherNo", "123456789");

        params.put("seqno", seqno);//每次不同
        params.put("cardNo", "");//未知，根据返回页面取值
        params.put("staffId", "");//未知，根据返回页面取值
        params.put("csmRecomCardNo", "");//未知，根据返回页面取值
        params.put("cardFormat", creditEntity.getCardFormat());//未知，根据返回页面取值

        params.put("totalTimes", "");//未知，根据返回页面取值
        params.put("temp", "");//未知，根据返回页面取值
        params.put("passageCode", "");//通路代码
        params.put("staffNum", "");//电营营销员代号
        params.put("uniqueCode", "");//电营唯一标识

        //第一步填写信息
        params.put("cardName", creditEntity.getCardName());//*申请卡名
        params.put("capTypeNo", creditEntity.getCapTypeNo());//*卡号

        params.put("capBrand", creditEntity.getCapBrand());//*品牌:C银联UnionPay，M万事达MasterCard，V维萨VISA
        params.put("capLevel", creditEntity.getCapLevel());//卡种：P普卡，J金卡，B白金卡
        params.put("capAptCommon", creditEntity.getCapAptCommon());//0接受普卡，1不接受普卡
        params.put("capClass", creditEntity.getCapClass());//卡别：Z主卡，F附属卡，B主卡+附属卡
        params.put("capStyle", creditEntity.getCapStyle());//*卡版：S竖版，H横版（淘宝卡为竖版）

        params.put("capStt", "0");//?
        params.put("cbsProvince", guangFaModel.getCbsProvince());//*省份
        params.put("cbsCity", guangFaModel.getCbsCity());//*城市
        params.put("cbsNameCn", guangFaModel.getCbsNameCn());//*申请人姓名
        params.put("cbsNameSpell", guangFaModel.getCbsNameSpell());//*拼音
        params.put("cbsIdCardNo", guangFaModel.getCbsIdCardNo());//*身份证
        params.put("cbsSex", guangFaModel.getCbsSex());//*性别
        params.put("cbsBirthday", guangFaModel.getCbsBirthday());//*生日

        //补充信息
        //南航卡
        params.put("northSelectNo", creditEntity.getNorthSelectNo());//--是否南航明珠俱乐部会员：0是，1否
        params.put("s00014", creditEntity.getS00014());//--南航明珠俱乐部会员号

        //广发DIY卡
        params.put("cdfSelectNo6",creditEntity.getCdfSelectNo6());
        params.put("cdfOptionNo6",creditEntity.getCdfOptionNo6());
        params.put("cdfSelectName6",creditEntity.getCdfSelectName6());//&#x4E09;&#x500D;&#x79EF;&#x5206;&#x4F18;&#x60E0;&#x5546;&#x6237;&#x7C7B;&#x578B;
        params.put("cdfOptionName6",creditEntity.getCdfOptionName6());//&#x3010;B&#x3011;&#x8D2D;&#x7269;&#x7C7B;#|#|

        //臻尚白金卡
        params.put("feature1", creditEntity.getFeature1());//I,全年6次酒后代驾&无限次道路救援（988元/年）
        params.put("feature2", creditEntity.getFeature2());//J,全年6次机场接送（1288元/年）
        params.put("feature3", creditEntity.getFeature3());//K,全年24场高尔夫练球（988元/年）
        params.put("feature4", creditEntity.getFeature4());//L,5小时高尔夫1对1教练课程（2588元/年，限首年，不自动续费）
//		params.put("warmTips", "Y");
        params.put("cdfSelectNo4", creditEntity.getCdfSelectNo4());
        params.put("cdfOptionNo4", creditEntity.getCdfOptionNo4());
        params.put("cdfSelectNo5", creditEntity.getCdfSelectNo5());
        params.put("cdfOptionNo5", creditEntity.getCdfOptionNo5());

        //东航卡
        params.put("eastSelectNo",creditEntity.getEastSelectNo());//--是否东航俱乐部会员：0是，1否
        params.put("s00016", creditEntity.getS00016());//东航会员号

        //唯品会卡
        params.put("s00114", "");//唯品会会员卡号

//		<!-- 新增南航卡、东航卡、唯品会卡、DIY卡会员卡 -->
        //唯品会卡
        params.put("cdfSelectNo7", "s00114");
        params.put("cdfOptionNo7", "1");
        params.put("cdfCstInput7", "");

        //南航卡
        params.put("cdfSelectNo8", "s00014");
        params.put("cdfOptionNo8", "1");//?
        params.put("cdfCstInput8", "");

        //东航卡
        params.put("cdfSelectNo9", "s00016");
        params.put("cdfOptionNo9", "");
        params.put("cdfCstInput9", "");

        //动态添加按钮
        params.put("buttonName", "");
        params.put("returnurl", "");


        params.put("cbsEffectDate", guangFaModel.getCbsEffectDate());//身份证有效期，格式yyyyMMdd，留空代表永久有效
        params.put("cbsMobile", guangFaModel.getCbsMobile());//*手机号
        params.put("smsPassword",guangFaModel.getSmsPassword());//*短信验证码

        //第二页数据
        params.put("functionFlag", "1");
        params.put("cwkCompanyName", guangFaModel.getCwkCompanyName());//*单位名称
        params.put("cwkCompanyProv", guangFaModel.getCwkCompanyProv());//*单位地址-省份
        params.put("cwkCompanyCity", guangFaModel.getCwkCompanyCity());//*单位地址-城市
        params.put("cwkCompanyAddr", guangFaModel.getCwkCompanyAddr());//*单位地址-详细地址
        params.put("cbsCompanyTel", guangFaModel.getCbsCompanyTel());//*单位电话
        params.put("area_code", guangFaModel.getArea_code());//*单位电话--区号
        params.put("host_num", guangFaModel.getHost_num());//*单位电话--主机号
        params.put("mobilef", guangFaModel.getMobilef());//*单位电话--分机号(如无分机号不需填写)
        params.put("cwkCompanyKind", guangFaModel.getCwkCompanyKind());//*单位性质
        params.put("largeKind", guangFaModel.getLargeKind());//*行业性质--行业大类
        params.put("cwkIndustryKind", guangFaModel.getCwkIndustryKind());//*行业性质--行业小类
        params.put("cwkEmployeeNo", guangFaModel.getCwkEmployeeNo());//*员工人数
        params.put("cwkJobLevel", guangFaModel.getCwkJobLevel());//
        params.put("cwkDepartment", guangFaModel.getCwkDepartment());//*任职部门
        params.put("cwkJob", guangFaModel.getCwkJob());//*职位
        params.put("cwkJobYear", guangFaModel.getCwkJobYear());//*任职年数。0请选择，1:1年以下,2:1年,3:2年,4:3年,5:4年,6:5年,7:6年及6年以上
        params.put("cwkYearPay", guangFaModel.getCwkYearPay());//年薪。0请选择，1:1.5万以下，2:1.5-2万，3:2-2.5万，4:2.5-3万，5:3.4万，6:4-5万，7:5-8万，8:8万以上


        //第三步填写内容
        params.put("cpsEmail", guangFaModel.getCpsEmail());//*电子邮箱
        params.put("cpsIsMarry", guangFaModel.getCpsIsMarry());//*婚姻状况.1未婚,2已婚,3其他
        params.put("cpsDegree", guangFaModel.getCpsDegree());//*学历:1博士或以上毕业,2硕士,3本科,4大专,5高中、中专及以下
        params.put("cpsHomeProv", guangFaModel.getCpsHomeProv());//住宅-省份
        params.put("cpsHomeCity", guangFaModel.getCpsHomeCity());//住宅-市
        params.put("cpsHomeAddr", guangFaModel.getCpsHomeAddr());//住宅-详细地址
        params.put("cpsHouseType", guangFaModel.getCpsHouseType());//*住宅类型
        params.put("cpsYear", guangFaModel.getCpsYear());//*居住年限
        params.put("cpsFamilyName", guangFaModel.getCpsFamilyName());//*亲属姓名，不能是自己名字
        params.put("cpsFamilyMobile", guangFaModel.getCpsFamilyMobile());//*亲属手机
        params.put("cpsFamilyRelation", guangFaModel.getCpsFamilyRelation());//亲属与持卡人关系。(隐藏信息，手机版不需要填)
        params.put("csvBillType", guangFaModel.getCsvBillType());//*账单类型:1电子账单
        params.put("csvBillAddr", guangFaModel.getCsvBillAddr());
        params.put("csvPostAddr", guangFaModel.getCsvPostAddr());//*寄卡地址:2单位地址

        if(creditEntity.getCapTypeNo().equals("1030")){
            params.put("cdfOptionNo3", "1");//*安全卫士：1 开通(收费4元/月),0不开通（广发臻尚白金卡-1030免费）
        }else{
            params.put("cdfOptionNo3", "0");//*安全卫士：1 开通(收费4元/月),0不开通（广发臻尚白金卡-1030免费）
        }
        params.put("cdfSelectNo3", "securityService");
        params.put("cdfOptionName3", "0");

        //第三步补充信息	 以下资料为非必填
        params.put("ccrHaveCar", "0");//是否有车，默认0--粤通卡专有属性
        params.put("ccrCarNo", "");//车牌号默认为空--粤通卡专有属性

        params.put("cbsHomeTel", "");//住宅电话
        params.put("houseAreaCode", "");//住宅电话-区号
        params.put("houseHostNum", "");//住宅电话-主机号

        params.put("cpsEmergName", "");//紧急联系人姓名
        params.put("cpsEmergMobile", "");//紧急联系人手机
        params.put("cpsEmergRelation", "");//紧急联系人与持卡人关系:1亲属,2同事,3朋友
        params.put("cpsEmergTel", "");//紧急联系人固话
        params.put("emergAreaCode", "");//紧急联系人固话-区号
        params.put("emergHostNum", "");//紧急联系人固话-主机号

        params.put("cpsFamilyTel", "");	//亲属固话
        params.put("familyAreaCode", "");//亲属固话-区号
        params.put("familyHostNum", "");////亲属固话-主机号

        //其它参数
        params.put("type","1");
        params.put("result","");

        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Origin", "https://wap.cgbchina.com.cn");
        pros.put("Cookie", session);
        pros.put("Referer", "https://wap.cgbchina.com.cn/saveUnComplete.do");
        pros.put("User-Agent", userAgent);

        printParams(params);
        HttpUtil http = new HttpUtil(saveComplete, "utf-8",params,pros);
        String result = http.getResponseString();
//        String result = "";
        if(result.contains("mainWrap")){
            logger.info("提交申卡资料到广发银行失败，手机号："+guangFaModel.getCbsMobile());
            //提示代码：PB010103；提示信息：数据不能为空
            String errorMsg = getErrorMsg(result);
            ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis() + ".html", params, result, saveComplete, -1, result, guangFaModel.getCbsMobile());
            BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.submit_apply, errBean);
            return Message.error(errorMsg);
        }else if (result.contains("registerForm")){
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.submit_apply);
            logger.info("提交申卡资料到广发银行成功！，手机号："+guangFaModel.getCbsMobile());
            return Message.success("恭喜您，您的申请已成功提交！邮件已发送给您的电子邮箱，请查收！");
        }else {
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.submit_apply);
            return Message.success("恭喜您，您的申请已成功提交！提示邮件已发送给您的电子邮箱，请查收！");
        }
    }

    /**
     * 打印提交参数
     * @param params
     */
    private static void printParams(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for(String s :params.keySet()){
//            sb.append(s).append("\t").append(params.get(s)).append("\n");
            sb.append(s).append("=").append(params.get(s)).append("&");
        }
        logger.info(sb.toString());
    }

    /**
     * 获取html中input的值
     * @param html
     * @param string
     * @return
     */
    private static String getHtmlContent(String html, String string) {
        int index = html.indexOf("name=\""+string+"\" ");

        String result1 = html.substring(index, index+67);

        String result = result1.substring(result1.indexOf("value=\"")+7, result1.indexOf("\"/>"));
        return result;
    }

    /**
     * 获取第一步提交返回的错误信息
     * @param html
     * @return
     */
    private static String getErrorMsg(String html){
        String result1 = html.substring(html.indexOf("<table class=\"mainWrap\">"), html.indexOf("</table>"));

        String msg = result1.substring(result1.indexOf("<td colspan=\"2\">")+16, result1.indexOf("<br/>")).trim();
        String code = result1.substring(result1.lastIndexOf(";")+1, result1.indexOf("</td>")).trim();
        msg = Unicode.unicodeToGB(msg);

        logger.info("广发银行，用户信息第一步提交：错误代码："+code+";"+msg);

        return "提示代码："+code+";"+msg;
    }

    private static Message verify(MaterialBean bean){
        String smsCode = bean.getPhoneauthcode();
        MaterialModel model = bean.getModel();
        //第一页验证
        if (BankUtils.isEmpty(model.getCname())){
            return Message.error("姓名为空");
        }
        if (BankUtils.isEmpty(model.getCenglishname())){
            return Message.error("英文姓名为空");
        }
        if (!BankUtils.checkIdCardNo(model.getCidcard())){
            return Message.error("请输入正确的身份证号码");
        }
        if (!CheckUtil.isMobilephone(model.getCphone())){
            return Message.error("手机号码错误");
        }
        //第二页验证
        if (BankUtils.isEmpty(model.getCcompanyname())){
            return Message.error("单位名为空！");
        }
        if (BankUtils.isEmpty(model.getCcompany_detailaddress())){
            return Message.error("单位地址为空！");
        }
        if (BankUtils.isEmpty(model.getCcompany_telnum())){
            return Message.error("单位电话有误！");
        }
        if (BankUtils.isEmpty(model.getCdepartmentname())){
            return Message.error("任职部门为空");
        }
        if (BankUtils.isEmpty(model.getIdepartment())){
            return Message.error("职位为空");
        }
        if (!BankUtils.isInteger(model.getItimeinjob())){
            return Message.error("任职年数有误");
        }
        if (!BankUtils.isDouble(model.getIannualsalary())){
            return Message.error("年薪有误");
        }
        //第三页验证
        if (!BankUtils.isEmail(model.getCemail())){
            return Message.error("邮箱有误");
        }
        return Message.success("success");
    }

    /**
     * 发送查询广发信用卡申请进度的短信验证码
     * @param bean
     * @return
     */
    public static Message smsCodeForQueryApply(MaterialBean bean, MemCachedClient client) {
        String mobileNo = bean.getCphone();
        String idcardno = bean.getIdcardid();

        if(StringUtils.isEmpty(mobileNo)){
            return Message.errorJson(bean,"没有获取到手机号");
        }
        if(StringUtils.isEmpty(idcardno)){
            return Message.errorJson(bean,"没有获取到身份证号");
        }
        HashMap<String, String> params0 = new HashMap<String, String>();
        HashMap<String, String> pros0 = new HashMap<String, String>();
        pros0.put("User-Agent", userAgent);
        HttpUtil http0 = new HttpUtil(queryApplyInfo, "utf-8", params0, pros0);
        String result0 = "";
        try {
            result0 = http0.getResponseString();
        } catch (Exception e) {
            e.printStackTrace();
            return Message.errorJson(bean, "验证码发送异常："+e.getMessage());
        }
        String rtnType = getHtmlContent(result0, "rtnType");
        String session = http0.getHeaderField("Set-Cookie");

        if(StringUtils.isEmpty(rtnType)){
            return Message.errorJson(bean, "系统异常，请稍后再试，错误信息：rtnType为空。");
        }

        //发送验证码
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobileNo", mobileNo);//手机号
        params.put("cbs_idcardno",idcardno);
        params.put("last6OfCardNo",idcardno.substring(6,12));
        params.put("busCode","32");
        params.put("feeValue","undefined");
        params.put("srcChannel","");
        params.put("channel","MB");
        params.put("rtnType",rtnType);//变动

        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Origin", "https://wap.cgbchina.com.cn");//固定
        pros.put("Referer", "https://wap.cgbchina.com.cn/queryApply.do");//固定
        pros.put("User-Agent", userAgent);
        pros.put("Cookie", session);

        String result = "";
        try {
            HttpUtil http = new HttpUtil(smsCodeForQueryApplyStatus, "utf-8", params, pros);
            result = http.getResponseString();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis()+".html",params,result,smsCodeForQueryApplyStatus,-1,result,mobileNo);
            BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.phone_code, errBean);
            return Message.errorJson(bean, "验证码发送异常："+e.getMessage());
        }

//        JXmlWapper xml = JXmlWapper.parse(result);
        JSONObject object=JSONObject.parseObject(result);

        if ("0".equals(object.get("ec"))){
            client.set(mobileNo+"_session", session);
            client.set(mobileNo+"_rtnType",rtnType);
            logger.info("申卡进度查查询短信验证码发送成功，手机号："+mobileNo);
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.phone_code);
            return Message.successJson(bean, "验证码发送成功");
        }else {
            ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis()+".html",params,result,smsCodeForQueryApplyStatus,-1,result,mobileNo);
            BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.phone_code, errBean);
            return Message.errorJson(bean, object.get("em").toString());
        }
    }

    /**
     * 查询广发信用卡申请进度
     * @param bean
     * @return resultcode  -1 代表接口查询失败 0 申请中 ，1 申请通过，2申请拒绝，3没有申请记录
     */
    public static Message queryApply(MaterialBean bean, MemCachedClient client) {
        String mobile = bean.getCphone();
        String idcardno = bean.getIdcardid();
        String smsCode = bean.getPhoneauthcode();
        if(StringUtils.isEmpty(mobile)){
            return Message.errorJson(bean, "没有获取到手机号");
        }
        if(StringUtils.isEmpty(idcardno)){
            return Message.errorJson(bean, "没有获取到身份证号");
        }
        if(StringUtils.isEmpty(smsCode)){
            return Message.errorJson(bean, "没有获取到短信验证码");
        }
        String session = (String) client.get(mobile+"_session");
        String rtnType = (String)client.get(mobile + "_rtnType");

        if(StringUtils.isEmpty(session)){
            return Message.errorJson(bean, "查询超时，请重新获取验证码并查询");
        }
        if(StringUtils.isEmpty(rtnType)){
            return Message.errorJson(bean, "系统异常，请稍后再试，错误信息：rtnType为空。");
        }
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("sendTradeNo","");
        params.put("rtnType",rtnType);//变动
        params.put("s",rtnType);//变动
        params.put("busCode","32");//?
        params.put("businessCode","0");
        params.put("channel","MB");
        params.put("tranCode","MB9101");
        params.put("srcChannel","");
        params.put("sendTime", DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss"));
        params.put("feeValue","Y");
        params.put("cbs_idcardno",idcardno);
        params.put("mobileNo",mobile);
        params.put("smsCode",smsCode);

        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Origin", "https://wap.cgbchina.com.cn");//固定
        pros.put("Cookie", session);
        pros.put("Referer", "https://wap.cgbchina.com.cn/queryApply.do");//固定
        pros.put("User-Agent", userAgent);

        HttpUtil http = new HttpUtil(queryApply, "utf-8",params,pros);
        String html = "";
        try {
            html = http.getResponseString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject json = new JSONObject();
            json.put("resultcode","-1");
            json.put("resultdesc","查询失败");
            json.put("resean",e.getMessage());
            return Message.errorJson(bean, "查询失败：" + e.getMessage());
        }

      /*测试用
        String html = null;
        try {
            html = FileUtils.readFileToString(new File("C:/Users/Administrator/Desktop/7.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Document dom = Jsoup.parse(html);
        Elements element = dom.select("td[class=trightR]");
        if (element.size()==0){
            Elements ele = dom.getElementsByClass("mainWrap");
            ele = ele.select("span");
            //短信验证码错误，请重新输入
            //短信验证码超时或不存在，请重新输入
            String msg = ele.text();
            if(null!=msg&&msg.contains("短信验证码")&&(msg.contains("有误")||msg.contains("错误")||msg.contains("超时"))){
                return Message.errorJson(bean, msg);
            }else if (msg.contains("资料")&&msg.contains("有误")){
                JSONObject json = new JSONObject();
                bean.setCstatus("3");
                json.put("resultcode","3");
                json.put("resultdesc","没有申请记录");
                json.put("resean",msg);
                return Message.successJson(bean, "没有申请记录", json.toString());
            }else{
                ErrorRequestBean errBean = new ErrorRequestBean(System.currentTimeMillis() + ".html", params, html, queryApply, -1, msg, mobile);
                BankApplyListener.sendError(BankEnum.guangfa, BankApplyStepEnum.query_apply, errBean);
                return Message.errorJson(bean, msg);
            }
        }else {
            String result = element.get(3).text();//处理中、通过、不通过
            String reason = "";
            JSONObject json = new JSONObject();
            String code = "";
            if ("处理中".equals(result)){
                code = "0";
                result = "审核中";
            } else if ("通过".equals(result)){
                code = "1";
                element.get(4).text();
            } else if ("不通过".equals(result)){
                code = "2";
                result = "未通过";
                element.get(4).text();
            }
            if (StringUtils.isEmpty(reason)){
                reason = "";
            }
            if (result.equals("通过")){
                reason = "";
            }
            json.put("resultcode",code);
            json.put("resultdesc",result);
            json.put("resean",reason);
            bean.setCstatus(code);
            logger.info("手机号：["+mobile+"]，申卡进度查询结果："+json.toString());
            BankApplyListener.sendSucess(BankEnum.guangfa, BankApplyStepEnum.query_apply);
            return Message.successJson(bean, result, json.toString());
        }
    }

}
