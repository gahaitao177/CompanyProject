package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import com.caiyi.financial.nirvana.discount.user.bean.SpecialPreferentialBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.github.pagehelper.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Created by lizhijie on 2016/11/14.
 */
public class UserServiceTest extends TestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private HskUserService hskUserService;

    @Test
    public void qurey_special_preferential() throws Exception {
        HomePageBean bean = new HomePageBean();
        bean.setPageNum(1);
        bean.setAdcode("310100");
        bean.setPageSize(3);
        bean.setBankids(Arrays.asList(new String[]{"10"}));
        Page<SpecialPreferentialBean> page = userService.qurey_special_preferential(bean);
        System.out.println(page.getTotal());

        System.out.println(page.getPages());
        System.out.println(page.getPageNum());
    }
    @Test
    public  void register(){
        User user=new User();
        user.setYzm("123456");
        user.setYzmType("0");
        user.setUid("18717861758");
        user.setIclient(1);
        user.setSource(6000);
        user.setCimei("111111111111111111");
        user.setUserType(0);
        user.setPwd("123456");
//        user.setActionName("");
        Integer i=hskUserService.saveUserInfo(user);
        System.out.println("插入结果:"+i);
    }
}