package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolBean;
import com.caiyi.financial.nirvana.discount.utils.JsonUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhijie on 2016/8/9.
 */
@RestController
@RequestMapping("/credit")
public class Toolcontroller {
    private  static Logger log= LoggerFactory.getLogger(Toolcontroller.class);
    @Resource(name = Constant.HSK_TOOL)
    IDrpcClient client;

    String result="";
    @RequestMapping("/ll/test.go")
    public String test(HttpServletRequest request, HttpServletResponse response){
        String result=client.execute(new DrpcRequest("tool","test"));
        return result;
    }

    /**
     * 精品推荐接口
     * @param request
     * @param response
     */
    @RequestMapping("/qualitySpread.go")
    public void qualitySpread(HttpServletRequest request, HttpServletResponse response){
        String typeid=request.getParameter("typeid");
        result=client.execute(new DrpcRequest("tool","qualitySpread",typeid));
        JSONObject object=JSONObject.parseObject(result);
        Document dom= DocumentHelper.createDocument();
        Element resp=new DOMElement("Resp");
        dom.setRootElement(resp);
        if(object!=null&&object.size()>0){
            Element app=new DOMElement("App");
            if(object.getJSONArray("data")!=null){
                JSONArray dataList=object.getJSONArray("data");
                JsonUtil.jsonToElement(dataList,app,"AppItem",null);
                resp.add(app);
                resp.addAttribute("code","1");
                resp.addAttribute("desc","查询成功");
            }else {
                resp.addAttribute("code", (String) object.get("code"));
                resp.addAttribute("desc", String.valueOf(object.get("desc")));
            }
        }else {
            resp.addAttribute("code","0");
            resp.addAttribute("desc","程序异常");
        }
        XmlUtils.writeXml(dom,response);
    }
    @SetUserDataRequired
    @RequestMapping("/mergerDecoration.go")
    public  String mergerDecoration(HttpServletRequest request, HttpServletResponse response){
        String cuserId=request.getParameter("cuserId");
        JSONObject json=new JSONObject();
        if (StringUtils.isEmpty(cuserId)) {
            json.put("code","0");
            json.put("desc","没有登录");
            log.info("没有登录");
            return json.toJSONString();
        }
        String data=request.getParameter("data");
        ToolBean toolBean=new ToolBean();
        toolBean.setCuserId(cuserId.toString());
        if(StringUtils.isEmpty(data)) {
            toolBean.setData(data);
        }else {
            json.put("code","0");
            json.put("desc","参数错误");
            log.info("参数错误");
            return json.toJSONString();
        }
        return  result=client.execute(new DrpcRequest("tool","mergerDecoration",toolBean));
    }
    @RequestMapping("/getFinProList.go")
    public String queryFinProducts(HttpServletRequest request, HttpServletResponse response){
        result=client.execute(new DrpcRequest("tool","queryFinProducts"));
        return result;
    }
    @RequestMapping("/editFinPro.go")
    public String updateProduct(HttpServletRequest request, HttpServletResponse response){
        JSONObject dd=new JSONObject();
        String pid=request.getParameter("pid");
        if(StringUtils.isEmpty(pid)){
            dd.put("code","0");
            dd.put("desc","无效参数");
            log.info("无效参数");
            return dd.toJSONString();
        }
        String profitNum=request.getParameter("profitNum");
        String convenienceNum=request.getParameter("convenienceNum");
        String safeNum=request.getParameter("safeNum");
        String inTimeNum=request.getParameter("inTimeNum");
        if(StringUtils.isEmpty(profitNum)&&StringUtils.isEmpty(convenienceNum)&&
                StringUtils.isEmpty(safeNum)&& StringUtils.isEmpty(inTimeNum)){
            dd.put("code","0");
            dd.put("desc","无效参数");
            log.info("无效参数");
            return dd.toJSONString();
        }
        Map<String,String> map=new HashMap<>();
        map.put("ip_id",pid);
        map.put("profitNum",profitNum);
        map.put("convenienceNum",convenienceNum);
        map.put("safeNum",safeNum);
        map.put("inTimeNum",inTimeNum);
//        result=client.execute(new DrpcRequest("tool","updateProduct",map));
        result=client.execute(new DrpcRequest("tool","updateProduct",map));
        log.info("请求结果:"+result);
        return result;
    }
    @RequestMapping("/toolUpgrade.go")
    public void toolUpgrade(ToolBean bean,HttpServletRequest request, HttpServletResponse response){
        String result = client.execute(new DrpcRequest("tool","toolUpgrade",bean));
        XmlUtils.writeXml(result,response);

    }

    @RequestMapping("/calculate.go")
    public  String calculate(HttpServletRequest request, HttpServletResponse response){
        String func=request.getParameter("func");
        JSONObject json=new JSONObject();
        if(StringUtils.isEmpty(func)){
            json.put("code","0");
            json.put("desc","参数不能为空");
            return json.toJSONString();
        }
        return client.execute(new DrpcRequest("tool","calculate",func));
    }

}
