package com.caiyi.financial.nirvana.ccard.material.materialCardImp;

import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.GuangDaException;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.query.GuangDaQueryUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.GuangFaSubmit;
import com.caiyi.financial.nirvana.ccard.material.banks.jiaotong.JiaoTongHelper;
import com.caiyi.financial.nirvana.ccard.material.banks.minsheng.MinshengQuery;
import com.caiyi.financial.nirvana.ccard.material.banks.pingan.PingAnProgUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.xingye.XingYeQueryUtils;
import com.caiyi.financial.nirvana.ccard.material.banks.zhongXin.ZhongXinUtil;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.danga.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * Created by lizhijie on 2016/7/29.
 * 办卡进度查询
 */
@MVCComponent
public class ProgressImp {

    private static  Logger logger= LoggerFactory.getLogger(ProgressImp.class);
    @Autowired
    MemCachedClient client;


    /**
     * 获取图片验证码
     * @param bean
     * @param request
     * @param response
     * @throws Exception
     */
    public int apply_credit_img_yzm(MaterialBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ibankid = bean.getIbankid();
        String iapplyid = bean.getOrderid();
        String cname = bean.getCname();
        System.out.println("申请真实姓名  ： "+cname);
        if (CheckUtil.isNullString(iapplyid)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("非法请求");
            return 0;
        }
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        ServletOutputStream localServletOutputStream = response.getOutputStream();
        BufferedImage localBufferedImage = null;
        bean.setBusiJSON("success");
        try {
            switch (ibankid) {
                case "16"://交通银行
                    localBufferedImage = JiaoTongHelper.getJiaoTongVcode(bean,client);
                    break;
                case "3"://光大
                    localBufferedImage = GuangDaQueryUtil.guangDaStatusImgCode(bean,client);
                    break;
                case "7"://平安
                    localBufferedImage = PingAnProgUtil.getProgQueryImg(bean,client);
                    if(localBufferedImage==null){
                        return 0;
                    }
                    break;
            }
            if (localBufferedImage != null) {
                ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                localServletOutputStream.flush();
                localServletOutputStream.close();
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效银行ID");
                return 0;
            }
        } catch (Exception e) {
            logger.error(bean.getOrderid()+" apply_credit_log_yzm 异常", e);
        }
        return 1;
    }

    /**
     * 查询申卡，获得手机验证码
     * @param bean
     * @param request
     * @param response
     * @throws Exception
     */
    public int apply_credit_phone_yzm(MaterialBean bean,HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ibankid = bean.getIbankid();
        String iapplyid = bean.getOrderid();

        if (CheckUtil.isNullString(iapplyid)) {
            bean.setBusiJSON("fail");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("非法请求");
            return 0;
        }
        bean.setBusiJSON("success");
        try {
            switch (ibankid){
                case "2":
                    ZhongXinUtil zhongXinUtil = new ZhongXinUtil();
                    zhongXinUtil.smsCodeForQueryApply(bean,client);
                    break;
                case "3":
                    //光大
                    GuangDaQueryUtil.guangDaStatusPhoneCode(bean,client);
                    break;
                case "1":
                    //广发
                    GuangFaSubmit.smsCodeForQueryApply(bean,client);
                    break;
                case "7":
                    PingAnProgUtil.smsCodeForQueryApply(bean,client);
                    break;
                default:
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("不存在这个银行");
            }
        }catch (GuangDaException e){
            bean.setBusiJSON("fail");
            bean.setBusiErrCode(e.getCode());
            bean.setBusiErrDesc(e.getMessage());
            logger.error(bean.getOrderid() + " apply_credit_phone_yzm 异常", e);
        }catch (Exception e){
            bean.setBusiJSON("fail");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("出错了");
            logger.error(bean.getOrderid() + " apply_credit_phone_yzm 异常", e);
        }
        return 0;
    }

