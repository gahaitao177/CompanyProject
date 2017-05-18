package com.caiyi.financial.nirvana.ccard.material.service;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.mapper.MaterialCardMapper;
import com.caiyi.financial.nirvana.ccard.material.util.Constant;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/28.
 */
public class ConvertBankData {

    private MemCachedClient client;
    MaterialCardMapper materialCardMapper;
    private Logger logger=logger = LoggerFactory.getLogger(ConvertBankData.class);

    public ConvertBankData(MemCachedClient cc,MaterialCardMapper mapper){
        this.client=cc;
        this.materialCardMapper=mapper;
    }
    public void  jiaoTongBank(MaterialBean bean){
        MaterialModel md=bean.getModel();
        Map<String,String> result;
        String bankcardid="";
        result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
        if(result!=null&&result.size()>0){
            bankcardid=result.get("ibankcityid");
            result.clear();
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data ibankid=16 获得申卡 md.getCardid()="+md.getCardid());
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("您申请的卡片暂停申请，请另选其他卡片");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您申请的卡片暂停申请，请另选其他卡片 md.getCardid()="+md.getCardid());
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("ibankid",bean.getIbankid());
        map.put("icityid",md.getIhome_cid());
        map.put("itype","1");
        Map<String,String> cityOrPro=materialCardMapper.query_bank_ProOrCity(map);
        String cityid = "";//住宅城市
        String cityName="";//住宅城市名称
        String provinceId = "";//住宅省市
        String provinceName = "";//住宅省市名称
        if (cityOrPro != null ) {
            cityid = cityOrPro.get("ibankcityid");
            cityName = cityOrPro.get("ccityname");
            logger.info("cityName="+cityName);
            cityOrPro.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅城市");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到住宅城市 getIhome_cid="+md.getIhome_cid());
            return;
        }
        map.put("icityid",md.getIhome_pid());
        cityOrPro=materialCardMapper.query_bank_ProOrCity(map);
        if (cityOrPro != null && cityOrPro.size() > 0) {
            provinceId = cityOrPro.get("ibankcityid");
            provinceName = cityOrPro.get("ccityname");
            logger.info("provinceName="+provinceName);
            cityOrPro.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅省市");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到住宅省市 getIhome_pid="+md.getIhome_pid());
            return;
        }
        map.clear();

//                if (CheckUtil.isNullString(md.getCidissueaddress())) {
//                    bean.setBusiErrCode(0);
//                    bean.setBusiErrDesc("身份证发证机关不能为空");
//                    bean.setBusiJSON("fail");
//                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 身份证发证机关不能为空");
//                    return;
//                }
//                String[] cids = md.getCidissueaddress().split("-");
//                if (cids.length < 2) {
//                    bean.setBusiErrCode(0);
//                    bean.setBusiErrDesc("无效的身份证发证机关");
//                    bean.setBusiJSON("fail");
//                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 无效的身份证发证机关 Cidissueaddress="+md.getCidissueaddress());
//                    return;
//                }
//
//                String applyNativeProvince = "";//籍贯 省份
//                String applyNativeCity = "";//籍贯 城市
//                jrs = jcn.executeQuery(apply_credit_city_sql_type, new Object[]{bean.getIbankid(), cids[0],1});
//                if (jrs != null && jrs.size() > 0) {
//                    jrs.first();
//                    applyNativeProvince = jrs.get("ibankcityid");
//                    jrs.clear();
//                } else {
//                    bean.setBusiErrCode(0);
//                    bean.setBusiErrDesc("未匹配到贯籍省，请检查身份证发证机关是否有误");
//                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到贯籍省，请检查身份证发证机关是否有误 cid0="+cids[0]);
//                    bean.setBusiJSON("fail");
//                    return;
//                }
//                jrs = jcn.executeQuery(apply_credit_city_sql_type, new Object[]{bean.getIbankid(), cids[1],1});
//                if (jrs != null && jrs.size() > 0) {
//                    jrs.first();
//                    applyNativeCity = jrs.get("ibankcityid");
//                    jrs.clear();
//                } else {
//                    bean.setBusiErrCode(0);
//                    bean.setBusiErrDesc("未匹配到贯籍市，请检查身份证发证机关是否有误");
//                    bean.setBusiJSON("fail");
//                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到贯籍市，请检查身份证发证机关是否有误 cid1="+cids[1]);
//                    return;
//                }

