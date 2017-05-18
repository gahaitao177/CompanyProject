package com.caiyi.financial.nirvana.ccard.investigation.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created By zhaojie 2016/9/8 11:24:26
 */
public class Channel extends BaseBean {
    private String taskId;
    private static final long serialVersionUID = 5277102587330585478L;
    private String bankId = "";
    private String bankSessionId = "";
    private String bankRand = "";//图片验证码
    private String optRand = "";//短信验证码
    private String idCardNo = "";
    private String cardNo = "";
    private String bankPwd = "";
    private String taskid = "";
    private String type = "";
    private String creditId = "";
    private List<Channel> beanList = null;
    private String client = "0";
    private String iskeep = "0";
    // 0是iOS，1是Android
    private String clientType = "";
    // creditId

    // 导入类型0银行卡1邮件3身份证
    private String importType = "0";
    // 卡号或身份证号或邮件地址账号
    private String code = "";
    //身份证号后六位 登录用
    private String idCard6Num = "";
    // 密码
    private String cardPwd = "";
    // 密码密钥串
    private String secretKey = "";
    // 银行卡号或邮箱地址密钥串
    private String creditIdKey = "";
    // TB_BANK_BILL
    // 账单ID
    private String billId = "";
    // 还款用，用户指定的还款金额
    private String repayAmount = "";
    // 卡号后四位
    private String card4Num = "";
    // 显示名称
    private String displayName = "";
    // 开始周期日期 (YYYY-MM-DD)
    private String startPeriodDate = "";
    // 结束周期日期
    private String endPeriodDate = "";
    // 应还额度
    private String shouldRepayment = "";
    // 最低还款额度
    private String lowstRepayment = "";
    // 未出账单额度
    private String noBillAmount = "";
    // 信用卡总额度
    private String totalQuota = "";
    // 信用卡可用额度
    private String availableQuota = "";
    // 信用卡提现额度
    private String cashAmount = "";
    // 是否已出账单0未出1已出
    private String isBill = "";
    // 月份
    private String month = "";
    // 账单日
    private String billDate = "";
    // 还款日
    private String repaymentDate = "";
    // 外部账户ID
    private String outsideId = "";
    // 来源 网银或邮件0网银1邮箱
    private String webOrMail = "";

    private String monthId = "";
    // TB_BILL_DETAIL
    // 出入账0出1入
    private String detailType = "";
    // 出入账金额
    private String money = "";
    // 出入账时间
    private String occurDate = "";
    // 出入账描述
    private String desc = "";
    // 币种0人民币1美元2日元
    private String currency = "";
    // 消费类型
    private String costType = "";

    // 是否首次请求
    private String first = "0";
    // 读取状态标识
    private String readStatus = "";
    // 读取说明
    private String readDesc = "";
    //读取是否结束标志
    private String readIsEnd = "";

    // mail address
    private String mailAddress = "";
    // mai pwd
    private String mailPwd = "";
    // 邮箱类型 (谷歌：0  腾讯：1  网易163:2  网易126:3  新浪：4  搜狐：5  微软：6  189电子邮箱：7  139电子邮箱：8)
    private String mailType = "";
    // 是否同意自动读取
    private String autoRead = "";
    // add by lcs 20150722 end
    private String indePwd = "";

    private String username = "";

    private String password = "";

    private String loginname = "";

    private String confirmpassword = "";

    private String mobileTel = "";

    private String tcId = "";

    private String options = "";

    private String reportHtml1 = "";

    private String reportHtml2 = "";

    private String repaymentStatus = "0";

    private List<Integer> taskList;
    private String cityId;
    private String clng;
    private String clat;
    private String method;
    private String phoneCode;
    private String sourceCode;//渠道
    private boolean isBeta;// 是否测试
    private String appVersion="";//app应用版本
    private String keyword;// 关键字 add by lcs 20160902

    //Added By zhaojie 2016/9/6 15:07:10
    //征信公共平台调用方法参数，约定安全校验参数
    private String sign;
    private String from;
    private String isOld="0";
    private String isSignBase64="0";
    private Date lastLoginDate;
    private Date applyDate;

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getIsSignBase64() {
        return isSignBase64;
    }

    public void setIsSignBase64(String isSignBase64) {
        this.isSignBase64 = isSignBase64;
    }

    public String getIsOld() {
        return isOld;
    }