    /**
     * 信用卡协议接口
     * @param bean
     * @param request
     * @param response
     * @return
     */
    public int getBankAgreement(MaterialBean bean, HttpServletRequest request, HttpServletResponse response) {
        if ("16".equals(bean.getIbankid())) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"交通银行太平洋个人贷记卡章程\",\"agreementURL\":\"https://creditcardapp.bankcomm.com/applynew/assets/pc/dialog/PCProtocol.html\"}]");
            return 1;
        }else if ("1".equals(bean.getIbankid())){//广发银行
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"广发银行信用卡客户协议\",\"agreementURL\":\"http://www.huishuaka.com/5/gfyhxykkhxy.html\"}]");
            return 1;
        }else if("2".equals(bean.getIbankid())){//中信银行,zhaojie
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"中信信用卡(个人卡)领用合约(新版)\",\"agreementURL\":\"http://creditcard.ecitic.com/heyue/new_wap/lingyong.html\"}]");
            return 1;
        }else if("3".equals(bean.getIbankid())){
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"中国光大银行信用卡申请须知\",\"agreementURL\":\"https://xyk.cebbank.com/home/portal/notes.html\"}," +
                    "{\"agreementName\":\"中国光大银行信用卡领用合约\",\"agreementURL\":\"https://xyk.cebbank.com/home/portal/contract.html\"},"+
                    "{\"agreementName\":\"中国光大银行信用卡章程\",\"agreementURL\":\"https://xyk.cebbank.com/home/portal/rules.html\"},"+
                    "{\"agreementName\":\"芝麻信用服务协议及相关授权\",\"agreementURL\":\"https://zmcustprod.zmxy.com.cn/auth/protocol.htm?type=03&merchantId=268820000065710935745\"}"+
                    "]");
        }else if("7".equals(bean.getIbankid())){//平安银行
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"平安银行信用卡申领合约\",\"agreementURL\":\"http://www.huishuaka.com/5/payhbkxy.html\"}]");
            return 1;
        }else if("11".equals(bean.getIbankid())){// by denghong  民生银行
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"中国民生银行信用卡(个人卡)领用合约\",\"agreementURL\":\"http://www.huishuaka.com/5/msyhbkxy.html\"}," +
                    "{\"agreementName\":\"民生银行办卡协议\",\"agreementURL\":\"http://www.huishuaka.com/5/msyhbkxz.html\"}"+
                    "]");
            return 1;
        }else if("10".equals(bean.getIbankid())){
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
//             bean.setBusiJSON("[{\"agreementName\":\"兴业银行信用卡（个人卡）领用合约\",\"agreementURL\":\"http://creditcard.cib.com.cn/card/apply/other/drawrule_1.html\"}]");
            bean.setBusiJSON("[{\"agreementName\":\"兴业银行信用卡（个人卡）领用合约\",\"agreementURL\":\"http://www.huishuaka.com/5/xythbkxy.html\"}]");
            return 1;
        } else if ("6".equals(bean.getIbankid())){ // 渣打
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"渣打银行隐私政策和申明\",\"agreementURL\":\"http://www.huishuaka.com/5/scbank.html\"}]");
            return 1;
        }else if ("5".equals(bean.getIbankid())){ // 花旗
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取成功");
            bean.setBusiJSON("[{\"agreementName\":\"花旗银行申请人条件\",\"agreementURL\":\"http://www.huishuaka.com/5/citybankrequirement.html\"},{\"agreementName\":\"花旗银行办卡服务协议\",\"agreementURL\":\"http://www.huishuaka.com/5/citybankagreement.html\"}]");
            return 1;
        }
        else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该银行暂不支持");
        }
        return 0;
    }
    /**
     * 查询申卡进度
     * @param bean
     * @param request
     * @param response
     */
    public int apply_credit_query(MaterialBean bean, HttpServletRequest request, HttpServletResponse response)  {
        String iapplyid = bean.getOrderid();
        String imgauthcode = bean.getImgauthcode();// imgauthcode:图片验证码 不需要则传空
        String phoneauthcode = bean.getPhoneauthcode();// phoneauthcode:手机验证码 不需要则传空
        String ibankid = bean.getIbankid();
        bean.setBusiJSON("success");
        if (CheckUtil.isNullString(iapplyid)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("用户资料ID不能为空");
            bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
            return 0;
        }

        try{
            switch (ibankid){
                case "16":
                    return JiaoTongHelper.jiaoTongJDCX(bean,client);
                case "3"://光大
                    return GuangDaQueryUtil.guangDaStatusQuery(bean,client);
                case "1":
                    //广发
                    return GuangFaSubmit.queryApply(bean,client).getCode();
                case "2":
                    ZhongXinUtil zhongXinUtil = new ZhongXinUtil();
                    return zhongXinUtil.queryCardAppInfo(bean,client).getCode();
                case "11":
                    return MinshengQuery.minshengJDCX(bean);
                case "10"://兴业
                    return XingYeQueryUtils.applyBankCreditCard(bean);
                case "7":
                    return PingAnProgUtil.pingAnProgressQuery(bean,client);
                default:
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("该银行暂不支持");
                    bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
                    return 0;
            }
        }catch (GuangDaException e){
            bean.setBusiErrCode(e.getCode());
            bean.setBusiErrDesc(e.getMessage());
            bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
            logger.error(bean.getOrderid() + " apply_credit_query 异常", e);
            logger.error(e.toString());
            return 0;
        }catch (Exception e){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("出错了");
            bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
            logger.error(bean.getOrderid() + " apply_credit_query 异常", e);
            logger.error(e.toString());
            return 0;
        }
    }
}