        String applyAddress = "";//住宅区（如徐汇区）
        applyAddress  = materialCardMapper.queryBankDistrict(md.getIhome_did(), md.getIhome_cid());
        if (StringUtils.isEmpty(applyAddress)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅区");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到住宅区 getIhome_did="+md.getIhome_did()+" getIhome_cid="+md.getIhome_cid());
            return;
        }
        String applyCompanyProvince = "";//公司所属省份(单位省)
        String applyCompanyCity = "";//公司所属城市(单位市)
        String applyCompanyCityName = "";//公司所属城市名称
        String applyCompanyProvinceName = "";//公司所属省份名称

        String applyCompanyAddress = "";//公司所属城市区(单位地区或县)

        map.put("ibankid",bean.getIbankid());
        map.put("icityid",md.getIcompany_pid());
        map.put("itype","1");

        cityOrPro=materialCardMapper.query_bank_ProOrCity(map);
        if (cityOrPro != null && cityOrPro.size() > 0) {
            applyCompanyProvince = cityOrPro.get("ibankcityid");
            applyCompanyProvinceName = cityOrPro.get("ccityname");
            logger.info("applyCompanyProvinceName="+applyCompanyProvinceName);
            cityOrPro.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所在省市");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到公司所在省市 getIcompany_pid="+md.getIcompany_pid());
            return;
        }
        map.put("icityid",md.getIcompany_cid());
        cityOrPro=materialCardMapper.query_bank_ProOrCity(map);
        if (cityOrPro != null && cityOrPro.size() > 0) {
            applyCompanyCity = cityOrPro.get("ibankcityid");
            applyCompanyCityName = cityOrPro.get("ccityname");
            logger.info("applyCompanyCityName="+applyCompanyCityName);
            cityOrPro.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所在城市");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到公司所在城市 getIcompany_cid="+md.getIcompany_cid());
            return;
        }
        applyCompanyAddress= materialCardMapper.queryBankDistrict(md.getIcompany_did(), md.getIcompany_cid());
        if (StringUtils.isEmpty(applyCompanyAddress)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所属城市区");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到公司所在城市   getIcompany_did="+md.getIcompany_did()+" getIcompany_cid="+md.getIcompany_cid());
            return;
        }
        String applyCompanyPropery = "";//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
        String applyIndustryType = "";//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
        String applyJobPost = "";//职位ID
        String cgfid = "";
        map.clear();

        map.put("ibankid",bean.getIbankid());
        map.put("itype","1");
        map.put("clocalid",md.getInatureofunit());
        Map<String,String> relation=materialCardMapper.apply_credit_relation(map);
        String sql = "select * from tb_applycredit_relation where itype=? and clocalid=? and ibankid=16";
//        jrs = jcn.executeQuery(sql, new Object[]{1, md.getInatureofunit()});
        if (relation != null && relation.size() > 0) {
            applyCompanyPropery = relation.get("cbankid");
            cgfid = relation.get("irid");
            relation.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到单位性质");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到单位性质   getInatureofunit="+md.getInatureofunit());
            return;
        }
        String cfid = "";
        map.clear();
        map.put("ibankid",bean.getIbankid());
        map.put("itype","2");
        map.put("clocalid",md.getIniatureofbusiness());
        map.put("cfatherid",cgfid);
        relation=materialCardMapper.apply_credit_relation(map);
        sql = "select * from tb_applycredit_relation where itype=? and clocalid=? and cfatherid=? and ibankid=16";
//        jrs = jcn.executeQuery(sql, new Object[]{2, md.getIniatureofbusiness(), cgfid});
        if (relation != null && relation.size() > 0) {
            applyIndustryType = relation.get("cbankid");
            cfid = relation.get("irid");
            relation.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到行业类别");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到行业类别   getIniatureofbusiness="+md.getIniatureofbusiness()+" cgfid="+cgfid);
            return;
        }
        String jobName = "";
        if ("1".equals(md.getIdepartment())) {
            if ("1".equals(md.getInatureofunit())) {
                jobName = "一般公务员";
            } else {
                jobName = "普通职员、技师";
            }
        } else if ("2".equals(md.getIdepartment())) {
            if ("1".equals(md.getInatureofunit())) {
                jobName = "处级以上行政领导（公务员 ）";
            } else {
                jobName = "经理及同等级别管理人员";
            }
        }
        if (!StringUtils.isEmpty(jobName)) {
            map.clear();
            map.put("ibankid",bean.getIbankid());
            map.put("itype","3");
            map.put("clocalid",md.getIdepartment());
            map.put("cfatherid",cfid);
            map.put("cdesc",jobName);
            relation=materialCardMapper.apply_credit_relation(map);
//            sql = "select * from tb_applycredit_relation where itype=? and clocalid=? and cfatherid=? and cdesc=? and ibankid=16";
//            jrs = jcn.executeQuery(sql, new Object[]{3, md.getIdepartment(), cfid, jobName});
        } else {
            map.clear();
            map.put("ibankid",bean.getIbankid());
            map.put("itype","3");
            map.put("clocalid",md.getIdepartment());
            map.put("cfatherid",cfid);
            relation=materialCardMapper.apply_credit_relation(map);
//            sql = "select * from tb_applycredit_relation where itype=? and clocalid=? and cfatherid=? and ibankid=16";
//            jrs = jcn.executeQuery(sql, new Object[]{3, md.getIdepartment(), cfid});
        }
        if (relation != null && relation.size() > 0) {
            applyJobPost = relation.get("cbankid");
            relation.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到职位");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 未匹配到职位  getIdepartment="+md.getIdepartment()+" cfid="+cfid+" jobName="+jobName);
            return;
        }
        bean.setApplyBankCardId(bankcardid);
        bean.setApplyCityId(cityid);
        bean.setCityName(cityName);
        bean.setApplyProvinceId(provinceId);
        bean.setProvinceName(provinceName);
