package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.discount.user.bean.HskUser;
import com.caiyi.financial.nirvana.discount.user.dto.HskUserDto;
import com.caiyi.financial.nirvana.discount.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Created by lizhijie on 2017/2/9.
 */
public class ClientTest {

    public  static  String baseUrl="http://192.168.1.51:10021";
    public static void main(String[] args) throws Exception {
        HskUser user=new HskUser();
        BaseBean baseBean=new BaseBean();
        baseBean.setAccessToken("+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvu+3E5uNgAS6SEilNNhcyGvONH97oMkEsefLD6XkwOgU" +
                "UXGpwXovSIlG5KL36ukPd31+OWQ770ScksV5/5hftuYGUd6YAtMdy6eaztM/spS6EdTYceoRSSpV7ttbWfyztVp62UOGX9Rww==");
        baseBean.setAppId("yuD20170ZHHS220O0WXYG230Q33TL11T1");
        user.setYzm("123456");
//        baseBean.setSource(6000);
//        baseBean.setIpAddr("127.0.0.1");
//        baseBean.setPackagename("hsk.com.test.li");

//        user.setCuserId("dc329ad9aca24ad8b5a1721e61dad034");
//        user.setMobileType(1);
//        user.setCphone("15216611690");
//        user.setYzmType("0");
//        user.setCsource(6000);
//        user.setIpAddr("127.0.0.1");
//        user.setPackagename("hsk.com.test.li");
        System.out.println( MD5Util.compute("123456"+"http://www.9188.com/"));

//        banddingPhone(user);
//        getHskUserByBaseBean(baseBean);
//        getUserId(creteUser(user));
//        getTokenAndAppId(queryToken(user),new HskUserDto());
//        motifyPassword(user);
//        login(user);
//        http://www.huishuaka.com/
//       String sendSmsResult= sendSms(user);
//        System.out.println("sendSmsResult:"+sendSmsResult);
//        try {
//            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
//            System.out.print("请输入一个字符串：");
//            String str = strin.readLine();
//            System.out.println("第一个："+str);
//            user.setYzm(str);
//        }  catch (Exception e) {
//            e.printStackTrace();
//        }
//        resetPassword(user);
//        quickLogin(user);

    }
    public static  void checkIsExists(){
        String checkIsExists="http://192.168.1.51:10021/checkExistCphone";
        HashMap<String,String> params=new HashMap<>();
        params.put("cphone","18717861758");
        String  checkIsExistsResult= HttpClientUtil.callHttpPost_Map(checkIsExists,params);
        System.out.println("checkIsExistsResult:"+checkIsExistsResult);
    }
    public static String quickLogin(HskUser user){
        HashMap<String,String> params=new HashMap<>();
        String quickLoginUrl=baseUrl+"/fastLogin";
        params.put("cphone",user.getCphone());
        params.put("yzm",user.getYzm());
        params.put("yzmType",user.getYzmType());
        params.put("csource",String.valueOf(user.getCsource()));
        params.put("ipAddr",user.getIpAddr());
        params.put("iloginfrom","0");
        params.put("mobileType",String.valueOf(user.getMobileType()));
        params.put("packageName",user.getPackagename());
        String quickLoginResult= HttpClientUtil.callHttpPost_Map(quickLoginUrl,params);
        System.out.println("快速登录结果:"+quickLoginResult);
        return  quickLoginResult;
    }
    public static String sendSms(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String sendSmsUrl=baseUrl+"/sendSms";
        params.put("cphone",bean.getCphone());
        params.put("yzmType",bean.getYzmType());
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("channelType","0");
        String result= HttpClientUtil.callHttpPost_Map(sendSmsUrl,params);
        System.out.println("短信发送结果:"+result);
        return  result;
    }

    public static String register (HskUser bean) throws Exception{
        HashMap<String,String> params=new HashMap<>();
        String sendSmsUrl=baseUrl+"/register";
        params.put("cphone",bean.getCphone());
        params.put("yzm",bean.getYzm());
        params.put("yzmType",bean.getYzmType());
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("ipAddr",bean.getIpAddr());
        params.put("ibelongValue","0");
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("cpassword",bean.getCpassword());
        params.put("packageName",bean.getPackagename());

        String result= HttpClientUtil.callHttpPost_Map(sendSmsUrl,params);
        System.out.println("注册结果:"+result);
        return  result;
    }
    public static String login (HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String loginUrl=baseUrl+"/login";
        params.put("cuserId",bean.getCphone());
        params.put("cpassword",bean.getCpassword());
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("iloginfrom","0");
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("packageName",bean.getPackagename());

        String result= HttpClientUtil.callHttpPost_Map(loginUrl,params);
        System.out.println("登录结果:"+result);
        return  result;
    }
    private static String motifyPassword(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String loginUrl=baseUrl+"/updatePwd";
        params.put("cuserId",bean.getCuserId());
        params.put("cpassword",bean.getCpassword());
        params.put("oldPassword",bean.getOldPassword());
//        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("iloginfrom","0");
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("packageName",bean.getPackagename());
        String result= HttpClientUtil.callHttpPost_Map(loginUrl,params);
        System.out.println(("修改密码结果:"+result));
        return  result;
    }

