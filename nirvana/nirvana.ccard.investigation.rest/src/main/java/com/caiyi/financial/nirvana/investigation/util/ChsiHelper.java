package com.caiyi.financial.nirvana.investigation.util;

import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiEducationDto;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shaoqinghua on 2017/1/22.
 * 学信网使用的一些辅助方法
 */
public class ChsiHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChsiHelper.class);
    private static final String CHSI_DEGREE_PATH = "/opt/export/data/chsi/";

    /**
     * 获取请求配置信息
     *
     * @return
     */
    public static RequestConfig getRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();
        return requestConfig;
    }

    /**
     * 获取请求头信息
     *
     * @return
     */
    public static Map<String, String> getRequestHeaderMap() {
        Map<String, String> requestHeaderMap = new HashMap<>();
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        requestHeaderMap.put("Cache-Control", "max-age=0");
        requestHeaderMap.put("Connection", "keep-alive");
        requestHeaderMap.put("Host", "account.chsi.com.cn");
        requestHeaderMap.put("Upgrade-Insecure-Requests", "1");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/55.0.2883.87 Safari/537.36");
        return requestHeaderMap;
    }

    /**
     * 判断是否需要验证码
     *
     * @param doc
     * @return
     */
    public static boolean isNeedCaptcha(Element doc) {
        boolean isNeed = false;
        Element captcha = doc.getElementById("captcha");
        if (captcha == null) {
            LOGGER.info("captcha:无需验证码");
        } else {
            isNeed = true;
            LOGGER.info("captcha:需要验证码");
        }
        return isNeed;
    }

    /**
     * 获取学信网验证码图片base64转换字符串
     *
     * @param cookieStore
     * @return
     */
    public static String getChsiImgCode(CookieStore cookieStore, String imgCodeType) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpContext localContext = new BasicHttpContext();
        //设置请求配置信息
        RequestConfig requestConfig = ChsiHelper.getRequestConfig();
        //设置请求头信息
        Map<String, String> requestHeaderMap = ChsiHelper.getRequestHeaderMap();
        //启用cookie存储
        localContext.setAttribute("http.cookie-store", cookieStore);
        String ImgCodeUrl = "";
        //String filePath = "C:/Users/Sylvia/Desktop";
        if ("3".equals(imgCodeType)) {
            ImgCodeUrl = "https://account.chsi.com.cn/passport/captcha.image?id=" + Math.random();
        } else {
            ImgCodeUrl = "https://account.chsi.com.cn/account/captchimagecreateaction.action?time=" + new Date()
                    .getTime();
        }

        BufferedImage bufferedImage = HpClientUtil.getRandomImageOfJPEG(ImgCodeUrl, requestHeaderMap, httpClient,
                localContext, requestConfig);
        byte[] data = null;
        try {
            if (bufferedImage != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                data = baos.toByteArray();
                // 对字节数组Base64编码
                BASE64Encoder encoder = new BASE64Encoder();
                return encoder.encode(data);// 返回Base64编码过的字节数组字符串
            }
        } catch (IOException e) {
            LOGGER.error("获取验证码图片base64字符串失败", e);
        }
        //HpClientUtil.httpGetImage(ImgCodeUrl, requestHeaderMap, filePath, httpClient, localContext, requestConfig);
        return null;
    }

    /**
     * 从响应对象中获取响应页面内容
     *
     * @param response
     * @param encode
     * @return
     */
    public static String getContentFromResponse(HttpResponse response, String encode) {
        String content = "";
        InputStream in = null;

        try {
            HttpEntity entity = response.getEntity();
            StringBuffer buffer = new StringBuffer();
            in = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));

            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }

            content = buffer.toString();
            in.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Post请求的响应对象
     *
     * @param url
     * @param headers
     * @param parames
     * @param httpClient
     * @param localContext
     * @param encode
     * @param requestConfig
     * @return
     */
    public static HttpResponse getResponsePost(String url, Map<String, String> headers, Map<String, String> parames,
                                               HttpClient httpClient, HttpContext localContext, String encode,
                                               RequestConfig requestConfig) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if (headers != null) {
                Iterator e = headers.keySet().iterator();

                while (e.hasNext()) {
                    String entity = (String) e.next();
                    httpPost.setHeader(entity, (String) headers.get(entity));
                }
            }

            if (parames != null) {
                ArrayList e1 = new ArrayList();
                Iterator entity1 = parames.entrySet().iterator();

                while (entity1.hasNext()) {
                    Map.Entry statusCode = (Map.Entry) entity1.next();
                    e1.add(new BasicNameValuePair((String) statusCode.getKey(), (String) statusCode.getValue()));
                }

                UrlEncodedFormEntity statusCode1 = new UrlEncodedFormEntity(e1, encode);
                httpPost.setEntity(statusCode1);
            }

            HttpResponse response = httpClient.execute(httpPost, localContext);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpPost != null) {
                httpPost.abort();
            }
        }
        return null;
    }

    /**
     * 保存学历html页面
     *
     * @param cuserId 用户id
     * @param date    学信账号记录创建时间
     * @param content 学历页面内容
     */
    public static void saveDegreeHtmlPage(String cuserId, Date date, String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String createTime = sdf.format(date);
        // /opt/export/data/chsi/createTime/cuserId.html
        String chsiDegreePath = SystemConfig.get("CHSI_DEGREE_PATH");
        if (CheckUtil.isNullString(chsiDegreePath)) {
            chsiDegreePath = CHSI_DEGREE_PATH;
        }
        LOGGER.info("学历Html页面保存位置:" + chsiDegreePath + createTime + "/{}.html", cuserId);
        File filePath = new File(chsiDegreePath + createTime);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        BufferedWriter bw = null;
        try {
            //将学历页面内容写入html文件
            bw = new BufferedWriter(new FileWriter(new File(filePath, cuserId + ".html")));
            bw.write(content);
        } catch (IOException e) {
            LOGGER.error("保存学历html页面失败", e);
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                LOGGER.error("流关闭失败", e);
            }
        }
    }

    /**
     * 查找全部学历详情数据
     *
     * @param content
     * @return
     */
    public static List<ChsiEducationDto> getAllDegrees(String content) {
        Document degreeDoc = Jsoup.parse(content);
        List<Map<String, String>> degrees = ChsiHelper.getDegreesInfo(degreeDoc);
        List<List<String>> otherInfoList = ChsiHelper.getDegreesOtherInfo(degreeDoc);
        try {
            if (degrees.size() > 0 && otherInfoList.size() > 0) {
                //填充学历表格数据
                for (int i = 0; i < otherInfoList.size(); i++) {
                    degrees.get(i).put("学校名称：", otherInfoList.get(i).get(0));
                    degrees.get(i).put("专业：", otherInfoList.get(i).get(1));
                    degrees.get(i).put("学号：", otherInfoList.get(i).get(2));
                    degrees.get(i).put("层次：", otherInfoList.get(i).get(3));
                    degrees.get(i).put("学历类别：", otherInfoList.get(i).get(4));
                    degrees.get(i).put("学习形式：", otherInfoList.get(i).get(5));
                    degrees.get(i).put("证件号码：", otherInfoList.get(i).get(6));
                }
                //封装到学历对象中
                List<ChsiEducationDto> eduList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                for (Map<String, String> degree : degrees) {
                    ChsiEducationDto edu = new ChsiEducationDto();
                    edu.setName(degree.get("姓名："));
                    edu.setSex(("男".equals(degree.get("性别：")) ? 1 : 0));
                    edu.setBirthday(sdf.parse(degree.get("出生日期：")));
                    edu.setNation(degree.get("民族："));
                    edu.setCode(degree.get("证件号码："));
                    edu.setCollege(degree.get("学校名称："));
                    edu.setLevels(degree.get("层次："));
                    edu.setMajor(degree.get("专业："));
                    edu.setSchooling(Double.parseDouble(degree.get("学制：")));
                    edu.setSchoolingType(degree.get("学历类别："));
                    edu.setLearnForm(degree.get("学习形式："));
                    edu.setDepartment(degree.get("分院："));
                    edu.setPlace(degree.get("系（所、函授站）："));
                    edu.setiClass(degree.get("班级："));
                    edu.setStudentNo(degree.get("学号："));
                    edu.setJoinTime(sdf.parse(degree.get("入学日期：")));
                    edu.setGraduate(sdf.parse(degree.get("离校日期：")));
                    edu.setState(degree.get("学籍状态：").contains("不在籍") ? 0 : 1);
                    edu.setStateDetail(degree.get("学籍状态："));
                    eduList.add(edu);
                }
                return eduList;
            }
            return null;
        } catch (ParseException e) {
            LOGGER.info("解析页面学历数据失败", e);
        }
        return null;
    }


    /**
     * 获取学历表格中的信息
     *
     * @param doc
     * @return
     */
    public static List<Map<String, String>> getDegreesInfo(Document doc) {
        Elements elements = doc.getElementsByAttributeValue("class", "mb-table");
        List<Map<String, String>> degrees = new ArrayList<>();
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                Map<String, String> temp = new LinkedHashMap<>();
                Elements items = element.getElementsByTag("th");
                Elements values = element.getElementsByTag("td");
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).html().contains("预计毕业日期：")) {
                        temp.put("离校日期：", values.get(i).html());
                    } else {
                        temp.put(items.get(i).html(), values.get(i).html());
                    }
                }
                degrees.add(temp);
            }
        }
        return degrees;
    }

    /**
     * 获取学历表格中缺失的信息
     *
     * @param doc
     * @return
     */
    public static List<List<String>> getDegreesOtherInfo(Document doc) {
        Elements scripts = doc.select("script");
        List<List<String>> infoList = new ArrayList<>();
        if (scripts != null && scripts.size() > 0) {
            for (Element element : scripts) {
                List info = new ArrayList();
                String scriptStr = element.outerHtml();
                if (scriptStr.contains("initDataInfo")) {
                    scriptStr = scriptStr.substring(scriptStr.indexOf("initDataInfo(\""), scriptStr.lastIndexOf
                            ("</script>"));
                    String[] strs = scriptStr.split("initDataInfo\\(\"ds2");
                    for (int i = 0; i < strs.length; i++) {
                        strs[i] = strs[i].trim();
                    }
                    for (String str : strs) {
                        if (str.length() > 0) {
                            info.add((str.split("\""))[4]);
                        }
                    }
                    infoList.add(info);
                }
            }
        }
        return infoList;
    }

    /**
     * 获取最高学历等级
     *
     * @param degrees
     * @return
     */
    public static int getTopEducationLevel(List<ChsiEducationDto> degrees) {
        int educationLevel = 0;
        if (degrees != null && degrees.size() > 0) {
            String levels = degrees.get(0).getLevels();
            if (levels.contains("专科")) {
                educationLevel = 1;
            } else if (levels.contains("本科")) {
                educationLevel = 2;
            } else if (levels.contains("硕士")) {
                educationLevel = 3;
            } else if (levels.contains("博士")) {
                educationLevel = 4;
            }
        }
        return educationLevel;
    }


}
