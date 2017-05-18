package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.rest.controller.BankController;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.CookieUtil;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import com.hsk.cardUtil.HttpUtil;
import com.security.client.QuerySecurityInfoById;
import org.dom4j.Document;
import org.json.JSONObject;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by terry on 2016/7/4.
 */
public class PingAnBank {
    /***
     * 获取平安图片验证码
     * @param bean
     * @return BufferedImage 验证码对象
     */
    public static BufferedImage getPAVcode(Channel bean,Logger logger) {
        BufferedImage localBufferedImage=null;
        try {
            String cookies = "WEBTRENDS_ID=116.226.73.166-744009648.30459312;" +
                    " USER_TRACKING_COOKIE=115.231.133.13-1437701391839.839000000;" +
                    " MEDIA_SOURCE_NAME=creditcard.pingan.com;" +
                    " BIGipServerMTOA-paue_webPrdPool=1526996140.43893.0000;" +
                    " BIGipServerelis-pa18-nginx_DMZ_PrdPool=3607370924.40565.0000;" +
                    " BIGipServerTOA-sdc_DMZ_443_PrdPool=1543773356.43893.0000;" +
                    " WT-FPC=id=116.226.73.166-744009648.30459312:lv=1437716480593:ss=1437716480593:fs=1437716480593:pv_Num=1:vt_Num=1;" ;
            Map<String,String> propertys = new HashMap<String,String>();
            propertys.put("Cookie", cookies);
            HttpRequester httprequest = new HttpRequester();
            HttpRespons hr = httprequest.sendGet("https://m.pingan.com/xinyongka/index.screen?menuType=accountInfo?_=" + System.currentTimeMillis(), null, propertys);
            String jsession = hr.getCookieParam("JSESSIONID");
            //下载验证码
            String vcodeUrl="https://m.pingan.com/xinyongka/ImageGif.do?rd="+System.currentTimeMillis();
            Map<String,String> requestHeaderMap = new HashMap<String,String>();
            requestHeaderMap.put("Cookie", cookies + jsession);
            String path = BankController.LOC+"/pianan/"+bean.getCuserId();
            CookieUtil.getRandom("GET", vcodeUrl, null, requestHeaderMap, false, path);
            localBufferedImage= ImageIO.read(new File(path + "/code.bmp"));
            deleteDirectory(path);
            MemCachedClient cc = SpringContextUtilBro.getBean(MemCachedClient.class);
            cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", jsession);
        } catch (Exception e) {
            logger.error(bean.getCuserId()+" getPAVcode ",e);
        }
        return localBufferedImage;
    }

    /**
     * 获取平安银行Base64位图片验证码字符串
     * @param bean
     * @return Base64位图片验证码字符串
     */
    public static String getPABase64Vcode(Channel bean,Logger logger,MemCachedClient cc) {
        String base64code=null;
        try {
            String cookies = "WEBTRENDS_ID=116.226.73.166-744009648.30459312;" +
                    " USER_TRACKING_COOKIE=115.231.133.13-1437701391839.839000000;" +
                    " MEDIA_SOURCE_NAME=creditcard.pingan.com;" +
                    " BIGipServerMTOA-paue_webPrdPool=1526996140.43893.0000;" +
                    " BIGipServerelis-pa18-nginx_DMZ_PrdPool=3607370924.40565.0000;" +
                    " BIGipServerTOA-sdc_DMZ_443_PrdPool=1543773356.43893.0000;" +
                    " WT-FPC=id=116.226.73.166-744009648.30459312:lv=1437716480593:ss=1437716480593:fs=1437716480593:pv_Num=1:vt_Num=1;" ;
            Map<String,String> propertys = new HashMap<String,String>();
            propertys.put("Cookie", cookies);
            HttpRequester httprequest = new HttpRequester();
            HttpRespons hr = httprequest.sendGet("https://m.pingan.com/xinyongka/index.screen?menuType=accountInfo?_=" + System.currentTimeMillis(), null, propertys);
            String jsession = hr.getCookieParam("JSESSIONID");
            //下载验证码
            String vcodeUrl="https://m.pingan.com/xinyongka/ImageGif.do?rd="+System.currentTimeMillis();
            Map<String,String> requestHeaderMap = new HashMap<String,String>();
            requestHeaderMap.put("Cookie", cookies + jsession);
            String path = BankController.LOC+"/pianan/"+bean.getCuserId();
            CookieUtil.getRandom("GET",vcodeUrl, null, requestHeaderMap, false,path,"gif");
            BufferedImage localBufferedImage=ImageIO.read(new File(path + "/code.gif"));
            base64code= BankHelper.GetImageBase64(localBufferedImage, "gif");
            deleteDirectory(path);
            //base64code=CookieUtil.getRandomBase64Image("GET", vcodeUrl, null, requestHeaderMap, false, path);
            cc.set(bean.getCuserId()+bean.getBankId()+"bankSession", jsession);
        } catch (Exception e) {
            logger.error(bean.getCuserId()+" getPAVcode ",e);
        }
        return base64code;
    }



