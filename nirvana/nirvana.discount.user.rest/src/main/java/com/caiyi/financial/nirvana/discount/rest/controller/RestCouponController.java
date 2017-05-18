package com.caiyi.financial.nirvana.discount.rest.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.user.bean.*;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengh on 2016/8/26.
 * RestUserInfoController 替换
 */
@Deprecated
@RestController
@RequestMapping("/user")
public class RestCouponController {
    private static Logger logger = LoggerFactory.getLogger(RestCouponController.class);
    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;
    @Autowired
    private MemCachedClient cc;



    /**
     * 收藏列表
     **/
    @SetUserDataRequired
    @RequestMapping("/qCollectList.go")
    public void qCollectList(User bean, HttpServletRequest request, HttpServletResponse response) {
        logger.info("调用qCollectList.go，userid:[{}]",bean.getCuserId());
        throw new RuntimeException("过期方法");
    }




    /****
     * 收藏过期
     ****/
    @SetUserDataRequired
    @RequestMapping("/expiredCollect.go")
    public void expiredCollection(User bean, HttpServletRequest request, HttpServletResponse response) {

        logger.info("删除过期收藏: ");
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("删除过期收藏成功");
        try {
            // 门店收藏过期
            List<Integer> collectJrs = query_collect_id(bean);
            if (collectJrs != null && collectJrs.size() > 0) {
                for (int i = 0; i < collectJrs.size(); i++) {
                    String storeId = collectJrs.get(i).toString();
                    bean.setStoreId(storeId);
                    List<StoreCheapBean> cheapJrs = storeCheap(storeId);
                    if (cheapJrs != null && cheapJrs.size() > 0) {
                        int cheapExpireCount = 0;
                        String expire = "0";
                        for (int j = 0; j < 0; j++) {
                            expire = "0";
                            String cheapExpire = cheapJrs.get(j).getIexpire().toString();
                            if ("1".equals(cheapExpire)) {
                                cheapExpireCount++;
                            }
                            if (cheapExpireCount == cheapJrs.size()) {
                                expire = "1";
                            }
                        }
                        if ("1".equals(expire)) {
                            System.out.println("過期ID--" + storeId);
                            int ret = collectionOverdue(bean.getCuserId(), storeId);
                            System.out.println("過期ret--" + ret);
                            logger.info("删除过期门店收藏成功：" + " ret: " + ret);
                            if (ret == 1) {
                                logger.info("删除过期门店收藏成功：" + " storeId: " + storeId);
                            } else {
                                logger.info("删除过期门店收藏失败：" + " storeId: " + storeId);
                            }
                        }
                    }
                }

            }
            // 删除优惠价收藏
            Integer ret = collectExpire(bean);
            System.out.println("删除过期過期ret--" + ret);
            if (ret >= 1) {
                logger.info("删除过期优惠券收藏成功：" + " storeId: " + bean.getCuserId() + " 共" + ret + "条记录");
            } else {
                logger.info("删除过期优惠券收藏失败：" + " storeId: " + bean.getCuserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("删除过期收藏失败");
        }

        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        resp.addAttribute("code", bean.getBusiErrCode().toString());
        resp.addAttribute("desc", bean.getBusiErrDesc());
        XmlUtils.writeXml(dom.asXML(), response);


    }



    /***
     * 关注银行
     **/
    @SetUserDataRequired
    @RequestMapping("/bankFocus.go")
    public void bankFocus(User user, HttpServletRequest request, HttpServletResponse response) {
        String result = client.execute(new DrpcRequest("user", "bankFocus", user));
        ;
        user = JSONObject.parseObject(result, User.class);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<Resp code=\"" + user.getBusiErrCode() + "\" desc=\"" + user.getBusiErrDesc() + "\">");
        sb.append("</Resp>");
        XmlUtils.writeXml(sb.toString(), response);
    }


    /**
     * 关注银行
     ***/
    private List<String> focusBanks(User user) {

        String result = client.execute(new DrpcRequest("CouponBolt", "focusBanks", user));
        logger.info("focusBanks\n" + result);
        String retXml = "";
        int bankCount = 0;
        List<String> banks = JSON.parseArray(result, String.class);
        return banks;
    }

    /**
     * 收藏商店
     ***/
    private List<StoreLogo> focusStore(User user) {
        logger.info("获取关注商店Logo");
        String result = client.execute(new DrpcRequest("CouponBolt", "storelogo", user));
        List<StoreLogo> collectJrs = JSONArray.parseArray(result, StoreLogo.class);
        return collectJrs;
    }

    /****
     * 获取单个商店信息
     ****/
    private List<StoreCheapBean> storeCheap(String storeId) {
        Map<String, String> map = new HashMap<>();
        map.put("storeId", storeId);
        String result = client.execute(new DrpcRequest("CouponBolt", "storeCheap", map));
        List<StoreCheapBean> cheapJrs = JSONArray.parseArray(result, StoreCheapBean.class);
        return cheapJrs;
    }

    /********
     * 获取关注超市
     ************/
    private List<MarketBean> focusMarket(User user) {
        logger.info("获取关注超市");
        String result = client.execute(new DrpcRequest("CouponBolt", "queryMarket", user));
        logger.info("qCollectInfo_ex: queryMarket\n" + result);
        List<MarketBean> marketJrs = JSONArray.parseArray(result, MarketBean.class);
        return marketJrs;
    }

    /**********
     * 获取关注超市优惠券
     ***********/
    private List<CouponBean> marketCoupon(String useid, String markeiId) {
        Map<String, String> map = new HashMap<>();
        //  String cuserId, String markeiId
        map.put("cuserId", useid);
        map.put("markeiId", markeiId);
        String result = client.execute(new DrpcRequest("CouponBolt", "userCoupon", map));
        List<CouponBean> couponsJrs = JSONArray.parseArray(result, CouponBean.class);
        return couponsJrs;
    }

    /********
     * 绑定银行卡
     *************/
    private JSONObject bankBind(User bean) {

        String result = client.execute(new DrpcRequest("CouponBolt", "bankBind", bean));
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    /******
     * 添加收藏
     ******/
    private JSONObject collectAdd(User bean) {
        String result = client.execute(new DrpcRequest("CouponBolt", "collectAdd", bean));
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    /***
     * 收藏过期查询
     *****/
    private List<Integer> query_collect_id(User bean) {

        String result = client.execute(new DrpcRequest("CouponBolt", "query_collect_id", bean));
        List<Integer> jsonObject = JSONArray.parseArray(result, Integer.class);
        return jsonObject;
    }

    /***
     * 修改过期优惠
     **/
    private Integer collectionOverdue(String useid, String storeId) {
        Map<String, String> map = new HashMap<>();

        map.put("cuserId", useid);
        map.put("storeId", storeId);
        String result = client.execute(new DrpcRequest("CouponBolt", "collectionOverdue", map));
        return Integer.valueOf(result);
    }

    /********/
    private Integer collectExpire(User bean) {
        String result = client.execute(new DrpcRequest("CouponBolt", "collectionOverdue", bean));
        return Integer.valueOf(result);
    }


}
