package com.caiyi.financial.nirvana.bill.util;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.bank.*;
import com.caiyi.financial.nirvana.bill.bank.multibank.PluginBankService;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.utils.LoginUtil;
import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import com.caiyi.financial.nirvana.discount.utils.XmlTool;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import org.apache.http.client.config.RequestConfig;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by terry on 2016/6/28.
 */
public class BankHelper {
    public final static String MD5_KEY = SystemConfig.get("security.md5Key");
    public static Logger logger = LoggerFactory.getLogger(BankHelper.class);
    public final static int yzNum= SystemConfig.getInt("file.res_picNums");
    public final static String enUrl= SystemConfig.get("file.enUrl");
    public final static String androidUrl= SystemConfig.get("file.androidUrl");
    public final static String iosUrl= SystemConfig.get("file.iosUrl");
    public final static String fileUrl =  SystemConfig.get("file.filePath");
    public static Map<String,String> bankNames=new HashMap<>();
//    public static TokenUtil tokenUtil = null;
    public static LoginUtil loginUtil = null;
    static {
        bankNames.put(BillConstant.GUANGFA,"广发银行");
        bankNames.put(BillConstant.ZHONGXIN,"中信银行");
        bankNames.put(BillConstant.GUANGDA,"光大银行");
        bankNames.put(BillConstant.NONGYE,"农业银行");
        bankNames.put(BillConstant.HUAQI,"花旗银行");
        bankNames.put(BillConstant.PINGAN,"平安银行");
        bankNames.put(BillConstant.PUFA,"浦发银行");
        bankNames.put(BillConstant.XINGYE,"兴业银行");
        bankNames.put(BillConstant.MINSHENG,"民生银行");
        bankNames.put(BillConstant.JIANSHE,"建设银行");
        bankNames.put(BillConstant.JIAOTONG,"交通银行");
        bankNames.put(BillConstant.SHANGHAI,"上海银行");
        bankNames.put(BillConstant.ZHAOSHANG,"招商银行");
        bankNames.put(BillConstant.GONGSHANG,"工商银行");
        bankNames.put(BillConstant.ZHONGGUO,"中国银行");
        bankNames.put(BillConstant.HUAXIA,"华夏银行");
//        if (tokenUtil == null) {
//            tokenUtil = SpringContextUtilBro.getBean(TokenUtil.class);
//        }
        if (loginUtil == null) {
            loginUtil = SpringContextUtilBro.getBean(LoginUtil.class);
        }

    }


    /**
     * 获取验证码图片
     * @param bean
     * @return
     * @throws Exception
     */
    public static String getBankVerifyCode(Channel bean, MemCachedClient cc) throws Exception{
        String base64Img=null;
        String bankId = bean.getBankId();
        if (BillConstant.ZHAOSHANG.equals(bankId)) {//招商银行
            ZhaoShangBank zhaoshang = new ZhaoShangBank();
            base64Img = zhaoshang.setYzm(bean,cc);
        }else if(BillConstant.GUANGDA.equals(bankId)) {//光大银行
            GuangDaBank guangDaBank = new GuangDaBank();
            base64Img = guangDaBank.setYzm(bean,cc);//getGDVcode(bean);
        }else if (BillConstant.PINGAN.equals(bankId)) {//平安银行
            base64Img = PingAnBank.getPABase64Vcode(bean,logger,cc);
        }else if (BillConstant.ZHONGGUO.equals(bankId)||
                BillConstant.GONGSHANG.equals(bankId)||
                BillConstant.HUAXIA.equals(bankId)) {//有插件的银行老方式登录获取验证码
            PluginBankService pluginBankService = new PluginBankService();
            base64Img = pluginBankService.getYzmBase64(bean);
        }else if (BillConstant.NONGYE.equals(bankId)){
            NongYeBank nongye = new NongYeBank();
            base64Img = nongye.setYzm(bean,cc);
        }else if (BillConstant.PUFA.equals(bankId)){
            PuFaBank pufa = new PuFaBank();
            base64Img = pufa.setYzm(bean,cc);
        }else if (BillConstant.SHANGHAI.equals(bankId)){
            ShangHaiBank shangHai = new ShangHaiBank();
            base64Img = shangHai.setYzm(bean,cc);
        }else if (BillConstant.MINSHENG.equals(bankId)){
            MinShengBank minSheng = new MinShengBank();
            base64Img = minSheng.setYzm(bean,cc);
        }else if (BillConstant.ZHONGXIN.equals(bankId)){
            ZhongXinBank zhongXin = new ZhongXinBank();
            base64Img = zhongXin.setYzm(bean,cc);
        }else{
            base64Img="-1";
        }
        return  base64Img;
    }