    public static int verifyMsg(Channel bean,MemCachedClient cc,String userIp,Logger logger){
        String bankrand=bean.getBankRand();
        String isclent=bean.getClient();
        String bankSessionId=bean.getBankSessionId();
        String idcard="";
        String bankpwd="";
        String errorcontent="";

        try{
            String cookies = "WEBTRENDS_ID=116.226.73.166-744009648.30459312;" +
                    " USER_TRACKING_COOKIE=115.231.133.13-1437701391839.839000000;" +
                    " MEDIA_SOURCE_NAME=creditcard.pingan.com;" +
                    " BIGipServerMTOA-paue_webPrdPool=1526996140.43893.0000;" +
                    " BIGipServerelis-pa18-nginx_DMZ_PrdPool=3607370924.40565.0000;" +
                    " BIGipServerTOA-sdc_DMZ_443_PrdPool=1543773356.43893.0000;" +
                    " WT-FPC=id=116.226.73.166-744009648.30459312:lv=1437716480593:ss=1437716480593:fs=1437716480593:pv_Num=1:vt_Num=1;" ;

            if ("0".equals(bean.getType())) {
                if (CheckUtil.isNullString(bean.getBankRand())||CheckUtil.isNullString(bean.getIdCardNo())||CheckUtil.isNullString(bean.getIskeep())||CheckUtil.isNullString(bean.getBankPwd())) {
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("非法操作,缺少必要参数");
                    return 0;
                }
                if ("1".equals(isclent)) {
                    bankpwd= CaiyiEncryptIOS.dencryptStr(bean.getBankPwd());
                    idcard=CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
                }else {
                    bankpwd= CaiyiEncrypt.dencryptStr(bean.getBankPwd());
                    idcard=CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
                }
            }else if ("1".equals(bean.getType())) {

                if ("0".equals(bean.getIskeep())) {
                    //已保存密码
                    QuerySecurityInfoById ssi = new QuerySecurityInfoById();
                    ssi.setUid(bean.getCuserId());
                    ssi.setCreditId(bean.getCreditId());
                    ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId() + BankHelper.MD5_KEY));
                    ssi.setServiceID("2000");
                    String s = ssi.call(30);
                    logger.info("s="+s);
                    if (CheckUtil.isNullString(s)) {
                        logger.info(ssi.getUid() +"-"+ ssi.getCreditId() +"-"+ BankHelper.MD5_KEY);
                        bean.setBusiErrCode(BillConstant.fail);
                        bean.setBusiErrDesc("无效的银行卡信息");
                        return 0;
                    }

                    Document doc = XmlTool.stringToXml(s);
                    org.dom4j.Element ele = doc.getRootElement();
                    String errcode = ele.attributeValue("errcode");
                    if ("0".equalsIgnoreCase(errcode)) {
                        idcard = XmlTool.getElementValue("accountName",ele);
                        bankpwd = XmlTool.getElementValue("accountPwd", ele);
                    }else {
                        bean.setBusiErrCode(BillConstant.fail);
                        bean.setBusiErrDesc("无效的银行卡信息");
                        return 0;
                    }
                }else {
                    if (CheckUtil.isNullString(bean.getIdCardNo())||CheckUtil.isNullString(bean.getBankPwd())) {
                        bean.setBusiErrCode(BillConstant.fail);
                        bean.setBusiErrDesc("身份证或查询密码为空！");
                        return 0;
                    }
                    if ("1".equals(isclent)) {
                        idcard=CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
                    }else {
                        idcard=CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
                    }
                }
                if (!CheckUtil.isNullString(bean.getBankPwd())) {
                    if ("1".equals(isclent)) {
                        bankpwd=CaiyiEncryptIOS.dencryptStr(bean.getBankPwd());
                    }else {
                        bankpwd=CaiyiEncrypt.dencryptStr(bean.getBankPwd());
                    }
                }

            }else {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("无效的操作类型");
                return 0;
            }

            Map<String, String> parames = new HashMap<String, String>();
            parames.put("userId", idcard);
            parames.put("pwd", bankpwd);
            parames.put("rndCode", bankrand);
            parames.put("rmbUserId", "");

            Map<String,String> propertys = new HashMap<String,String>();
            propertys.put("Cookie", cookies+bankSessionId);
//            if (!CheckUtil.isNullString(userIp)) {
//                propertys.put("X-Forwarded-For", userIp);
//                logger.info(bean.getTaskid()+" userip="+userIp);
//            }

