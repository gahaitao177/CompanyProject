package test;

import com.caiyi.financial.nirvana.discount.filter.PrivilegeFilter;
import com.caiyi.financial.nirvana.discount.filter.RequestFilter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
    @Autowired
    public WebApplicationContext wac;

    public MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
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

    }
}

