package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.apply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaException;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaHttpsUtils;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums.*;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.ErrorFileUtil;
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
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wsl on 2016/3/2.
 */
public class GuangDaApplyUtil {
    public static Logger logger = LoggerFactory.getLogger("GuangDaApplyUtil");
    private static boolean LOCAL_TEST = false;
    private static String ERROR_PATH = "/data/";

//    @Autowired
//    static  MemCachedClient  client;

    /**
     * 获得图片验证码
     * @param bean
     * @return
     * @throws IOException
     */
    public static BufferedImage getImgCode(MaterialBean bean,MemCachedClient  client) throws IOException {
        MaterialModel model = bean.getModel();

        String cidcard = model.getCidcard().toUpperCase();
        String cphone = model.getCphone();
        if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("参数错误");
        }
        String keyPrefix = cidcard + cphone;

        CookieStore cookieStore = new BasicCookieStore();
        GuangDaApplyBean gd = new GuangDaApplyBean(cookieStore);
        byte[] bytes = getCode(cookieStore);
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        BufferedImage localBufferedImage = ImageIO.read(bin);
        client.set(keyPrefix + "_guangda", gd, Constants.TIME_HOUR);

        return localBufferedImage;
    }


    /**
     * 获得手机验证码
     * @param bean
     * @return
     */
    public static int getBankMessage(MaterialBean bean,MemCachedClient  client) {
        String imgCode = bean.getImgauthcode();
        if (bean.getBusiErrCode() == 0 && !StringUtils.isEmpty(bean.getBusiErrDesc())) {
            return 0;
        }
        if (StringUtils.isEmpty(imgCode)) {
            throw new GuangDaException(-1, "图片验证码不能为空");
        }
        MaterialModel model = bean.getModel();
        String cidcard = model.getCidcard().toUpperCase();
        String cphone = model.getCphone();
        if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
            throw new GuangDaException("参数错误");
        }
        String key = cidcard + cphone + "_guangda";
//            logger.info("会话key：" + key);
        GuangDaApplyBean gd = (GuangDaApplyBean) client.get(key);
        if (gd == null) {
            throw new GuangDaException(-1, "会话已经过期，请刷新图片验证码后重试");
        }
        getGuangDaBean(gd, bean);
        gd.setVerify_code(imgCode);
        String desc = sendDynPasswd(gd);
        client.set(key, gd, Constants.TIME_HOUR);
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc(desc);
        bean.setBusiJSON("success");
        return 1;
    }

    /**
     * 提交申请
     * @param bean
     * @return
     */
    public static int applyBankCreditCard(MaterialBean bean,MemCachedClient  client){
        String phoneCode = bean.getPhoneauthcode();
        if (StringUtils.isEmpty(phoneCode)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("动态密码不能为空");
            return 0;
        }
        MaterialModel model = bean.getModel();

        String cidcard = model.getCidcard().toUpperCase();
        String cphone = model.getCphone();
        if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("参数错误");
            return 0;
        }
        String key = cidcard + cphone + "_guangda";
        GuangDaApplyBean gd = (GuangDaApplyBean) client.get(key);
        if (gd == null) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("会话已经过期，请刷新图片验证码后重试");
            return 0;
        }


        gd.setDynPasswd(phoneCode);

        checkDynPasswd(gd);
        boolean flag = checkCus(cidcard,gd.getCookieStore());
        if (!flag) {
            throw new GuangDaException("您已有光大信用卡，如需再次申请光大信用卡，请前往光大银行网点办理");
        }
        String desc = submitApply(gd);
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc(desc);
        bean.setBusiJSON("success");
        return 1;

    }



    /**
     * 检查身份证是否没有申请信用卡 1
     *
     * @param id_no 身份证
     * @return
     */
    static boolean checkCus(String id_no,CookieStore cookieStore) {
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(cookieStore);
        String url = "https://xyk.cebbank.com/cebmms/apply/fz/check_cus.htm";
        Map<String, String> map = new HashMap<>();
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };
        map.put("id_no", id_no);
        map.put("id_type", "A");
        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);
        logger.info(respStr);
        GuangDaHttpsUtils.close(client, null);
        JSONObject json = JSON.parseObject(respStr);
        String flag = json.getString("flag");
        if ("1".equals(flag)) {
            return true;
        }
        //{"flag":"0","msg":"您已有光大信用卡，如需再次申请光大信用卡，请访问光大银行信用卡网站xyk.cebbank.com。","mobilePhoneHidden":"[?mobilePhoneHidden]"}
        return false;
    }


    /**
     * 访问办卡页面，添加隐藏参数
     *
     * @param gd
     */
    static void applyindex(GuangDaApplyBean gd) {
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());
        String url = "https://xyk.cebbank.com/cebmms/apply/fz/card-apply-index.htm";
        Map<String, String> map = new HashMap<>();
        map.put("req_card_id", gd.getReq_card_id());
        map.put("pro_code", gd.getPro_code());
        String resEntityStr = GuangDaHttpsUtils.doGet(url, client, null, map);

        GuangDaHttpsUtils.close(client, null);


        Document doc = Jsoup.parse(resEntityStr);

        //判断是否错误页面
        Element mainDoc = doc.getElementsByClass("main").get(0);
        if (mainDoc != null) {
            Elements els = mainDoc.getElementsByClass("e_txt");
            if (els != null && els.size() > 0) {
                throw new GuangDaException(els.get(0).text());
            }
        }

