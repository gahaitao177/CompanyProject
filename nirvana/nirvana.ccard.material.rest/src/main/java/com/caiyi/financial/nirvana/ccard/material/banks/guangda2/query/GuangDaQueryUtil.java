package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.query;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaException;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaHttpsUtils;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.discount.Constants;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsl on 2016/3/2.
 */
public class GuangDaQueryUtil {
    public static Logger logger = LoggerFactory.getLogger("GuangDaQueryUtil");
    private static boolean LOCAL_TEST = false;
//     @Autowired
//    private   MemCachedClient client;




    public static BufferedImage guangDaStatusImgCode(MaterialBean bean,MemCachedClient client) throws IOException {
        String idcardid = bean.getIdcardid();//身份证
        String cphone = bean.getCphone();

        GuangDaQueryBean gd = new GuangDaQueryBean();
        CookieStore cookieStore = new BasicCookieStore();
        gd.setCookieStore(cookieStore);
        byte[] bytes = cardApplyStatusGetImgCode(gd);

        gd.setId_no(idcardid);
        client.set(idcardid+"_guangda",gd, Constants.TIME_HOUR);

        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        return ImageIO.read(bin);
    }


    /**
     * 光大手机验证码
     * @param bean
     */
    public static void guangDaStatusPhoneCode(MaterialBean bean,MemCachedClient client) {
        String idcardid = bean.getIdcardid();//身份证
        String imgauthcode = bean.getImgauthcode();// imgauthcode:图片验证码

        GuangDaQueryBean gd = (GuangDaQueryBean) client.get(idcardid+"_guangda");
        if(gd!=null){
            gd.setVerify_code(imgauthcode);
            String desc =  cardApplyStatusGetActiveCode(gd);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc(desc);

        }else{
            throw new GuangDaException(-1,"会话已经过期");
        }
    }


    /**
     * 光大进度查询
     * 需要姓名 身份证 图片验证码 短信验证码
     * @param bean
     * @return
     */
    public static int guangDaStatusQuery(MaterialBean bean,MemCachedClient client) {
        String idcardid = bean.getIdcardid();//身份证
        String phoneauthcode = bean.getPhoneauthcode();// phoneauthcode:手机验证码
        String cname = bean.getCname();
        String imgauthcode = bean.getImgauthcode();

        GuangDaQueryBean gd = (GuangDaQueryBean) client.get(idcardid+"_guangda");
        if(gd==null){
            throw new GuangDaException(-1,"会话已经过期");
        }
        gd.setDynPasswd(phoneauthcode);
        gd.setName(cname);
        gd.setVerify_code(imgauthcode);
        GuangDaApplyStatus applyStatus =  cardApplyStatusQuery(gd);

        bean.setBusiErrCode(1);
        bean.setBusiErrDesc(applyStatus.getStateValue());
        bean.setCstatus(applyStatus.getState()+"");

        bean.setBusiJSON("{\"resultcode\": " + applyStatus.getState() + ",\"resultdesc\":\"" + applyStatus.getStateValue() + "\",\"resean\":\"\"}");
        return 1;
    }







    /**
     * 获得申请进度的图片验证码  1
     * @param gd
     * @return
     */
     static byte[] cardApplyStatusGetImgCode(GuangDaQueryBean gd){
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());

        HttpGet get = new HttpGet("https://xyk.cebbank.com/verify_code.jpg");

        CloseableHttpResponse response = null;
        byte[] bytes = null;
        try {
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            System.out.println("statusLine----------" + statusLine);
            HttpEntity entity = response.getEntity();
            bytes = EntityUtils.toByteArray(entity);

            if(LOCAL_TEST){
                FileOutputStream fos = new FileOutputStream("/data/c.jpg");
                fos.write(bytes);
                System.out.println("图片下载目录：/data/c.jpg");
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GuangDaHttpsUtils.close(client, response);
            return bytes;
        }
    }
    /**
     * 获得申请进度的动态验证码 2
     * @param gd
     * @return
     */
    static String cardApplyStatusGetActiveCode(GuangDaQueryBean gd){
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());

        String url = "https://xyk.cebbank.com/home/fz/application_get_activityCode.htm";