//                bean.setApplyNativeProvince(applyNativeProvince);
//                bean.setApplyNativeCity(applyNativeCity);
        bean.setApplyAddress(applyAddress);
        bean.setApplyCompanyAddress(applyCompanyAddress);
        bean.setApplyCompanyProvince(applyCompanyProvince);
        bean.setApplyCompanyCity(applyCompanyCity);
        bean.setApplyCompanyCityName(applyCompanyCityName);
        bean.setApplyCompanyPropery(applyCompanyPropery);
        bean.setApplyCompanyProvinceName(applyCompanyProvinceName);
        bean.setApplyIndustryType(applyIndustryType);
        bean.setApplyJobPost(applyJobPost);
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("银行数据匹配成功");
        bean.setBusiJSON("success");
        logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 验证办卡地 cardId=" + bankcardid + " cityId=" + cityid+" success ibankid="+bean.getIbankid());
    }
    public  void guangDa(MaterialBean bean){
        //光大
        MaterialModel md=bean.getModel();
        logger.info("进入光大查询信息");
        Map<String,String> result;
        //光大cardid
        String bankcardid="";
        result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
        if (result!=null&&result.size()>0) {
            bankcardid = result.get("cbankcardid");
            result.clear();
            logger.info("查询光大卡牌id" + bankcardid);
        }else{
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("您申请的卡片暂停申请，请另选其他卡片");
            bean.setBusiJSON("fail");
            return;
        }
        bean.setApplyBankCardId(bankcardid);
        String applyCompanyProvince = "";//公司所属省份(单位省)
        String applyCompanyCity = "";//公司所属城市(单位市)
        String applyCompanyAddress = "";//公司地址
        String cbanklevel = "";

        String chome_pname;//住宅省名称
        String chome_cname;//住宅市名称
        String chome_dname;//住宅区名称

        Map<String,String> map=new HashMap<>();
        map.put("ibankid",bean.getIbankid());
        map.put("icityid",md.getIhome_pid());

        Map<String,String> district=materialCardMapper.query_bank_ProOrCity(map);

//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_pid()});
        if (district!=null&&district.size()>0) {
            chome_pname=district.get("ibankcityid").trim();
            district.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅省市");
            bean.setBusiJSON("fail");
            return;
        }
//        map.put("ibankid",bean.getIbankid());
        map.put("icityid",md.getIhome_cid());
        district=materialCardMapper.query_bank_ProOrCity(map);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_cid()});
        if (district!=null&&district.size()>0) {
            chome_cname=district.get("ibankcityid").trim();
//                    chome_cname=jrs.get("ccityname");
            district.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅城市");
            bean.setBusiJSON("fail");
            return;
        }
        map.put("icityid",md.getIhome_did());
        district=materialCardMapper.query_bank_ProOrCity(map);