//        gd.setPro_code(PRO_CODE);

        String linkfrom = doc.getElementById("linkfrom").attr("value");
        gd.setLinkfrom(linkfrom);
        logger.info("linkfrom--------" + linkfrom);

        String termtype = doc.getElementById("termtype").attr("value");
        gd.setTermtype(termtype);
        logger.info("termtype--------" + termtype);

        String applyFlag = doc.getElementById("applyFlag").attr("value");
        gd.setApplyFlag(applyFlag);
        logger.info("applyFlag--------" + applyFlag);

        String diyFlag = doc.getElementById("diyFlag").attr("value");
        gd.setDiyFlag(diyFlag);
        logger.info("diyFlag--------" + diyFlag);

        String orderno = doc.getElementById("orderno").attr("value");
        gd.setOrderno(orderno);
        logger.info("orderno--------" + orderno);

        String req_card_id = doc.getElementById("req_card_id").attr("value");
        gd.setReq_card_id(req_card_id);
        logger.info("req_card_id--------" + req_card_id);

        String card_name = doc.getElementById("card_name").attr("value");
        gd.setCard_name(card_name);
        logger.info("card_name--------" + card_name);

        String area_adress = doc.getElementById("area_adress").attr("value");
        gd.setArea_adress(area_adress);
        logger.info("area_adress--------" + area_adress);

        String kd_flag = doc.getElementById("kd_flag").attr("value");
        gd.setKd_flag(kd_flag);
        logger.info("kd_flag--------" + kd_flag);

        String kd_address = doc.getElementById("kd_address").attr("value");
        gd.setKd_address(kd_address);
        logger.info("kd_address--------" + kd_address);

        String card_logo = doc.getElementById("card_logo").attr("value");
        gd.setCard_logo(card_logo);
        logger.info("card_logo--------" + card_logo);

        String company_logo = doc.getElementById("company_logo").attr("value");
        gd.setCompany_logo(company_logo);
        logger.info("company_logo--------" + company_logo);
    }


    /**
     * 访问申请页面，组装申请bean 2
     *
     * @param materialBean
     * @return
     */
    static GuangDaApplyBean getGuangDaBean(GuangDaApplyBean gd, MaterialBean materialBean) {

//        GuangDaBean gd = new GuangDaBean();
//
//        CookieStore cookieStore = new BasicCookieStore();
//        gd.setCookieStore(cookieStore);

        MaterialModel model = materialBean.getModel();

        logger.info("model.getApplyBankCardId():"+materialBean.getApplyBankCardId());
        gd.setReq_card_id(materialBean.getApplyBankCardId());

        applyindex(gd);


        /**
         * 个人信息
         */
        if (StringUtils.isEmpty(model.getCname())) {
            throw new GuangDaException("姓名为空");
        }
        gd.setName(model.getCname());
        if (StringUtils.isEmpty(model.getCenglishname())) {
            throw new GuangDaException("姓名拼音为空");
        }

        String namepy = model.getCenglishname();
        String[] ss = namepy.split(" ");
        namepy = ss[0] + " ";
        for(int i = 1;i<ss.length;i++){
            namepy += ss[i];
        }
        gd.setNamepy(namepy);

        if (StringUtils.isEmpty(model.getCidcard())) {
            throw new GuangDaException("身份证为空");
        }
        gd.setId_no(model.getCidcard().toUpperCase());
        if (StringUtils.isEmpty(model.getCphone())) {
            throw new GuangDaException("手机号为空");
        }
        gd.setMobilephone(model.getCphone());
        gd.setMobileHidden(model.getCphone());
        gd.setRecomment("0");

        gd.setBirth(gd.getId_no().substring(6, 14));
        if ("1".equals(model.getIsex())) {
            gd.setSex("M");
        } else {
            gd.setSex("F");
        }

//        private String recomment;//是否他人推荐 1：是  0：否
//        private String recomtel;//推荐人电话


        /**
         * 工作信息
         */
        if (!StringUtils.isNumeric(model.getMaritalstatus())) {
            throw new GuangDaException("婚姻状况错误");
        }
        gd.setMarriage(MaritalStatus.getGuangDaKey(model.getMaritalstatus()));
        if (!StringUtils.isNumeric(model.getIdegree())) {
            throw new GuangDaException("学历情况错误");
        }
        gd.setEducation(Degree.getGuangDaKey(model.getIdegree()));
        if (!StringUtils.isNumeric(model.getResidencestatus())) {
            throw new GuangDaException("住宅类型错误");
        }
        gd.setHousetype(ResidenceStatus.getGuangDaKey(model.getResidencestatus()));
        if (StringUtils.isEmpty(model.getCemail())) {
            throw new GuangDaException("邮箱错误");
        }
        gd.setEmail(model.getCemail());
        if (StringUtils.isEmpty(model.getCcompanyname())) {
            throw new GuangDaException("单位名称为空");
        }
        gd.setComname(model.getCcompanyname());
        if (!StringUtils.isNumeric(model.getInatureofunit())) {
            throw new GuangDaException("单位性质错误");
        }
        gd.setCpy_kind(NatureOfUnit.getGuangDaKey(model.getInatureofunit()));
        if (!StringUtils.isNumeric(model.getIniatureofbusiness())) {
            throw new GuangDaException("行业性质错误");
        }
        gd.setCpy_vocation(NatureOfBusiness.getGuangDaKey(model.getIniatureofbusiness()));

        if ("16".equals(gd.getCpy_vocation())) {
            gd.setVocation_remark("其他");
        }
        if (!StringUtils.isNumeric(model.getIdepartment())) {
            throw new GuangDaException("职位错误");
        }
        gd.setDuty(Post.getGuangDaKey(model.getIdepartment()));
        if (StringUtils.isEmpty(model.getIannualsalary())) {
            throw new GuangDaException("年薪错误");
        }

        //对年薪处理
        //超过1000的默认输入的为元，除以10000
        Double income = Double.valueOf(model.getIannualsalary());
        long yearNcome = Math.round(income);
        if(income>1000){
            gd.setIncome(Long.toString(yearNcome/10000));
        }else{
            gd.setIncome(Long.toString(yearNcome));
        }


        /**
         * 其他信息
         */
        if (StringUtils.isEmpty(materialBean.getApplyCompanyProvince())) {
            throw new GuangDaException("工作地区错误");
        }
        gd.setComprovince(materialBean.getApplyCompanyProvince());
        if (StringUtils.isEmpty(materialBean.getApplyCompanyCity())) {
            throw new GuangDaException("工作地区错误");
        }
        gd.setComcityname(materialBean.getApplyCompanyCity());
        if (StringUtils.isEmpty(materialBean.getApplyCompanyAddress())) {
            throw new GuangDaException("工作地区错误");
        }
        gd.setComareaname(materialBean.getApplyCompanyAddress());
        if (StringUtils.isEmpty(model.getCcompany_detailaddress())) {
            throw new GuangDaException("工作详细地区错误");
        }
        gd.setComaddr(model.getCcompany_detailaddress());
//
//        //默认 住宅的省、市和工作省市相同
//
//        gd.setHouseprovince(gd.getComprovince());
//        gd.setHousecityname(gd.getComcityname());

        /**
         * 添加住宅地址提交
         * update wsl by 2016年7月11日15:42:24
         */

        if(StringUtils.isEmpty(materialBean.getChome_pname())){
            throw new GuangDaException("居住地址错误");
        }
        gd.setHouseprovince(materialBean.getChome_pname());
        if(StringUtils.isEmpty(materialBean.getChome_cname())){
            throw new GuangDaException("居住地址错误");
        }
        gd.setHousecityname(materialBean.getChome_cname());
        if(StringUtils.isEmpty(materialBean.getChome_dname())){
            throw new GuangDaException("居住地址错误");
        }
        gd.setHouseareaname(materialBean.getChome_dname());
        gd.setHouseaddr(StringUtils.isNotBlank(model.getChome_detailaddress()) ? model.getChome_detailaddress() : "");
        //end




        gd.setFamilyname(model.getFamilyname());
        gd.setRelation(FamilyTies.getGuangDaKey(model.getIfamilyties()));
        gd.setFamilymobile(model.getCfamilyphonenum());

//        String idnovaliddate = model.getCidexpirationtime();
//        if (StringUtils.isNotEmpty(idnovaliddate) && (!"-1".equals(idnovaliddate))) {
//            gd.setIdnovaliddate(idnovaliddate.split(",")[1]);
//
//        }


        gd.setCzprovince(gd.getComprovince());
        gd.setCzcityname("999");
//        private String postaddrtype = "2";// 默认为2
        gd.setComzip(gd.getComareaname().split("\\|")[2]);
        String houseareaname = gd.getHouseareaname();
        logger.info(houseareaname);
        gd.setHomezip(StringUtils.isNotBlank(houseareaname) && !houseareaname.equals("999") ? houseareaname.split("\\|")[2] : "100000");


        String comtel = model.getCcompany_telnum();//单位电话 comphonetel+"-"+comphoneqh
        gd.setComtel(comtel);
        String[] comtels = comtel.split("-");
        gd.setComphoneqh(comtels[0]);
        gd.setComphonetel(comtels[1]);


        gd.setBranch_code(gd.getComcityname().split("\\|")[2]);


        return gd;
    }


    /**
     * 获得图片验证码 3
     *
     * @param cookieStore
     * @return
     */
    static byte[] getCode(CookieStore cookieStore) {

        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(cookieStore);

        HttpGet get = new HttpGet("https://xyk.cebbank.com/cebmms/verify_code.jpg");
//        HttpGet get = new HttpGet("https://xyk.cebbank.com/verify_code.jpg");

        CloseableHttpResponse response = null;
        byte[] bytes = null;
        try {
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            logger.info("statusLine----------" + statusLine);
            HttpEntity entity = response.getEntity();
            bytes = EntityUtils.toByteArray(entity);

            if (LOCAL_TEST) {
                FileOutputStream fos = new FileOutputStream("d:/data/e.jpg");
                fos.write(bytes);
//                logger.info("图片下载目录：/data/c.jpg");
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
     * 发送办卡动态验证码 4
     *
     * @param gd
     */
    public static String sendDynPasswd(GuangDaApplyBean gd) {
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());
        String url = "https://xyk.cebbank.com/cebmms/home/send-online-apply-msg.htm";
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };
        Map<String, String> map = new HashMap<>();
        map.put("mobilePhone", gd.getMobilephone());//手机
        map.put("cardname", gd.getCard_name());//信用卡名称
        map.put("ver_code", gd.getVerify_code());//验证码

        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);
        GuangDaHttpsUtils.close(client, null);
        JSONObject json = JSONObject.parseObject(respStr);
        logger.info(respStr);

        if (json.getInteger("flag") == 1) {
            //成功
            return "动态口令已发送，请注意查收！";
        } else if(json.getInteger("flag") == 2){
            String errMsg = json.getString("msg");
            throw new GuangDaException(-1,errMsg);
        }else {
            String errMsg = json.getString("msg");
            logger.info("发送办卡动态验证码其他结果：" + respStr);
            throw new GuangDaException(errMsg);
        }
    }


    static String checkDynPasswd(GuangDaApplyBean gd) {
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());
        String url = "https://xyk.cebbank.com/cebmms/home/online_apply_check.htm";
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };
        Map<String, String> map = new HashMap<>();
        map.put("mobilePhone", gd.getMobilephone());//手机
        map.put("dynPasswd", gd.getDynPasswd());//
        map.put("ver_code", gd.getVerify_code());//
        logger.info("ver_code-------"+gd.getVerify_code());
        logger.info("dynPasswd-------"+gd.getDynPasswd());
        logger.info("mobilePhone-------"+gd.getMobilephone());
        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);
        GuangDaHttpsUtils.close(client, null);
        JSONObject json = JSONObject.parseObject(respStr);

        int flag = json.getInteger("flag");
        if (flag == 0) {
            BankApplyListener.sendSucess(BankEnum.guangda, BankApplyStepEnum.phone_code);
            return "验证短信验证码成功";
        } else if (flag == 1) {
            BankApplyListener.sendSucess(BankEnum.guangda, BankApplyStepEnum.phone_code);
            throw new GuangDaException(-1,"动态密码已失效，请您重新获取！");
        } else if (flag == 2) {
            BankApplyListener.sendSucess(BankEnum.guangda, BankApplyStepEnum.phone_code);
            throw new GuangDaException(-1,"动态密码输入错误，请您重新输入！");
        } else if(flag==4){
            BankApplyListener.sendSucess(BankEnum.guangda, BankApplyStepEnum.phone_code);
            logger.info("动态验证码flag4："+respStr);
            throw new GuangDaException(-1,"动态密码输入错误，请您重新输入！");
            //{"flag":"4","msg":"验证码错误，请重新输入！","mobilePhoneHidden":"[?mobilePhoneHidden]","add_home":"[?add_home]","add_com":"[?add_com]"}
        }else{
            ErrorRequestBean bean = new ErrorRequestBean(System.currentTimeMillis()+".html",gd,respStr,url,-1,respStr,gd.getMobilephone());
            BankApplyListener.sendError(BankEnum.guangda, BankApplyStepEnum.phone_code, bean);
            logger.info("动态验证码其他验证结果：" + respStr);
            return "验证短信验证码成功";
        }

    }

    /**
     * 提交申请信用卡 6
     *
     * @param gd
     */
    static String submitApply(GuangDaApplyBean gd) {
        CloseableHttpClient client = GuangDaHttpsUtils.getHttpClient(gd.getCookieStore());

        String url = "https://xyk.cebbank.com/cebmms/apply/fz/card-apply-siml.htm";
        Header[] requestHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded")
//                new BasicHeader("Cache-Control", "max-age=0"),
//                new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
//                new BasicHeader("Accept-Encoding", "gzip, deflate"),
//                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"),
//                new BasicHeader("Referer", "https://xyk.cebbank.com/cebmms/apply/fz/card-apply-index.htm?pro_code="+gd.getPro_code()+"&req_card_id="+gd.getReq_card_id()),
//                new BasicHeader("Origin", "https://xyk.cebbank.com")
        };
        Map<String, String> map = new HashMap<>();

        map.put("name", gd.getName());
        map.put("namepy", gd.getNamepy());
        map.put("id_no", gd.getId_no());
        map.put("id_type", gd.getId_type());
        map.put("merchantmsg", gd.getMerchantmsg());
        map.put("mobilephone", gd.getMobilephone());
        map.put("mobileHidden", gd.getMobileHidden());
        map.put("verify_code", gd.getVerify_code());
        map.put("dynPasswd", gd.getDynPasswd());
        map.put("passwdHidden", gd.getPasswdHidden());
        map.put("recomment", gd.getRecomment());
        map.put("recomtel", gd.getRecomtel());
        map.put("c2c_recom_flag", gd.getC2c_recom_flag());
        map.put("c2c_act_id", gd.getC2c_act_id());
        map.put("marriage", gd.getMarriage());
        map.put("education", gd.getEducation());
        map.put("housetype", gd.getHousetype());
        map.put("email", gd.getEmail());
        map.put("comname", gd.getComname());
        map.put("cpy_kind", gd.getCpy_kind());
        map.put("cpy_vocation", gd.getCpy_vocation());
        map.put("vocation_remark", gd.getVocation_remark());
        map.put("duty", gd.getDuty());
        map.put("income", gd.getIncome());
        map.put("comcityname", gd.getComcityname());
        map.put("comprovince", gd.getComprovince());
        map.put("comareaname", gd.getComareaname());
        map.put("comaddrHD", gd.getComaddrHD());
        map.put("comaddr", gd.getComaddr());
        map.put("housecityname", gd.getHousecityname());
        map.put("houseprovince", gd.getHouseprovince());
        map.put("houseareaname", gd.getHouseareaname());
        map.put("houseaddr", gd.getHouseaddr());
        map.put("comphonetel", gd.getComphonetel());
        map.put("comphoneqh", gd.getComphoneqh());
        map.put("familyname", gd.getFamilyname());
        map.put("relation", gd.getRelation());
        map.put("familymobile", gd.getFamilymobile());
        map.put("idnovaliddate", gd.getIdnovaliddate());
        map.put("czcityname", gd.getCzcityname());
        map.put("czprovince", gd.getCzprovince());
        map.put("postaddrtype", gd.getPostaddrtype());
        map.put("comzip", gd.getComzip());
        map.put("homezip", gd.getHomezip());
        map.put("linkfrom", gd.getLinkfrom());
        map.put("termtype", gd.getTermtype());
        map.put("applyFlag", gd.getApplyFlag());
        map.put("diyFlag", gd.getDiyFlag());
        map.put("birth", gd.getBirth());
        map.put("sex", gd.getSex());
        map.put("pro_code", gd.getPro_code());
        map.put("comtel", gd.getComtel());
        map.put("orderno", gd.getOrderno());
        map.put("req_card_id", gd.getReq_card_id());
        map.put("card_name", gd.getCard_name());
        map.put("cookie_id", gd.getCookie_id());
        map.put("branch_code", gd.getBranch_code());
        map.put("area_adress", gd.getArea_adress());
        map.put("cxtype", gd.getCxtype());
        map.put("kd_flag", gd.getKd_flag());
        map.put("kd_address", gd.getKd_address());
        map.put("card_logo", gd.getCard_logo());
        map.put("company_logo", gd.getCompany_logo());
        map.put("zhimaName", gd.getZhimaName());
        map.put("zmvalue", gd.getZmvalue());

        logger.info("-------------申请请求值开始--------------");
        logger.info(gd.toString());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            logger.info(entry.getKey() + "--------" + entry.getValue());
        }
        logger.info("-------------申请请求值结束--------------");

