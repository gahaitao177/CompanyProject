package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelCardBean;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/6/20.
 */
@RestController
public class H5ChannelController {
    private static Logger logger = LoggerFactory.getLogger(H5ChannelController.class);

    @Resource(name = Constant.HSK_CCARD_INFO)
    private IDrpcClient drpcClient;


    /**
     * h5版本惠刷卡首页接口
     * 必要参数:hskcityid 例如:101
     * @param card
     * @return
     *
     * add by lcs 2017-02-10
     */
    @RequestMapping("/notcontrol/h5channel/h5index.go")
    public BoltResult h5HskIndex(Card card){
        // 返回结果初始化
        BoltResult result = new BoltResult(BoltResult.SUCCESS,"success");
        JSONObject data = new JSONObject();

        // 查询用户所在城市支持的银行  必须参数:hskcityid
        logger.info("hskcityid:" +  card.getHskcityid());
        JSONObject cardResult = drpcClient.execute(new DrpcRequest("cardYouyu", "cardApplyIndex", card), JSONObject.class);
        logger.info("cardResult:" +  cardResult.toJSONString());

        // 将银行列表放入data中
        if (cardResult != null && cardResult.getJSONObject("data") != null ){
            JSONObject tempData = cardResult.getJSONObject("data");
            if (tempData.getJSONArray("bankList") != null){
                data.put("bankList",tempData.getJSONArray("bankList") );
            }
        }

        // 信用卡特惠 和 单张人气卡片
        JSONObject newConfJson = drpcClient.execute(new DrpcRequest("cardYouyu", "newHomeIndex",card),JSONObject.class);
        if (newConfJson != null){
            if (newConfJson.get("welfare") != null){
                data.put("welfare",newConfJson.getJSONArray("welfare").get(0));
            }
            if (newConfJson.get("topCard") != null){
                data.put("hotCard",newConfJson.getJSONObject("topCard"));
            }
        }
        result.setData(data);
        return result;
    }


    /**
     * h5渠道推广首页接口
     * @param ichannelid
     * @return
     */
    @RequestMapping("/notcontrol/h5channel/index.go")
    public String index(Long ichannelid){

        Map<String,Object> map = new HashMap<>();
        map.put("ichannelid",ichannelid);
        String result = drpcClient.execute(new DrpcRequest("h5ChannelBolt","index",map));

        return result;
    }

    /**
     * h5渠道推广银行服务，进度查询 列表接口
     * @param ichannelid
     * @return
     */
    @RequestMapping("/notcontrol/h5channel/selectProgressUrl.go")
    public String selectProgressUrl(Long ichannelid){
        Map<String,Object> map = new HashMap<>();
        map.put("ichannelid",ichannelid);
        String result = drpcClient.execute(new DrpcRequest("h5ChannelBolt","selectProgressUrl",map));
        return result;
    }


    /**
     * h5渠道推广 信用卡列表查询接口
     * @param bean
     * @return
     */
    @RequestMapping("/notcontrol/h5channel/selectCard.go")
    public String selectCard(H5ChannelCardBean bean){
        String result = drpcClient.execute(new DrpcRequest("h5ChannelBolt","selectCard",bean));
        return result;
    }

    /**
     * h5渠道推广 信用卡点击量+1接口
     * @param bean
     * @return
     */
    @RequestMapping("/notcontrol/h5channel/clickCard.go")
    public String clickCard(H5ChannelCardBean bean){
        String result = drpcClient.execute(new DrpcRequest("h5ChannelBolt","clickCard",bean));
        return result;
    }

    /**
     * h5渠道推广 信用卡点击量+1接口
     * @param bean
     * @return
     */
    @RequestMapping("/notcontrol/h5channel/clickBank.go")
    public String clickBank(H5ChannelCardBean bean){
        String result = drpcClient.execute(new DrpcRequest("h5ChannelBolt","clickBank",bean));
        return result;
    }

}
