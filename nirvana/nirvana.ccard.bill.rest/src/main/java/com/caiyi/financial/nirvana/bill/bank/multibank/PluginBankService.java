package com.caiyi.financial.nirvana.bill.bank.multibank;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.base.OSUtil;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ljl
 * Windows 插件网银登陆服务类
 */
public class PluginBankService extends AbstractHttpService{


    /**
     * 开始执行登录
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int startTask(Channel bean,MemCachedClient cc) {
        dencrypt_data(bean);
        String bankFlag = bean.getBankId().length() >= 2 ? "" + bean.getBankId() : "0" + bean.getBankId();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        loginContextMap.put(bean.getSourceCode() + "userRand",uuid);//保存随机任务id
        loginContextMap.put(bean.getSourceCode() + "acctParams",
                bean.getDencryIdcard()+"@"+bean.getDencryBankPwd()+"@"+bankFlag);//保存登陆账户信息

        Map<String,String> params = buildParams(bean);
        params.put("action","s");
        String result = httpService(params,bean);
        return transReturn(bean,cc,result);
    }

    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     */
    public String getYzmBase64(Channel bean) {
        Map<String,String> params = buildParams(bean);
        params.put("action","1");
        httpService(params,bean);
        String base64Code = bean.getBankRand();
        if (3==bean.getBusiErrCode()) {
            if (!OSUtil.isLinux()) {
                //非线上环境将验证码存入磁盘,用于测试,返回验证码位置
                saveYzm(bean.getCuserId(),base64Code);
            }
            return base64Code;
        } else {
            logger.info("cuserId=="+bean.getCuserId()+"图片验证码获取失败,失败原因:"+bean.getBusiErrDesc());
            String desc = bean.getBusiErrDesc();
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc(desc);
            return null;
        }
    }