            /*********** 单卡情况  **************/
            // 1 个人信息            2 本期账单              3 未出账单
            //登录个人信息
            errorcontent = HttpUtil.http("https://m.pingan.com/xinyongka/toLogin.do?menuType=accountInfo", parames, propertys);
            if (CheckUtil.isNullString(errorcontent)) {
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("登录失败，请确认你输入的账号是否正确或稍后再试");
                return 0;
            }


            JSONObject json = null;
            try{
                json = new JSONObject(errorcontent);
            }catch (Exception e){
                logger.info(bean.getTaskid()+" 系统繁忙,请稍候再试 errorcontent="+errorcontent);
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("系统繁忙,请稍候再试");
                return 0;
            }
            String recode=String.valueOf(json.get("ret_code"));
            String errMsg="";
            if(!("0000").equals(recode)){//登录失败

                if ("1010".equals(recode)||"0012".equals(recode)||"0010".equals(recode)){
                    errMsg="系统繁忙";
                    logger.info(bean.getCuserId()+" 系统繁忙["+errorcontent+"]");
                }else {
                    errMsg=String.valueOf(json.get("msg"));
                }

                logger.info(bean.getCuserId()+" 登录失败 ["+errMsg+"]");
                if (errMsg.equals("用户名、密码验证未通过！")||errMsg.equals("密码被连续输错5次，账户已被锁定24小时")) {
                    bean.setBusiErrCode(0);
                }else {
                    bean.setBusiErrCode(5);
                }
                bean.setBusiErrDesc(errMsg);
                return 0;
            }
            bean.setReportHtml1(json.getString("redirectURL"));
        }catch (Exception e){
            logger.error("PingAnBank-verifyMsg-error ",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("网络繁忙");
            logger.info(bean.getCuserId() + " 网络繁忙[" + errorcontent + "]");
            return 0;
        }
        return 1;
    }




    public static boolean deleteDirectory(String sPath) {
        boolean flag = true;
        try {
            //如果sPath不以文件分隔符结尾，自动添加文件分隔符
            if (!sPath.endsWith(File.separator)) {
                sPath = sPath + File.separator;
            }
            File dirFile = new File(sPath);
            //如果dir对应的文件不存在，或者不是一个目录，则退出
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return false;
            }
            //删除文件夹下的所有文件(包括子目录)
            File[] files = dirFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                //删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) break;
                } //删除子目录
                else {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) break;
                }
            }
            if (!flag) return false;
            //删除当前目录
            if (dirFile.delete()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static void main(String[] args) throws Exception {
        String cookies = "WEBTRENDS_ID=116.226.73.166-744009648.30459312;" +
                " USER_TRACKING_COOKIE=115.231.133.13-1437701391839.839000000;" +
                " MEDIA_SOURCE_NAME=creditcard.pingan.com;" +
                " BIGipServerMTOA-paue_webPrdPool=1526996140.43893.0000;" +
                " BIGipServerelis-pa18-nginx_DMZ_PrdPool=3607370924.40565.0000;" +
                " BIGipServerTOA-sdc_DMZ_443_PrdPool=1543773356.43893.0000;" +
                " WT-FPC=id=116.226.73.166-744009648.30459312:lv=1437716480593:ss=1437716480593:fs=1437716480593:pv_Num=1:vt_Num=1;" ;
        Map<String,String> propertys = new HashMap<String,String>();
        propertys.put("Cookie", cookies);
        HttpRequester httprequest = new HttpRequester();
        HttpRespons hr = httprequest.sendGet("https://m.pingan.com/xinyongka/index.screen?menuType=accountInfo?_=" + System.currentTimeMillis(), null, propertys);
        String jsession = hr.getCookieParam("JSESSIONID");
        //下载验证码
        String vcodeUrl = "https://m.pingan.com/xinyongka/ImageGif.do?rd=" + System.currentTimeMillis();
        Map<String,String> requestHeaderMap = new HashMap<String,String>();
        requestHeaderMap.put("Cookie", cookies + jsession);
        String path = "D:\\opt\\007";
        CookieUtil.getRandom("GET",vcodeUrl, null, requestHeaderMap, false,path,"gif");
        BufferedImage localBufferedImage=ImageIO.read(new File(path + "/code.gif"));
//        String base64code= BankHelper.GetImageBase64(localBufferedImage, "gif");

//        String base64code= CookieUtil.getRandomBase64Image("GET", vcodeUrl, null, requestHeaderMap, false, path);

        deleteDirectory(path);
        System.out.println(1111);
    }


}
