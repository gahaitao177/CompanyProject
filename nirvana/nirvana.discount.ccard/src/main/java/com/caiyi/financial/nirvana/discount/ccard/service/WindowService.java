package com.caiyi.financial.nirvana.discount.ccard.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.constant.ApplicationConstant;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Model;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.ccard.mapper.CheapMapper;
import com.caiyi.financial.nirvana.discount.util.DistanceUtil;
import com.caiyi.financial.nirvana.discount.util.LuceneUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.sqlsource.PageRawSqlSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by lizhijie on 2016/8/15.
 */
@Service
public class WindowService extends AbstractService {
    @Autowired
    CheapMapper cheapMapper;

    public Map<String, String> saveUserStatistics(Map<String, String> para) {
        Map<String, String> map = new HashMap<>();
        if (null == para) {
            map.put("code", "0");
            map.put("desc", "参数不能为空");
            return map;
        }
        para.put("", "");
        Integer count = cheapMapper.saveUserStatistics(para);
        if (count > 0) {
            map.put("code", "1");
            map.put("desc", "保存成功");
        } else {
            map.put("code", "0");
            map.put("desc", "保存失败");
        }
        return map;
    }

    public JSONObject startpage(Window bean) {
        JSONObject result = new JSONObject();
        try {
            //查询版本信息
            Map<String, Object> version = cheapMapper.queryStartPage();
            if (version != null) {
                result.put("loadimg", version);
                result.put("code", "1");
                result.put("desc", "查询成功");
            } else {
                result.put("code", "0");
                result.put("desc", "无记录");
            }
            result.put("findbanner", findbanner());
            result.put("loan", getLoanRate());
            result.put("newBanner", newBanner());
//			sb.append(newstartpage(jcn));
            //add by wsl 20160613  市场银行定制包功能
            result.put("appBankPage", appBankPage(bean));
            result.put("loanSwitch", addLoanSwitchToStart());
            // add by lcs 20161103 start
            Map<String, JSONObject> mqkfInfo = addMqkfInfo(bean.getIclient(), bean.getPackagename(), bean.getAppVersion(), bean.getSource());
            result.put("gid", mqkfInfo.get("mqkfId"));
            result.put("serviceTel", mqkfInfo.get("serviceTel"));
            result.put("closeFlag", mqkfInfo.get("closeFlag"));
            // add by lcs 20161103 end
            logger.info("启动页的最终获得信息,{}", result.toString());
            return result;
        } catch (Exception e) {
            result.put("code", "0");
            result.put("desc", "程序异常");
            logger.info(e.getMessage(), e);
            return result;
        }
    }

    /**
     * 查询版本和banner
     *
     * @return
     */
    private JSONObject findbanner() {
        JSONObject findBanner = new JSONObject();
        String version = cheapMapper.queryVersion(2);
        if (!StringUtils.isEmpty(version)) {
            findBanner.put("version", version);
        }
        List<Map<String, Object>> list = cheapMapper.queryBanner(0);
        if (list != null && list.size() > 0) {
            findBanner.put("bannerrowList", list);
        }
        logger.info("旧banner的版本信息,{}", findBanner.toJSONString());
        return findBanner;
    }

    /**
     * 获取贷款利率
     * Created By zhaojie 2015/11/20 13:52:52
     * updated by lcs 20160628 增加SDRateLv2 = 4.75  GJJRateLv2 = 2.75
     *
     * @return <Loan SDDesc="商业贷款基准利率" SDRate="4.90" GJJDesc="公积金贷款基准利率" GJJRate="3.25">
     */
    private JSONObject getLoanRate() {
        JSONObject loan = new JSONObject();
        loan.put("SDDesc", "2015年10月最新商业贷款基准利率");
        loan.put("SDRate", "4.90");
        loan.put("GJJDesc", "2015年8月最新公积金贷款基准利率");
        loan.put("GJJRate", "3.25");
        loan.put("SDRateLv2", "4.75");
        loan.put("GJJRateLv2", "2.75");
        return loan;
    }