    /**
     * 验证登录
     * @param token
     * @param appId
     * @param base
     * @param ipAddr
     * @param cc
     * @param client
     */
//    public static void setUserData(String token,String appId, BaseBean base,String ipAddr,MemCachedClient cc,IDrpcClient client){
//        base.setCuserId("");
//        base.setPwd("");
//        base.setBusiErrCode(BillConstant.success);
//        try{
//            if (CheckUtil.isNullString(token) || CheckUtil.isNullString(appId)) {
//                System.out.println(" bean.getAccessToken() :"+ token);
//                System.out.println(" bean.getAppId() :"+ appId);
//                base.setBusiErrCode(BillConstant.fail);
//                base.setBusiErrDesc("非法操作，缺少系统参数");
//                return ;
//            }
//
//            String [] result = TokenGenerator.authToken(token, appId);
//            if ("1".equals(result[0])) {
////            String ipAddr = getRealIp(request).trim();
//                base.setIpAddr(ipAddr);
//                TokenBean tokenn = null;
//                Object obj =  cc.get(appId);
//                if (obj != null) {//30分钟内使用过该token
//                    tokenn = (TokenBean) obj;
//
//                    base.setCuserId(tokenn.getCuserId());
//                    base.setPwd(tokenn.getPwd());
//                    base.setPwd9188(tokenn.getPwd9188());
//                    base.setParamJson(tokenn.getParamJson());
//                    base.setBusiErrCode(1);
//                    base.setBusiErrDesc("success");
//                } else {
//                    User user = new User();
//                    user.setAccessToken(token);
//                    user.setAppId(appId);
//                    user.setIpAddr(ipAddr);
//
//                    String str = client.execute(Constant.HSK_USER, new DrpcRequest("user", "query_userToken", user));
//                    logger.info("query_userToken  str="+str);
//                    if (CheckUtil.isNullString(str)){
//                        base.setBusiErrCode(BillConstant.fail);
//                        base.setBusiErrDesc("未找到相关token");
//                        return;
//                    }
//                    JSONObject jsonObject = JSONObject.parseObject(str);
//                    String code = jsonObject.getString("code");
//                    String desc = jsonObject.getString("desc");
//                    if(code == null || BoltResult.SUCCESS.equals(code)) {
//                        String userId = jsonObject.getString("cuserId");
//                        String pwd = jsonObject.getString("pwd");
//                        String pwd9188 = jsonObject.getString("pwd9188");
//                        String paramJson = jsonObject.getString("paramJson");
//                        base.setCuserId(userId);
//                        base.setPwd(pwd);
//                        base.setPwd9188(pwd9188);
//                        base.setParamJson(paramJson);
//                        base.setBusiErrCode(1);
//                        base.setBusiErrDesc("success");
//
//                        tokenn = new TokenBean();
//                        tokenn.setCuserId(userId);
//                        tokenn.setPwd(pwd);
//                        tokenn.setPwd9188(pwd9188);
//                        tokenn.setAccessToken(token);
//                        tokenn.setAppid(appId);
//                        tokenn.setParamJson(paramJson);
//
//                        boolean flag = cc.set(appId, tokenn, 30); //放入缓存中30分钟
//                        System.out.println("缓存token信息 =" + appId + "  结果:" + flag);
//                    }
//                }
//            } else {
//                base.setBusiErrCode(BillConstant.fail);
//                base.setBusiErrDesc(result[1]);
//            }
//        }catch (Exception e){
//            logger.error("setUserData异常",e);
//            base.setBusiErrCode(BillConstant.fail);
//            base.setBusiErrDesc("验证token失败");
//            return;
//        }
//    }