//        String sql="select * from tb_district_bank where ibankid=? and icityid=?";
//        jrs = jcn.executeQuery(sql, new Object[]{bean.getIbankid(),md.getIhome_did()});
        if (district!=null&&district.size()>0) {
            chome_dname=district.get("ibankcityid");
//                    chome_dname=jrs.get("ccityname");
            district.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅区");
            bean.setBusiJSON("fail");
            return;
        }
        map.put("icityid",md.getIcompany_pid());
        district=materialCardMapper.query_bank_ProOrCity(map);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{bean.getIbankid(), md.getIcompany_pid()});
        if (district != null && district.size() > 0) {
            applyCompanyProvince = district.get("ibankcityid");
            district.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该省份不支持办理该卡");
            bean.setBusiJSON("fail");
            logger.info("光大未匹配到办卡省份：" + md.getIcompany_pid());
            return;
        }
        map.put("icityid",md.getIcompany_cid());
        district=materialCardMapper.query_bank_ProOrCity(map);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{bean.getIbankid(), md.getIcompany_cid()});
        if (district != null && district.size() > 0) {
            applyCompanyCity = district.get("ibankcityid");
            cbanklevel = district.get("cbanklevel");
            applyCompanyAddress = district.get("careavalue");
            district.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该城市不支持办理该卡");
            bean.setBusiJSON("fail");
            logger.info("光大未匹配到办卡城市："+md.getIcompany_cid());
            return;
        }
        if (StringUtils.isEmpty(cbanklevel)) {
            System.out.println("sql:apply_credit_city_sql 参数：" + md.getIbankid() + "--" + md.getIcompany_did());
            map.put("icityid",md.getIcompany_did());
            district=materialCardMapper.query_bank_ProOrCity(map);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{md.getIbankid(), md.getIcompany_did()});
            if (district != null && district.size() > 0) {
                applyCompanyAddress = district.get("ibankcityid");
                district.clear();
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("该地区不支持办理该卡");
                bean.setBusiJSON("fail");
                logger.info("光大未匹配到办卡地区：" + md.getIcompany_did());
                return;
            }
        }
        bean.setApplyCompanyProvince(applyCompanyProvince);
        bean.setApplyCompanyCity(applyCompanyCity);
        bean.setApplyCompanyAddress(applyCompanyAddress);
        bean.setChome_pname(chome_pname);
        bean.setChome_cname(chome_cname);
        bean.setChome_dname(chome_dname);
        logger.info("工作省市区：" + applyCompanyProvince + applyCompanyCity + applyCompanyAddress);
        logger.info("住宅省市区"+chome_pname+chome_cname+chome_dname);
    }

    /**
     * 广发银行
     * @param bean
     */
    public void guangFa(MaterialBean bean){
        //*申请卡号
        String cardLevel = "";
        String bankcardid="";
        MaterialModel md=bean.getModel();
        Map<String,String> result=null;
        Map<String,String> para=new HashMap<>();;
        result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
//        JdbcRecordSet jrs = jcn.executeQuery(apply_bank_card_sql, new Object[]{bean.getIbankid(), md.getCardid()});
        if (result!=null&&result.size()>0) {
            bankcardid = result.get("cbankcardid");
            cardLevel = result.get("ccardlevel");
            result.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("您申请的卡片暂停申请，请另选其他卡片");
            bean.setBusiJSON("fail");
            return;
        }
        //*常驻城市
        para.put("ibankid",bean.getIbankid());
        para.put("icityid",md.getIhome_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{bean.getIbankid(), md.getIhome_cid()});
        String cityid = "";//住宅城市
        String provinceId = "";//住宅省市
        if (result != null && result.size() > 0) {
            cityid = result.get("ibankcityid");
            provinceId = result.get("careavalue");
            result.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅城市");
            bean.setBusiJSON("fail");
            return;
        }
        //*单位地址
        String applyCompanyProvince = "";//公司所属省份(单位省)
        String applyCompanyCity = "";//公司所属城市(单位市)
        String applyCompanyAddress = "";//公司地址

        para.put("icityid",md.getIcompany_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{bean.getIbankid(), md.getIcompany_cid()});
        if (result != null && result.size() > 0) {
            applyCompanyCity = result.get("ibankcityid");
            applyCompanyProvince = result.get("careavalue");
            result.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所在城市");
            bean.setBusiJSON("fail");
            return;
        }
        //*单位性质
        String applyCompanyPropery = "";//单位性质:1机关、事业；2大型国有、股份制；3外商独资；4中外合作企业;5私营、集体;6个体
        String sql = "select t.cbankid from tb_applycredit_relation t where t.ibankid=1 and t.itype=1 and t.clocalid=?";
        Map<String,String> relationPara=new HashMap<>();
        relationPara.put("ibankid","1");
        relationPara.put("itype","1");
        relationPara.put("clocalid",md.getInatureofunit());
        result=materialCardMapper.apply_credit_relation(relationPara);
//        jrs = jcn.executeQuery(sql, new Object[]{md.getInatureofunit()});
        if (result != null && result.size() > 0) {
            applyCompanyPropery = result.get("cbankid");
            result.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到单位性质");
            bean.setBusiJSON("fail");
            return;
        }
        //*行业性质大类-行业性质小类
        String largeKind = "";// *行业性质--行业大类
        String cwkIndustryKind = "";// *行业性质--行业小类
        relationPara.put("itype","2");
        relationPara.put("clocalid",md.getIniatureofbusiness());
        result=materialCardMapper.apply_credit_relation(relationPara);
//        sql = "select t.cbankid from tb_applycredit_relation t where t.ibankid=1 and t.itype=2 and t.clocalid=?";
//        jrs = jcn.executeQuery(sql, new Object[]{md.getIniatureofbusiness()});
        if (result != null && result.size() > 0) {
            String[] iniatureofbusiness = result.get("cbankid").split("\\|");
            largeKind = iniatureofbusiness[0];
            cwkIndustryKind = iniatureofbusiness[1];
            result.clear();
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到行业性质");
            bean.setBusiJSON("fail");
            return;
        }
        bean.setApplyBankCardId(bankcardid);
        bean.setApplyBankCardLevel(cardLevel);
        bean.setApplyProvinceId(provinceId);//住宅省
        bean.setApplyCityId(cityid);//住宅城市
        bean.setApplyCompanyProvince(applyCompanyProvince);//公司省
        bean.setApplyCompanyCity(applyCompanyCity);//公司城市
        bean.setApplyCompanyAddress(applyCompanyAddress);//公司地址
        bean.setApplyCompanyPropery(applyCompanyPropery);//单位性质
        bean.setApplyIndustryType(largeKind);//行业大类
        bean.setApplyIndustryType2(cwkIndustryKind);//行业小类
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("银行数据匹配成功");
        bean.setBusiJSON("success");
    }
    public void pingAn(MaterialBean bean){
        //平安银行
        bean.setBusiErrCode(1);
        MaterialModel md=bean.getModel();
        String cidcard = md.getCidcard();
        String cphone = md.getCphone();
        String cardKey = cidcard + cphone + "_pingAn_cardtype";
        String city_key = cidcard + cphone + "_pingAn_citydata";
        String idAddress_key = cidcard + cphone + "_pingan_idAddress";
        if(client.get(cardKey)!=null && client.get(city_key)!=null && client.get(idAddress_key)!=null){
            logger.info("apply_credit_conversion 平安银行转换数据已保存到MemCache:"+client.get(cardKey));
        }else{
            logger.info("apply_credit_conversion 平安银行开始");
            //*申请卡号
            String bankcardid = "";
            Map<String,String> para=new HashMap<>();
            Map<String,String> result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
//            JdbcRecordSet jrs = jcn.executeQuery(apply_bank_card_sql, new Object[] {bean.getIbankid(),md.getCardid()});
            if (result!=null&&result.size()>0) {
                bankcardid = result.get("cbankcardid");
                client.set(cardKey, bankcardid, Constant.TIME_HOUR);
                logger.info("set cardKey>>>>>>>>>>>>"+cardKey+";value=="+bankcardid);
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您申请的卡片暂停申请，请另选其他卡片");
                bean.setBusiJSON("fail");
                logger.info("您申请的卡片暂停申请，请另选其他卡>>>>>>>>>>>>>>>>>："+md.getCardid());
                return;
            }
            String chome_cname;//住宅市名称
            String cacheData = "";
            para.put("ibankid",bean.getIbankid());
            para.put("icityid",md.getIhome_pid());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_pid()});
            if (result!=null&&result.size()>0) {
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname")+",";
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到住宅省市");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到住宅省份："+md.getIhome_pid());
                return;
            }
            para.put("icityid",md.getIhome_cid());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_cid()});
            if (result!=null&&result.size()>0) {
                chome_cname = result.get("ccityname");
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname")+",";
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到住宅城市");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到住宅城市："+md.getIhome_cid());
                return;
            }
            para.put("icityid",md.getIhome_did());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{md.getIbankid(),md.getIhome_did()});
            if (result!=null&&result.size()>0) {
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname");
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到住宅区");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到住宅所在市区："+md.getIhome_did());
                return;
            }
            cacheData += "@";
            String ccompany_cname;//公司市名称
            para.put("icityid",md.getIcompany_pid());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIcompany_pid()});
            if (result!=null&&result.size()>0) {
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname")+",";
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到公司所在省市");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到工作省份："+md.getIcompany_pid());
                return;
            }
            para.put("icityid",md.getIcompany_cid());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIcompany_cid()});
            if (result!=null&&result.size()>0) {
                ccompany_cname = result.get("ccityname");
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname")+",";
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到公司所在城市");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到公司所在城市："+md.getIcompany_cid());
                return;
            }
            para.put("icityid",md.getIcompany_did());
            result=materialCardMapper.query_bank_ProOrCity(para);
