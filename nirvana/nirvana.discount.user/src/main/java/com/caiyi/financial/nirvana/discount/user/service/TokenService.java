package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.discount.user.mapper.TokenMapper;
import com.caiyi.financial.nirvana.discount.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wenshiliang on 2016/11/18.
 */
@Service
public class TokenService extends AbstractService{

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存token
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveToken(User bean){
        TokenDto dto = new TokenDto();
        dto.setAccessToken(bean.getAccessToken());
//        dto.setUserType(bean.getUserType());??
        dto.setCuserId(bean.getCuserId());
        dto.setCpassword(bean.getPwd());
        dto.setAppid(bean.getAppId());
        dto.setParamJson(bean.getParamJson());
        dto.setCpwd9188(bean.getPwd9188());
        dto.setIloginfrom(bean.getItype());
        return saveToken(dto);
    }

    /**
     * 保存token信息
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveToken(TokenDto dto){
        if(dto.getExpiresin()==null || dto.getExpiresin()<0 ){
            int expiresIn = 60 * 60 * 24 * 14;//二周
            dto.setExpiresin(expiresIn);
        }
        if(dto.getIloginfrom()==null){
            dto.setIloginfrom(0);
        }
        //删除同一个cuserid下,同一个登陆类型的其他token
        tokenMapper.deleteToken(dto.getCuserId(),dto.getIloginfrom());
        return tokenMapper.saveToken(dto);
    }

    /**
     * 查询token信息,根据appid和accessToken查询
     * @param user
     * @return
     * @throws ParseException
     * @throws UserException
     */
    @Transactional(rollbackFor = Exception.class)
    public TokenDto queryToken(User user) throws ParseException {
        //查询token

        TokenDto token = tokenMapper.query_token(user.getAccessToken(),user.getAppId());
        //判断过期和失效,token状态 0 正常  1 注销
        if(token==null){
            throw new UserException("9001","token已注销");
        }
        if(token.getIstate()==1){
            throw new UserException("9001","token已注销");
        }
        token.setPwd(token.getCpassword());
        String lastTime = token.getLastTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(lastTime);
        long lastSeconds = date.getTime()/1000;
        long sysSeconds = System.currentTimeMillis()/1000;
        if((sysSeconds-lastSeconds)>token.getExpiresin()){
            logger.info("验证失败,token已过期：" + token.getAppid());
            throw new UserException("9007","验证不通过");
        }
        //todo :判断是否更改密码(应该在修改密码后将token过期)，整理修改密码接口时候再微调
        if(token.getIloginfrom()==0){//惠刷卡用户 登录来源0惠刷卡1公积金2记账
            UserDto userDto =  userMapper.query_user_byId2(token.getCuserId());
            if (userDto != null) {
                if (!token.getCpassword().equals(userDto.getPwd()) && !token.getCpwd9188().equals(userDto.getPwd9188())) {
                    logger.info("密码已修改,请重新登录：" +  userDto.getCuserId());
                    throw new UserException("9003","密码已修改");
                }
                if (!"0".equals(userDto.getIstate())) {
                    logger.info("账户已禁用：" + userDto.getCuserId());
                    throw new UserException("9004","账户已禁用");
                }
            }
        }
        //更新最后登陆时间
        tokenMapper.updateTokenLastTime(user.getAccessToken(),user.getAppId());
        return token;
    }

    /**
     * 失效token信息
     * @param cause  失效原因
     * @param accessToken
     * @param appId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int logoutToken(String cause,String accessToken,String appId){
        return tokenMapper.logoutToken(cause,accessToken,appId);
    }


}
