package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReferenceDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReportDetailsDto;
import com.caiyi.financial.nirvana.ccard.investigation.service.InvestigationService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mario on 2016/7/25 0025.
 * 征信报表相关bolt
 */
@Bolt(boltId = "investReport", parallelismHint = 1, numTasks = 1)
public class ReportBolt extends BaseBolt {

    private InvestigationService investigationService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        investigationService = getBean(InvestigationService.class);
        logger.info("---------------------investReportBolt _prepare");
    }


    /**
     * 更新重新申请征信报告标识
     *
     * @param bean
     * @return
     */
    @BoltController
    public void updateReApplyStatus(Channel bean) {
        try {
            investigationService.updateReApplyStatus(bean);
            logger.info("updateReApplyStatus 更新成功.");
        } catch (Exception e) {
            logger.error("updateReApplyStatus异常");
            e.printStackTrace();
        }
    }

    /**
     * 查询报告
     *
     * @param bean
     */
    @BoltController
    public JSONObject queryUserReference(Channel bean) {
        logger.info("queryUserCreditreFerence start :");
        JSONObject jsonObj = new JSONObject();
        try {
            List<CreditReferenceDto> listCrDto = investigationService.queryUserReference(bean);
            if (listCrDto != null && listCrDto.size() > 0) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("查询成功");
                StringBuffer sb = new StringBuffer();
                for (CreditReferenceDto item : listCrDto) {
                    long icrid = item.getIcrid();
                    sb.append("<row icrid=\"" + icrid + "\" cloginname=\"" + item.getCloginname() + "\" cuserid=\"" + item.getCuserid() + "\" creportno=\"" + item.getCreportno() + "\" "
                            + "creportdate=\"" + item.getCreportdate() + "\" itotaldefault=\"" + item.getItotaldefault() + "\" crealname=\"" + item.getCrealname() + "\" cmarstatus=\"" + item.getCmarstatus() + "\" "
                            + "cidtype=\"" + item.getCidtype() + "\" cidcard=\"" + item.getCidcard() + "\" icreditcc=\"" + item.getIcreditcc() + "\" icreditcu=\"" + item.getIcreditcu() + "\"  icreditco=\"" + item.getIcreditco() + "\" "
                            + "icreditcso=\"" + item.getIcreditcso() + "\" icreditcg=\"" + item.getIcreditcg() + "\" iloanc=\"" + item.getIloanc() + "\" iloanu=\"" + item.getIloanu() + "\" iloano=\"" + item.getIloano() + "\" iloanso=\"" + item.getIloanso() + "\" "
                            + "iloang=\"" + item.getIloang() + "\" iot=\"" + item.getIot() + "\" icj=\"" + item.getIcj() + "\" ice=\"" + item.getIce() + "\" iap=\"" + item.getIap() + "\" ita=\"" + item.getIta() + "\" imi=\"" + item.getImi() + "\" ipi=\"" + item.getIpi() + "\" isobtain=\"" + item.getIsobtain() + "\" "
                            + "cadddate=\"" + item.getCadddate() + "\" isapply=\"" + item.getIsapply() + "\" clastapplydate=\"" + item.getClastapplydate() + "\" isreapply=\"" + item.getIsreapply() + "\" >");

                    List<CreditReportDetailsDto> listCRDDto = investigationService.queryCreditReportDetailById(icrid);

                    Map<String, String> mapType = new HashMap<>();
                    if (listCRDDto != null && listCRDDto.size() > 0) {
                        for (CreditReportDetailsDto itemDetail : listCRDDto) {
                            String type = itemDetail.getItype().toString();
                            String value = itemDetail.getCdetails();
                            if (!mapType.containsKey(type)) {
                                mapType.put(type, "");
                            }
                            mapType.put(type, mapType.get(type) + "<details value=\"" + value + "\" itype=\"" + type + "\" />");
                        }
                    }
                    for (String key : mapType.keySet()) {
                        sb.append("<type" + key + ">");
                        sb.append(mapType.get(key));
                        sb.append("</type" + key + ">");
                    }
                    sb.append("</row>");
                }
                bean.setBusiXml(sb.toString());
                logger.info(sb.toString());
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未查询到记录");
            }
        } catch (Exception e) {
            logger.error("queryCreditTransaction异常", e);
            e.printStackTrace();
        } finally {
            jsonObj.put("code", bean.getBusiErrCode());
            jsonObj.put("desc", bean.getBusiErrDesc());
            jsonObj.put("xml", bean.getBusiXml());
            return jsonObj;
        }
    }


    /**
     * 查询报告
     *
     * @param bean
     */
    @BoltController
    public JSONObject queryUserReferenceByLoginname(Channel bean) {
        logger.info("queryUserCreditreFerence start :");
        JSONObject jsonObj = new JSONObject();
        try {

            CreditAccountDto cadto = investigationService.queryZhengxinAccountByCuserId(bean);
            if (cadto == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未查询到记录");
                return jsonObj;
            }
            bean.setLoginname(cadto.getCloginname());
            List<CreditReferenceDto> listCrDto = investigationService.queryUserReferenceByLoginname(bean);
            if (listCrDto != null && listCrDto.size() > 0) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("查询成功");
                JSONObject rowJson = new JSONObject();
                CreditReferenceDto item = listCrDto.get(0);
                long icrid = item.getIcrid();
                rowJson.put("icrid", icrid + "");
                rowJson.put("cloginname", item.getCloginname());
                rowJson.put("cuserid", item.getCuserid());
                rowJson.put("creportno", item.getCreportno());
                rowJson.put("creportdate", item.getCreportdate());
                rowJson.put("itotaldefault", item.getItotaldefault() + "");
                rowJson.put("crealname", item.getCrealname());
                rowJson.put("cmarstatus", item.getCmarstatus());
                rowJson.put("cidtype", item.getCidtype());
                rowJson.put("cidcard", item.getCidcard());
                rowJson.put("icreditcc", item.getIcreditcc() + "");
                rowJson.put("icreditcu", item.getIcreditcu() + "");
                rowJson.put("icreditco", item.getIcreditco() + "");
                rowJson.put("icreditcso", item.getIcreditcso() + "");
                rowJson.put("icreditcg", item.getIcreditcg() + "");
                rowJson.put("iloanc", item.getIloanc() + "");
                rowJson.put("iloanu", item.getIloanu() + "");
                rowJson.put("iloano", item.getIloano() + "");
                rowJson.put("icreditcso", item.getIcreditcso() + "");
                rowJson.put("iloanso", item.getIloanso() + "");
                rowJson.put("iloang", item.getIloang() + "");
                rowJson.put("iot", item.getIot() + "");
                rowJson.put("icj", item.getIcj() + "");
                rowJson.put("ice", item.getIce() + "");
                rowJson.put("iap", item.getIap() + "");
                rowJson.put("ita", item.getIta() + "");
                rowJson.put("imi", item.getImi() + "");
                rowJson.put("ipi", item.getIpi() + "");
                rowJson.put("isobtain", item.getIsobtain() + "");
                rowJson.put("cadddate", item.getCadddate());
                rowJson.put("isapply", item.getIsapply() + "");
                rowJson.put("clastapplydate", item.getClastapplydate());
                rowJson.put("isreapply", item.getIsreapply() + "");
                List<CreditReportDetailsDto> listCRDDto = investigationService.queryCreditReportDetailById(icrid);
                JSONArray detailsArray = new JSONArray();
                if (listCRDDto != null && listCRDDto.size() > 0) {
                    for (CreditReportDetailsDto itemDetail : listCRDDto) {
                        String type = itemDetail.getItype().toString();
                        String value = itemDetail.getCdetails();
                        JSONObject detail = new JSONObject();
                        detail.put("itype", type);
                        detail.put("value", value);
                        detailsArray.add(detail);
                    }
                }
                rowJson.put("details", detailsArray);
                jsonObj.put("report", rowJson);
                logger.info("rowJson=" + rowJson.toString());
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未查询到记录");
            }
        } catch (Exception e) {
            logger.error("queryCreditTransaction异常", e);
            e.printStackTrace();
        } finally {
            jsonObj.put("code", bean.getBusiErrCode());
            jsonObj.put("desc", bean.getBusiErrDesc());
            return jsonObj;
        }
    }


    /**
     * 分析报告
     *
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject analyticalReport(Channel bean) {
        JSONObject jsonObj = new JSONObject();

        try {
            Document reghtml = Jsoup.parse(bean.getReportHtml2());
            Elements tabs = reghtml.select("table");
            logger.info(bean.getCuserId() + "ReportHtml2 [" + bean.getReportHtml2() + "] ReportHtml1[" + bean.getReportHtml1() + "]");
            String reportno = "";//报告编号
            String reportTime = "";//报告时间
            String realname = "";//用户姓名
            String idcard = "";//证件号码
            String idtype = "";//证件类型
            String mstatus = "";//婚姻状况
            Integer creditcc = 0;//信用卡账户数
            Integer loanc = 0;//贷款账户数
            Integer creditcu = 0;//信用卡未销户数
            Integer loanu = 0;//贷款未销户数
            Integer creditco = 0;//信用卡逾期账户数
            Integer loano = 0;//贷款逾期账户数
            Integer creditcso = 0;//信用卡严重逾期账户数
            Integer loanso = 0;//贷款严重逾期账户数
            Integer creditcg = 0;//信用卡担保次数
            Integer loang = 0;//贷款担保次数

            Integer ot = 0;//欠税记录
            Integer cj = 0;//民事判决记录
            Integer ce = 0;//强制执行记录
            Integer ap = 0;//行政处罚记录
            Integer ta = 0;//电信欠费记录

            Integer jgquerys = 0;
            Integer selfquerys = 0;
            String seldetail = "";

            boolean isexist = false;
            long icid = 0;

            CreditReferenceDto creditRfDto = investigationService.queryCreditRefDto(bean);

            if (creditRfDto != null) {
                icid = creditRfDto.getIcrid();
                isexist = true;
                int res = investigationService.deleteReportDetail(icid);
                logger.info("delete reportdetails rows " + res);
            } else {
                long res = investigationService.selectSeqCreditReference();
                icid = res;
                isexist = false;
            }

            for (int i = 1; i < tabs.size(); i++) {
                org.jsoup.nodes.Element tab = tabs.get(i);
                String tabhtml = tab.html();
                Elements tds = tab.select("td");
                if (tabhtml.indexOf("报告编号") != -1) {

                    for (int j = 0; j < tds.size(); j++) {
                        String trtext = tds.get(j).text();
                        if (trtext.indexOf("报告编号") != -1) {
                            //报告编号： 2015110403000187602546
                            reportno = trtext.replaceAll("报告编号：", "").trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 报告编号:" + reportno);
                        } else if (trtext.indexOf("报告时间") != -1) {
                            //报告时间：2015.11.03 20:31:37
                            reportTime = trtext.replaceAll("报告时间：", "").trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 报告时间:" + reportTime);
                        }

                    }
                } else if (tabhtml.indexOf("姓名") != -1) {

                    for (int j = 0; j < tds.size(); j++) {
                        String trtext = tds.get(j).text();
                        if (trtext.indexOf("姓名") != -1) {
                            realname = trtext.replaceAll("姓名：", "").trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 姓名:" + realname);
                        } else if (trtext.indexOf("证件类型") != -1) {
                            //报告时间：2015.11.03 20:31:37
                            idtype = trtext.replaceAll("证件类型：", "").trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 证件类型:" + idtype);
                        } else if (trtext.indexOf("证件号码") != -1) {
                            //报告时间：2015.11.03 20:31:37
                            idcard = trtext.replaceAll("证件号码：", "").trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 证件号码:" + idcard);
                        } else {
                            mstatus = trtext.trim();
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 婚姻状况:" + mstatus);
                        }
                    }
                } else if (tabhtml.indexOf("购房贷款") != -1 && tabhtml.indexOf("table") == -1) {
                    Elements trs = tab.select("tr");
                    if (trs.size() > 1) {
                        for (int j = 1; j < trs.size(); j++) {
                            Elements etds = trs.get(j).select("td");
                            if (etds.size() != 4) {
                                logger.info(etds.html());
                                continue;
                            }
                            String tdhtml = etds.get(0).text().replaceAll(" ", "").trim();
                            String tdzhtext = etds.get(1).text();
                            String tddktext1 = etds.get(2).text();
                            String tddktext2 = etds.get(3).text();
                            if (tdhtml.equals("账户数")) {
                                if (isNumeric(tdzhtext)) {
                                    creditcc = Integer.valueOf(tdzhtext);
                                }
                                if (isNumeric(tddktext1)) {
                                    loanc = Integer.valueOf(tddktext1);
                                }
                                if (isNumeric(tddktext2)) {
                                    loanc += Integer.valueOf(tddktext2);
                                }
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 信用卡账户数=" + creditcc + " 贷款账户数=" + loanc);
                            } else if (tdhtml.equals("未结清/未销户账户数")) {
                                if (isNumeric(tdzhtext)) {
                                    creditcu = Integer.valueOf(tdzhtext);
                                }
                                if (isNumeric(tddktext1)) {
                                    loanu = Integer.valueOf(tddktext1);
                                }
                                if (isNumeric(tddktext2)) {
                                    loanu += Integer.valueOf(tddktext2);
                                }
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 信用卡未销户账户数=" + creditcu + " 贷款未销户账户数=" + loanu);
                            } else if (tdhtml.equals("发生过逾期的账户数")) {
                                if (isNumeric(tdzhtext)) {
                                    creditco = Integer.valueOf(tdzhtext);
                                }
                                if (isNumeric(tddktext1)) {
                                    loano = Integer.valueOf(tddktext1);
                                }
                                if (isNumeric(tddktext2)) {
                                    loano += Integer.valueOf(tddktext2);
                                }
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 信用卡发生过逾期的账户数=" + creditco + " 贷款发生过逾期的账户数=" + loano);
                            } else if (tdhtml.equals("发生过90天以上逾期的账户数")) {
                                if (isNumeric(tdzhtext)) {
                                    creditcso = Integer.valueOf(tdzhtext);
                                }
                                if (isNumeric(tddktext1)) {
                                    loanso = Integer.valueOf(tddktext1);
                                }
                                if (isNumeric(tddktext2)) {
                                    loanso += Integer.valueOf(tddktext2);
                                }
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 信用卡发生过90天以上逾期的账户数=" + creditcso + " 贷款发生过90天以上逾期的账户数=" + loanso);
                            } else if (tdhtml.equals("为他人担保笔数")) {
                                if (isNumeric(tdzhtext)) {
                                    creditcg = Integer.valueOf(tdzhtext);
                                }
                                if (isNumeric(tddktext1)) {
                                    loang = Integer.valueOf(tddktext1);
                                }
                                if (isNumeric(tddktext2)) {
                                    loang += Integer.valueOf(tddktext2);
                                }
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 信用卡为他人担保笔数=" + creditcg + " 贷款为他人担保笔数=" + loang);
                            }
                        }
                    }
                } else if (tabhtml.indexOf("公共记录") != -1 && tabhtml.indexOf("table") == -1) {
                    if (tds.size() > 1) {
                        for (int j = 1; j < tds.size(); j++) {
                            String trtext = tds.get(j).text();
                            if (trtext.indexOf("系统中没有您最近5年内的欠税记录、民事判决记录、强制执行记录、行政处罚记录及电信欠费记录。") != -1) {
                                //报告编号： 2015110403000187602546
                                //ot = 0;//欠税记录
                                cj = 0;//民事判决记录
                                //ce = 0;//强制执行记录
                                ap = 0;//行政处罚记录
                                ta = 0;//电信欠费记录
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "]  欠税记录[" + ot + "] 民事判决记录[" + cj + "] 强制执行记录[" + ce + "] 行政处罚记录[" + ap + "] 电信欠费记录[" + ta + "]");
                            } else {
//								Pattern p = Pattern.compile("[0-9\\.]+");
//								Matcher m = p.matcher(trtext);
//								while (m.find()) {
//									System.out.print(m.group() + ",");
//								}
                                logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] unkown[" + trtext + "]");
                            }
                        }
                    } else {
                        logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] unkown [" + tds.html() + "]");
                    }
                } else if (tabhtml.contains("执行法院") && tabhtml.contains("tbody")) {
                    if (tds.size() > 1) {
                        ce++;
                        StringBuffer result = new StringBuffer();
                        for (int j = 0; j < tds.size(); j++) {
                            String trtext = tds.get(j).text();
                            if (trtext.indexOf("执行法院") != -1) {
                                result.append("执行法院:" + trtext.replaceAll("执行法院：", "").trim() + "、");
                            } else if (trtext.indexOf("案号") != -1) {
                                result.append("案号:" + trtext.replaceAll("案号：", "").trim() + "、");
                            } else if (trtext.indexOf("执行案由") != -1) {
                                result.append("执行案由:" + trtext.replaceAll("执行案由：", "").trim() + "、");
                            } else if (trtext.indexOf("结案方式") != -1) {
                                result.append("结案方式:" + trtext.replaceAll("结案方式：", "").trim() + "、");
                            } else if (trtext.indexOf("立案时间") != -1) {
                                result.append("立案时间:" + trtext.replaceAll("立案时间：", "").trim() + "、");
                            } else if (trtext.indexOf("案件状态") != -1) {
                                result.append("案件状态:" + trtext.replaceAll("案件状态：", "").trim() + "、");
                            } else if (trtext.indexOf("申请执行标的金额") != -1) {
                                result.append("申请执行标的金额:" + trtext.replaceAll("申请执行标的金额：", "").trim() + "、");
                            } else if (trtext.indexOf("申请执行标的") != -1) {
                                result.append("申请执行标的:" + trtext.replaceAll("申请执行标的：", "").trim() + "、");
                            } else if (trtext.indexOf("已执行标的") != -1) {
                                result.append("已执行标的:" + trtext.replaceAll("已执行标的：", "").trim() + "、");
                            } else if (trtext.indexOf("已执行标的金额") != -1) {
                                result.append("已执行标的金额:" + trtext.replaceAll("已执行标的金额：", "").trim() + "、");
                            } else if (trtext.indexOf("结案时间") != -1) {
                                result.append("结案时间:" + trtext.replaceAll("结案时间：", "").trim() + "、");
                            }
                        }
                        String details = result.toString();
                        details = details.substring(0, details.length() - 1);
                        CreditReportDetailsDto newDto = new CreditReportDetailsDto();
                        newDto.setIcrid(icid);
                        newDto.setCdetails(details);
                        newDto.setItype(9);
                        investigationService.insertCreditReportDetail(newDto);
                        logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 强制执行记录" + details);
                    } else {
                        logger.info(tds.html());
                    }
                } else if (tabhtml.contains("主管税务机关") && tabhtml.contains("tbody")) {
                    if (tds.size() > 1) {
                        ot++;
                        StringBuffer result = new StringBuffer();
                        for (int j = 0; j < tds.size(); j++) {
                            String trtext = tds.get(j).text();
                            if (trtext.indexOf("主管税务机关") != -1) {
                                result.append("主管税务机关:" + trtext.replaceAll("主管税务机关：", "").trim() + "、");
                            } else if (trtext.indexOf("欠税统计时间") != -1) {
                                result.append("欠税统计时间:" + trtext.replaceAll("欠税统计时间：", "").trim() + "、");
                            } else if (trtext.indexOf("欠税总额") != -1) {
                                result.append("欠税总额:" + trtext.replaceAll("欠税总额：", "").trim() + "、");
                            } else if (trtext.indexOf("纳税人识别号") != -1) {
                                result.append("纳税人识别号:" + trtext.replaceAll("纳税人识别号：", "").trim() + "、");
                            }
                        }
                        String details = result.toString();
                        details = details.substring(0, details.length() - 1);
                        CreditReportDetailsDto newDto = new CreditReportDetailsDto();
                        newDto.setIcrid(icid);
                        newDto.setCdetails(details);
                        newDto.setItype(7);
                        investigationService.insertCreditReportDetail(newDto);
                        logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 欠税记录" + details);
                    } else {
                        logger.info(tds.html());
                    }

                } else if (tabhtml.indexOf("机构查询记录明细") != -1 && tabhtml.indexOf("table") == -1) {
                    Elements trs = tab.select("tr");
                    if (trs.size() > 3) {
                        for (int j = 3; j < trs.size() - 1; j++) {
                            jgquerys++;
                            Elements etds = trs.get(j).select("td");
                            String details = etds.get(1).text() + " " + etds.get(2).text() + " " + etds.get(3).text();
                            CreditReportDetailsDto newDto = new CreditReportDetailsDto();
                            newDto.setIcrid(icid);
                            newDto.setCdetails(details);
                            newDto.setItype(3);
                            investigationService.insertCreditReportDetail(newDto);
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 机构查询记录明细 编号[" + etds.get(0).text() + "]  查询日期[" + etds.get(1).text() + "]  查询操作员[" + etds.get(2).text() + "]  查询原因[" + etds.get(3).text() + "]");
                        }
                    } else {
                        logger.info(trs.html());
                    }
                    logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 机构查询记录" + jgquerys + "次");
                } else if (tabhtml.indexOf("个人查询记录明细") != -1 && tabhtml.indexOf("table") == -1) {
                    Elements trs = tab.select("tr");
                    if (trs.size() > 3) {
                        for (int j = 3; j < trs.size() - 1; j++) {
                            selfquerys++;
                            Elements etds = trs.get(j).select("td");
                            String details = etds.get(1).text() + " " + etds.get(2).text() + " " + etds.get(3).text();
                            CreditReportDetailsDto newDto = new CreditReportDetailsDto();
                            newDto.setIcrid(icid);
                            newDto.setCdetails(details);
                            newDto.setItype(4);
                            investigationService.insertCreditReportDetail(newDto);
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 个人查询记录明细 编号[" + etds.get(0).text() + "]  查询日期[" + etds.get(1).text() + "]  查询操作员[" + etds.get(2).text() + "]  查询原因[" + etds.get(3).text() + "]");
                        }
                    } else {
                        logger.info(trs.html());
                    }
                    logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 个人查询记录明细" + selfquerys + "次");
                }
            }

            ///信贷记录分类
            Elements undestroyInfo = reghtml.getElementsByClass("olstyle");

            for (Element item : undestroyInfo) {
                //判断类型
                Integer type = -1;
                Elements elements = item.children();
                if (elements != null && elements.size() > 0) {
                    for (Element element : elements) {
                        String text = element.text();
                        if (StringUtils.isNotBlank(text) && text.contains("从未逾期过的贷记卡及透支未超过60天的准贷记卡账户")) {
                            type = 1;
                            continue;
                        } else if (StringUtils.isNotBlank(text) && text.contains("透支超过60天的准贷记卡账户明细如下")) {
                            type = 2;
                            continue;
                        } else if (StringUtils.isNotBlank(text) && text.contains("发生过逾期的贷记卡账户")) {
                            type = 0;
                            continue;
                        } else if (StringUtils.isNotBlank(text) && text.contains("从未逾期过的账户明细如下")) {
                            type = 6;
                            continue;
                        } else if (StringUtils.isNotBlank(text) && (text.contains("发生过逾期账户明细如下")||text.contains("发生过逾期的账户明细如下"))) {
                            type = 5;
                            continue;
                        }else{
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 未识别title=" +text);
                        }
                        if (StringUtils.isNotBlank(text)) {
                            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] " + "type = " + type + "---text =" + text);
                            CreditReportDetailsDto newDto = new CreditReportDetailsDto();
                            newDto.setIcrid(icid);
                            newDto.setCdetails(text);
                            newDto.setItype(type);
                            investigationService.insertCreditReportDetail(newDto);
                        }
                    }
                }
            }

            if (selfquerys == 0) {
                selfquerys = 1;
            }
            Document reg2html = Jsoup.parse(bean.getReportHtml1());
            Elements tabs2 = reg2html.select("table");
            for (int i = 1; i < tabs2.size(); i++) {
                org.jsoup.nodes.Element tab = tabs2.get(i);
                String tabhtml = tab.html();
                Elements tds = tab.select("td");

                if (tabhtml.indexOf("这部分包含您的信用报告最近2年被查询的记录") != -1 && tabhtml.indexOf("table") == -1) {
                    String temptd = tds.last().text();
                    logger.info(temptd);
                    if (temptd.indexOf("(") != -1) {
                        seldetail = temptd.substring(temptd.indexOf("(") + 1, temptd.indexOf(")"));
                        logger.info(seldetail);
//						Pattern p = Pattern.compile("[0-9\\.]+");
//						Matcher m = p.matcher(seldetail);
//						while (m.find()) {
//							String tn=m.group();
//							if (isNumeric(tn)) {
//								selfquerys+=Integer.valueOf(tn);
//							}
//						}
                    }
                }
            }

            logger.info("用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "] 本人查询[" + selfquerys + "]次");
            CreditReferenceDto creditReferenceDto = new CreditReferenceDto();
            creditReferenceDto.setCreportno(reportno);
            creditReferenceDto.setCloginname(bean.getLoginname());
            if (!StringUtils.isEmpty(reportTime)) {
                Date reportDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(reportTime);
                creditReferenceDto.setCreportdate(new java.sql.Timestamp(reportDate.getTime()));
                logger.info(bean.getCuserId() + " getCreportdate[" + creditReferenceDto.getCreportdate() + "] reportTime[" + reportTime + "]");
            }
            creditReferenceDto.setItype(CheckUtil.isNullString(bean.getFrom()) ? null : Integer.parseInt(bean.getFrom()));
            creditReferenceDto.setItotaldefault(creditcso + loanso);
            creditReferenceDto.setCuserid(bean.getCuserId());
            creditReferenceDto.setCrealname(realname);
            creditReferenceDto.setCmarstatus(mstatus);
            creditReferenceDto.setCidtype(idtype);
            creditReferenceDto.setCidcard(idcard);
            creditReferenceDto.setIcreditcc(creditcc);
            creditReferenceDto.setIcreditcu(creditcu);
            creditReferenceDto.setIcreditcg(creditcg);
            creditReferenceDto.setIcreditco(creditco);
            creditReferenceDto.setIcreditcso(creditcso);
            creditReferenceDto.setIloanc(loanc);
            creditReferenceDto.setIloanu(loanu);
            creditReferenceDto.setIloano(loano);
            creditReferenceDto.setIloanso(loanso);
            creditReferenceDto.setIloang(loang);
            creditReferenceDto.setIot(ot);
            creditReferenceDto.setIcj(cj);
            creditReferenceDto.setIce(ce);
            creditReferenceDto.setIap(ap);
            creditReferenceDto.setIta(ta);
            creditReferenceDto.setImi(jgquerys);
            creditReferenceDto.setIpi(selfquerys);
            creditReferenceDto.setIsobtain(1);
            creditReferenceDto.setIsreapply(0);
            creditReferenceDto.setIsapply(1);
            creditReferenceDto.setIcrid(icid);
            if (isexist) {
                int res = investigationService.updateCreditReference(creditReferenceDto);
                if (res == 1) {
                    logger.info("征信获取报告更新成功 用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "]");
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("获取信用报告成功");
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取信用报告失败，请稍后重试");
                    logger.info("征信获取报告更新失败 用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "]");
                }
            } else {
                int res = investigationService.insertCreditReference(creditReferenceDto);
                if (res == 1) {
                    logger.info("征信信用报告录入成功 用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "]");
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("获取信用报告成功");
                } else {
                    logger.info("征信信用报告录入失败 用户[" + bean.getCuserId() + "] loginname[" + bean.getLoginname() + "]");
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取信用报告失败，请稍后重试");
                }
            }

        } catch (
                Exception e)

        {
            logger.error(bean.getCuserId() + " jiexiReport异常" + bean.getReportHtml2(), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取信用报告失败，请稍后重试");
        } finally

        {
            jsonObj.put("code", bean.getBusiErrCode());
            jsonObj.put("desc", bean.getBusiErrDesc());
            return jsonObj;
        }

    }

    /**********************************
     * 以下方法移植自老工程
     */
    private boolean isNumeric(String str) {
        if (CheckUtil.isNullString(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
