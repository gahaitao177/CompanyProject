package com.caiyi.financial.nirvana.discount.tools.bolts;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolBean;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolVersionBean;
import com.caiyi.financial.nirvana.discount.tools.dto.HomeMarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.TbHomePageDto;
import com.caiyi.financial.nirvana.discount.tools.service.FinancialService;
import com.caiyi.financial.nirvana.discount.tools.service.ToolService;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/8/9.
 */
@Bolt(boltId = "tool", parallelismHint = 1, numTasks = 1)
public class ToolBolt extends BaseBolt {

    // 房贷工具发现页面banner
    private static final String H5_FD_TOOL_BANNER ="H5_FD_TOOL_BANNER";

    @Autowired
    ToolService toolService;
    @Autowired
    FinancialService financialService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context)
    {
    }
    @BoltController
    public  Map<String,Object> test(){
        logger.info("-------ToolBolt---------test");
        return  toolService.test();
    }
    @BoltController
    public Map<String,Object> qualitySpread(String typeid){
        logger.info("-------ToolBolt---------qualitySpread---");
        return  toolService.qualitySpread(typeid);
    }
    @BoltController
    public Map<String,String> mergerDecoration(ToolBean bean){
        logger.info("-------ToolBolt-------mergerDecoration---");
        return  toolService.mergerDecoration(bean);
    }
    @BoltController
    public Map<String,String> queryFinProducts(){
        logger.info("-------ToolBolt-------queryFinProducts---");
        return financialService.queryFinProducts();
    }
    @BoltController
    public JSONObject updateProduct(Map<String,String> map){
        logger.info("-------ToolBolt-------updateProduct---");
        return financialService.updateProduct(map);
    }
    @BoltController
    public  ToolBean toolUpgrade(ToolBean bean) {
        String toolid = bean.getToolid();
        String type = bean.getTypeid();
        String version = bean.getBversion();
        String source = bean.getCsource();
        if (StringUtils.isEmpty(toolid) || StringUtils.isEmpty(type) || StringUtils.isEmpty(version) || StringUtils.isEmpty(source)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("参数错误");
            return bean;
        }
        try {
            int itype = Integer.parseInt(type);
            if (itype == 1 || itype == 0) {
                ToolVersionBean verBean = new ToolVersionBean();
                verBean.setCsource(source);
                verBean.setItype(itype);
                verBean.setCtoolid(toolid);
                verBean.setCversion(version);
                List<ToolVersionBean> verBeanList = toolService.query_tool_version(verBean);
                StringBuilder xml = new StringBuilder();
                xml.append("<app ");
                if (verBeanList != null && verBeanList.size() > 0) {
                    xml.append(XmlTool.createAttrXml("isup", "1"));

                    xml.append(XmlTool.createAttrXml("content", verBeanList.get(0).getCcontent()));
                    xml.append(XmlTool.createAttrXml("url", verBeanList.get(0).getCdownloadurl()));

                } else {
                    xml.append(XmlTool.createAttrXml("isup", "0"));
                }
                xml.append("/>");
                bean.setBusiXml(xml.toString());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("参数错误");
        }

        return bean;
    }
    @BoltController
    public JSONObject calculate(String func){
        logger.info("-------ToolBolt-------calculate---");
        return financialService.calculate(func);
    }

    @BoltController
    public BoltResult selectHomePage(String adcode){
        logger.info("----ToolBolt-----selectHomepage----");
        BoltResult boltResult = new BoltResult();
        if(StringUtils.isEmpty(adcode)){
            adcode = "all";
        }
        List<TbHomePageDto> list = toolService.selectHomePage(adcode,H5_FD_TOOL_BANNER);
        if (list == null) {
            boltResult.setCode("0");
            boltResult.setData("查询失败");
            boltResult.setDesc("查询失败");
            return boltResult;
        }
        boltResult.setData(list);
        boltResult.setCode("1");
        boltResult.setDesc("success");
        return boltResult;
    }

}
