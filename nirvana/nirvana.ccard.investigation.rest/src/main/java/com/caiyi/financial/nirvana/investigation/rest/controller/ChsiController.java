package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.ChsiBean;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiEducationDto;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.investigation.util.ChsiHelper;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shaoqinghua on 2017/1/22.
 * 学信网相关接口
 */
@RestController
public class ChsiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChsiController.class);

    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
    public IDrpcClient client;
    @Autowired
    public MemCachedClient memCachedClient;
    //登录地址
    private static final String LOGIN_URL = "https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com" +
            ".cn%2Farchive%2Fj_spring_cas_security_check";
    //高等教育学籍地址
    private static final String DEGREE_URL = "https://my.chsi.com.cn/archive/gdjy/xj/show.action";
    //注册页面地址
    private static final String REGISTER_URL = "https://account.chsi.com.cn/account/preregister.action?from=archive";
    //校验手机号码地址
    private static final String CHECK_MOBILE_URL = "https://account.chsi.com.cn/account/checkmobilephoneother.action";
    //手机校验码地址
    private static final String MOBILE_CODE_URL = "https://account.chsi.com.cn/account/getmphonpincode.action";
    //通过手机找回地址   第一步
    private static final String RESET_PWD_STEP_ONE_URL = "https://account.chsi.com.cn/account/password!retrive.action";
    //通过手机找回地址   第二步
    private static final String RESET_PWD_STEP_TWO_ONE_URL = "https://account.chsi.com.cn/account/forgot/rtvbymphoneindex.action";

    private static final String RESET_PWD_STEP_TWO_TWO_URL = "https://account.chsi.com.cn/account/forgot/rtvbymphone.action";
    //通过手机找回地址   第三步
    private static final String RESET_PWD_STEP_THREE_URL = "https://account.chsi.com.cn/account/forgot/rstpwdbymphone.action";
    //找回学信账号
    private static final String RETRIEVE_USERNAME_URL = "https://account.chsi.com.cn/account/password!rtvlgname.action";
    //注册请求地址
    private static final String REGISTER_PROCESS_URL = "https://account.chsi.com.cn/account/registerprocess.action";

    /**
     * 学信网登录
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/chsiLogin.go")
    public BoltResult chsiLogin(ChsiBean chsiBean) throws IOException {
        BoltResult boltResult = new BoltResult("1", "登录成功");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        String enName = chsiBean.getUsername();
        String enPwd = chsiBean.getPassword();
        int iclient = chsiBean.getIclient();
        String code = chsiBean.getCode();
        String iskeep = chsiBean.getIskeep();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            LOGGER.info("chsiLogin:用户未登录,cuserId:{}", cuserId);
            return boltResult;
        }
        if (CheckUtil.isNullString(enName) || CheckUtil.isNullString(enPwd)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户名或秘密不能为空");
            LOGGER.info("chsiLogin:用户名或秘密为空:enName:" + enName + ",enPwd:" + enPwd);
            return boltResult;
        }
        //用户名密码解析
        String username = dencryptStrByClient(iclient, enName);
        String password = dencryptStrByClient(iclient, enPwd);
        if (CheckUtil.isNullString(username) || CheckUtil.isNullString(password)) {
            LOGGER.info("用户名或密码加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("用户名或密码加密不正确");
            return boltResult;
        }
        LOGGER.info("username:{},password:{},cuserId:{},code:{},iskeep:{}", username, password, cuserId, code, iskeep);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore;
        String lt = "";
        String base64img; //验证码图片base64转换的字符串
        Object object = memCachedClient.get(cuserId + "chsiLt");
        Object object2 = memCachedClient.get(cuserId + "chsiLoginCookie");
        try {
            if (object == null || object2 == null) {
                // 初始化登录页面
                cookieStore = new BasicCookieStore();
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
                String initContent = HpClientUtil.httpGet(LOGIN_URL, requestHeaderMap, httpClient, localContext,
                        "utf-8",
                        false, requestConfig);
                Document initDoc = Jsoup.parse(initContent);
                //获取隐藏域lt值
                Elements lts = initDoc.getElementsByAttributeValue("name", "lt");
                if (lts != null && lts.size() > 0) {
                    lt = lts.get(0).attr("value");
                }
                LOGGER.info("lt:" + lt);
                //将cookie和lt存入缓存
                memCachedClient.set(cuserId + "chsiLoginCookie", cookieStore, 1000 * 60 * 30);
                memCachedClient.set(cuserId + "chsiLt", lt, 1000 * 60 * 30);
                //判断是否需要验证码
                if (ChsiHelper.isNeedCaptcha(initDoc)) {
                    String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "3");
                    if (CheckUtil.isNullString(base64imgStr)) {
                        boltResult.setCode("0");
                        boltResult.setDesc("获取图片验证码失败");
                        LOGGER.info("获取图片验证码失败");
                        return boltResult;
                    }
                    base64img = base64imgStr.replace("\r\n", "");
                    boltResult.setCode("2");
                    boltResult.setDesc("需要图片验证码");
                    data.put("base64img", base64img);
                    LOGGER.info("base64img:" + base64img);
                    boltResult.setData(data);
                    return boltResult;
                }
            } else {
                lt = (String) object;
                cookieStore = (CookieStore) object2;
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
            }

            //组装请求参数
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("lt", lt);
            params.put("_eventId", "submit");
            params.put("submit", "登  录");
            //判断是否有验证码
            if ("1".equals(iskeep)) {
                params.put("captcha", code);
            }

            //带参请求登录页面
            HttpResponse response = ChsiHelper.getResponsePost(LOGIN_URL, requestHeaderMap, params,
                    httpClient, localContext, "utf-8", requestConfig);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("statusCode:{}", statusCode);
            //根据状态码判断登录结果
            BoltResult loginResult = this.getLoginResultByStatusCode(statusCode, cuserId, response,
                    cookieStore, requestHeaderMap, httpClient, localContext, requestConfig);
            String resultCode = loginResult.getCode();
            String resultDesc = loginResult.getDesc();
            LOGGER.info("根据状态码判断登录结果,resultCode:{},resultDesc:{}", resultCode, resultDesc);
            if ("0".equals(resultCode) || "2".equals(resultCode)) {
                return loginResult;
            }
            //登录成功，清除缓存中cookie和lt，获取重定向地址
            String firstRedirectUrl = response.getLastHeader("Location").getValue();
            memCachedClient.delete(cuserId + "chsiLoginCookie");
            memCachedClient.delete(cuserId + "chsiLt");
            LOGGER.info("登录成功重定向地址：{}", firstRedirectUrl);
            requestHeaderMap.put("Host", "my.chsi.com.cn");
            //第二次请求
            String indexContent = HpClientUtil.httpGet(firstRedirectUrl, requestHeaderMap, httpClient, localContext,
                    "utf-8", false, requestConfig);
            //LOGGER.info("学信网登录成功主页面：{}", indexContent);
            //登录成功判断是否有异常信息
            BoltResult errorResult = this.getErrorFromContent(indexContent);
            String errorCode = errorResult.getCode();
            String errorDesc = errorResult.getDesc();
            LOGGER.info("判断登录成功后是否有错误信息:errorCode:{},errorDesc:{}", errorCode, errorDesc);
            if ("0".equals(errorCode)) {
                return errorResult;
            }
            //对用户名和密码加密(统一用CaiyiEncryptIOS加密)
            String enLoginName = CaiyiEncryptIOS.encryptStr(username);
            String enLoginPwd = CaiyiEncryptIOS.encryptStr(password);
            //处理学信账号和学历信息
            BoltResult processResult = this.processChsiAccountAndDegrees(cuserId, enLoginName, enLoginPwd, requestHeaderMap,
                    httpClient, localContext, requestConfig);
            String processCode = processResult.getCode();
            String processDesc = processResult.getDesc();
            LOGGER.info("处理学信账号和学历信息,processCode:{},processDesc:{}", processCode, processDesc);
            return processResult;
        } catch (Exception e) {
            LOGGER.error("学信登录失败", e);
            boltResult.setCode("0");
            boltResult.setDesc("学信登录失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return boltResult;
    }

    /**
     * 根据状态码获取学信登录结果
     *
     * @param statusCode
     * @param cuserId
     * @param response
     * @param cookieStore
     * @param requestHeaderMap
     * @param httpClient
     * @param localContext
     * @param requestConfig
     * @return
     */
    private BoltResult getLoginResultByStatusCode(int statusCode, String cuserId, HttpResponse response, CookieStore
            cookieStore, Map<String, String> requestHeaderMap, CloseableHttpClient httpClient, HttpContext localContext,
                                                  RequestConfig requestConfig) {
        BoltResult boltResult = new BoltResult("1", "登录成功");
        JSONObject data = new JSONObject();
        if (statusCode == 200) {
            //登录失败
            String secondContent = ChsiHelper.getContentFromResponse(response, "utf-8");
            Document secondDoc = Jsoup.parse(secondContent);
            Element statusElement = secondDoc.getElementById("status");
            Elements errorElements = secondDoc.getElementsByAttributeValue("class", "ct_input errors");
            if (statusElement != null) {
                //状态提示
                String status = statusElement.html();
                LOGGER.info("status:{}", status);
                boltResult.setCode("0");
                boltResult.setDesc(status);
                //用户名密码错误，删除缓存
                memCachedClient.delete(cuserId + "chsiLoginCookie");
                memCachedClient.delete(cuserId + "chsiLt");
                return boltResult;
            }
            if (errorElements != null && errorElements.size() > 0) {
                //错误提示
                String error = errorElements.get(0).html();
                LOGGER.info("error:{}", error);
                String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "3");
                String base64img = base64imgStr.replace("\r\n", "");
                //LOGGER.info("base64img:{}", base64img);
                if (!CheckUtil.isNullString(base64img)) {
                    boltResult.setCode("2");
                    boltResult.setDesc(error);
                    data.put("base64img", base64img);
                    boltResult.setData(data);
                } else {
                    boltResult.setCode("0");
                    boltResult.setDesc("图片验证码获取失败");
                    memCachedClient.delete(cuserId + "chsiLoginCookie");
                    memCachedClient.delete(cuserId + "chsiLt");
                }
                return boltResult;
            }
            boltResult.setCode("0");
            boltResult.setDesc("登录失败,请重新登录");
            memCachedClient.delete(cuserId + "chsiLoginCookie");
            memCachedClient.delete(cuserId + "chsiLt");
            return boltResult;
        } else if (statusCode == 301) {
            boltResult.setCode("0");
            boltResult.setDesc("登录失败,请重新登录");
            memCachedClient.delete(cuserId + "chsiLoginCookie");
            memCachedClient.delete(cuserId + "chsiLt");
            return boltResult;
        } else if (statusCode == 302) {
            String location = response.getLastHeader("Location").getValue();
            LOGGER.info("statusCode:302:{}", location);
            if (location.contains(LOGIN_URL.replace("https://", ""))) {
                //重定向地址为登录地址，登录失败
                //请求重定向请求
                String firstContext = HpClientUtil.httpGet(location, requestHeaderMap, httpClient, localContext,
                        "utf-8", false, requestConfig);
                LOGGER.info("登录失败重定向内容:{}", firstContext);
                boltResult.setCode("0");
                boltResult.setDesc("登录失败,请重新登录");
                memCachedClient.delete(cuserId + "chsiLoginCookie");
                memCachedClient.delete(cuserId + "chsiLt");
                return boltResult;
            }
        }
        return boltResult;
    }

    /**
     * 获取登录成功页面中的错误信息
     *
     * @param content
     * @return
     */
    private BoltResult getErrorFromContent(String content) {
        BoltResult boltResult = new BoltResult("1", "无异常错误信息");
        Document doc = Jsoup.parse(content);
        Elements elements = doc.getElementsByClass("no-info-content");
        if (elements != null && elements.size() > 0) {
            String warnStr = elements.get(0).html();
            String warn = warnStr.substring(warnStr.indexOf(">") + 1, warnStr.indexOf("</h5>"));
            LOGGER.info("warn:{}", warn);
            boltResult.setCode("0");
            boltResult.setDesc(warn);
            return boltResult;
        }
        return boltResult;
    }

    /**
     * 登录成功之后处理学信账号及学历信息
     *
     * @param cuserId
     * @return
     */
    public BoltResult processChsiAccountAndDegrees(String cuserId, String enLoginName, String enLoginPwd, Map<String, String>
            requestHeaderMap, CloseableHttpClient httpClient, HttpContext localContext, RequestConfig requestConfig) {
        BoltResult boltResult = new BoltResult("1", "登录成功");
        ChsiAccountDto chsiAccount = client.execute(new DrpcRequest("chsi", "queryChsiAccount", cuserId),
                ChsiAccountDto.class);
        LOGGER.info("根据用户id查询的学信账号,chsiAccount:{}", chsiAccount);
        ChsiAccountDto newAccount = new ChsiAccountDto();
        if (chsiAccount == null) {
            LOGGER.info("数据库中没有该用户的学信账号信息");
            //数据库没有学信账号信息
            newAccount.setLoginName(enLoginName);
            newAccount.setLoginPwd(enLoginPwd);
            newAccount.setAddTime(new Date());
            newAccount.setUpdateTime(newAccount.getAddTime());
            newAccount.setUserId(cuserId);
            //添加学信网账号到数据库
            LOGGER.info("开始添加学信账号到数据库");
            ChsiAccountDto backAccount = client.execute(new DrpcRequest("chsi", "addChsiAccountBackId", newAccount),
                    ChsiAccountDto.class);
            if (backAccount == null) {
                LOGGER.info("addChsiAccount失败");
                boltResult.setCode("0");
                boltResult.setDesc("学信账号添加失败");
                return boltResult;
            }
            //获取生成的学信账号id
            int chsiAccountId = backAccount.getChsiAccountId();
            LOGGER.info("学信账号添加成功,chsiAccountId:{}", chsiAccountId);
            //请求学籍页面信息
            String degreeContent = HpClientUtil.httpGet(DEGREE_URL, requestHeaderMap, httpClient,
                    localContext, "utf-8", false, requestConfig);
            //保存抓取的学历页面
            ChsiHelper.saveDegreeHtmlPage(cuserId, newAccount.getAddTime(), degreeContent);
            List<ChsiEducationDto> degrees = ChsiHelper.getAllDegrees(degreeContent);
            if (degrees != null & degrees.size() > 0) {
                LOGGER.info("开始添加学历数据到数据库");
                for (ChsiEducationDto degree : degrees) {
                    degree.setChsiAccountId(chsiAccountId);
                }
                //将学历信息存入数据库
                client.execute(new DrpcRequest("chsi", "addChsiEducation", degrees));
                LOGGER.info("学信学历数据添加成功,chsiAccountId:{}", chsiAccountId);
            }
            //获取最高学历，更新学信账号表
            LOGGER.info("开始更新学信账号最高学历");
            int educationLevel = ChsiHelper.getTopEducationLevel(degrees);
            backAccount.setEducationLevel(educationLevel);
            backAccount.setUpdateTime(new Date());
            //更新学信账号表
            client.execute(new DrpcRequest("chsi", "updateChsiAccount", backAccount));
            LOGGER.info("更新学信账号:设置最高学历成功,educationLevel:{}", educationLevel);
            return boltResult;
        } else {
            //已经拥有学信账号信息，则进行更新
            LOGGER.info("查询到该用户的学信账号信息，进行数据更新");
            //根据学信账号id查询数据库中学历信息
            int chsiAccountId = chsiAccount.getChsiAccountId();
            newAccount.setChsiAccountId(chsiAccountId);
            newAccount.setLoginName(enLoginName);
            newAccount.setLoginPwd(enLoginPwd);
            newAccount.setAddTime(chsiAccount.getAddTime());
            newAccount.setUpdateTime(new Date());
            newAccount.setUserId(chsiAccount.getUserId());
            newAccount.setState(chsiAccount.getState());
            //查询数据库中的学历信息
            LOGGER.info("开始查询数据库中的学历数据");
            JSONObject degreesJson = client.execute(new DrpcRequest("chsi", "queryChsiEducation", chsiAccountId),
                    JSONObject.class);
            JSONArray degreesJsonArray = degreesJson.getJSONArray("degrees");
            if (degreesJsonArray != null && degreesJsonArray.size() > 0) {
                LOGGER.info("查询到数据库中的学历条数：" + degreesJsonArray.size());
                //数据库中有该用户学历信息，则判断与当前登录学信账号是否为同一身份证号
                JSONObject jsonObject = (JSONObject) degreesJsonArray.get(0);
                String dbIdCode = (String) jsonObject.get("code");
                //获取当前登录学信账号的学历信息
                LOGGER.info("开始获取当前登录学信账号的学历数据");
                String degreeContent = HpClientUtil.httpGet(DEGREE_URL, requestHeaderMap, httpClient,
                        localContext, "utf-8", false, requestConfig);
                //保存抓取的学历页面
                ChsiHelper.saveDegreeHtmlPage(cuserId, newAccount.getAddTime(), degreeContent);
                List<ChsiEducationDto> degrees = ChsiHelper.getAllDegrees(degreeContent);
                if (degrees != null && degrees.size() > 0) {
                    LOGGER.info("获取的当前登录学信账号的学历数据条数：" + degrees.size());
                    String nowIdCode = degrees.get(0).getCode();
                    if (dbIdCode.equals(nowIdCode)) {
                        LOGGER.info("登录的是同一身份证号学信账号,dbIdCode:{},nowIdCode:{}", dbIdCode, nowIdCode);
                        //登录的是同一个身份证号的学信账号
                        newAccount.setEducationLevel(chsiAccount.getEducationLevel());
                    } else {
                        //登录的不是同一个身份证号的学信账号
                        LOGGER.info("登录的不是同一身份证号的学信账号");
                        LOGGER.info("开始删除数据库中的原有学历数据");
                        //1.删除该用户的原有学历
                        client.execute(new DrpcRequest("chsi", "deleteChsiEducation", chsiAccountId));
                        LOGGER.info("删除数据库中学历数据成功,chsiAccountId:{}", chsiAccountId);
                        LOGGER.info("开始添加最新登录学信账号的学历信息");
                        //2.添加新的学历信息
                        for (ChsiEducationDto edu : degrees) {
                            edu.setChsiAccountId(chsiAccountId);
                        }
                        client.execute(new DrpcRequest("chsi", "addChsiEducation", degrees));
                        LOGGER.info("添加最新登录学信账号的学历信息成功,学历条数：", degrees.size());
                        //设置最高学历
                        int educationLevel = ChsiHelper.getTopEducationLevel(degrees);
                        newAccount.setEducationLevel(educationLevel);
                    }
                } else {
                    //数据库有学历信息，当前登录学信账号没有学历信息，删除学历信息
                    LOGGER.info("当前登录学信账号无学历信息，数据库存在学历信息，开始删除学历信息");
                    client.execute(new DrpcRequest("chsi", "deleteChsiEducation", chsiAccountId));
                    LOGGER.info("删除原有学历数据成功,chsiAccountId:{}", chsiAccountId);
                    //设置最高学历
                    int educationLevel = ChsiHelper.getTopEducationLevel(degrees);
                    newAccount.setEducationLevel(educationLevel);
                }
                //更新学信账号信息
                LOGGER.info("开始更新学信账号信息，设置最高学历");
                client.execute(new DrpcRequest("chsi", "updateChsiAccount", newAccount));
                LOGGER.info("更新学信账号成功，设置最高学历成功，educationLevel:{}", newAccount.getEducationLevel());
                return boltResult;
            } else {
                //数据库没有学历信息，请求学籍页面获取
                LOGGER.info("没有在数据库中查询到该用户的学历数据");
                LOGGER.info("开始获取当前登录学信账号的学历数据");
                String degreeContent = HpClientUtil.httpGet(DEGREE_URL, requestHeaderMap, httpClient,
                        localContext, "utf-8", false, requestConfig);
                //保存抓取的学历页面
                ChsiHelper.saveDegreeHtmlPage(cuserId, chsiAccount.getAddTime(), degreeContent);
                List<ChsiEducationDto> degrees = ChsiHelper.getAllDegrees(degreeContent);
                if (degrees != null && degrees.size() > 0) {
                    LOGGER.info("获取的当前登录学信账号的学历数据条数：" + degrees.size());
                    for (ChsiEducationDto edu : degrees) {
                        edu.setChsiAccountId(chsiAccountId);
                    }
                    //添加学历数据
                    LOGGER.info("开始添加该学信账号的学历数据");
                    client.execute(new DrpcRequest("chsi", "addChsiEducation", degrees));
                    LOGGER.info("添加该学信账号的学历数据成功");
                }
                //获取最高学历，更新学信账号表
                int educationLevel = ChsiHelper.getTopEducationLevel(degrees);
                newAccount.setEducationLevel(educationLevel);
                newAccount.setUpdateTime(new Date());
                //更新学信账号表
                LOGGER.info("开始更新学信账号，设置最高学历");
                client.execute(new DrpcRequest("chsi", "updateChsiAccount", newAccount));
                LOGGER.info("更新学信账号成功，设置最高学历成功，educationLevel:{}", educationLevel);
                return boltResult;
            }
        }
    }

    /**
     * 获取学信网图片验证码
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/getChsiImgCode.go")
    public BoltResult getChsiImgCode(ChsiBean chsiBean) {
        BoltResult boltResult = new BoltResult("1", "获取图片验证码成功");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        String imgCodeType = chsiBean.getImgCodeType();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        if (CheckUtil.isNullString(imgCodeType)) {
            boltResult.setCode("0");
            boltResult.setDesc("验证码类型不存在");
            return boltResult;
        }
        //获取缓存中的cookie
        CookieStore cookieStore;
        Object object = null;
        if ("3".equals(imgCodeType)) {
            object = memCachedClient.get(cuserId + "chsiLoginCookie");
        } else if ("4".equals(imgCodeType)) {
            object = memCachedClient.get(cuserId + "chsiRegisterCookie");
        }
        if (object == null) {
            boltResult.setCode("0");
            boltResult.setDesc("获取Cookie信息失败");
            return boltResult;
        }
        cookieStore = (CookieStore) object;
        String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, imgCodeType);
        if (CheckUtil.isNullString(base64imgStr)) {
            boltResult.setCode("0");
            boltResult.setDesc("图片验证码获取失败");
            LOGGER.info("图片验证码获取失败");
            return boltResult;
        }
        String base64img = base64imgStr.replace("\r\n", "");
        //LOGGER.info("base64img:{}", base64img);
        boltResult.setCode("1");
        boltResult.setDesc("需要图片验证码");
        data.put("base64img", base64img);
        boltResult.setData(data);
        return boltResult;
    }


    /**
     * 加载学信学历详情信息
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/loadChsiDegrees.go")
    public BoltResult loadChsiDegrees(ChsiBean chsiBean) throws Exception {
        BoltResult boltResult = new BoltResult("1", "查询成功");
        String cuserId = chsiBean.getCuserId();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        LOGGER.info("cuserId:{}", cuserId);
        //根据cuserId查询学信账号
        ChsiAccountDto chsiAccount = client.execute(new DrpcRequest("chsi", "queryChsiAccount", cuserId),
                ChsiAccountDto.class);
        //根据学信账号id查询学历信息
        JSONObject degreeJson = client.execute(new DrpcRequest("chsi", "queryChsiEducation", chsiAccount
                .getChsiAccountId()), JSONObject.class);
        JSONArray degreeJsonArray = degreeJson.getJSONArray("degrees");
        if (degreeJsonArray == null || degreeJsonArray.size() == 0) {
            boltResult.setCode("1");
            boltResult.setDesc("没有查询到该用户的学历信息");
            return boltResult;
        }
        JSONArray backJsonArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        for (int i = 0; i < degreeJsonArray.size(); i++) {
            JSONObject tmp = (JSONObject) degreeJsonArray.get(i);
            //判断学历
            String levels = (String) tmp.get("levels");
            String degree = "";
            if (levels.contains("专科")) {
                degree = "专科";
            } else if (levels.contains("本科")) {
                degree = "本科";
            } else if (levels.contains("硕士")) {
                degree = "硕士";
            } else if (levels.contains("博士")) {
                degree = "博士";
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("degree", degree);
            StringBuilder sb = new StringBuilder();
            sb.append("学校|" + tmp.get("college") + ",");
            sb.append("专业|" + tmp.get("major") + ",");
            String stateDetail = (String) tmp.get("stateDetail");
            if (CheckUtil.isNullString(stateDetail)) {
                stateDetail = (int) tmp.get("state") == 0 ? "不在籍" : "在籍";
            }
            sb.append("状态|" + stateDetail + ",");
            sb.append("入学时间|" + sdf.format(tmp.getDate("joinTime")) + ",");
            if (stateDetail.contains("不在籍")) {
                sb.append("离校时间|" + sdf.format(tmp.getDate("graduate")));
            } else {
                sb.append("预计离校时间|" + sdf.format(tmp.getDate("graduate")));
            }
            jsonObject.put("detail", sb.toString());
            backJsonArray.add(jsonObject);
        }
        boltResult.setData(backJsonArray);
        return boltResult;
    }


    /**
     * 更新学历信息
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/updateChsiDegrees.go")
    public BoltResult updateChsiDegrees(ChsiBean chsiBean) throws IOException {
        BoltResult boltResult = new BoltResult("1", "更新成功");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        String iskeep = chsiBean.getIskeep();
        String code = chsiBean.getCode();
        LOGGER.info("cuserId:{},iskeep:{},code:{}", cuserId, iskeep, code);
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore;
        String lt = "";
        String base64img; //验证码图片base64转换的字符串
        try {
            //根据cuserId查询学信账号
            ChsiAccountDto chsiAccount = client.execute(new DrpcRequest("chsi", "queryChsiAccount", cuserId), ChsiAccountDto
                    .class);
            LOGGER.info("根据cuserId查询的学信账号,chsiAccount:{}", chsiAccount);
            int chsiAccountId = chsiAccount.getChsiAccountId();
            //对学信账号和密码进行解密(统一用CaiyiEncryptIOS解密)
            String deLoginName = CaiyiEncryptIOS.dencryptStr(chsiAccount.getLoginName());
            String deLoginPwd = CaiyiEncryptIOS.dencryptStr(chsiAccount.getLoginPwd());
            if (CheckUtil.isNullString(deLoginName) || CheckUtil.isNullString(deLoginPwd)) {
                LOGGER.info("学信账号信息解析失败");
                boltResult.setCode("0");
                boltResult.setDesc("学信账号信息解析失败");
                return boltResult;
            }
            Object object = memCachedClient.get(cuserId + "chsiLt");
            Object object2 = memCachedClient.get(cuserId + "chsiLoginCookie");
            if (object == null || object2 == null) {
                // 初始化登录页面
                cookieStore = new BasicCookieStore();
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
                String initContent = HpClientUtil.httpGet(LOGIN_URL, requestHeaderMap, httpClient, localContext,
                        "utf-8",
                        false, requestConfig);
                Document initDoc = Jsoup.parse(initContent);
                //获取隐藏域lt值
                Elements lts = initDoc.getElementsByAttributeValue("name", "lt");
                if (lts != null) {
                    lt = lts.get(0).attr("value");
                }
                LOGGER.info("lt:{}", lt);
                //将cookie和lt存入缓存
                memCachedClient.set(cuserId + "chsiLoginCookie", cookieStore, 1000 * 60 * 30);
                memCachedClient.set(cuserId + "chsiLt", lt, 1000 * 60 * 30);
                //判断是否需要验证码
                if (ChsiHelper.isNeedCaptcha(initDoc)) {
                    String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "3");
                    base64img = base64imgStr.replace("\r\n", "");
                    boltResult.setCode("2");
                    boltResult.setDesc("需要图片验证码");
                    data.put("base64img", base64img);
                    LOGGER.info("base64img:{}", base64img);
                    boltResult.setData(data);
                    return boltResult;
                }
            } else {
                lt = (String) object;
                cookieStore = (CookieStore) object2;
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
            }

            //组装请求参数
            Map<String, String> params = new HashMap<>();
            params.put("username", deLoginName);
            params.put("password", deLoginPwd);
            params.put("lt", lt);
            params.put("_eventId", "submit");
            params.put("submit", "登  录");
            //判断是否有验证码
            if ("1".equals(iskeep)) {
                params.put("captcha", code);
            }
            //带参请求登录页面
            HttpResponse response = ChsiHelper.getResponsePost(LOGIN_URL, requestHeaderMap, params,
                    httpClient, localContext, "utf-8", requestConfig);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("statusCode:{}", statusCode);
            //根据状态码判断登录结果
            BoltResult loginResult = this.getLoginResultByStatusCode(statusCode, cuserId, response,
                    cookieStore, requestHeaderMap, httpClient, localContext, requestConfig);
            String resultCode = loginResult.getCode();
            String resultDesc = loginResult.getDesc();
            LOGGER.info("根据状态码判断登录结果,resultCode:{},resultDesc:{}", resultCode, resultDesc);
            if ("0".equals(resultCode) || "2".equals(resultCode)) {
                return loginResult;
            }
            //登录成功，清除缓存中cookie和lt，获取重定向地址
            String firstRedirectUrl = response.getLastHeader("Location").getValue();
            memCachedClient.delete(cuserId + "chsiLoginCookie");
            memCachedClient.delete(cuserId + "chsiLt");
            LOGGER.info("登录成功重定向地址：" + firstRedirectUrl);
            requestHeaderMap.put("Host", "my.chsi.com.cn");
            //第二次请求
            String indexContent = HpClientUtil.httpGet(firstRedirectUrl, requestHeaderMap, httpClient, localContext,
                    "utf-8", false, requestConfig);
            //LOGGER.info("学信网登录成功主页面:{}", indexContent);
            //请求学籍页面获取最新学籍信息
            String degreeContent = HpClientUtil.httpGet(DEGREE_URL, requestHeaderMap, httpClient,
                    localContext, "utf-8", false, requestConfig);
            //保存抓取的学历页面
            ChsiHelper.saveDegreeHtmlPage(cuserId, chsiAccount.getAddTime(), degreeContent);
            List<ChsiEducationDto> degrees = ChsiHelper.getAllDegrees(degreeContent);
            LOGGER.info("从学信网获取最新学历数据,degrees:{}", degrees);
            if (degrees == null) {
                LOGGER.info("没有获取到该用户的学历信息，结束更新操作");
                return boltResult;
            }
            //获取数据库中的学历信息
            JSONObject degreesJson = client.execute(new DrpcRequest("chsi", "queryChsiEducation", chsiAccountId),
                    JSONObject.class);
            JSONArray degreeJsonArray = degreesJson.getJSONArray("degrees");
            List<ChsiEducationDto> degreeDB = new ArrayList<>();
            for (int i = 0; i < degreeJsonArray.size(); i++) {
                ChsiEducationDto tmp = new ChsiEducationDto();
                tmp.setChsiEductionId((int) (degreeJsonArray.getJSONObject(i).get("chsiEductionId")));
                degreeDB.add(tmp);
            }
            //数据库存在对应的学历，进行更新操作
            ChsiEducationDto edu;
            if (degreeDB.size() > 0) {
                for (int i = degreeDB.size() - 1, j = 0; i >= 0; i--, j++) {
                    edu = degrees.get(degrees.size() - 1 - j);
                    edu.setChsiAccountId(chsiAccountId);
                    edu.setChsiEductionId(degreeDB.get(i).getChsiEductionId());
                    client.execute(new DrpcRequest("chsi", "updateChsiEducation", edu));
                }
                LOGGER.info("更新已存在学历信息成功");
            }
            //新获取的学历，数据库中不存在
            List<ChsiEducationDto> addList = new ArrayList<>();
            for (int i = 0; i < degrees.size() - degreeDB.size(); i++) {
                edu = degrees.get(i);
                edu.setChsiAccountId(chsiAccountId);
                addList.add(edu);
            }
            if (addList.size() > 0) {
                client.execute(new DrpcRequest("chsi", "addChsiEducation", addList));
                LOGGER.info("添加新获取学历成功");
            }
            //获取最高学历，更新学信账号表
            int educationLevel = ChsiHelper.getTopEducationLevel(degrees);
            chsiAccount.setEducationLevel(educationLevel);
            chsiAccount.setUpdateTime(new Date());
            client.execute(new DrpcRequest("chsi", "updateChsiAccount", chsiAccount));
            LOGGER.info("更新学信账号最高学历成功");
            return boltResult;
        } catch (Exception e) {
            LOGGER.error("学历信息更新失败", e);
            boltResult.setCode("0");
            boltResult.setDesc("学历信息更新失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return boltResult;
    }

    /**
     * 获取注册短信校验码
     *
     * @return
     */
    @RequestMapping("/control/investigation/getRegisterMessageCode.go")
    public BoltResult getRegisterMessageCode(ChsiBean chsiBean) throws IOException {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        int iclient = chsiBean.getIclient();
        String iskeep = chsiBean.getIskeep();
        String code = chsiBean.getCode();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        String mphone = dencryptStrByClient(iclient, chsiBean.getMphone());
        LOGGER.info("cuserId:{},mphone:{},iskeep:{},code:{}", cuserId, mphone, iskeep, code);
        if (CheckUtil.isNullString(mphone)) {
            LOGGER.info("手机号码加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("手机号码加密不正确");
            return boltResult;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        //获取缓存中的cookie
        CookieStore cookieStore;
        Object object = memCachedClient.get(cuserId + "chsiRegisterCookie");
        try {
            if (object == null) {
                cookieStore = new BasicCookieStore();
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
                //初始化注册页面
                HpClientUtil.httpGet(REGISTER_URL, requestHeaderMap, httpClient, localContext, "utf-8",
                        false, requestConfig);
                //将cookie存入缓存
                memCachedClient.set(cuserId + "chsiRegisterCookie", cookieStore, 1000 * 60 * 30);
                LOGGER.info("chsiRegisterCookie:{}", cookieStore);
            } else {
                cookieStore = (CookieStore) object;
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
            }
            Map<String, String> params = new HashMap<>();
            if (!"1".equals(iskeep)) {
                //组装请求参数
                params.put("mphone", mphone);
                params.put("dataInfo", mphone);
                params.put("optType", "REGISTER");
                //检查手机号码是否被注册
                String checkContent = HpClientUtil.httpPost(CHECK_MOBILE_URL, requestHeaderMap, params, httpClient,
                        localContext, "utf-8", requestConfig);
                if (checkContent.contains("false")) {
                    LOGGER.info("手机号码已被注册");
                    boltResult.setCode("0");
                    boltResult.setDesc("手机号码已被注册");
                    memCachedClient.delete(cuserId + "chsiRegisterCookie");
                    return boltResult;
                }
                LOGGER.info("手机号可以注册");
                //获取图片验证码
                String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "4");
                if (CheckUtil.isNullString(base64imgStr)) {
                    boltResult.setCode("0");
                    boltResult.setDesc("获取图片验证码失败");
                    memCachedClient.delete(cuserId + "chsiRegisterCookie");
                    return boltResult;
                }
                String base64img = base64imgStr.replace("\r\n", "");
                LOGGER.info("base64img:{}", base64img);
                data.put("base64img", base64img);
                boltResult.setCode("2");
                boltResult.setDesc("需要图片验证码");
                boltResult.setData(data);
                return boltResult;
            }
            params.clear();
            params.put("captch", code);
            params.put("mobilePhone", mphone);
            params.put("optType", "REGISTER");
            params.put("ignoremphone", "false");
            //请求获取手机校验码
            String content = HpClientUtil.httpPost(MOBILE_CODE_URL, requestHeaderMap, params, httpClient, localContext,
                    "utf-8", requestConfig);
            LOGGER.info("content:{}", content);
            JSONObject result = (JSONObject) JSONObject.parse(content.trim());
            String status = (String) result.get("status");
            if ("0".equals(status)) {
                //验证码不正确 或 手机校验码获取过于频繁,操作被禁止
                String tipStr = (String) result.get("tips");
                String tips = tipStr.substring(tipStr.indexOf("[") + 1, tipStr.indexOf("]"));
                LOGGER.info("status:" + status + ",tips:" + tips);
                if ("验证码不正确".equals(tips)) {
                    //清空缓存
                    memCachedClient.delete(cuserId + "chsiRegisterCookie");
                    cookieStore = new BasicCookieStore();
                    //启用cookie存储
                    localContext.setAttribute("http.cookie-store", cookieStore);
                    //初始化注册页面
                    HpClientUtil.httpGet(REGISTER_URL, requestHeaderMap, httpClient, localContext, "utf-8",
                            false, requestConfig);
                    //将cookie存入缓存
                    memCachedClient.set(cuserId + "chsiRegisterCookie", cookieStore, 1000 * 60 * 30);
                    String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "4");
                    if (CheckUtil.isNullString(base64imgStr)) {
                        boltResult.setCode("0");
                        boltResult.setDesc("图片验证码获取失败");
                        return boltResult;
                    }
                    String base64img = base64imgStr.replace("\r\n", "");
                    LOGGER.info("base64img:{}", base64img);
                    boltResult.setCode("2");
                    boltResult.setDesc(tips);
                    data.put("base64img", base64img);
                    boltResult.setData(data);
                    return boltResult;
                }
                //手机校验码获取过于频繁,操作被禁止
                //手机号码受限,短信发送次数已达到上限,请24小时后再试
                boltResult.setCode("0");
                boltResult.setDesc(tips);
                memCachedClient.delete(cuserId + "chsiRegisterCookie");
                return boltResult;
            } else if ("2".equals(status)) {
                String tips = "短信验证码已发送到手机号" + mphone.substring(0, 4) + "*" + mphone.substring(8, 11);
                LOGGER.info("status:" + status + ",tips:" + tips);
                boltResult.setCode("1");
                boltResult.setDesc(tips);
                return boltResult;
            }
        } catch (Exception e) {
            LOGGER.error("发送短信验证码失败", e);
            boltResult.setCode("0");
            boltResult.setDesc("发送短信验证码失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return boltResult;
    }

    /**
     * 学信账号注册
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/chsiRegister.go")
    public BoltResult chsiRegister(ChsiBean chsiBean) throws IOException {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        int iclient = chsiBean.getIclient();
        String vcode = chsiBean.getVcode();
        String iskeep = chsiBean.getIskeep();
        String code = chsiBean.getCode();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        String mphone = dencryptStrByClient(iclient, chsiBean.getMphone());
        String password = dencryptStrByClient(iclient, chsiBean.getPassword());
        String xm = dencryptStrByClient(iclient, chsiBean.getXm());
        String sfzh = dencryptStrByClient(iclient, chsiBean.getSfzh());
        LOGGER.info("cuserId:{},iclient:{},vcode:{},mphone:{},password:{},xm:{},sfzh:{},iskeep:{},code:{}", cuserId,
                iclient, vcode, mphone, password, xm, sfzh, iskeep, code);
        if (CheckUtil.isNullString(mphone) || CheckUtil.isNullString(password) || CheckUtil.isNullString(xm) ||
                CheckUtil.isNullString(sfzh)) {
            LOGGER.info("用户信息加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("用户信息加密不正确");
            return boltResult;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        //获取缓存中的cookie
        CookieStore cookieStore;
        Map<String, String> params = new HashMap<>();
        String lt = "";
        String base64img;
        try {
            if (!"1".equals(iskeep)) {
                Object object = memCachedClient.get(cuserId + "chsiRegisterCookie");
                if (object != null) {
                    cookieStore = (CookieStore) object;
                    //启用cookie存储
                    localContext.setAttribute("http.cookie-store", cookieStore);
                }
                //组装请求参数
                params.put("from", "");
                params.put("mphone", mphone);
                params.put("ignoremphone", "true");
                params.put("vcode", vcode);
                params.put("password", password);
                params.put("password1", password);
                params.put("xm", xm);
                params.put("credentialtype", "SFZ");
                params.put("sfzh", sfzh);
                params.put("email", "");
                params.put("pwdreq1", "");
                params.put("pwdanswer1", "");
                params.put("pwdreq2", "");
                params.put("pwdanswer2", "");
                params.put("pwdreq3", "");
                params.put("pwdanswer3", "");
                params.put("continueurl", "");
                params.put("serviceId", "");
                params.put("serviceNote", "1");
                params.put("serviceNote_res", "0");
                //请求注册处理地址
                String content = HpClientUtil.httpPost(REGISTER_PROCESS_URL, requestHeaderMap, params, httpClient,
                        localContext, "utf-8", requestConfig);
                Document doc = Jsoup.parse(content);
                Element errorInfo = doc.getElementById("user_reg_fm_error_info");
                if (errorInfo != null) {
                    //注册失败
                    Elements span = errorInfo.getElementsByTag("span");
                    String error = span.html();
                    LOGGER.info("error:{}", error);
                    boltResult.setCode("0");
                    boltResult.setDesc(error);
                    return boltResult;
                }
                LOGGER.info("学信账号注册成功");
                //注册成功
                memCachedClient.delete(cuserId + "chsiRegisterCookie");
            }

            //自动登录
            Object object = memCachedClient.get(cuserId + "chsiLt");
            Object object2 = memCachedClient.get(cuserId + "chsiLoginCookie");
            if (object == null || object2 == null) {
                cookieStore = new BasicCookieStore();
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
                String initContent = HpClientUtil.httpGet(LOGIN_URL, requestHeaderMap, httpClient, localContext,
                        "utf-8",
                        false, requestConfig);
                Document initDoc = Jsoup.parse(initContent);
                //获取隐藏域lt值
                Elements lts = initDoc.getElementsByAttributeValue("name", "lt");
                if (lts != null) {
                    lt = lts.get(0).attr("value");
                }
                LOGGER.info("lt:" + lt);
                //将cookie和lt存入缓存
                memCachedClient.set(cuserId + "chsiLoginCookie", cookieStore, 1000 * 60 * 30);
                memCachedClient.set(cuserId + "chsiLt", lt, 1000 * 60 * 30);
                //判断是否需要验证码
                if (ChsiHelper.isNeedCaptcha(initDoc)) {
                    String base64imgStr = ChsiHelper.getChsiImgCode(cookieStore, "3");
                    if (CheckUtil.isNullString(base64imgStr)) {
                        boltResult.setCode("3");
                        boltResult.setDesc("注册成功，请登录");
                        LOGGER.info("注册成功，自动登录获取图片验证码失败，切换登录页面手动登录");
                        memCachedClient.delete(cuserId + "chsiLoginCookie");
                        memCachedClient.delete(cuserId + "chsiLt");
                        return boltResult;
                    }
                    base64img = base64imgStr.replace("\r\n", "");
                    LOGGER.info("base64img:" + base64img);
                    boltResult.setCode("2");
                    boltResult.setDesc("请输入图片验证码");
                    data.put("base64img", base64img);
                    boltResult.setData(data);
                    return boltResult;
                }
            } else {
                lt = (String) object;
                cookieStore = (CookieStore) object2;
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
            }

            //组装请求参数
            params.clear();
            params.put("username", mphone);
            params.put("password", password);
            params.put("lt", lt);
            params.put("_eventId", "submit");
            params.put("submit", "登  录");
            if ("1".equals(iskeep)) {
                params.put("captcha", code);
            }
            //带参请求登录页面
            HttpResponse response = ChsiHelper.getResponsePost(LOGIN_URL, requestHeaderMap, params,
                    httpClient, localContext, "utf-8", requestConfig);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("statusCode:{}", statusCode);
            BoltResult loginResult = this.getLoginResultByStatusCode(statusCode, cuserId, response,
                    cookieStore, requestHeaderMap, httpClient, localContext, requestConfig);
            String resultCode = loginResult.getCode();
            String resultDesc = loginResult.getDesc();
            LOGGER.info("根据状态码判断登录结果,resultCode:{},resultDesc:{}", resultCode, resultDesc);
            if ("0".equals(resultCode)) {
                //登录失败
                boltResult.setCode("3");
                boltResult.setDesc("注册成功，请登录");
                LOGGER.info("注册成功，自动登录失败，切换到登录页面手动登录");
                memCachedClient.delete(cuserId + "chsiLoginCookie");
                memCachedClient.delete(cuserId + "chsiLt");
                return boltResult;
            } else if ("2".equals(resultCode)) {
                //需要验证码
                boltResult.setCode("2");
                boltResult.setDesc("请输入图片验证码");
                boltResult.setData(loginResult.getData());
                LOGGER.info("注册成功，自动登录需要验证码");
                return boltResult;
            }
            //登录成功，清除缓存中cookie和lt，获取重定向地址
            String firstRedirectUrl = response.getLastHeader("Location").getValue();
            memCachedClient.delete(cuserId + "chsiLoginCookie");
            memCachedClient.delete(cuserId + "chsiLt");
            LOGGER.info("登录成功重定向地址：{}", firstRedirectUrl);
            requestHeaderMap.put("Host", "my.chsi.com.cn");
            //第二次请求
            String indexContent = HpClientUtil.httpGet(firstRedirectUrl, requestHeaderMap, httpClient, localContext,
                    "utf-8", false, requestConfig);
            LOGGER.info("学信网登录成功主页面：{}", indexContent);
            //登录成功判断是否有异常信息
            BoltResult errorResult = this.getErrorFromContent(indexContent);
            String errorCode = errorResult.getCode();
            String errorDesc = errorResult.getDesc();
            LOGGER.info("判断登录成功后是否有错误信息:errorCode:{},errorDesc:{}", errorCode, errorDesc);
            if ("0".equals(errorCode)) {
                boltResult.setCode("3");
                boltResult.setDesc(errorDesc);
                return errorResult;
            }
            //对用户名和密码加密(统一用CaiyiEncryptIOS加密)
            String enLoginName = CaiyiEncryptIOS.encryptStr(mphone);
            String enLoginPwd = CaiyiEncryptIOS.encryptStr(password);
            //处理学信账号和学历信息
            BoltResult processResult = this.processChsiAccountAndDegrees(cuserId, enLoginName, enLoginPwd, requestHeaderMap,
                    httpClient, localContext, requestConfig);
            String processCode = processResult.getCode();
            String processDesc = processResult.getDesc();
            if ("0".equals(processCode)) {
                //处理学信账号和学历信息失败
                LOGGER.info("处理学信账号和学历信息失败,processCode:{},processDesc:{}", processCode, processDesc);
                boltResult.setCode("3");
                boltResult.setDesc("注册成功，请登录");
                return boltResult;
            } else if ("1".equals(processCode)) {
                //处理学信账号和学历信息成功
                LOGGER.info("处理学信账号和学历信息成功,processCode:{},processDesc:{}", processCode, processDesc);
                boltResult.setCode("1");
                boltResult.setDesc("注册成功，自动登录成功");
                return boltResult;
            }
        } catch (Exception e) {
            LOGGER.error("注册成功，自动登录失败，切换到登录页面手动登录", e);
            boltResult.setCode("3");
            boltResult.setDesc("注册成功，请登录");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return boltResult;
    }


    /**
     * 获取学信网图片验证码 找回密码用
     *
     * @param chsiBean
     * @return
     */
    @RequestMapping("/control/investigation/getChsiPwdImgCode.go")
    public BoltResult getChsiPwdImgCode(ChsiBean chsiBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult("1", "获取图片验证码成功");
        JSONObject data = new JSONObject();
        String cuserId = chsiBean.getCuserId();
        String imgCodeType = chsiBean.getImgCodeType();
        if (CheckUtil.isNullString(cuserId)) {
            boltResult.setCode("0");
            boltResult.setDesc("用户未登录");
            return boltResult;
        }
        if (CheckUtil.isNullString(imgCodeType)) {
            boltResult.setCode("0");
            boltResult.setDesc("验证码类型不存在");
            return boltResult;
        }
        //获取缓存中的cookie
        CookieStore cookieStore;
        Object object = null;
        String base64img = "";

        HttpContext localContext = new BasicHttpContext();
        cookieStore = new BasicCookieStore();
        object = memCachedClient.get(cuserId + "pwdCookie");

        if (object != null) {
            cookieStore = (CookieStore) object;
            localContext.setAttribute("http.cookie-store", cookieStore);
        }
        base64img = this.getChsiPwdImgCodeBase64(cookieStore, cuserId);
        memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
        object = memCachedClient.get(cuserId + "pwdCookie");
        if (object == null) {
            boltResult.setCode("0");
            boltResult.setDesc("获取Cookie信息失败");
            return boltResult;
        }
        if (CheckUtil.isNullString(base64img)) {
            LOGGER.info("获取图片验证码失败");
            boltResult.setCode("0");
            boltResult.setDesc("获取图片验证码失败");
            return boltResult;
        }
        data.put("base64img", base64img);
        boltResult.setData(data);
        LOGGER.info("获取验证码消耗时间:" + (System.currentTimeMillis() - start));
        return boltResult;
    }

    /**
     * 修改密码第一步
     */
    @RequestMapping("/control/investigation/findPasswordStep1.go")
    public BoltResult findPasswordStepOne(ChsiBean chsiBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        LOGGER.info("找回学信密码第一步开始");
        String cuserId = chsiBean.getCuserId();
        String code = chsiBean.getCode();
        int iclient = chsiBean.getIclient();
        String userName = dencryptStrByClient(iclient, chsiBean.getUsername());
        LOGGER.info("username:{},password:{},cuserId:{},code:{},iskeep:{}", userName, cuserId, code);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = null;
        Object object = memCachedClient.get(cuserId + "pwdCookie");
        if (object != null) {
            cookieStore = (CookieStore) object;
        } else {
            cookieStore = new BasicCookieStore();
        }
        localContext.setAttribute("http.cookie-store", cookieStore);
        Map<String, String> params = new HashMap<>();
        params.put("loginName", userName);
        params.put("captch", code);
        String clientToken = HpClientUtil.httpPost(RESET_PWD_STEP_ONE_URL, requestHeaderMap, params, httpClient,
                localContext, "utf-8", requestConfig);
        memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
        Document doc = Jsoup.parse(clientToken);
        Element error = doc.getElementById("user_retrivePsd_form_error_info");
        Elements elements = doc.getElementsByAttributeValue("class", "psdRtv_c");
        String result = "";
        if (error == null) {
            //没有错误提示信息，获取隐藏域token值
            Elements elt = doc.getElementsByAttributeValue("name", "ctoken");
            String token = "";
            if (elt != null && elt.size() != 0) {
                token = elt.get(0).attr("value");
                //将cookie和token存入缓存
                memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
                memCachedClient.set(cuserId + "token", token, 1000 * 60 * 30);
            }
            result = elements.get(1).html();
            boltResult.setCode("1");
            boltResult.setData("");
            boltResult.setDesc(result);
            LOGGER.info("找回学信密码第一步成功");
            LOGGER.info("result:{}", result);
        } else {
            //有错误提示信息
            result = error.text();
            if ("验证码不正确".equals(result)) {
                String base64imgStr = this.getChsiPwdImgCodeBase64(cookieStore, cuserId);
                String base64img = base64imgStr.replace("\r\n", "");
                LOGGER.info("base64img:{}", base64img);
                if (CheckUtil.isNullString(base64img)) {
                    boltResult.setCode("0");
                    boltResult.setDesc("获取图片验证码失败");
                    return boltResult;
                }
                JSONObject data = new JSONObject();
                data.put("base64img", base64img);
                boltResult.setCode("2");
                boltResult.setDesc(result);
                boltResult.setData(data);
            } else {
                //其它错误
                boltResult.setCode("0");
                boltResult.setDesc(result);
                boltResult.setData("");
            }

        }
        LOGGER.info("修改密码第一步请求消耗时间:" + (System.currentTimeMillis() - start));
        return boltResult;
    }

    /**
     * 修改密码第二步
     */
    @RequestMapping("/control/investigation/findPasswordStep2.go")
    public BoltResult findPasswordStep2(ChsiBean chsiBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore;

        String cuserId = chsiBean.getCuserId();
        String code = chsiBean.getCode();
        String imgCodeType = chsiBean.getImgCodeType();
        int iclient = chsiBean.getIclient();
        String mphone = dencryptStrByClient(iclient, chsiBean.getMphone());
        String xm = dencryptStrByClient(iclient, chsiBean.getXm());
        String sfzh = dencryptStrByClient(iclient, chsiBean.getSfzh());
        if (CheckUtil.isNullString(mphone) || CheckUtil.isNullString(xm) || CheckUtil.isNullString(sfzh)) {
            LOGGER.info("数据加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("数据加密不正确");
            return boltResult;
        }
        LOGGER.info("code:{},mphone:{},xm:{},sfzh:{}", code, mphone, xm, sfzh);
        Object object = memCachedClient.get(cuserId + "token");
        Object object2 = memCachedClient.get(cuserId + "pwdCookie");
        Map<String, String> paramToken = new HashMap<>();
        if (object != null && object2 != null) {
            String token = (String) object;
            cookieStore = (CookieStore) object2;
            //启用cookie存储
            paramToken.put("ctoken", token);
            localContext.setAttribute("http.cookie-store", cookieStore);
        } else {
            boltResult.setCode("0");
            boltResult.setDesc("连接超时，请重新找回");
            boltResult.setData("");
            return boltResult;
        }
        String ctoken = "";
        Map<String, String> params = new HashMap<>();
        Document doc;
        if (!"5".equals(imgCodeType)) {
            String client = HpClientUtil.httpPost(RESET_PWD_STEP_TWO_ONE_URL, requestHeaderMap, paramToken,
                    httpClient, localContext,
                    "utf-8", requestConfig);
            //进入找回立即找回页面
            doc = Jsoup.parse(client);
            Elements elt = doc.getElementsByAttributeValue("name", "ctoken");

            if (elt != null && elt.size() != 0) {
                ctoken = elt.get(0).attr("value");
                memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
                memCachedClient.set(cuserId + "ctoken", ctoken, 1000 * 60 * 30);
            } else {
                boltResult.setCode("0");
                boltResult.setDesc("连接超时，请重新找回");
                boltResult.setData("");
                return boltResult;
            }
        } else {
            LOGGER.info("重新获取手机验证码");
            Object object3 = memCachedClient.get(cuserId + "ctoken");
            if (object3 != null) {
                ctoken = (String) object3;
                //启用cookie存储
                localContext.setAttribute("http.cookie-store", cookieStore);
            } else {
                boltResult.setCode("0");
                boltResult.setDesc("连接超时，请重新找回");
                boltResult.setData("");
                return boltResult;
            }
        }

        params.put("ctoken", ctoken);
        params.put("captch", code);
        params.put("mphone", mphone);
        params.put("xm", xm);
        params.put("sfzh", sfzh);
        //进入密码重置
        String clientPhone = HpClientUtil.httpPost(RESET_PWD_STEP_TWO_TWO_URL, requestHeaderMap, params, httpClient,
                localContext, "utf-8", requestConfig);
        doc = Jsoup.parse(clientPhone);
        Element error1 = doc.getElementById("user_reg_fm_error_info");
        Element error2 = doc.getElementById("error_info");

        if (error1 != null) {
            boltResult.setCode("0");
            boltResult.setDesc(error1.text());
            boltResult.setData("");
            return boltResult;
        } else if (error2 != null) {
            if ("验证码不正确".equals(error2.text())) {
                String base64img = this.getChsiPwdImgCodeBase64(cookieStore, cuserId);
                LOGGER.info("base64img:{}", base64img);
                if (CheckUtil.isNullString(base64img)) {
                    boltResult.setCode("0");
                    boltResult.setDesc("获取图片验证码失败");
                    return boltResult;
                }
                JSONObject data = new JSONObject();
                data.put("base64img", base64img);
                boltResult.setCode("2");
                boltResult.setDesc(error2.text());
                boltResult.setData(data);
                return boltResult;
            } else if ("请求错误".equals(error2.text())) {
                boltResult.setCode("0");
                boltResult.setDesc("连接超时，请重新找回");
                boltResult.setData("");
                return boltResult;
            } else {
                boltResult.setCode("0");
                boltResult.setDesc(error2.text());
                boltResult.setData("");
                return boltResult;
            }
        } else {
            Elements elc = doc.getElementsByAttributeValue("name", "clst");
            String clst = "";
            if (elc != null) {
                clst = elc.get(0).attr("value");
            }
            //将cookie和lt存入缓存
            memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
            memCachedClient.set(cuserId + "clst", clst, 1000 * 60 * 30);
            boltResult.setCode("1");
            boltResult.setDesc("验证成功");
            boltResult.setData("");
            LOGGER.info("找回学信密码第二步成功");
        }
        LOGGER.info("修改密码第二步请求消耗时间:" + (System.currentTimeMillis() - start));
        return boltResult;
    }

    /**
     * 修改密码第三步
     */
    @RequestMapping("/control/investigation/findPasswordStep3.go")
    public BoltResult findPasswordStep3(ChsiBean chsiBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = null;

        String cuserId = chsiBean.getCuserId();
        String passwordTemp = chsiBean.getPassword();
        String confirmPwdTemp = chsiBean.getConfirmPwd();
        String vcode = chsiBean.getVcode();
        int iclient = chsiBean.getIclient();

        String password = dencryptStrByClient(iclient, passwordTemp);
        String confirmPwd = dencryptStrByClient(iclient, confirmPwdTemp);

        if (CheckUtil.isNullString(password) || CheckUtil.isNullString(confirmPwd)) {
            LOGGER.info("数据加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("数据加密不正确");
            return boltResult;
        }
        LOGGER.info("vcode:{},password:{},confirmPwd:{}", vcode, password, confirmPwd);
        Object object = memCachedClient.get(cuserId + "clst");
        Object object2 = memCachedClient.get(cuserId + "pwdCookie");
        String clst = "";
        if (object != null && object2 != null) {
            clst = (String) object;
            cookieStore = (CookieStore) object2;
        } else {
            boltResult.setCode("0");
            boltResult.setDesc("连接超时，请重新找回");
            boltResult.setData("");
        }
        Map<String, String> params = new HashMap<>();
        params.put("clst", clst);
        params.put("password", password);
        params.put("password1", confirmPwd);
        params.put("vcode", vcode);
        localContext.setAttribute("http.cookie-store", cookieStore);
        String clientReset = HpClientUtil.httpPost(RESET_PWD_STEP_THREE_URL, requestHeaderMap, params, httpClient,
                localContext, "utf-8", requestConfig);
        Document doc = Jsoup.parse(clientReset);
        Element error = doc.getElementById("error_info");
        if (error == null) {
            boltResult.setCode("1");
            boltResult.setDesc("验证成功");
            boltResult.setData("");
            memCachedClient.delete(cuserId + "pwdCookie");
        } else if ("请求错误".equals(error.text())) {
            boltResult.setCode("0");
            boltResult.setDesc("连接超时，请重新找回");
            boltResult.setData("");
            return boltResult;
        } else if ("验证码错误".equals(error.text())) {
            boltResult.setCode("2");
            boltResult.setDesc("手机验证码错误，请重新输入");
            boltResult.setData("");
            return boltResult;
        } else {
            boltResult.setCode("0");
            boltResult.setDesc(error.text());
            boltResult.setData("");
        }
        LOGGER.info("修改密码第三步请求消耗时间:" + (System.currentTimeMillis() - start));
        return boltResult;
    }

    /**
     * 找回学信用户名
     */
    @RequestMapping("/control/investigation/findChsiUserName.go")
    public BoltResult findChsiUserName(ChsiBean chsiBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = null;

        String cuserId = chsiBean.getCuserId();
        // 客户端类型 1:ios
        int iclient = chsiBean.getIclient();

        String xm = dencryptStrByClient(iclient, chsiBean.getXm());
        String sfzh = dencryptStrByClient(iclient, chsiBean.getSfzh());
        String code = chsiBean.getCode();
        if (CheckUtil.isNullString(xm) || CheckUtil.isNullString(sfzh)) {
            LOGGER.info("数据加密不正确");
            boltResult.setCode("0");
            boltResult.setDesc("数据加密不正确");
            return boltResult;
        }

        Object object = memCachedClient.get(cuserId + "pwdCookie");
        if (object != null) {
            cookieStore = (CookieStore) object;
            localContext.setAttribute("http.cookie-store", cookieStore);
        }
        Map<String, String> params = new HashMap<>();
        params.put("sfzh", sfzh);
        params.put("xm", xm);
        params.put("captch", code);

        String client = HpClientUtil.httpPost(RETRIEVE_USERNAME_URL, requestHeaderMap, params, httpClient,
                localContext, "utf-8", requestConfig);
        Document doc = Jsoup.parse(client);
        Element error = doc.getElementById("user_retrivelgname_fm_error_info");
        if (error == null) {
            Element elt = doc.getElementById("regCont");
            String result = elt.getElementsByClass("retriveName").text();
            result = result.replace("【找回密码?】", "");
            boltResult.setCode("1");
            boltResult.setDesc(result);
            boltResult.setData("");
        } else {
            if ("验证码不正确".equals(error.text())) {
                String base64img = this.getChsiPwdImgCodeBase64(cookieStore, cuserId);
                LOGGER.info("base64img:{}", base64img);
                if (CheckUtil.isNullString(base64img)) {
                    boltResult.setCode("0");
                    boltResult.setDesc("获取图片验证码失败");
                    return boltResult;
                }
                JSONObject data = new JSONObject();
                data.put("base64img", base64img);
                boltResult.setCode("2");
                boltResult.setDesc(error.text());
                boltResult.setData(data);
            }
        }
        LOGGER.info("找回学信用户名:" + (System.currentTimeMillis() - start));
        return boltResult;
    }

    /**
     * 获取学信网验证码图片base64转换字符串 找回密码用
     *
     * @param cookieStore
     * @return
     */
    private String getChsiPwdImgCodeBase64(CookieStore cookieStore, String cuserId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpContext localContext = new BasicHttpContext();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        //启用cookie存储
        localContext.setAttribute("http.cookie-store", cookieStore);
        String captchaUrl = "https://account.chsi.com.cn/account/captchimagecreateaction.action?time=" + new Date()
                .getTime();
        BufferedImage bufferedImage = HpClientUtil.getRandomImageOfJPEG(captchaUrl, requestHeaderMap, httpClient,
                localContext, requestConfig);
        memCachedClient.set(cuserId + "pwdCookie", cookieStore, 1000 * 60 * 30);
        byte[] data = null;
        try {
            if (bufferedImage != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                data = baos.toByteArray();
                // 对字节数组Base64编
                BASE64Encoder encoder = new BASE64Encoder();
                String base64Str = encoder.encode(data);// 返回Base64编码过的字节数组字符串
                base64Str = base64Str.replace("\r\n", "");
                return base64Str;
            }
        } catch (IOException e) {
            LOGGER.error("获取验证码图片base64字符串失败", e);
        }
        return null;
    }


    /**
     * 根据客户端类型解密
     *
     * @param client 客户端类型 1:ios
     * @param str
     * @return
     */
    private String dencryptStrByClient(int client, String str) {
        //  如果是ios
        if (client == 1) {
            return CaiyiEncryptIOS.dencryptStr(str);
        } else {
            return CaiyiEncrypt.dencryptStr(str);
        }
    }

}

