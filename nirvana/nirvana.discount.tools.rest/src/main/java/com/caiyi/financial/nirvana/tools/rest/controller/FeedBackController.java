package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.tools.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by dengh on 2016/8/11.
 */
@RestController
@RequestMapping("/credit")
public class FeedBackController {
    private static Logger log = LoggerFactory.getLogger(FeedBackController.class);

    private static  String PIC_PATH = "imgs/user_icon/";
    private static  String USER_GROUP = "2";
    private static  String SEVER_ADDRESS = "http://web.img.huishuaka.com";
    public static   String ENCODING = "UTF-8";
    public static   String PATH = "/opt/export/www/";
    @Resource(name = Constant.HSK_TOOL)
    IDrpcClient client;

    // 上传优惠图片
    @RequestMapping("/submitCheap.go")
    public void submitCheap(FeedBackBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String enctype = request.getHeader("content-type");
        PIC_PATH = "imgs/feed_back/";
        if (enctype != null && enctype.indexOf("multipart/form-data") > -1) {
            try {
                handleMultiparRequest(request,response,bean,2);
                System.out.println("icon:"+bean.getIcon());
                String result    =  client.execute(new DrpcRequest("FeedBackBolt", "submitWrong", bean));
                FeedBackBean feedBackBean = JSON.parseObject(result, FeedBackBean.class);

                if ( feedBackBean.getBusiErrCode() == 1 ) {
                    feedBackBean.setBusiErrCode(1);
                    feedBackBean.setBusiErrDesc("上传优惠图片成功");
                    System.out.println("上传优惠图片成功");
                } else {
                    feedBackBean.setBusiErrCode(-1000);
                    feedBackBean.setBusiErrDesc("上传优惠图片失败");
                    System.out.println("上传优惠图片失败");
                }
                sendData(JSONObject.toJSONString(feedBackBean), request,response);


            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(-1000);
                bean.setBusiErrDesc("上传失败");
                sendData(JSONObject.toJSONString(bean), request,response);
            }
        }

    }
    @RequestMapping("/submitWrong.go")
    public void submitWrong(FeedBackBean bean, HttpServletRequest request, HttpServletResponse response){
        String result    =  client.execute(new DrpcRequest("FeedBackBolt", "submitWrong", bean));
        sendData(result, request,response);
    }

    @RequestMapping("/customServiceqq.go")
    public void custom_service(FeedBackBean bean, HttpServletRequest request, HttpServletResponse response){
        String result    =  client.execute(new DrpcRequest("FeedBackBolt", "custom_service", bean));
        sendData(result, request,response);
    }
    protected int sendData(String str, HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(str);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return 1;
    }


    private  void handleMultiparRequest(HttpServletRequest request, HttpServletResponse response, FeedBackBean bean, int type) throws Exception {
        String uploadPath= PIC_PATH + getUploadDir();
        request.setCharacterEncoding(ENCODING);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        String photoPath = PATH  + uploadPath;
        File uploadFile = new File(photoPath);
        System.out.println("photoPath:" + photoPath);
        System.out.println("photoPathexists:" + uploadFile.exists());
        if (!uploadFile.exists()) {
            boolean cret = uploadFile.mkdirs();
            System.out.println("cret:" + cret);
        }
        factory.setRepository(new File(photoPath));
        factory.setSizeThreshold(5242880);
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileItemFactory(factory);
        List<FileItem> items = (List<FileItem>)upload.parseRequest(request);
        Method[] methods = User.class.getMethods();
        if(2 == type){
            methods = FeedBackBean.class.getMethods();
        }
        String classtype = null;
        for(FileItem item : items) {
            String fieldName = item.getFieldName();
            String firstChar = fieldName.substring(0, 1).toUpperCase();
            fieldName = firstChar + fieldName.substring(1);
            if(item.isFormField()) {
                //普通参数设置到bean中
                String setter = "set" + fieldName;
                Object arg = item.getString("UTF-8");
                for(Method method : methods) {
                    if(setter.equalsIgnoreCase(method.getName())) {
                        classtype = method.getParameterTypes()[0].getName();
                        if ("int".equals(classtype)) {
                            arg = Integer.parseInt(item.getString());;
                        }
                        method.invoke(bean, arg);
                        break;
                    }
                }
            } else {
                //二进制文件参数处理
                List<String> exts = new ArrayList<String>();
                exts.add(".jpg");
                exts.add(".JPG");
                exts.add(".PNG");
                exts.add(".png");
                String tmpName = item.getName();
                String ext = tmpName.substring(tmpName.lastIndexOf("."));
                if(!exts.contains(ext)) {
                    throw new Exception("只能上传.jpg或.png格式图片");
                }
                Random random = new Random();
                BigInteger big = new BigInteger(64, random);
                String imgName = big.toString() + ext;
                File originalImg = new File(photoPath, imgName);
                OutputStream outStream = new FileOutputStream(originalImg);
                InputStream inStream = item.getInputStream();
                byte[] buffer = new byte[4096];
                int size = inStream.read(buffer);
                while(size != -1) {
                    outStream.write(buffer, 0, size);
                    size = inStream.read(buffer);
                }
                inStream.close();
                outStream.close();
                inStream = null;
                outStream = null;
                StringBuilder builder = new StringBuilder();
                // add by lcs 20150604 start
                builder.append(SEVER_ADDRESS);
                // add by lcs 20150604 end
                builder.append("/");
                builder.append(uploadPath);
                builder.append("/");
                builder.append(imgName);
                bean.setIcon(builder.toString());
                System.out.println(bean.getCuserId()+" 图片地址："+builder.toString());

            }
        }
    }
    //以年份+月为单位保存用户上传头像的目录(最大文件数为32000)
    private  String getUploadDir(){
        Calendar cal = Calendar.getInstance();
        // 获取年
        int year = cal.get(Calendar.YEAR);
        // 获取月
        int month = cal.get(Calendar.MONTH) + 1;
        // 获取日期
        int day = cal.get(Calendar.DATE);
        StringBuilder dir = new StringBuilder();
        dir.append(year);
        dir.append("/");
        dir.append(String.valueOf(year) + String.valueOf(month <10 ? ("0" + month) : ("" + month)));
        dir.append("/");
        dir.append(String.valueOf(year) + String.valueOf(month <10 ? ("0" + month) : ("" + month)) + String.valueOf(day < 10?("0" + day) : ("" + day)));
        return dir.toString();
    }



}
