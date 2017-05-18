//package com.caiyi.financial.nirvana.discount.mock;
//
//import com.caiyi.financial.nirvana.discount.exception.UserCenterException;
//import com.caiyi.user.api.UserInterface;
//import com.caiyi.user.domain.base.AlipayAuthInfo;
//import com.caiyi.user.domain.base.UserCard;
//import com.caiyi.user.domain.base.UserCardApply;
//import com.caiyi.user.domain.base.UserClientInfo;
//import com.caiyi.user.domain.request.*;
//import com.caiyi.user.domain.response.*;
//
///**
// * 当用户中心接口网络访问异常时，访问此实现
// * Created by liuweiguo on 2016/8/24.
// */
//public class UserInterfaceMock implements UserInterface {
//    @Override
//    public SimpleRsp randomUsername(UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isUsernameExist(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isMobilenoBind(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp getMobilenoBindAccountNum(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserListRsp getMobilenoBindAccountList(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserInfoRsp getMobilenoBindAccountList(String s, String s1, String s2, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isSimilarUserExist(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isMobilenoRegister(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserRegisterRsp register(UserRegisterReq userRegisterReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserLoginRsp login(String s, String s1, LoginInfoReq loginInfoReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserLoginRsp login(LoginInfoReq loginInfoReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp logout(String s) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp enableMobilenoLogin(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp disableMobilenoLogin(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp changeLoginPassword(String s, String s1, String s2, String s3, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp setTempLoginPass(String s, String s1, String s2, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp bindIdcard(String s, String s1, String s2, String s3, String s4, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserInfoRsp getUserInfo(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserInfoDetailRsp getUserInfoDetail(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserInfoRsp getUserByToken(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp bindBankCard(UserCardReq userCardReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp checkBankCard(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserCardsRsp getAllBankCards(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserCardRsp getBankCardDetail(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserCardRsp getBankCard(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isModifyBankcardEligible(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp applyModifyBankcard(UserCardApplyReq userCardApplyReq, String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp getBankcardModificationHistory(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp modifyBankInfo(String s, UserCard userCard, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public AlipayAuthInfo getAlipaySign(UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public AlipayLoginRsp alipayBindCheck(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public AlipayLoginRsp bindAlipay2CaiyiAccount(AlipayLoginReq alipayLoginReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public AlipayLoginRsp alipayRegister(AlipayLoginReq alipayLoginReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public WechatLoginRsp wechatLogin(WechatLoginReq wechatLoginReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public WechatLoginRsp bindWechat2CaiyiAccount(WechatLoginReq wechatLoginReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public WechatLoginRsp wechatRegister(WechatLoginReq wechatLoginReq, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp isUserBindMobileno(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp bindMobileno(String s, String s1, String s2, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp modifyWechatPwd(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp setAlipayPwd(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserInfoRsp checkUserInfo(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp resetPwd(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp bindUser(String s, String s1, String s2, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp getMobileno(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public PageQueryRsp<UserCardApply> getBankcardModifyRecode(UserCardModifyRecQuery userCardModifyRecQuery) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp checkMobileno(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp sendMsg(String s) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public UserLoginRsp registerAndLogin(String s, String s1, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp checkSmsCheckNum(String s, String s1) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//
//    @Override
//    public SimpleRsp getNickIdByMobileno(String s, UserClientInfo userClientInfo) {
//        throw new UserCenterException("用户中心网络连接异常");
//    }
//}