        String id_no = gd.getId_no();
        String img_code = gd.getVerify_code();
        if(StringUtils.isEmpty(id_no) ){
            throw new GuangDaException("参数异常");
        }
        if( StringUtils.isEmpty(img_code)){
            throw new GuangDaException("图片验证码不能为空");
        }
        Map<String,String> map = new HashMap<>();
        map.put("id_no", gd.getId_no());//证件号码
        map.put("ver_code", gd.getVerify_code());//验证码
        map.put("id_Type", "A");//证件类型
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };
        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);
        System.out.println(respStr);
        logger.info(respStr);
        GuangDaHttpsUtils.close(client, null);

        JSONObject json = JSONObject.parseObject(respStr);

        if(json.getInteger("flag")==0){
            //成功
            return "动态口令已发送，请注意查收！";
        }else{
            String errMsg = json.getString("msg");
            throw new GuangDaException(errMsg);
        }

    }


    /**
     * 查询申请进度 3
     * @param gd
     */
     static GuangDaApplyStatus cardApplyStatusQuery(GuangDaQueryBean gd){
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());
        String url = "https://xyk.cebbank.com/home/fz/card-app-status-query.htm";
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };
        Map<String,String> map = new HashMap<>();

        String cname = gd.getName();
        String id_no = gd.getId_no();
        String imgcode = gd.getVerify_code();
        String dynPasswd = gd.getDynPasswd();
        if(StringUtils.isEmpty(cname) || StringUtils.isEmpty(id_no)){
            throw new GuangDaException("参数错误");
        }
        if(StringUtils.isEmpty(imgcode)){
            throw new GuangDaException("图片验证码为空");
        }
        if(StringUtils.isEmpty(dynPasswd)){
            throw new GuangDaException("手机动态验证码为空");
        }
        map.put("name",cname);//姓名
        map.put("card_id_type","A");//证件类型
        map.put("id_no",id_no);//证件号码
        map.put("ver_code", imgcode);//验证码
        map.put("activity_code", dynPasswd);//动态密码

        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);
        GuangDaHttpsUtils.close(client, null);

        Document doc = Jsoup.parse(respStr);

        //表格
        Element ele = null;
        try{

            ele = doc.getElementsByClass("lab").get(0).getElementsByTag("tbody").get(0);
        }catch (Exception e){
            e.printStackTrace();
            ErrorRequestBean bean = new ErrorRequestBean(gd,respStr);
            BankApplyListener.sendError(BankEnum.guangda, BankApplyStepEnum.query_apply,bean);
            if(LOCAL_TEST){
                System.out.println("-----------------------------------------请求结果------------------------------------");
                System.out.println(respStr);
                System.out.println("-----------------------------------------请求结果------------------------------------");
            }
            throw new GuangDaException("请求失败，错误原因：动态密码错误或者服务器繁忙!");
        }
        try{

        }catch (Exception e){
            logger.error(e.toString());
        }
        //
        ele = ele.getElementsByTag("tr").get(1);
        Elements trs = ele.children();
        //持卡人
        String name = trs.get(0).text().trim().replace(" ", "");
        //申请卡种
        String cardName = trs.get(1).text().trim().replace(" ", "");
        //批复卡种
        String replyCardName = trs.get(2).text().trim().replace(" ", "");

        //进件日期
        String date = trs.get(3).text().trim().replace(" ","");
        //主卡申请状态  审批已拒绝
        String stateValue =  trs.get(4).text().trim().replace(" ","");
        //副卡持卡人
        //副卡申请状态
        //运单号

        int state = 0;
        if(stateValue.contains("审批已拒绝")){
            stateValue = "未通过";
            state = 2;
        }else if(stateValue.contains("审批通过")){
            stateValue = "通过";
            state = 1;
        }else{
            stateValue = "审核中";
            logger.info("光大申卡查询："+name + "---------" + cardName + "------" + date + "-------" + stateValue);
            state = 0;
        }

         BankApplyListener.sendSucess(BankEnum.guangda,BankApplyStepEnum.query_apply);
//        logger.info(name + "---------" + cardName + "------" + date + "-------" + stateValue);
        GuangDaApplyStatus applyStatus = new GuangDaApplyStatus();
        applyStatus.setName(name);
        applyStatus.setCardName(cardName);
        applyStatus.setReplyCardName(replyCardName);
        applyStatus.setDate(date);
        applyStatus.setStateValue(stateValue);
        applyStatus.setState(state);
        return applyStatus;

    }
}