//            jrs = jcn.executeQuery(apply_credit_city_sql, new Object[]{md.getIbankid(),md.getIcompany_did()});
            if (result!=null&&result.size()>0) {
                cacheData += result.get("ibankcityid")+",";
                cacheData += result.get("ccityname");
                result.clear();
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未匹配到公司所在市区");
                bean.setBusiJSON("fail");
                logger.info("平安未匹配到公司所在市区："+md.getIcompany_did());
                return;
            }
            cacheData += "@";
            String ipostaddress = md.getIpostaddress();// 邮寄地址：（1、单位地址  2、住宅地址
            if("1".equals(ipostaddress)){
                cacheData += ccompany_cname;
            }else{
                cacheData += chome_cname;
            }
            //以银行+身份证为Key
            client.set(city_key, cacheData,Constant.TIME_HOUR);
            logger.info("apply_credit_conversion 平安银行数据转换结束,已保存到MemCache:"+client.get(city_key));
                    /*发证机关字符串*/
            String[] idaddress = bean.getModel().getCidissueaddress().split("-");
            String strIdaddress = "";
            //市
            String cname=materialCardMapper.queryBankDistrict2(idaddress[1]);
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{idaddress[1]});
            if (!StringUtils.isEmpty(cname)) {
                strIdaddress += cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证签发机关市id错误.");
                bean.setBusiJSON("fail");
                logger.info("平安身份证签发机关市id未匹配："+idaddress);
                return;
            }
            //县区
            cname=materialCardMapper.queryBankDistrict2(idaddress[2]);
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{idaddress[2]});
            if (!StringUtils.isEmpty(cname)) {
                strIdaddress += cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证签发机关县区id错误.");
                bean.setBusiJSON("fail");
                logger.info("平安身份证签发机关县区id未匹配："+idaddress);
                return;
            }
            //以银行+身份证为Key
            client.set(idAddress_key, strIdaddress,Constant.TIME_HOUR);
            logger.info("apply_credit_conversion 身份证签发机关已保存到MemCache:"+client.get(cidcard + cphone + "_pingan_idAddress"));
        }
    }

    /**
     * 中信银行 zhaojie
     * @param bean
     */
    public void zhongXin(MaterialBean bean){
        logger.info("apply_credit_conversion 中信银行开始.");
        MaterialModel md=bean.getModel();
        bean.setBusiErrCode(1);
        try {
//            CacheClient cc = CacheClient.getInstance();
            //转化cardType
            logger.info("中信银行查询卡类型对应:queryBankCardId"+bean.getIbankid()+" "+md.getCardid());

            Map<String,String> result=null;
            Map<String,String> para=new HashMap<>();;
            result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());

//            JdbcRecordSet jrs = jcn.executeQuery(apply_bank_card_sql, new Object[]{bean.getIbankid(), md.getCardid()});
            if (result != null && result.size() > 0) {
                client.set("zhongxin_cardtype_" + md.getCidcard(), result.get("cbankcardid"), Constant.TIME_HOUR);
                logger.info("卡类型信息已存储到MemCache:" + client.get("zhongxin_cardtype_" + md.getCidcard()));
                result.clear();
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持办理此类型卡片");
                bean.setBusiJSON("fail");
                logger.info("暂不支持办理此类型卡片");
            }
            /*家庭住址*/
            String cacheData = "";
            //省
            String cname=materialCardMapper.queryBankDistrict2(md.getIhome_pid());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIhome_pid()});
            if (!StringUtils.isEmpty(cname)) {
                cacheData += cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            //市
            cname=materialCardMapper.queryBankDistrict2(md.getIhome_cid());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIhome_cid()});
            if (!StringUtils.isEmpty(cname)) {
//                jrs.first();
                cacheData += "," + cname;
//                jrs.clear();
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            //区、县
            cname=materialCardMapper.queryBankDistrict2(md.getIhome_did());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIhome_did()});
            if (!StringUtils.isEmpty(cname)) {
                cacheData += "," + cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            cacheData += "@";
                    /*公司地址*/
            //省
            cname=materialCardMapper.queryBankDistrict2(md.getIcompany_pid());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIcompany_pid()});
            if (!StringUtils.isEmpty(cname)) {
                cacheData += cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            //市
            cname=materialCardMapper.queryBankDistrict2(md.getIcompany_cid());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIcompany_cid()});
            if (!StringUtils.isEmpty(cname)) {
                cacheData += "," + cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            //区、县
            cname=materialCardMapper.queryBankDistrict2(md.getIcompany_did());
//            jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{md.getIcompany_did()});
            if (!StringUtils.isEmpty(cname)) {
                cacheData += "," + cname;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("地区不支持此卡片");
                bean.setBusiJSON("fail");
            }
            //以银行+身份证为Key
            client.set("zhongxin_data_" + md.getCidcard(), cacheData, Constant.TIME_HOUR);
            logger.info("apply_credit_conversion 中信银行数据转换结束,已保存到MemCache:"+client.get("zhongxin_data_" + md.getCidcard()));
                    /*发证机关字符串*/
            /**
             * 暂时不要，不是必填字段 Modified By zhaojie 2016/3/11 11:20:32
             String[] idaddress = bean.getModel().getCidissueaddress().split("-");
             String strIdaddress = "";
             //市
             jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{idaddress[1]});
             if (jrs != null && jrs.size() > 0) {
             jrs.first();
             strIdaddress += jrs.get("cname");
             jrs.clear();
             } else {
             bean.setBusiErrCode(0);
             bean.setBusiErrDesc("身份证签发机关市id错误.");
             bean.setBusiJSON("fail");
             }
             //县区
             jrs = jcn.executeQuery("select * from tb_district where icityid=?", new Object[]{idaddress[2]});
             if (jrs != null && jrs.size() > 0) {
             jrs.first();
             strIdaddress += jrs.get("cname");
             jrs.clear();
             } else {
             bean.setBusiErrCode(0);
             bean.setBusiErrDesc("身份证签发机关县区id错误.");
             bean.setBusiJSON("fail");
             }
             //以银行+身份证为Key
             cc.set("zhongxin_idAddress_" + md.getCidcard(), strIdaddress, 3600000);
             logger.info("apply_credit_conversion 身份证签发机关已保存到MemCache:"+cc.get("zhongxin_idAddress_" + md.getCidcard()));
             */
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * by denghong
     * @param bean
     */
    public  void minSheng(MaterialBean bean){
        MaterialModel md=bean.getModel();
        Map<String,String> result=null;
        Map<String,String> para=new HashMap<>();;
        result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
//        JdbcRecordSet jrs = jcn.executeQuery(apply_bank_card_sql, new Object[]{bean.getIbankid(), md.getCardid()});
        System.out.println("获取要办理的卡片：");
        if (result!=null&&result.size()>0) {
            client.set("minShenCardType_" + bean.getModel().getCidcard(), result.get("cbankcardid"), Constant.TIME_HOUR);
            System.out.println("获取要办理的卡片：");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("暂不支持办理此类型卡片");
            bean.setBusiJSON("fail");
        }
        //获取家庭地址
        // 省
        para.put("ibankid",bean.getIbankid());
        para.put("itype","0");
        para.put("icityid",md.getIhome_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 0 and ibankid=11", new Object[]{md.getIhome_pid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeP_0" + bean.getModel().getCidcard(), result.get("ibankcityid"),Constant.TIME_HOUR);
            client.set( "minShengHomeP_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIhome_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 1 and ibankid=11", new Object[]{md.getIhome_pid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeP_1" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
        //市
        para.put("itype","0");
        para.put("icityid",md.getIhome_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=? and itype = 0 and ibankid=11", new Object[]{md.getIhome_cid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeC_0" + bean.getModel().getCidcard(), result.get("ibankcityid"),Constant.TIME_HOUR);
            client.set("minShengHomeC_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIhome_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=? and itype = 1 and ibankid=11", new Object[]{md.getIhome_cid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeC_1"+bean.getModel().getCidcard(),result.get("ibankcityid"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
        //区、县
        para.put("itype","0");
        para.put("icityid",md.getIhome_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 0 and ibankid=11", new Object[]{md.getIhome_did()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeD_0" + bean.getModel().getCidcard(), result.get("ibankcityid"),Constant.TIME_HOUR);
            client.set("minShengHomeD_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIhome_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 1 and ibankid=11", new Object[]{md.getIhome_did()});
        if (result!=null&&result.size()>0) {
            client.set("minShengHomeD_1"+bean.getModel().getCidcard(),result.get("ibankcityid"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到家庭住址");
            bean.setBusiJSON("fail");
        }
         /*公司地址*/
        //省
        para.put("itype","0");
        para.put("icityid",md.getIcompany_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=? and itype = 0 and ibankid=11", new Object[]{md.getIcompany_pid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobP_0" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengJobP_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIcompany_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=? and itype = 1 and ibankid=11", new Object[]{md.getIcompany_pid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobP_1"+bean.getModel().getCidcard(),result.get("ibankcityid"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        //市
        para.put("itype","0");
        para.put("icityid",md.getIcompany_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 0 and ibankid=11", new Object[]{md.getIcompany_cid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobC_0" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengJobC_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIcompany_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?  and itype = 1 and ibankid=11", new Object[]{md.getIcompany_cid()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobC_1"+bean.getModel().getCidcard(),result.get("ibankcityid"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        //区、县
        para.put("itype","0");
        para.put("icityid",md.getIcompany_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?   and itype = 0 and ibankid=11", new Object[]{md.getIcompany_did()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobD_0" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengJobD_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        para.put("itype","1");
        para.put("icityid",md.getIcompany_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery("select * from tb_district_bank where icityid=?   and itype = 1 and ibankid=11", new Object[]{md.getIcompany_did()});
        if (result!=null&&result.size()>0) {
            client.set("minShengJobD_1"+bean.getModel().getCidcard(),result.get("ibankcityid"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司地址");
            bean.setBusiJSON("fail");
        }
        //身份证签发机关

        String addr[] =  md.getCidissueaddress().split("-");
        para.put("itype","1");
        para.put("icityid",addr[0]);
        result=materialCardMapper.query_bank_ProOrCity(para);
//        String sql ="select * from tb_district_bank where icityid=?  and itype = 1 and ibankid=11 " ;
        //省
//        jrs = jcn.executeQuery(sql,new Object[]{addr[0]});
        if (result!=null&&result.size()>0) {
            client.set("minShengIdP_1" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengIdP_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到身份证签发机关");
            bean.setBusiJSON("fail");
        }
        // 市
        para.put("itype","1");
        para.put("icityid",addr[1]);
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(sql,new Object[]{addr[1]});
        if (result!=null&&result.size()>0) {
            client.set("minShengIdC_1" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengIdC_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到身份证签发机关");
            bean.setBusiJSON("fail");
        }
        // 县
        para.put("icityid",addr[2]);
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(sql,new Object[]{addr[2]});
        if (result!=null&&result.size()>0) {
            client.set("minShengIdD_1" + bean.getModel().getCidcard(), result.get("ibankcityid"), Constant.TIME_HOUR);
            client.set("minShengIdD_"+bean.getModel().getCidcard(),result.get("ccityname"),Constant.TIME_HOUR);
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到身份证签发机关");
            bean.setBusiJSON("fail");
        }
    }

    /**
     * lizhijie
     * @param bean
     */
    public  void xingYe(MaterialBean bean){
        String bankcardid = "";  //*申请卡号
        String cardLevel = "";
        MaterialModel md=bean.getModel();
        Map<String,String> para=new HashMap<>();
        Map<String,String> result=null;
        result=materialCardMapper.queryBankCardId(bean.getIbankid(),md.getCardid());
//        JdbcRecordSet jrs = jcn.executeQuery(apply_bank_card_sql, new Object[] {bean.getIbankid(),md.getCardid()});
        if (result!=null&&result.size()>0) {
            bankcardid = result.get("cbankcardid").trim();
            cardLevel = result.get("ccardlevel");
            result.clear();
            bean.setApplyBankCardId(bankcardid);
            bean.setApplyBankCardLevel(cardLevel);
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("您申请的卡片暂停申请，请另选其他卡片");
            bean.setBusiJSON("fail");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=10 您申请的卡片暂停申请，请另选其他卡片 md.getCardid()="+md.getCardid());
            return;
        }
        String hprovinceId="";//住宅省id
        String hcityid="";//住宅城市id
        String hdistrictId="";//住宅区（如徐汇区）id
        String chome_pname;//住宅省名称
        String chome_cname;//住宅市名称
        String chome_dname;//住宅区名称
        para.put("ibankid",bean.getIbankid());
        para.put("icityid",md.getIhome_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_pid()});
        if (result!=null&&result.size()>0) {
            hprovinceId=result.get("ibankcityid").trim();
            chome_pname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅省市");
            bean.setBusiJSON("fail");
            return;
        }
        para.put("icityid",md.getIhome_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIhome_cid()});
        if (result!=null&&result.size()>0) {
            hcityid=result.get("ibankcityid").trim();
            chome_cname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅城市");
            bean.setBusiJSON("fail");
            return;
        }
        para.put("icityid",md.getIhome_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        String sql="select * from tb_district_bank where ibankid=? and icityid=?";
//        jrs = jcn.executeQuery(sql, new Object[]{bean.getIbankid(),md.getIhome_did()});
        if (result!=null&&result.size()>0) {
            hdistrictId=result.get("ibankcityid");
            chome_dname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到住宅区");
            bean.setBusiJSON("fail");
            return;
        }
        //住宅城市id,name
        bean.setApplyProvinceId(hprovinceId);
        bean.setApplyCityId(hcityid);
        bean.setApplyAddress(hdistrictId);

        String company_provinceId="";//公司所属省份(单位省)
        String company_cityId="";//公司所属城市(单位市)
        String company_districtId="";//公司所属城市区(单位地区或县)
        String ccompany_pname;//公司省名称
        String ccompany_cname;//公司市名称
        String ccompany_dname;//公司区名称
        para.put("icityid",md.getIcompany_pid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIcompany_pid()});
        if (result!=null&&result.size()>0) {
            company_provinceId=result.get("ibankcityid").trim();
            ccompany_pname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所在省市");
            bean.setBusiJSON("fail");
            return;
        }
        para.put("icityid",md.getIcompany_cid());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(apply_credit_city_sql, new Object[] {bean.getIbankid(),md.getIcompany_cid()});
        if (result!=null&&result.size()>0) {
            company_cityId=result.get("ibankcityid");
            ccompany_cname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司所在城市");
            bean.setBusiJSON("fail");
            return;
        }
        para.put("icityid",md.getIcompany_did());
        result=materialCardMapper.query_bank_ProOrCity(para);
//        jrs = jcn.executeQuery(sql, new Object[]{bean.getIbankid(),md.getIcompany_did()});
        if (result!=null&&result.size()>0) {
            company_districtId=result.get("ibankcityid");
            ccompany_dname=result.get("ccityname");
            result.clear();
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("未匹配到公司的区县地址");
            bean.setBusiJSON("fail");
            return;
        }
        //工作城市id,name
        bean.setApplyCompanyProvince(company_provinceId);
        bean.setApplyCompanyCity(company_cityId);
        bean.setApplyCompanyAddress(company_districtId);
        md.setCbak1(chome_pname+chome_cname+chome_dname);
        md.setCbak2(ccompany_pname+ccompany_cname+ccompany_dname);
//				md.setCcompany_pname(ccompany_pname);
//				md.setCcompany_cname(ccompany_cname);
//				md.setCcompany_dname(ccompany_dname);
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("银行数据匹配成功");
        bean.setBusiJSON("success");
        logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] 验证办卡地 cardId="+bankcardid+" cityId="+hcityid);

    }
    public void  defaultCase(MaterialBean bean){
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc("该银行办卡暂不支持");
        bean.setBusiJSON("fail");
    }
}
