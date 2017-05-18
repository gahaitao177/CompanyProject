package com.caiyi.financial.nirvana.discount.rest.controller;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by been on 16/4/21.
 */
@RestController
public class RestDemoController {

    @RequestMapping("rest")
    public Demo rest(){
        return new Demo("been", 26);
    }

    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;


    @RequestMapping("/rest/add")
    public Object add(Demo  demo){
        Object obj = client.execute( new DrpcRequest("demo","add2",demo));
        return obj;
    }
    @RequestMapping("/rest/select1")
    public Object select1(Demo  demo){
        Object obj = client.execute( new DrpcRequest("demo","select1",""));
        return obj;
    }
    @RequestMapping("/rest/select2")
    public Object select2(Demo  demo,String id){
        Object obj = client.execute( new DrpcRequest("demo","select2",""));
        return obj;
    }
    @RequestMapping("/rest/select3")
    public Object select3(Demo  demo){
        Object obj = client.execute( new DrpcRequest("demo","select3",""));
        return obj;
    }

    @RequestMapping(value = "/rest/stringTest")
    public String stringTest(){
        return "aaaaaaa中文";
    }

    @RequestMapping(value = "/rest/stringTest2")
    public Object stringTest2(){
        return "aaaaaaa中文";
    }


    @RequestMapping(value = "/rest/stringTest3")
    public Object stringTest3(){

        if(true){
        }
        return "aaaaaaa中文";
    }
    @RequestMapping(value = "/rest/stringTest4")
    public Object stringTest4(){

        if(true){
            throw new RuntimeException("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");
        }
        return "aaaaaaa中文";
    }


//    @RequestMapping(value = "/rest/stringTest3")
//    public Object stringTest2(){
//        return new Demo("been", 26);
//    }
}
