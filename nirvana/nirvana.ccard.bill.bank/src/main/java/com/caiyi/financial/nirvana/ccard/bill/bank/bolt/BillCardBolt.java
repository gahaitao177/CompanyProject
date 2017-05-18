package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.CreditHandleDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.danga.MemCached.MemCachedClient;
import com.util.string.StringUtil;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mario on 2016/7/29 0029.
 */
@Bolt(boltId = "billCard", parallelismHint = 1, numTasks = 1)
public class BillCardBolt extends BaseBolt {
    // 预约办卡进度查询页面地址
    private final static String O2O_CARD_PROGRESS_ADDRESS = "http://www.huishuaka.com/5/yybk/jdcx/";
    private BankImportService bankImportService;
    @Autowired
    MemCachedClient cc;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        bankImportService = getBean(BankImportService.class);
        logger.info("---------------------billCard _prepare");
    }

    /**
     * 办卡
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel handleCredit(Channel bean) {
        try {
            List<HashMap<String, Object>> jrs = bankImportService.queryCreditHandle();
            if (null != jrs && jrs.size() > 0) {
                bean.setBusiXml(XmlTool.toRawXmlString(jrs, "row"));
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("查询成功");
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("没有数据");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(5);
            bean.setBusiErrDesc("查询异常");
        } finally {
            return bean;
        }
    }

    /**
     * 银行网点
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel bankPoint(Channel bean) {
        try {
            if (StringUtil.isEmpty(bean.getBankId()) || StringUtil.isEmpty(bean.getCityId())) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("参数错误");
                return bean;
            }
            int num = bankImportService.sumBankPoint(bean.getBankId(), bean.getCityId());
            // 总记录数
            int tp = (num + bean.getPs() - 1) / bean.getPs();
            // 总页数
            if (tp == 0) {
                tp = 1;
            }
            String pageXml = "<count tp=\"" + tp + "\" rc=\"" + num + "\" pn=\"" + bean.getPn() + "\" ps=\"" + bean.getPs() + "\"/>\r\n";
            if (StringUtil.isEmpty(bean.getClat())) {
                List<Map<String, Object>> latLng = bankImportService.queryLatLngByAreaId(bean.getCityId());
                if (null != latLng && latLng.size() > 0) {
                    bean.setClat(latLng.get(0).get("clat") + "");
                    bean.setClng(latLng.get(0).get("clng") + "");
                }
            }
            List<HashMap<String, Object>> jrs = bankImportService.queryBankPoint(bean.getClat(), bean.getClng(), bean.getBankId(), bean.getCityId(), bean.getPs(), bean.getPn());
            if (null != jrs && jrs.size() > 0) {
                bean.setBusiXml(pageXml + XmlTool.toRawXmlString(jrs, "row"));
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("查询成功");
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("没有数据");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(5);
            bean.setBusiErrDesc("查询异常");
        } finally {
            return bean;
        }
    }

    /**
     * 信用卡激活方式查询
     *
     * @return
     */
    @BoltController
    public JSONObject activation(Channel bean) {
        JSONObject result = new JSONObject();
        try {
            List<CreditHandleDto> creditHandleList = null;
            if (!StringUtil.isEmpty(bean.getBankId())) {
                creditHandleList = bankImportService.queryCreditHandle2(bean.getBankId());
            } else {
                creditHandleList = bankImportService.queryCreditHandle3();
            }
            if (null != creditHandleList && creditHandleList.size() > 0) {
                JSONArray rows = new JSONArray();
                for (CreditHandleDto creditHandle : creditHandleList) {
                    JSONObject row  = new JSONObject();
                    JSONObject json = JSONObject.parseObject(creditHandle.getCactivation());
                    JSONArray arr = json.getJSONArray("ebank");
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject ebank = arr.getJSONObject(i);
                        if (StringUtil.isEmpty(ebank.getString("title")) || StringUtil.isEmpty(ebank.getString("content"))) {
                            arr.remove(i);
                            i--;
                        }
                    }
                    row.put("ibankid", creditHandle.getIbankid());
                    row.put("cbankname", creditHandle.getCbankname());
                    row.put("cbankicon", creditHandle.getCbankicon());
                    row.put("cactivation", json.toString());
                    rows.add(row);
                }
                result.put("code",1);
                result.put("desc","查询成功");
                result.put("rows", rows);
            } else {
                result.put("code", -1);
                result.put("desc", "没有数据");
            }
        } catch (Exception e) {
            result.put("code", 5);
            result.put("desc", "查询异常");
        } finally {
            return result;
        }
    }

    /**
     * 办卡进度查询
     *
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject getCardProgress(Channel bean) {
        JSONObject result = new JSONObject();
        try {
            List<CreditHandleDto> creditHandleList = bankImportService.queryProgress();
            if(null!= creditHandleList && creditHandleList.size()>0){
                JSONArray rows = new JSONArray();
                for (CreditHandleDto creditHandle: creditHandleList) {
                    JSONObject row = new JSONObject();
                    row.put("ibankid", creditHandle.getIbankid());
                    row.put("cbankname", creditHandle.getCbankname());
                    row.put("cprogressaddr", creditHandle.getCprogressaddr());
                    row.put("cbankicon", creditHandle.getCbankicon());
                    rows.add(row);
                }
                result.put("code", 1);
                result.put("desc", "查询成功");
                result.put("rows", rows);
            }else{
                result.put("code", -1);
                result.put("desc", "没有数据");
            }
        } catch (Exception e) {
            result.put("code", 5);
            result.put("desc", "查询异常");
        }finally{
            return result;
        }
    }

    /**
     * 银行服务大厅
     * @param channel
     * @return
     */
    @BoltController
    public BoltResult bankServiceIndex(Channel channel) {
        logger.info("|银行服务大厅|bankServiceIndex");
        return bankImportService.bankServiceIndex(channel.getCuserId());
    }
}