    /**
     * 查询版本和 新的banner
     *
     * @return
     */
    private JSONObject newBanner() {
        JSONObject findBanner = new JSONObject();
        String version = cheapMapper.queryVersion(5);
        if (StringUtils.isEmpty(version)) {
            findBanner.put("newbanner", version);
        }
        List<Map<String, Object>> list = cheapMapper.queryBanner(1);
        if (list != null && list.size() > 0) {
            findBanner.put("newbannerrow", list);
        }
        logger.info("获得新banner的信息,{}", findBanner.toJSONString());
        return findBanner;
    }


    /**
     * 美洽客服信息,是否显示贷款
     * updated by jh 20161124 整合查询信息
     *
     * @param appVersion , Source
     * @return Map<String, JSONObject>
     */
    public Map<String, JSONObject> addMqkfInfo(int iclent, String packagename, String appVersion, int Source) {
        String appVersionSource;
        if (iclent == 0) {
            //安卓
            appVersionSource = packagename + "|" + appVersion + "|" + Source;
        } else {
            //IOS
            appVersionSource = appVersion + "|" + Source;
        }
        List<Map<String, Object>> mqkfInfoList = cheapMapper.queryMqkfInfo(appVersionSource);
        Map<String, JSONObject> mqkfInfoMap = new HashMap<>();
        JSONObject serviceTel = new JSONObject();
        JSONObject mqkfId = new JSONObject();
        JSONObject closeFlag = new JSONObject();
        serviceTel.put("serviceTel", "");
        mqkfId.put("gid", "");
        closeFlag.put("closeFlag", "0");
        if (mqkfInfoList != null && mqkfInfoList.size() > 0) {
            for (int i = 0; i < mqkfInfoList.size(); i++) {
                Object mqkfInfoCkey = mqkfInfoList.get(i).get("CKEY");
                Object mqkfInfoCvalue = mqkfInfoList.get(i).get("CVALUE");
                if (mqkfInfoCvalue == null) {
                    mqkfInfoCvalue = "";
                }
                if ("serviceTel".equals(mqkfInfoCkey)) {
                    serviceTel.put("serviceTel", mqkfInfoCvalue);
                }
                if ("MQKFID".equals(mqkfInfoCkey)) {
                    mqkfId.put("gid", mqkfInfoCvalue);
                }
                if (!CheckUtil.isNullString(appVersion)) {
                    if (appVersionSource.equals(mqkfInfoCkey)) {
                        closeFlag.put("closeFlag", mqkfInfoCvalue);
                    }
                }
            }
            mqkfInfoMap.put("serviceTel", serviceTel);
            mqkfInfoMap.put("mqkfId", mqkfId);
            mqkfInfoMap.put("closeFlag", closeFlag);
        }
        return mqkfInfoMap;
    }

    /**
     * 市场银行定制包功能
     *
     * @param bean packagename 包名,appVersion 版本号, source 渠道值
     */
    private JSONObject appBankPage(Window bean) {
        JSONObject appBankPage = new JSONObject();
        logger.info("包名：" + bean.getPackagename());
        String appVersion = bean.getAppVersion();
      /*  if(!StringUtils.isEmpty(bean.getAppVersion())){
            appVersion = bean.getAppVersion();
        }else{
            appVersion = bean.getAppversion();
        }*/
        logger.info("版本号：" + appVersion);

        logger.info("渠道值：" + bean.getSource());
        Map<String, String> map = new HashMap<>();
        map.put("packagename", bean.getPackagename());
        map.put("source", String.valueOf(bean.getSource()));
        map.put("appversion", appVersion);

        List<Integer> list = cheapMapper.queryAppBankPage(map);
        if (list != null && list.size() > 0) {
            appBankPage.put("mainswitch", 0);
            for (Integer i : list) {
                if (i == 1) {
                    appBankPage.put("mainswitch", i);
                }
            }
        }
        logger.info("获得市场银行定制包功能的信息,{}", appBankPage.toJSONString());
        return appBankPage;
    }

