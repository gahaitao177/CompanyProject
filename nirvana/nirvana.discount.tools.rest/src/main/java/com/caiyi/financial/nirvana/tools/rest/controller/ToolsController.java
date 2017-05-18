package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolBean;
import com.caiyi.financial.nirvana.tools.util.BillfFenqiUtil;
import com.caiyi.financial.nirvana.tools.util.EstateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * Created by lichuanshun on 16/10/31.
 * 新版本工具类
 */
@RestController
public class ToolsController {
    private static Logger logger = LoggerFactory.getLogger(ToolsController.class);
//    @Resource(name = Constant.HSK_TOOL)
//    private IDrpcClient drpcClient;
    @Resource(name= Constant.HSK_TOOL)
    private IDrpcClient drpcClient;

    /**
     * 房产工具接口
     * @param toolBean
     * @return
     */
    @RequestMapping("/notcontrol/tool/configinfos.go")
    public String gfzgConfInfos(ToolBean toolBean){
        logger.info("estateInfo start:" + toolBean.getDataversion() + "," + toolBean.getToolid() );
        JSONObject result = new JSONObject();
        result.put("code","0");
        result.put("desc","success");
        if (EstateHelper.DATA_VERSION > toolBean.getDataversion()){
            result.put("code","1");
            result.put("data",EstateHelper.ESTATE_DATA);
        }
        return result.toJSONString();
    }

    /**
     * 刷新工具信息接口
     */
    @RequestMapping("/notcontrol/tool/refreshToolConf.go")
    public void refreshToolConf(){
        logger.info("refreshToolConf start:" + EstateHelper.DATA_VERSION);
        EstateHelper.refreshEstateConf();
        logger.info("refreshToolConf end:" +EstateHelper.DATA_VERSION);
    }

    /**
     * 获取banner列表
     * @param adcode 高德地图城市码
     * @return
     */
    @RequestMapping("/notcontrol/tool/listBanner.go")
    public BoltResult listBanner(String adcode){
        BoltResult boltResult = new BoltResult();
        if(StringUtils.isEmpty(adcode)){
           adcode="all";
        }
        String banners = drpcClient.execute(new DrpcRequest("tool", "selectHomePage",adcode));
        JSONObject result = JSONObject.parseObject(banners);
        if("1".equals(result.get("code"))){
            boltResult.setCode("1");
            boltResult.setData(result.get("data"));
            boltResult.setDesc("success");
        }else{
            boltResult.setCode("0");
            boltResult.setDesc("failed");
            boltResult.setData("查询失败");
        }
        return boltResult;
    }

    /**
     * 根据channelid 获取icon 包名 下载地址
     * @param source 渠道ID
     * @return
     */
    @RequestMapping("/notcontrol/common/iosAppInfo.go")
    public BoltResult packageIcon(String source){
        BoltResult boltResult = new BoltResult();
        if(CheckUtil.isNullString(source)){
            boltResult.setCode("0");
            boltResult.setDesc("非法参数");
            return boltResult;
        }
        boltResult = drpcClient.execute(new DrpcRequest("advertisement", "queryAppInfo",source),BoltResult.class);
        return boltResult;
    }

    /**
     *  账单分期费率下发
     * @param toolBean 渠道ID
     * @return
     */
    @RequestMapping("/notcontrol/tool/billInstallmentConf.go")
    public BoltResult billInstallmentConf(ToolBean toolBean){
        logger.info("billInstallmentConf start:" + toolBean.getDataversion() + "," + toolBean.getToolid() );
        BoltResult boltResult = new BoltResult();
        boltResult.setCode("0");
        boltResult.setDesc("success");
        if (BillfFenqiUtil.DATA_VERSION > toolBean.getDataversion()){
            boltResult.setCode("1");
            boltResult.setDesc("success");
            boltResult.setData(BillfFenqiUtil.BILLFENQI_DATA);
        }
        return boltResult;
    }

    /**
     * 刷新工具信息接口
     */
    @RequestMapping("/notcontrol/tool/refreshBillInstallmentConf.go")
    public BoltResult refreshBillInstallmentConf(){
        logger.info("refreshBillInstallmentConf start:" + BillfFenqiUtil.DATA_VERSION);
        BillfFenqiUtil.refreshBillFenqiConf();
        logger.info("refreshBillInstallmentConf end:" +BillfFenqiUtil.DATA_VERSION);
        BoltResult boltResult = new BoltResult();
        boltResult.setCode("1");
        boltResult.setDesc("success");
        boltResult.setData(BillfFenqiUtil.BILLFENQI_DATA);
        return boltResult;
    }

}
