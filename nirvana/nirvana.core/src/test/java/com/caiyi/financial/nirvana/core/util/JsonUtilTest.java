package com.caiyi.financial.nirvana.core.util;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import org.junit.Test;

/**
 * Created by wenshiliang on 2016/5/5.
 */
public class JsonUtilTest {

    @Test
    public void toJSONStringTest(){
        System.out.println(JsonUtil.toJSONString(null));
        System.out.println(JSONObject.parseObject(JsonUtil.toJSONString(new BaseBean()),BaseBean.class));
        System.out.println(JsonUtil.toJSONString(new DrpcRequest<>()));


//        JSONObject.parseArray()

    }


}