    // 启动页添加 贷款进度查询开关接口
    private JSONObject addLoanSwitchToStart() {
        JSONObject object = new JSONObject();
        try {
            com.typesafe.config.Config config = SystemConfig.getConfig(ApplicationConstant.LOAN_SWITCH);
            if (config != null) {
                object.put("swicth", config.getString("swicth"));
                object.put("address", config.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("读取启动页的配置文件异常,{}", e.toString());
        }
        logger.info("获得启动页的开关信息,{}", object.toJSONString());
        return object;
    }

    /**
     * lcs 20160806  通过类型 全局搜索
     *
     * @param bean
     */
    public JSONObject totalSearchByType(Cheap bean) {
        JSONObject result = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        result.put("code", "1");
        result.put("desc", "操作成功");
        try {
            String query = bean.getQuery();
            String cityid = String.valueOf(bean.getIcityid());
            logger.info("query:" + query + ",cityid" + cityid);
//            // 查询卡片
//            if ("1".equals(bean.getSearchtype())){
//                JSONObject cardJson = totalSearchCard(bean);
//                if (cardJson != null && cardJson.size() > 0 && cardJson.containsKey("type")){
//                    jsonObject = cardJson;
//                }
//            }
            // 查询卡神攻略
            if ("3".equals(bean.getSearchtype())) {
                JSONObject contactJson = totalSearchContact(bean);
                if (contactJson != null && contactJson.size() > 0 && contactJson.containsKey("type")) {
                    jsonObject = contactJson;
                }
            }
            // 查询优惠
            if ("2".equals(bean.getSearchtype())) {
                JSONObject cheapJson = totalSearchCheap(bean);
                if (cheapJson != null && cheapJson.size() > 0 && cheapJson.containsKey("type")) {
                    jsonObject = cheapJson;
                }
            }
            result.put("data", jsonObject);
            if (jsonObject.getJSONArray("contents").size() <= 0) {
                result.put("code", "1");
                result.put("desc", "没有获得数据");
            }
            return result;
//            bean.setBusiJSON(jsonObject.toString());
        } catch (Exception e) {
            logger.info("查询异常,{}", e.toString());
            result.put("code", "-1");
            result.put("desc", "查询异常");
            return result;
        }
    }

    /**
     * lcs 20160806  全局搜索
     *
     * @param bean
     */
    public JSONObject totalSearch(Cheap bean) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        result.put("code", "1");
        result.put("desc", "查询成功");
        String query = bean.getQuery();
        String cityid = String.valueOf(bean.getIcityid());
        logger.info("query:" + query + ",cityid" + cityid);
        try {
            // 查询卡片
//            JSONObject cardJson = totalSearchCard(bean,pool,tid);
//            if (cardJson != null && cardJson.size() > 0 && cardJson.containsKey("type")){
//                jsonArray.add(cardJson);
//            }
            // 查询卡神攻略
            JSONObject contactJson = totalSearchContact(bean);
            if (contactJson != null && contactJson.size() > 0 && contactJson.containsKey("type")) {
                jsonArray.add(contactJson);
            }
            // 查询优惠
            JSONObject cheapJson = totalSearchCheap(bean);
            if (cheapJson != null && cheapJson.size() > 0 && cheapJson.containsKey("type")) {
                jsonArray.add(cheapJson);
            }
            result.put("data", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("数据库查询异常，方法totalSearch,异常=", e.toString());
            result.put("code", "-1");
            result.put("desc", "程序异常");
            return result;
        }
        if (jsonArray == null || jsonArray.size() == 0) {
            result.put("code", "0");
            result.put("desc", "查询失败");
        }
        return result;
    }

    /**
     * @param bean 搜索卡神攻略
     */
    private JSONObject totalSearchContact(Cheap bean) {
        JSONObject jsonObject = new JSONObject();
        String sqlWhere = "(ctitle like '%" + bean.getQuery() + "%' or csummary like '%" + bean.getQuery() + "%') ";
        String sql = "select count(1) as num from tb_contact t where " +
                sqlWhere +
                "       and t.ipublished = 1" +
                "       and t.iactive = 0" +
                "        and t.itype = 0 ";
        Map<String, String> where = new HashMap<String, String>();
        where.put("totalsearch", sqlWhere);
        logger.info("sql:" + sql);
        logger.info("where:" + where);
//			int count = JdbcSqlMapping.getRecordCount("count_totalsearch_contanct", bean, where, jcn);
        int count = 0;
        String[] param = {};
//            JdbcRecordSet countjrs = jcn.executeQuery(sql, param);
//            if (countjrs != null && countjrs.size() > 0 && countjrs.first()) {
//                count = countjrs.getInt("num");
//            }
        int pn = bean.getPn();
        pn = pn > 0 ? pn : 1;
        int ps = bean.getPs();
        ps = ps > 0 ? ps : 4;
        logger.info("TEST:count:" + count + "ps:" + ps + "pn:" + pn);
        try {
            PageHelper.offsetPage(ps * (pn - 1), ps * pn);
            Page<Map<String, Object>> totalsearch_contanct = cheapMapper.totalsearch_contanct(where);
            count = Integer.parseInt(totalsearch_contanct.getTotal() + "");
            int tp = (count + ps - 1) / ps;
//            JdbcRecordSet jrs = JdbcSqlMapping.executeQuery("totalsearch_contanct", bean, where, ps, pn);
            if (totalsearch_contanct.getResult() != null && totalsearch_contanct.getResult().size() >= 0) {
                jsonObject.put("type", "3");
                jsonObject.put("isMore", "0");
                if (count > 2) {
                    jsonObject.put("isMore", "1");
                }
                if ("3".equals(bean.getSearchtype())) {
                    jsonObject.put("pn", pn);
                    jsonObject.put("ps", ps);
                    jsonObject.put("tp", tp);
                    jsonObject.put("rc", count);
                }
                jsonObject.put("contents", toJSONArr(totalsearch_contanct.getResult()));
            }
        } catch (Exception e) {
            logger.info("查询totalsearch_contanct出错,{}", e.toString());
            return null;
        }
        logger.info("jsonObject:" + jsonObject);
        return jsonObject;
    }

    public static JSONArray toJSONArr(List<Map<String, Object>> list) {
        JSONArray arr = new JSONArray();
        for (Map<String, Object> map : list) {
            JSONObject json = new JSONObject();
            for (String f : map.keySet()) {
                if ("capplydate".equals(f) && (map.get(f) != null)) {
                    String value = map.get(f).toString().split(" ")[0];
                    json.put(f, value);
                }
            }
            arr.add(json);
        }
        return arr;
    }

    /**
     * @param bean
     * @return add by lcs 20160806 搜索优惠
     */
    private JSONObject totalSearchCheap(Cheap bean) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        int count = 0;
        int pagesize = bean.getPs();
        int pageno = bean.getPn();
        String query = bean.getQuery().toLowerCase();
        double clng0 = bean.getClng();
        double clat0 = bean.getClat();
        try {
            int icityid = Integer.parseInt(String.valueOf(bean.getIcityid()));
            List<Document> list = null;
            boolean iscname = false;
            //list = LuceneUtil.seacher(query,icityid,new String[]{"cname"}, pageno*pagesize, pagesize);
            if (Model.CATEGOERY.contains(query)) {
                list = LuceneUtil.searchByDis(query, clat0, clng0, icityid, new String[]{"ccategoeryname", "pccategoeryname"}, QueryParser.Operator.OR);
            } else {
                list = LuceneUtil.searchByDis(query, clat0, clng0, icityid, new String[]{"cname"}, QueryParser.Operator.AND);
                iscname = true;
            }
            if (list == null || list.size() == 0) {
                list = LuceneUtil.searchByDis(query, clat0, clng0, icityid, null, QueryParser.Operator.OR);
                iscname = false;
            }
            if (list == null || list.size() == 0) {
//				bean.setBusiErrCode(0);
//				bean.setBusiErrDesc("未查询到优惠");
                return jsonObject;
            }
            Set<Document> set = new TreeSet<Document>(new Comparator<Document>() {
                @Override
                public int compare(Document o1, Document o2) {
                    return Integer.parseInt(o1.get("distance")) - Integer.parseInt(o2.get("distance"));
                }
            });

            for (Document d : list) {
                d.removeField("cbus_info");
                double clat = 0;
                double clng = 0;
                try {
                    clat = Double.parseDouble(d.get("clat"));
                    clng = Double.parseDouble(d.get("clng"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int distance = (int) DistanceUtil.GetDistance(clng0, clat0, clng, clat);
                d.add(new Field("distance", (distance + "").toString(), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
                set.add(d);
            }
            //去重一下
            List<Integer> ibusinessids = new ArrayList<Integer>();
            List<Document> olist = new ArrayList<Document>();
            for (Document d : set) {
                int bid = Integer.parseInt(d.get("ibussinessid"));
                if (!ibusinessids.contains(bid) || iscname) {
                    ibusinessids.add(bid);
                    olist.add(d);
                }
            }
            int fromIndex = (pageno - 1) * pagesize;
            int toIndex = pageno * pagesize;
            if (fromIndex > -1 && fromIndex < olist.size() && fromIndex < toIndex) {
                toIndex = toIndex < olist.size() ? toIndex : olist.size();
                list = olist.subList(fromIndex, toIndex);
            } else {
//				bean.setBusiErrCode(0);
//				bean.setBusiErrDesc("未查询到优惠");
                return null;
            }
            StringBuilder ret = new StringBuilder();
            count = olist.size();
            int tp = (count + pagesize - 1) / pagesize;
//			ret.append("<rows rc='"+count+"' tp='"+tp+"'  pn='"+pageno+"' ps='"+pagesize+"' ccount='"+list.size()+"' >");

            for (Document d : list) {
                JSONObject itemCheap = new JSONObject();
                List<Fieldable> fs = d.getFields();
                for (Fieldable f : fs) {
                    String key = f.name();
                    String val = d.get(key);
                    itemCheap.put(key, val);
                }
                jsonArray.add(itemCheap);
            }
            if ("2".equals(bean.getSearchtype())) {
                jsonObject.put("pn", pageno);
                jsonObject.put("ps", pagesize);
                jsonObject.put("tp", tp);
                jsonObject.put("rc", count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("type", "2");
        jsonObject.put("isMore", "0");
        if (count > 2) {
            jsonObject.put("isMore", "1");

        }
        jsonObject.put("contents", jsonArray);
        return jsonObject;
    }

    public JSONObject shuaba() {
        JSONObject result = new JSONObject();
        List<Map<String, Object>> listTopic = cheapMapper.queryTopics();
        if (listTopic != null && listTopic.size() > 0) {
            result.put("listTopic", listTopic);
        }
        List<Map<String, Object>> listMarkets = cheapMapper.queryMarkets();
        if (listMarkets != null && listMarkets.size() > 0) {
            result.put("listMarkets", listMarkets);
        }
        List<Map<String, Object>> listStrategys = cheapMapper.queryStrategys();
        if (listStrategys != null && listStrategys.size() > 0) {
            result.put("listStrategys", listStrategys);
        }
        if (result.size() > 0) {
            result.put("code", "1");
            result.put("desc", "查询成功");
        } else {
            result.put("code", "0");
            result.put("desc", "查询失败");
        }
        return result;
    }

}