    /**
     *  新版本验证token
     * @param token
     * @param appId
     * @param ipAddr
     * @param base
     */
    public static void setUserData(String token,String appId,String ipAddr,BaseBean base){
        try {
            base.setAccessToken(token);
            base.setAppId(appId);
            base.setIpAddr(ipAddr);
//            tokenUtil.getUserByTokenbase);
            loginUtil.getUserByToken(base);
        }catch (Exception e){
            base.setBusiErrCode(BillConstant.fail);
            base.setBusiErrDesc("验证token失败");
            logger.error("setUserData:", e);
        }



    }
    public static int send_Sms(Channel bean, MemCachedClient cc, IDrpcClient client) throws Exception {
        String bankid = bean.getBankId();
        if (BillConstant.ZHONGXIN.equals(bankid)) {
            ZhongXinBank zhongXinBank = new ZhongXinBank();
            return zhongXinBank.getSms(bean,cc);
        }else if(BillConstant.HUAXIA.equals(bankid)
                || BillConstant.GONGSHANG.equals(bankid)||
                BillConstant.ZHONGGUO.equals(bankid)){
            PluginBankService pluginBankService = new PluginBankService();
            return pluginBankService.getSms(bean);
        }else if(BillConstant.GUANGDA.equals(bankid)){
            GuangDaBank guangDaService = new GuangDaBank();
            int retCode = guangDaService.getSms(bean, cc);
            String errMsg = bean.getBusiErrDesc();
            if(retCode==0 && errMsg.contains("验证码错误")){
                String base64Img = guangDaService.setYzm(bean,cc);
                bean.setBankRand(base64Img);
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("获取图片验证码");
                return 0;
            }
            return retCode;
        }else if (BillConstant.PUFA.equals(bankid)){
            PuFaBank pufa = new PuFaBank();
            return pufa.getSms(bean,cc);
        }else if (BillConstant.NONGYE.equals(bankid)){
            NongYeBank nongye = new NongYeBank();
            return nongye.getSms(bean,cc);
        }else if (BillConstant.SHANGHAI.equals(bankid)){
            ShangHaiBank shangHai = new ShangHaiBank();
            return shangHai.getSms(bean,cc);
        }else if (BillConstant.ZHAOSHANG.equals(bankid)){
            ZhaoShangBank zhaoShang = new ZhaoShangBank();
            return zhaoShang.getSms(bean,cc);
        }else {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("无效的银行类型!");
            return 0;
        }
    }

