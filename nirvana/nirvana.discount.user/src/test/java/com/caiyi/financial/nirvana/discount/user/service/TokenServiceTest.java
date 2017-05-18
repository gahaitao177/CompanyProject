package com.caiyi.financial.nirvana.discount.user.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * Created by wenshiliang on 2016/11/18.
 */
public class TokenServiceTest extends TestSupport {

    @Autowired
    TokenService tokenService;


    @Test
    public void saveToken() throws Exception {

    }

    @Test
    public void quertTokenTest() throws Exception {
        User user = new User();
        user.setAccessToken("+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtlAXi+mE7KZZ9bMq6HIDYIgJJZ/dxefwNveFbcTPWLfGbV4FW389MSyNSwq4fWEenbZ/s5g47UHQcgCSg1TSRqFFQcNksb8GdS2jnadg72gahKaipF+8MVaTMgJViiU1O0+qJ//1NNhw==");
        user.setAppId("lt20I1KDS6G1FQ11MY80Z64DU235J59S5");
        TokenDto dto = tokenService.queryToken(user);
        logger.info(JSONObject.toJSONString(dto));
    }

    @Test
    @Rollback(false)
    public void logoutToken(){
        String token = "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtlAXi+mE7KZZ9bMq6HIDYIgJJZ/dxefwNveFbcTPWLfGbV4FW389MSyNSwq4fWEenbZ/s5g47UHQcgCSg1TSRqFFQcNksb8GdS2jnadg72gahKaipF+8MVaTMgJViiU1O0+qJ//1NNhw==";
        String appid = "lt20I1KDS6G1FQ11MY80Z64DU235J59S5";
        tokenService.logoutToken("测试注销登陆",token,appid);
    }

}