    /**
     * 重置密码
     * @param bean
     * @return
     */
    private static String resetPassword(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl=baseUrl+"/resetPwd";
        params.put("cphone",bean.getCphone());
        params.put("cpassword",bean.getCpassword());
        params.put("yzm",bean.getYzm());
        params.put("yzmType",String.valueOf(bean.getYzmType()));
        params.put("iloginfrom","0");
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("packageName",bean.getPackagename());
        String result= HttpClientUtil.callHttpPost_Map(resetUrl,params);
        System.out.println(("重置密码结果:"+result));
        return  result;
    }

    /**
     * 重置密码
     * @param bean
     * @return
     */
    private static String creteUser(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl=baseUrl+"/createUser";
        params.put("cpassword",bean.getCpassword());
        params.put("cbelongValue","HSK");
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(bean.getMobileType()));
        params.put("packageName",bean.getPackagename());
        String result= HttpClientUtil.callHttpPost_Map(resetUrl,params);
        System.out.println(("创建用户结果:"+result));
        return  result;
    }
    private  static  String getUserId(String result){
        JSONObject jsonObject= JSON.parseObject(result);
        if(jsonObject!=null&&"1".equals(jsonObject.getString("code"))){
            JSONObject data=jsonObject.getJSONObject("data");
            if(data!=null){
                String cuserId=data.getString("cuserId");
                System.out.println("cuserId:"+cuserId);
                return cuserId;
            }else {
                return  null;
            }
        }else {
            return  null;
        }
    }
    /**
     * 查询token
     * @param bean
     * @return
     */
    public static   String queryToken(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl=baseUrl+"/createToken";
        params.put("cpassword",bean.getCuserId());
        params.put("cbelongValue","HSK");
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("ipAddr",bean.getIpAddr());
//        if(bean.getIclient()==-1){
//            bean.setIclient(0);
//        }
        params.put("mobileType",String.valueOf(0));
        params.put("packageName",bean.getPackagename());
        String result= com.hsk.common.HttpClientUtil.callHttpPost_Map(resetUrl,params);
//        logger.info(("查询token结果:"+result));
        return  result;
    }
    /**
     * 解析 第三方登录的返回结果  获得token
     * @param result
     * @return
     */
    private static void getTokenAndAppId(String result,HskUserDto userDto){
        if(userDto==null){
            userDto=new HskUserDto();
        }
        JSONObject jsonObject= JSON.parseObject(result);
        if(jsonObject!=null){
            if("1".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                userDto.setCode("1");
                userDto.setDesc("微信登录成功");
                if (data != null) {
                    userDto.setAppid(data.getString("appId"));
                    userDto.setAccessToken(data.getString("accessToken"));
//                    logger.info("appId:" + data.getString("appId"));
                } else {
                    userDto.setCode("-1");
                    userDto.setDesc("程序异常");
                }
            }else {
                userDto.setCode(jsonObject.getString("code"));
                if(StringUtils.isEmpty(jsonObject.getString("desc"))){
                    userDto.setDesc("token无效");
                }else {
                    userDto.setDesc(jsonObject.getString("desc"));
                }
            }
        }
    }

    /**
     * 通过appid和token获得userDto
     * @return
     */
    public static void getHskUserByBaseBean(BaseBean baseBean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl=baseUrl+"/queryToken";
        params.put("accessToken",baseBean.getAccessToken());
        params.put("appId",baseBean.getAppId());
        params.put("cloginfrom","HSK");
        params.put("csource", String.valueOf(baseBean.getSource()));
        params.put("ipAddr",baseBean.getIpAddr());
        if(baseBean.getIclient()==-1){
            baseBean.setIclient(0);
        }
        params.put("mobileType",String.valueOf(baseBean.getIclient()));
        params.put("packageName",baseBean.getPackagename());
        String result= com.hsk.common.HttpClientUtil.callHttpPost_Map(resetUrl,params);
        System.out.println("把token转化成userid的结果:"+result);
        JSONObject jsonObject=JSON.parseObject(result);
        if(jsonObject!=null){
            if("1".equals(jsonObject.getString("code"))){
                JSONObject data=jsonObject.getJSONObject("data");
                if(data!=null){
                    baseBean.setCuserId(data.getString("cuserId"));
                    baseBean.setCnickname(data.getString("cnickId"));
                }else {
                    baseBean.setBusiErrDesc("查询token信息出错");
                    baseBean.setBusiErrCode(9006);
                }
            }else {
                baseBean.setBusiErrDesc(jsonObject.getString("desc"));
                baseBean.setBusiErrCode(Integer.parseInt(jsonObject.getString("code")));
            }
        }else {
            baseBean.setBusiErrDesc("查询token信息出错");
            baseBean.setBusiErrCode(9006);
        }
    }
    /**
     * 查询token
     * @param bean
     * @return
     */
    public static   String banddingPhone(HskUser bean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl=baseUrl+"/bindingCphone";
        params.put("cuserId",bean.getCuserId());
        params.put("cphone",bean.getCphone());
        params.put("yzm",bean.getYzm());
        params.put("yzmType","0");
        params.put("iloginfrom","HSK");
        params.put("csource",String.valueOf(bean.getCsource()));
        params.put("ipAddr",bean.getIpAddr());
        params.put("mobileType",String.valueOf(0));
        params.put("packageName",bean.getPackagename());
        String result= com.hsk.common.HttpClientUtil.callHttpPost_Map(resetUrl,params);
        System.out.println(("绑定手机结果:"+result));
        return  result;
    }
}