    public void setIsOld(String isOld) {
        this.isOld = isOld;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getKeyword() { return keyword; }

    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public boolean getIsBeta() {
        return isBeta;
    }

    public void setIsBeta(boolean beta) {
        isBeta = beta;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getReportHtml1() {
        return reportHtml1;
    }

    public void setReportHtml1(String reportHtml1) {
        this.reportHtml1 = reportHtml1;
    }

    public String getReportHtml2() {
        return reportHtml2;
    }

    public void setReportHtml2(String reportHtml2) {
        this.reportHtml2 = reportHtml2;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public String getMobileTel() {
        return mobileTel;
    }

    public void setMobileTel(String mobileTel) {
        this.mobileTel = mobileTel;
    }

    public String getTcId() {
        return tcId;
    }

    public void setTcId(String tcId) {
        this.tcId = tcId;
    }

    public String getMailType() {
        return mailType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public String getAutoRead() {
        return autoRead;
    }

    public void setAutoRead(String autoRead) {
        this.autoRead = autoRead;
    }

    //流水表字段保存 add by lzj 20150806
    private String tradeDate = "";

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    private Date update = null;
    //add by lzj 20150730


    public List<Channel> getBeanList() {
        if (beanList == null) {
            beanList = new ArrayList<Channel>();
        }
        return beanList;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setBeanList(List<Channel> beanList) {
        this.beanList = beanList;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date date) {
        this.update = date;
    }

    public String getIskeep() {
        return iskeep;
    }

    public void setIskeep(String iskeep) {
        this.iskeep = iskeep;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getIdCard6Num() {
        return idCard6Num;
    }

    public void setIdCard6Num(String idCard6Num) {
        this.idCard6Num = idCard6Num;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankPwd() {
        return bankPwd;
    }

    public void setBankPwd(String bankPwd) {
        this.bankPwd = bankPwd;
    }

    public String getBankRand() {
        return bankRand;
    }

    public void setBankRand(String bankRand) {
        this.bankRand = bankRand;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankSessionId() {
        return bankSessionId;
    }

    public void setBankSessionId(String bankSessionId) {
        this.bankSessionId = bankSessionId;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCreditIdKey() {
        return creditIdKey;
    }

    public void setCreditIdKey(String creditIdKey) {
        this.creditIdKey = creditIdKey;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getStartPeriodDate() {
        return startPeriodDate;
    }

    public void setStartPeriodDate(String startPeriodDate) {
        this.startPeriodDate = startPeriodDate;
    }

    public String getEndPeriodDate() {
        return endPeriodDate;
    }

    public void setEndPeriodDate(String endPeriodDate) {
        this.endPeriodDate = endPeriodDate;
    }

    public String getShouldRepayment() {
        return shouldRepayment;
    }

    public void setShouldRepayment(String shouldRepayment) {
        this.shouldRepayment = shouldRepayment;
    }

    public String getLowstRepayment() {
        return lowstRepayment;
    }

    public void setLowstRepayment(String lowstRepayment) {
        this.lowstRepayment = lowstRepayment;
    }

    public String getNoBillAmount() {
        return noBillAmount;
    }

    public void setNoBillAmount(String noBillAmount) {
        this.noBillAmount = noBillAmount;
    }

    public String getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(String totalQuota) {
        this.totalQuota = totalQuota;
    }

    public String getAvailableQuota() {
        return availableQuota;
    }

    public void setAvailableQuota(String availableQuota) {
        this.availableQuota = availableQuota;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getIsBill() {
        return isBill;
    }

    public void setIsBill(String isBill) {
        this.isBill = isBill;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(String repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public String getOutsideId() {
        return outsideId;
    }

    public void setOutsideId(String outsideId) {
        this.outsideId = outsideId;
    }

    public String getWebOrMail() {
        return webOrMail;
    }

    public void setWebOrMail(String webOrMail) {
        this.webOrMail = webOrMail;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCard4Num() {
        return card4Num;
    }

    public String getCardPwd() {
        return cardPwd;
    }

    public void setCardPwd(String cardPwd) {
        this.cardPwd = cardPwd;
    }

    public void setCard4Num(String card4Num) {
        this.card4Num = card4Num;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getReadDesc() {
        return readDesc;
    }

    public void setReadDesc(String readDesc) {
        this.readDesc = readDesc;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getMailPwd() {
        return mailPwd;
    }

    public void setMailPwd(String mailPwd) {
        this.mailPwd = mailPwd;
    }

    public String getReadIsEnd() {
        return readIsEnd;
    }

    public void setReadIsEnd(String readIsEnd) {
        this.readIsEnd = readIsEnd;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getMonthId() {
        return monthId;
    }

    public void setMonthId(String monthId) {
        this.monthId = monthId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getIndePwd() {
        return indePwd;
    }

    public void setIndePwd(String indePwd) {
        this.indePwd = indePwd;
    }

    public String getRepaymentStatus() {
        return repaymentStatus;
    }

    public void setRepaymentStatus(String repaymentStatus) {
        this.repaymentStatus = repaymentStatus;
    }

    public List<Integer> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Integer> taskList) {
        this.taskList = taskList;
    }

    public String getOptRand() {
        return optRand;
    }

    public void setOptRand(String optRand) {
        this.optRand = optRand;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }
}
