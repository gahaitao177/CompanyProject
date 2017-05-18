package com.caiyi.nirvana.analyse.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.BaseService;
import com.caiyi.nirvana.analyse.env.Profile;
import com.caiyi.nirvana.analyse.kafka.KafkaService;
import com.caiyi.nirvana.analyse.util.AESCipher;
import com.rbc.frame.ServiceContext;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Properties;

/**
 * Created by been on 2017/1/9.
 */
public class UserBeanImpl extends BaseService {

    public Logger logger = LogManager.getLogger(getClass());
    public Properties properties = new Properties();


    public KafkaService kafkaService;
    public String topic = "nirvana_analyses_topic";
    public UserBeanImpl() {
        super();

        boolean isProd = Profile.instance.isProd();
        try {
            if (isProd) {
                properties.load(getClass().getResourceAsStream("/config_prod.properties"));

            } else {
                properties.load(getClass().getResourceAsStream("/config_dev.properties"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String brokers = properties.getProperty("brokers");
        topic = properties.getProperty("topic");
        kafkaService = new KafkaService(brokers);
    }

    /**
     * 文件上传接口
     *
     * @param bean
     * @param context
     * @param request
     * @param response
     * @return
     */
    public int uploadFiles(UserBean bean, ServiceContext context,
                           HttpServletRequest request, HttpServletResponse response) {
        logger.info("开始上传文件.......");
        bean.setBusiErrCode(-2000);
        bean.setBusiErrDesc("文件格式不合法");

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(5242880);
            ServletFileUpload upload = new ServletFileUpload();
            upload.setFileItemFactory(factory);
            List<FileItem> items = (List<FileItem>) upload.parseRequest(request);
            bean.setBusiErrDesc("文件上传成功");
            JSONArray jsonArray = new JSONArray();
            String realIp = request.getHeader("X-Forwarded-For");
            if (realIp != null) {
                logger.info("app ip ---------> " + realIp);
            }
            for (FileItem item : items) {
                if (!item.isFormField()) { // 文件
                    byte[] data = IOUtils.toByteArray(item.getInputStream());
                    String json = AESCipher.decryptAES(data);
                    if (StringUtils.isNotEmpty(json)) {
                        //保证传递到storm中的数据是json 格式
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        jsonObject.put("appIp", realIp);
                        jsonArray.add(jsonObject);
                    }
                }
            }
            if (jsonArray.size() > 0) {
                //将数据发送到后端storm中处理
                String data = JSONObject.toJSONString(jsonArray);
                kafkaService.sendMessage(topic, data);
            }
            bean.setBusiErrDesc("success");
            bean.setBusiErrCode(1);
        } catch (Exception e) {
            bean.setBusiErrCode(-4000);
            bean.setBusiErrDesc(e.getMessage() + " 文件上传失败");
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        logger.info("文件上传结束");
        return 1;
    }


    /**
     * 接口测试
     *
     * @param bean
     * @param context
     * @param request
     * @param response
     *
     * @return
     */

    public int test(UserBean bean, ServiceContext context, HttpServletRequest request, HttpServletResponse response) {
        logger.info("测试");
        bean.setBusiErrCode(1);
        boolean env = Profile.instance.isProd();
        bean.setBusiErrDesc("prod------>" + env);
        try {
//        String remoteIp = request.getRemoteHost();
            // TODO: 2017/2/19 获取远程ip,限制访问次数
//            DRPCClient drpcClient = DrpcClientFactory.getDefaultDRPCClient();
            String probe = "demo";
//            String result = drpcClient.execute(TopologyConfig.DRPC_FUNCNAME, probe);
            bean.setBusiErrDesc("prod------>" + env);
        } catch (Exception e) {
            bean.setBusiErrDesc("prod------> " + env + "   cause------> " + e.getMessage());
        } finally {
            return 1;
        }
    }
}
