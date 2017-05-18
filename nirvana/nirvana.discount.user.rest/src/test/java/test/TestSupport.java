package test;

import com.caiyi.financial.nirvana.discount.filter.PrivilegeFilter;
import com.caiyi.financial.nirvana.discount.filter.RequestFilter;
import com.caiyi.financial.nirvana.discount.utils.WebUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Created by wenshiliang on 2016/11/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:/springmvc-servlet.xml"})
@Rollback(false) //是否回滚
@ActiveProfiles("localdev")
//@ActiveProfiles("remotetest")//不同配置
public abstract class TestSupport {

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public WebApplicationContext wac;

    public MockMvc mockMvc;
    public String accessToken;
    public String appId;

    @Before
    public void asetup() throws Exception {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(encodingFilter,"/*" )
                .addFilter(new RequestFilter(),"/*")
                .addFilter(new PrivilegeFilter()
                        ,"/control/*"
                        ,"/credit/sendSms.go"
                        ,"/credit/checkSms.go"
                        ,"/credit/getBankBill.go"
                        ,"/credit/sendSms2.go"
                        ,"/credit/checkSms2.go"
                        ,"/credit/bankVerifyCode2.go"
                        ,"/credit/getBankBill2.go"
                        ,"/credit/deleteBill.go"
                        ,"/credit/billInfoByMail.go"
                        ,"/credit/updateBillInfoByMail.go"
                        ,"/credit/mailLogin.go"
                        ,"/credit/qCreditTransaction.go"
                        ,"/credit/qUserCardinfo.go"
                        ,"/credit/billConsumeInfo.go"
                        ,"/credit/zxVerifyCode.go"
                        ,"/credit/zxLogin.go"
                        ,"/credit/zxGetQuestions.go"
                        ,"/credit/zxApplyReport.go"
                        ,"/credit/zxViewReport.go"
                        ,"/credit/zxCheckIdentity.go"
                        ,"/credit/zxCheckRegLoginnameHasUsed.go"
                        ,"/credit/zxGetAcvitaveCode.go"
                        ,"/credit/zxRegistered.go"
                        ,"/credit/queryUserCreditreFerence.go"
                        ,"/credit/zxGoToResetPwd.go"
                        ,"/credit/zxGetResetAcvitaveCode.go"
                        ,"/credit/zxGetResetQuestions.go"
                        ,"/credit/zxApplyResetPwd.go"
                        ,"/credit/zxLoginOut.go"
                        ,"/credit/wechatMsg_action.go"
                        ,"/credit/coll_wechat_Msg_list.go"
                        ,"/credit/checkEmailCode.go"
                        ,"/credit/getEmailCode.go"
                        ,"/credit/getEmailBill.go"
                        ,"/credit/changeRepaymentStatus.go"
                        ,"/credit/countApply.go"
                        ,"/credit/lendApply.go"
                        ,"/credit/lendProgress.go"
                        ,"/credit/lendProgressDetail.go"
                        ,"/credit/lendCity.go"
                        ,"/credit/cancelLendApply.go"
                        ,"/user/chgNickName.go"
                        ,"/user/bankFocus.go"
                        ,"/user/cheapCollect.go"
                        ,"/user/sendForgetPwdYzm.go"
                        ,"/user/modifyUserInfo.go"
                        ,"/user/queryUserAccount.go"
                        ,"/user/cheapCollectAdd.go"
                        ,"/user/cheapCollectDel.go"
                        ,"/user/doPraise.go"
                        ,"/user/dataMerge.go"
                        ,"/user/qCollectList.go"
                        ,"/user/qCollectInfo.go"
                        ,"/user/expiredCollect.go"
                        ,"/user/uploadIcon.go"
                        ,"/user/cooperationUserBind.go"
                        ,"/credit/mergerDecoration.go"
                        ,"/credit/deleteBill.go"
                )
                .build();
        createToken();
    }

    public void createToken() throws Exception {
        String uid = "15200000010";
        String pwd = "123456";
        pwd = DigestUtils.md5Hex(pwd);

        pwd = "3fcdd2102e9071060a3b7a3aae6989f3";
        String merchantacctId = "130313001";//130313001（安卓） 130313002（iso）
        String signType = "1";
        //android：A9FK25RHT487ULMI  ios:A9FK25RHT487ULMI)
        String key = "A9FK25RHT487ULMI";
        String signMsgVal = "";
        signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signType);
        signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctId);
        signMsgVal = WebUtil.appendParam(signMsgVal, "uid", uid);
        signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", pwd);
        signMsgVal = WebUtil.appendParam(signMsgVal, "key", key);
        signMsgVal = DigestUtils.md5Hex(signMsgVal).toUpperCase();

        MvcResult result = mockMvc.perform((post("/user/login.go")
                .param("uid", uid)
                .param("pwd",pwd)
                .param("merchantacctId",merchantacctId)
                .param("signType",signType)
                .param("signMsg",signMsgVal)
        ))
//                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        /*
        如果未能执行到这里，可能的原因：
        1。参数：用户名，密码错误;加密更换了，
        2。代码有修改，登陆执行失败了。debug模式下调试登陆接口
        3。UserBolt未能成功加载（localdev 本地模式下）:查看pom.xml中是否依赖nirvana.discount.user
         */
        String str = result.getResponse().getContentAsString(); //返回值
        Document document = DocumentHelper.parseText(str);
        Element element = document.getRootElement();
        appId = element.attributeValue("appId");
        accessToken = element.attributeValue("accessToken");
        logger.info("----------------------------------\nappId={}\naccessToken={}\n---------------------------\n",appId,accessToken);
    }
}