    public static int check_Sms(Channel bean , MemCachedClient cc, IDrpcClient client) throws Exception {
        try {
            String bankid = bean.getBankId();
            AbstractHttpService httpService;
            int code = 0;
            if(BillConstant.ZHONGXIN.equals(bankid)){
                httpService = new ZhongXinBank();
                code = httpService.checkSms(bean,cc);
            }else if(BillConstant.HUAXIA.equals(bankid)
                    || BillConstant.GONGSHANG.equals(bankid)
                    || BillConstant.ZHONGGUO.equals(bankid)){
                httpService = new PluginBankService();
                code = httpService.checkSms(bean,cc);
            }else if (BillConstant.PUFA.equals(bankid)){
                httpService = new PuFaBank();
                code = httpService.checkSms(bean,cc);
            }else if (BillConstant.ZHAOSHANG.equals(bankid)){
                httpService = new ZhaoShangBank();
                code = httpService.checkSms(bean,cc);
            }else if (BillConstant.NONGYE.equals(bankid)){
                httpService = new NongYeBank();
                code = httpService.checkSms(bean,cc);
            }else if (BillConstant.SHANGHAI.equals(bankid)){
                httpService = new ShangHaiBank();
                code = httpService.checkSms(bean,cc);
            }else if(BillConstant.GUANGDA.equals(bankid)){
                cc.set(bean.getCuserId() + bankid + "guangda_optRand", bean.getBankRand(), 3600000);
                httpService = new GuangDaBank();
                String base64Img = httpService.setYzm(bean,cc);
                bean.setBankRand(base64Img);
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("需要图片验证码");
                bean.setCode("4");
                httpService.changeCode(bean,client);
                return 0;
            }else {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("无效的银行类型!");
                return 0;
            }
            if (code == 0){
                bean.setCode("0");
            }else{
                bean.setCode("1");
            }
            httpService.changeCode(bean,client);
            return code;
        } catch (Exception e) {
            logger.error("check_Sms异常["+bean.getCuserId()+"]", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("验证短信失败!");
        }
        return 0;
    }

    public static int send_teSms(Channel bean, MemCachedClient cc, IDrpcClient client) throws Exception {

        if (BillConstant.ZHAOSHANG.equals(bean.getBankId())) {
            ZhaoShangBank zhaoShangBank = new ZhaoShangBank();
            return zhaoShangBank.getSmste(bean, cc);
        }else if (BillConstant.JIAOTONG.equals(bean.getBankId())) {
            JiaoTongBank jiaoTongBank = new JiaoTongBank();
            return jiaoTongBank.getSms(bean, cc);
        } else {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("无效的银行类型!");
            return 0;
        }
    }

    public static int check_teSms(Channel bean , MemCachedClient cc, IDrpcClient client) throws Exception {
        try {
            if (BillConstant.ZHAOSHANG.equals(bean.getBankId())) {
                ZhaoShangBank zhaoShangBank = new ZhaoShangBank();
                return zhaoShangBank.checkSmsTe(bean, cc, client);
            }else if (BillConstant.JIAOTONG.equals(bean.getBankId())) {
                JiaoTongBank jiaoTongBank = new JiaoTongBank();
                return jiaoTongBank.checkSms(bean, cc, client);
            }else {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("无效的银行类型!");
                return 0;
            }
        } catch (Exception e) {
            logger.error("check_teSms异常[" + bean.getCuserId() + "]", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("验证短信失败!");
        }
        return 0;
    }


    public static String decodeYzm(String base64code,String bankId,String imgtype){
        HttpRequester hq = new HttpRequester();
        String decode="";
        try {
            String strImg=  java.net.URLEncoder.encode(base64code, "utf-8");
            String url =enUrl+ "?captcha="+strImg + "&bankid="+bankId+"&imgtype="+imgtype ;
            HttpRespons hr = hq.sendGet(url);
            //获取招商银行的登录sessionID
            String content=hr.getContent();
            System.out.println(content);
            JSONObject json=JSONObject.parseObject(content);
            String code=json.getString("code");
            if ("0".equals(code)){
                decode=json.getString("text");
                logger.info("识别成功 bankId[\"+bankId+\"] imgtype[\"+imgtype+\"] code="+decode);
            }else{
                logger.info("识别失败 bankId[\"+bankId+\"] imgtype[\"+imgtype+\"]"+content);
            }
        } catch (Exception e) {
            logger.error("decodeYzm 异常 bankId[" + bankId + "] imgtype[" + imgtype + "]", e);
        }
        return decode;
    }

    public static int readExtracodeFlag(Channel bean, String isFrist){
        if ("1".equals(isFrist)){
            return 1;
        }
        String url= fileUrl;
       /* //安卓
        if ("0".equals(bean.getClient())){
            url=fileUrl;
        }else {
            url=fileUrl;
        }*/
        String bname=bankNames.get(bean.getBankId());
        if (CheckUtil.isNullString(bname)){
            bean.setBusiErrDesc("暂不支持该银行");
            return -1;
        }
        try {
            File file=new File(url);
            Document doc= XmlTool.read(file);
//            Document doc=XmlTool.read(new URL(url), "gbk");
            List<org.dom4j.Element> eles=doc.getRootElement().elements("bank");
            for (int i = 0; i < eles.size(); i++) {
                org.dom4j.Element ele=eles.get(i);
                String bankName=ele.elementText("bankName");
                if (bname.equals(bankName)){
                    org.dom4j.Element loginType=ele.element("loginType");
                    if (loginType==null){
                        bean.setBusiErrDesc("暂不支持该银行");
                        return -1;
                    }
                    org.dom4j.Element accountType=loginType.element("accountType");
                    if (accountType==null){
                        bean.setBusiErrDesc("暂不支持该银行");
                        return -1;
                    }
                    String showAuthCode=accountType.elementText("showAuthCode");
                    if (CheckUtil.isNullString(showAuthCode)){
                        return 1;
                    }
                    if ("false".equals(showAuthCode)){
                        return 0;
                    }else if("true".equals(showAuthCode)){
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("readExtracodeFlag 异常",e);
            bean.setBusiErrDesc("读取配置异常，请稍后再试");
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        Channel bean=new Channel();
        bean.setBankId("21");
        bean.setClient("0");
        System.out.println(readExtracodeFlag(bean, "1"));


    }

    public static RequestConfig getRequestConfig() {
        RequestConfig.custom().setConnectTimeout(30000);
        RequestConfig.custom().setSocketTimeout(30000);
        RequestConfig.custom().setConnectionRequestTimeout(30000);
        RequestConfig requestConfig = RequestConfig.custom().build();
        return requestConfig;
    }
    /**
     * 获取URL链接参数
     * @param cookie
     * @param param
     * @return
     */
    public static String getURLParam(String cookie, String param) {
        int begin = cookie.lastIndexOf(param);
        if (begin != -1) {
            String beginStr = cookie.substring(begin);
            int end = beginStr.indexOf("&");
            int start=beginStr.indexOf("=")+1;
            if (end != -1) {
                return beginStr.substring(start, end);
            }else{
                return beginStr.substring(start);
            }
        }
        return "";
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip.indexOf("192.168") > -1 || ip.indexOf("127.0.0") > -1) {
            String xf = request.getHeader("X-Forwarded-For");
            if(xf != null){
                ip = xf.split(",")[0];
            }
        }
        return ip;
    }

    /***
     * 图片转化成base64位字符串
     * @param image BufferedImage对象
     * @param imgType 图片类型如jpeg,png
     * @return
     */
    public static String GetImageBase64(BufferedImage image,String imgType) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        try {
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            ImageIO.write(image, imgType, output);
            data=output.toByteArray();
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(data);// 返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