//        if(true){
//            //测试，一定成功，返回
//            return gd.toString();
//        }


        String respStr = GuangDaHttpsUtils.doPost(url, client, requestHeaders, map);

        GuangDaHttpsUtils.close(client, null);

        if (LOCAL_TEST) {
            logger.info("----------------------申请返回值开始----------------------");
            logger.info(respStr);
            logger.info("----------------------申请返回值结束----------------------");
        }

        Document doc = Jsoup.parse(respStr, "utf-8");




        try {
            //判断是否错误页面
            Element mainDoc = doc.getElementsByClass("main").get(0);
            if (mainDoc != null) {
                Elements els = mainDoc.getElementsByClass("e_txt");
                if (els != null && els.size() > 0) {
                    throw new GuangDaException(els.get(0).text());
                }
            }

            Element element = doc.getElementsByClass("content_box").get(0).child(0).child(0);
            String result = element.text();
            logger.info("申请结果---------------------------------" + result);

            if (result.contains("您的信用卡申请已提交，我行会对您的申请进行审核，")) {
                //成功
                BankApplyListener.sendSucess(BankEnum.guangda,BankApplyStepEnum.submit_apply);
                //// TODO: 2016/7/11 不管成功失败都保存  一段时间后删除
//                ErrorRequestBean req = new ErrorRequestBean(gd,respStr);
//                req.setFileName();
                ErrorFileUtil.saveFile(null, gd,respStr, "sucess_"+System.currentTimeMillis()+".html", BankApplyListener.getParentPath(BankEnum.guangda,BankApplyStepEnum.submit_apply));
                return result;
            }

            //您已经进行过申请，30日内不能重复申请，感谢您对光大银行信用卡的支持！ 感谢您使用光大银行信用卡！
            if(result.contains("您已经进行过申请，30天内不能")){
                throw new GuangDaException("您已经进行过申请，30天内不能重复申请");
            }
//            if(result.contains("此卡片不支持在线申请")){
//                throw new GuangDaException("此卡片不支持在线申请");
//            }
            throw new RuntimeException();

            //您的信用卡申请已提交，我行会对您的申请进行审核，请保持您的手机畅通。在您收到卡片后，    请持二代身份证原件、信用卡以及信用卡开卡函到我行营业网点进行开卡操作。
        } catch (GuangDaException e){
            ErrorRequestBean req = new ErrorRequestBean(gd,respStr);
            ErrorFileUtil.saveFile(req.getUrl(), req.getParam(), req.getResult(), req.getFileName(), BankApplyListener.getParentPath(BankEnum.guangda,BankApplyStepEnum.submit_apply));

            BankApplyListener.sendSucess(BankEnum.guangda,BankApplyStepEnum.submit_apply);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            int ierrortype = -1;
            String cerrordesc = "";

            if(respStr!=null){
                String[] patternArrays = {"var errMsg = '[^,]*?';","提交数据异常"};
                int[] errortypeArrays = {1,-1};
                for(int i=0;i<patternArrays.length;i++){
                    Pattern r = Pattern.compile(patternArrays[i]);
                    Matcher m = r.matcher(respStr);
                    if(m.find()) {
                        cerrordesc = m.group(0);
                        ierrortype = errortypeArrays[i];
                    }
                }
            }

            ErrorRequestBean bean = new ErrorRequestBean(System.currentTimeMillis()+".html",gd,respStr,url,ierrortype,cerrordesc,gd.getMobilephone());
            BankApplyListener.sendError(BankEnum.guangda, BankApplyStepEnum.submit_apply, bean);
            throw new GuangDaException("申请失败，再试试其他银行的申卡吧！");
        }

    }


    private static void localTestSave(GuangDaApplyBean gd, String respStr) {
        if (LOCAL_TEST) {
            long time = System.currentTimeMillis();
            String targetPath = ERROR_PATH + time + ".html";
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(targetPath));
                writer.println("<!-- 申请请求值");
                writer.println(gd);
                writer.println(" -->");
                writer.flush();
                writer.println(respStr);
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

}