    /**
     * 提交图片验证码
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int submitYzm(Channel bean,MemCachedClient cc) {
        Map<String,String> params = buildParams(bean);
        params.put("action","2");
        params.put("captcha",bean.getBankRand());
        String result = httpService(params,bean);
        return transReturn(bean,cc,result);
    }

    /**
     * 获取短信验证码
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int getSms(Channel bean) {
        Object phoneNum = loginContextMap.get(bean.getSourceCode()+"smsMsg");
        if (phoneNum!=null){
            logger.info(bean.getCuserId()+" ---短信验证码已发送");
            bean.setBusiErrCode(BillConstant.success);
            bean.setBusiErrDesc("短信验证码发送成功！");
            bean.setPhoneNum((String) phoneNum);
            loginContextMap.remove(bean.getSourceCode()+"smsMsg");
            return 1;
        }
        Map<String,String> params = buildParams(bean);
        params.put("action","3");
        params.put("captcha",bean.getBankRand());
        httpService(params,bean);
        logger.info("cuserId=="+bean.getCuserId()+"短信验证码获取结果:" + bean.getBusiErrDesc());
        if(2==bean.getBusiErrCode()){
            bean.setBusiErrCode(BillConstant.success);
            bean.setBusiErrDesc("短信验证码发送成功！");
            return 1;
        }else {
            logger.info("cuserId=="+bean.getCuserId()+"短信验证码获取失败,失败原因:"+bean.getBusiErrDesc());
            return 0;
        }
    }

    /**
     * 提交短信验证码
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int checkSms(Channel bean,MemCachedClient cc) {
        Map<String,String> params = buildParams(bean);
        params.put("action","4");
        params.put("phonemessage",bean.getBankRand());
        String result = httpService(params,bean);
        return transReturn(bean,cc,result);
    }

    /**
     * http请求结果处理
     * @param bean 参数bean
     * @return 执行结果 0:失败 1:成功
     */
    public int transReturn(Channel bean,MemCachedClient cc,String result){
        if(1==bean.getBusiErrCode()){
            logger.info("cuserId=="+bean.getCuserId()+"任务完成,开始解析账单");
            return 1;
        }else if(0==bean.getBusiErrCode()){//参数错误
            return 0;
        }else if (5==bean.getBusiErrCode() && !bean.getBusiErrDesc().contains("验证码")){
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBankRand("");
            return 0;
        }else{
            logger.info("cuserId=="+bean.getCuserId()+"任务未跑完,当前阶段,code=="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            return 0;
        }
    }
    /**
     * 任务完成,或者失败发生关闭命令
     * @param bean 参数对象
     */
    public void closeWindow(Channel bean){
        Object uuid = loginContextMap.get(bean.getSourceCode() + "userRand");
        if (uuid==null){
            return;
        }
        Map<String,String> params = buildParams(bean);
        params.put("action","c");
        loginContextMap.remove(bean.getSourceCode() + "userRand");
        loginContextMap.remove(bean.getSourceCode() + "acctParams");
        httpService(params,bean);
        logger.info("关闭窗口结果："+bean.getBusiErrDesc());
    }

    /**
     * 请求参数设置
     * @param bean 参数bean
     * @return 参数集合
     */
    public Map<String,String> buildParams(Channel bean){
        Map<String,String> params = new HashMap<>();
        String uuid = (String) loginContextMap.get(bean.getSourceCode() + "userRand");
        String acct_param = (String) loginContextMap.get(bean.getSourceCode() + "acctParams");
        String[] accts = acct_param.split("@");
        String bankFlag;
        if(!StringUtils.isEmpty(bean.getBankId())){
            bankFlag = bean.getBankId().length() >= 2 ? "" + bean.getBankId() : "0" + bean.getBankId();
        }else{
            bankFlag = accts[2];
        }
        params.put("method","login");
        params.put("taskid",uuid);
        params.put("bankcode",bankFlag);
        params.put("account",accts[0]);
        params.put("password",accts[1]);
        params.put("captcha","");
        params.put("phonemessage","");
        return params;
    }

    /**
     * http发送请求
     * @param params 请求参数集合
     * @param bean bean对象
     * @return 请求结果
     */
    public String httpService(Map<String,String> params,Channel bean) {
        LoginContext loginContext = null;
        try {
            String servip = SystemConfig.get("pluginBank.servip");
            String servport = SystemConfig.get("pluginBank.servport");
            String account = URLEncoder.encode(params.get("account").replaceAll("\\s*",""),"UTF-8");
            String password = URLEncoder.encode(params.get("password").replaceAll("\\s*",""),"UTF-8");
            String captcha = URLEncoder.encode(params.get("captcha").replaceAll("\\s*",""),"UTF-8");
            String phonemessage = URLEncoder.encode(params.get("phonemessage").replaceAll("\\s*",""),"UTF-8");

            String url = "http://" + servip + ":" + servport + "/windows/bank?method=" + params.get("method")
                    + "&taskid=" + params.get("taskid") + "&bankcode=" + params.get("bankcode")+"&action=" + params.get("action")
                    +"&account="+account +"&password="+password+"&captcha="+captcha +"&phonemessage="+phonemessage;
            loginContext = createLoginContext(new BasicCookieStore());
            String result = httpGet(url, loginContext);
            logger.info("cuserId=="+bean.getCuserId()+"请求结果>>>>" + result);
            JSONObject json = JSONObject.parseObject(result);
            String code = json.get("Code").toString();
            if ("1".equals(code)) {//请求成功
                String taskCode = json.get("TaskCode").toString();
                String taskDesc = json.get("TaskDesc").toString();
                bean.setBusiErrDesc(taskDesc);
                switch (taskCode) {
                    case "1": {
                        String imgBase64 = json.get("ImgBase64").toString();
                        bean.setBankRand(imgBase64);
                        bean.setBusiErrCode(BillConstant.needimg);
                        bean.setBusiErrDesc("需要图片验证码");
                        break;
                    }
                    case "2": {
                        String phoneNum = json.get("PhoneNum").toString();
                        String resendInterval = json.get("ResendInterval").toString();
                        bean.setPhoneNum(phoneNum);
                        bean.setResendInterval(resendInterval);
                        bean.setBusiErrCode(BillConstant.needmsg);
                        bean.setBusiErrDesc("需要短信验证");
                        break;
                    }
                    case "3": {
                        String cookie = json.get("Cookie").toString();
                        bean.setBankSessionId(cookie);
                        bean.setBusiErrCode(BillConstant.success);
                        bean.setBusiErrDesc("登录成功,开始解析账单");
                        break;
                    }
                    case "4": {
                        bean.setBusiErrCode(BillConstant.fail);
                        break;
                    }
                    case "5": {
                        String imgBase64 = json.get("ImgBase64").toString();
                        bean.setBankRand(imgBase64);
                        bean.setBusiErrCode(5);
                        break;
                    }
                    case "6": {
                        bean.setBusiErrCode(BillConstant.fail);
                        break;
                    }
                    case "m": {
                        bean.setBusiErrCode(BillConstant.fail);
                        break;
                    }
                    default: {
                        bean.setBusiErrCode(BillConstant.fail);
                        break;
                    }
                }
            } else {//请求失败
                String desc = json.get("Desc").toString();
                logger.info("cuserId=="+bean.getCuserId()+"请求失败,描述>>>" + desc);
                bean.setBusiErrDesc(URLEncoder.encode(desc, "utf-8"));
                bean.setBusiErrCode(BillConstant.htmlfail);
            }
            return result;
        } catch (Exception e) {
            loginContextMap.remove(bean.getSourceCode() + "userRand");
            loginContextMap.remove(bean.getSourceCode() + "acctParams");
            logger.info("cuserId=="+bean.getCuserId()+"PluginBankService.httpService 异常", e);
            bean.setBusiErrDesc("系统网络环境有问题,请稍后重试!");
            bean.setBusiErrCode(BillConstant.htmlfail);
            return null;
        }finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
    }

    /**
     * task方法执行
     * @param bean bean对象
     * @param client drpc对象
     * @return 执行结果 0:失败 1:成功
     */
    public int taskReceve(Channel bean, IDrpcClient client,MemCachedClient cc){
        boolean contains = loginContextMap.containsKey(bean.getSourceCode() + "userRand");
        int code;
        if(!contains){//初次调用task接口
            client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("bank", "billTaskConsume", bean));
            code = startTask(bean,cc);
        }else{
            code = submitYzm(bean,cc);
        }
        if (1==bean.getBusiErrCode()){//登陆成功,流程结束
            bean.setCode("1");
        }else if (2==bean.getBusiErrCode()){//发送短信验证码
            bean.setCode("3");
            loginContextMap.put(bean.getSourceCode()+"smsMsg",bean.getPhoneNum());
        }else if (3==bean.getBusiErrCode()){//获取图片验证码
            bean.setCode("2");
        }else if (5==bean.getBusiErrCode()&&bean.getBusiErrDesc().contains("验证码")){//验证码错误,刷新
            bean.setCode("2");
            bean.setBusiErrCode(BillConstant.needimg);
            bean.setBusiErrDesc("图片验证码错误,刷新图片");
        }else {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setCode("0");
        }
        changeCode(bean,client);
        return code;
    }
}