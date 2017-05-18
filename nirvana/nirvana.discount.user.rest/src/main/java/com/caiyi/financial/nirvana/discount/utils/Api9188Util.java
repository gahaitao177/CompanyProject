package com.caiyi.financial.nirvana.discount.utils;

import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/9/19.
 * 对9188接口的封装
 */
public class Api9188Util {
    private static String apiHost;
    static {
        apiHost =SystemConfig.get("apiHost");
        if(apiHost != null && apiHost.indexOf("http://")<0){
            apiHost = "http://"+apiHost;
        }
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(Api9188Util.class);

    /**
     * 9188手机号注册资格检测接口
     */
    private static String MOBREGISTERCHK = apiHost + "/user/mobregisterchk.go";
    /**
     * 9188手机号注册接口
     */
    private static String MOBREGISTER = apiHost + "/user/mobregister.go";

    /**
     * 9188获取个人信息接口
     */
    private final static String GETUSERINFO = apiHost+"/user/getuserbasicinfo.go";


    public static BoltResult registerchk(int source, String mobileNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", source);
        params.put("mobileNo", mobileNo);
        Element root = callApi(MOBREGISTERCHK, params);
        String code = root.attributeValue("code");
        String desc = root.attributeValue("desc");
        BoltResult result =  new BoltResult(code,desc);
        return result;
    }

    public static BoltResult getUserInfo(String appId, String accessToken, int source) {
        Map<String, Object> params = new HashMap<>();
        params.put("logintype", "1");
        params.put("appid", appId);
        params.put("accesstoken", accessToken);
        params.put("source", source);
        Element root = callApi(GETUSERINFO, params);
        String code = root.attributeValue("code");
        String desc = root.attributeValue("desc");
        BoltResult result =  new BoltResult(code,desc);
        if("0".equals(code)){
            Element row = root.element("row");
            User user = new User();
            user.setCnickname(row.attributeValue("nickid"));
            user.setImobbind(Integer.parseInt(row.attributeValue("mobbind")));
            user.setCuserId(row.attributeValue("userid"));
            user.setRealname(row.attributeValue("realname"));
            user.setIdcard(row.attributeValue("idcard"));

            result.setData(user);
        }
//        <Resp code="0" desc="查询成功">
//        <row userid="a8981db6-a908-444c-8b4b-b681495b91c9" nickid="豪放独立小提琴" source="6000" mobileno="18301852931" mobbind="1" phoneLogin="1" realname="" idcard="" bankCode="" bankCard="" branchName="" bankPro="" bankCity=""/>
//        </Resp>
        return result;
    }


    /**
     * @param pwd       明文 密码
     * @param source    渠道
     * @param mobileNo  手机
     * @param ipAddr    ip地址
     * @param logintype 类型
     * @return
     */
    public static BoltResult register(String pwd, int source, String mobileNo, String ipAddr, int logintype) {
        Map<String, Object> params = new HashMap<>();
        params.put("pwd", pwd);//明文
        params.put("source", source);
        params.put("mobileNo", mobileNo);
        params.put("ipAddr", ipAddr);
        params.put("logintype", logintype);

        Element root = callApi(MOBREGISTER, params);
        String code = root.attributeValue("code");
        String desc = root.attributeValue("desc");
        BoltResult result =  new BoltResult(code,desc);
        if("0".equals(code)){
            User user = new User();
            user.setAccessToken(root.attributeValue("accesstoken"));
            user.setAppId(root.attributeValue("appid"));
            user.setCuserId(root.element("row").attributeValue("userid"));
            result.setData(user);
        }
        return result;
    }


    private static Element callApi(String url, Map<String, Object> param) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> nvps = new ArrayList<>();
            if (param != null) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue().toString()));
                }
                post.setEntity((new UrlEncodedFormEntity(nvps)));
            }
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(entity.getContent());
            Element root = doc.getRootElement();
            LOGGER.info("调用接口{}\n参数{}\n返回值{}",url,builder.toString(),root.asXML());
            return root;
        } catch (Exception e) {
            LOGGER.error("请求9188接口失败", e);
            throw new UserException("调用异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) throws IOException, DocumentException {
//        LOGGER.info("111111111");
//        register("123456",6000,"18301852931","127.0.0.1",1);

        StringBuilder s = new StringBuilder("123");
        System.out.println(s.substring(0,s.length()-2));
        getUserInfo("ltJS201YH60O9IX200BYK14S401C2C2C9","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtLIjxk3vWmGZZ8GUmBWcUeQPztCPac4ebKcZpx8LaUIjTyqi81TrmZrV4imiC1reWYBp4LncfObzjy7bNM0ryJxzvWG5/Pxl6ZOdBKTWx85xh5Quu6fnpSTuTABmCyN+me4Zyxyi1ZPA==",6000);

//        String str = "<Resp code=\"0\" desc=\"注册成功,祝您中大奖!\" appid=\"lt2016PA0FX92FPN0L110U62HO8VP3M48\" accesstoken=\"+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K96nf/VO8u7hFewq6ywDUTvBnlGpAcvQ/UtNpJryF2hsyNOdLA8gg1XD0PD9LqYUcw5uYiLJFb/0/gvSZcz2dD4mb/xLusoH49Usxf5ChmPqhaF+0IJiFLdQlGrl+/By0Q==\"><row userid=\"a29b0bef-8b53-4136-b712-66921b52a164\"/></Resp>";
//        Document doc = DocumentHelper.parseText(str);
//        Element root = doc.getRootElement();
//        System.out.println(root.asXML());
//        System.out.println(root.element("row").attributeValue("userid"));

//        String url = "http://t2015.9188.com/user/mobregisterchk.go";
//        Map<String,Object> map = new HashMap<>();
//        map.put("source","6000");
//        map.put("mobileNo","18301852937");
//        System.out.println(callApi(url,map).toJsonString());
//
//
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost post = new HttpPost(url);
//        HashMap<String, String> params = new HashMap<>();
//        post.setEntity(new StringEntity("source=6000&mobileNo=18301852937"));
//        post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
//        CloseableHttpResponse response =  client.execute(post);
//        HttpEntity entity = response.getEntity();
////        String respStr = EntityUtils.toString(entity);
////        System.out.println(respStr);
//
//        SAXReader saxReader = new SAXReader();
//        Document doc = saxReader.read(entity.getContent());
//        Element root = doc.getRootElement();
//        String code = root.attribute("code").getValue();
//        String desc = root.attribute("desc").getValue();
//        System.out.println(code+"--"+desc);
//        response.close();
    }
}
