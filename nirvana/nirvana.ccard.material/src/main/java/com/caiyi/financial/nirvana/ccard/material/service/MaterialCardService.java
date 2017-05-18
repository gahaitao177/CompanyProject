package com.caiyi.financial.nirvana.ccard.material.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.mapper.MaterialCardMapper;
import com.caiyi.financial.nirvana.ccard.material.mapper.MaterialMapper;
import com.caiyi.financial.nirvana.ccard.material.util.Constant;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.danga.MemCached.MemCachedClient;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by lizhijie on 2016/7/26.
 * 模拟办卡的办卡服务
 */
@Service
public class MaterialCardService extends AbstractService {

    @Autowired
    MaterialCardMapper materialCardMapper;
    @Autowired
    MaterialMapper materialMapper;
    @Autowired
    MemCachedClient client;

   public MaterialBean applyCreditConversion(MaterialBean bean){
       MaterialModel md = bean.getModel();
        if(md==null){
            logger.info("参数错误");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("无效的请求");
            bean.setBusiJSON("fail");
            return bean;
        }else {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("对提交信息进行检测");
            bean.setBusiJSON("success");
        }
       ConvertBankData convert=new ConvertBankData(client,materialCardMapper);
       switch (bean.getIbankid()){
           case "16": //交通银行
               convert.jiaoTongBank(bean);
               break;
           case "7"://平安银行
               convert.pingAn(bean);
               break;
           case "3"://光大银行
               convert.guangDa(bean);
               break;
           case "1"://广发银行
               convert.guangFa(bean);
               break;
           case "10"://兴业银行
               convert.xingYe(bean);
               break;
           case "11"://民生银行
               convert.minSheng(bean);
               break;
           case "2"://中信银行
               convert.zhongXin(bean);
               break;
           default:
               convert.defaultCase(bean);
               break;
       }
       logger.info("md.getCphone=" + md.getCphone());
       logger.info("bean.getApplyBankCardId=" + bean.getApplyBankCardId());
       logger.info("bean.getApplyBankCardLevel=" + bean.getApplyBankCardLevel());
       return  bean;
   }
    public Map<String,String> saveOrUpdateMaterialDirty(MaterialBean bean){
        Map<String,String> map=new HashMap<>();
        if (bean==null|| bean.getModel()==null){
            map.put("code","0");
            map.put("desc","参数错误");
            return map;
        }
        MaterialModel md=bean.getModel();
        String phone=md.getCtmpphone();
        if(StringUtils.isEmpty(phone)){
            map.put("code","0");
            map.put("desc","手机号不能为空");
            return map;
        }
        Integer count =materialCardMapper.findCreditMaterialDirty(phone);
        int result=-1;
        if(count==1){
            result=materialCardMapper.update_apply_credit_material_dirty(md);
        }else{
            result=materialCardMapper.save_apply_credit_material_dirty(md);
        }
        logger.info("保存了"+result+"条数据");
        if(result>0){
            map.put("code","1");
            map.put("desc","成功保存或者更新了"+result+"条数据");
            return map;
        }else{
            map.put("code","1");
            map.put("desc","没有数据保存成功");
            return map;
        }
    }
    public MaterialBean applyCreditCard(MaterialBean bean){
        saveOrUpdateMaterial(bean);
//        bean.setBusiErrCode(1);
        if (1==bean.getBusiErrCode()) {
            saveApplyCreditLog(bean);
        }else {
            return  bean;
        }
        if (1==bean.getBusiErrCode()) {
            Map<String,String> map=new HashMap<>();
            map.put("icityid",bean.getIcityid());
            map.put("merchantacctId",bean.getMerchantacctId());
            Object tmp=o2oApplyCity(map).get("data");
            if(tmp!=null) {
                bean.setBusiJSON(tmp.toString());
            }
            return bean;
        }else {
            return  bean;
        }
    }
    public Map<String,String> saveOrUpdateMaterial(MaterialBean bean){
        Map<String,String> map=new HashMap<>();
        if (bean==null|| bean.getModel()==null){
            map.put("code","0");
            map.put("desc","参数错误");
            return map;
        }
        MaterialModel model = bean.getModel();
        String prephone = model.getPrecphone();
        String phone = model.getCphone();
        logger.info("保存更新资料---phone:"+phone+"----prephone:"+prephone);
        Integer count=null;
        if (!MaterialModel.isNotNull(prephone)) {
            //号码没有修改
            model.setPrecphone(phone);
        }else{
            //检查新号码是否已存在
            model.setPrecphone(phone);
            count= materialMapper.queryMaterialByPhone(model.getPrecphone());
            if(count!=null && count>0){
                logger.info("更新资料："+phone+"：已经存在");
                map.put("code","-1");
                map.put("desc","此号码已经存在");
                bean.setBusiErrCode(-1);
                return map ;
            }
            model.setPrecphone(prephone);
        }
        int z = 0;
        if(count==null){
            count=materialMapper.queryMaterialByPhone(model.getPrecphone());
        }
//        JdbcRecordSet jrs = JdbcSqlMapping.executeQuery("find_apply_credit_material", model, null, jcn);
        if (count == null || count == 0) {
//            z = JdbcSqlMapping.executeUpdate("save_apply_credit_material", model, null, jcn);
            z=materialMapper.save_apply_credit_material(model);
        } else {
//            z = JdbcSqlMapping.executeUpdate("update_apply_credit_material", model, null, jcn);
            z=materialMapper.update_apply_credit_material(model);
        }
        if (bean.getBusiErrCode()!=3) {
            map.put("code","1");
            map.put("data","success" + z);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("success" + z);
        }
        return map;
    }
    public Map<String,String> saveApplyCreditLog(MaterialBean bean){
        logger.info("code:" + bean.getBusiErrCode());
        logger.info("desc:" + bean.getBusiErrDesc());
        Map<String,String> result=new HashMap<>();
        if(bean.getBusiErrCode()<0){
            result.put("code","0");
            result.put("desc","程序异常");
            bean.setBusiErrCode(0);
            return result;
        }
        if (bean.getBusiErrCode()==3) {
            bean.setBusiErrCode(0);
        }
        MaterialModel md = bean.getModel();
        if (StringUtils.isEmpty(md.getIbankid())){
            md.setIbankid(bean.getIbankid());
        }
        String imaterialId="";
        try {
            imaterialId=materialCardMapper.queryMaterialIdByPhoneAndId(md.getCphone(),md.getCidcard());
        }catch (Exception e){
            bean.setBusiErrCode(0);
            result.put("code","0");
            result.put("desc","程序异常");
            return result;
        }
        if(StringUtils.isEmpty(imaterialId)){
            logger.info("插入记录失败 ICREDITID=" + md.getCardid() + " IMATERIALID=" + imaterialId +
                    " CPHONE=" + md.getCphone() + " CIDCARD=" + md.getCidcard() + " IBANKID=" + md.getIbankid());
            result.put("code","0");
            result.put("desc","程序异常");
            bean.setBusiErrCode(0);
            return result;
        }
        String iparamtype = "0";
        switch (md.getIbankid()) {
            case "16":
                //交通
                iparamtype = "0";
                break;
            case "3":
                iparamtype = "2";
                break;
            case "1":
                iparamtype = "1";//需要短信验证码
                break;
            case "11":
                iparamtype = "3";//不需要图片验证码，不要手机验证码
                break;
            case "10":
                iparamtype = "3";//不需要图片验证码，不要手机验证码
                break;
            case "2":
                iparamtype = "1";
                break;
            case "7":
                iparamtype = "0";
                break;
            default:
        }
        String birthStr = "1900-01-01";
        // 获取用户的生日
        try {
            String id = md.getCidcard();
            String brith = "";
            Date  brithDate =null ;
            if(id !=null){
                String year = id.substring(6, 10);
                String month = id.substring(10,12);
                String day = id.substring(12,14);
                brith = year+ "-"+ month + "-" + day;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                brithDate = sdf.parse(brith);
                birthStr = sdf.format(brithDate);
            }
        } catch (Exception e) {
//				birthStr="";
        }
        int istatus=0;
        String cresults="审核中";
        if ("2".equals(bean.getCstatus())) {
            istatus=2;
            cresults="不通过预审";
        }
        Integer logid=0;
        try {
            logid=materialCardMapper.queryMaterialId();
        }catch (Exception e){
            logger.info("查询queryMaterialId()方法异常，身份证号={}, 手机号={}",md.getCidcard(),md.getCphone());
            bean.setBusiErrCode(0);
            result.put("code","0");
            result.put("desc","程序异常");
            return result;
        }
        bean.setMerchantacctId(String.valueOf(logid));
        Map<String,String> para=new HashMap<>();
        para.put("iapplyid",logid+"");
        para.put("icreditid",md.getCardid());
        para.put("imaterialid",imaterialId);
        para.put("cphone",md.getCphone());
        para.put("cidcard", md.getCidcard());
        para.put("istatus",istatus+"");
        para.put("cresults",cresults);
        para.put("ibankid",md.getIbankid());
        para.put("iparamtype",iparamtype);
        para.put("cname",md.getCname());
        para.put("brithday",birthStr);
        Integer i=-1;
        try {
            i=materialCardMapper.save_apply_credit_log(para);
        }catch ( Exception e){
            logger.info("保存日志信息错误，身份证号={}, 手机号={},申请卡={}",md.getCidcard(),md.getCphone(),md.getCardid());
            result.put("code","0");
            result.put("desc","保存日志信息错误");
            bean.setBusiErrCode(0);
            return result;
        }
        if(i>0){
            logger.info("保存日志信息成功，身份证号={}, 手机号={},申请卡={}",md.getCidcard(),md.getCphone(),md.getCardid());
            result.put("code","1");
            result.put("desc","保存日志信息成功");
            bean.setBusiErrCode(1);
            return result;
        }else{
            logger.info("保存日志信息失败，身份证号={}, 手机号={},申请卡={}",md.getCidcard(),md.getCphone(),md.getCardid());
            result.put("code","0");
            result.put("desc","保存日志信息失败");
            bean.setBusiErrCode(0);
            return result;
        }
    }
    public Map<String,Object> o2oApplyCity(Map<String,String> map){
        Map<String,Object> result=new HashMap<>();
        logger.info("进入o2oApplyCity..........................");
        logger.info("接收到城市参数getIcityid="+map.get("icityid"));
        if (StringUtils.isEmpty(map.get("icityid"))) {
            result.put("code","0");
            result.put("desc","城市id不能为空");
            return result;
        }
        JSONObject json=new JSONObject();
        List<Map<String,Object>> list=null;
        try {
            list =materialCardMapper.queryO2OCityIsOpen();
        }catch (Exception e){
            logger.info("查询O2O办卡异常",e.toString());
            json.put("o2o","false");
            result.put("data",json);
            return  result;
        }
        if(list!=null&&list.size()>0){
            for(Map<String,Object> objectMap:list){
                String cityCode = objectMap.get("ccode")==null?null:objectMap.get("ccode").toString();
                String ciso2o = objectMap.get("ciso2o")==null?"false":objectMap.get("ciso2o").toString();
                if(map.get("icityid").trim().equals(cityCode.trim())){
                    logger.info("查询到支持O2O办卡城市="+map.get("icityid"));
                    json.put("o2o",ciso2o);
                    break;
                }
            }
        }
        logger.info("返回当前申请纪录数据："  + map.get("merchantacctId"));
        Map<String,Object> record=materialCardMapper.apply_credit_log_by_id(map.get("merchantacctId"));
        if(record!=null&&record.size()>0){
            for(String key:record.keySet()){
                if("capplydate".equals(key)&&record.get(key)!=null){
                    record.put(key,record.get(key).toString().split(" ")[0]);
                }
            }
            String arr = JSON.toJSONString(record).toString();
            logger.info("getBusiJSON="+JSON.parse(arr));
            json.put("apply", JSON.parse(arr));
        }
        result.put("data",json);
        return result;
    }
    /**
     * 根据城市查询支持O2O办卡的银行
     * @param cityId
     * @return
     */
    public List<Map<String,Object>> queryO2OBank(String cityId){
        logger.info("进入queryO2OBank...........cityId={}",cityId);
        List<Map<String,Object>> mapList=materialCardMapper.queryO2OBank(cityId);
        return  mapList;
    }
    /**
     * 根据区查询商业圈
     * @param adcode
     * @return
     */
    public List<Map<String,Object>> queryBusiness(String adcode){
        logger.info("queryBusiness...........adcode={}",adcode);
        List<Map<String,Object>> mapList=materialCardMapper.queryBusiness(adcode);
        if(mapList==null){
            mapList=new ArrayList<>();
            Map map=new HashMap<>();
            map.put("bizId","-1");
            map.put("bizName","全部");
            mapList.add(map);
        }
        return  mapList;
    }
    public Map<String,String> saveO2OMaterial(MaterialBean bean){
        Map<String,String> result=new HashMap<>();
        MaterialModel model=bean.getModel();
        //职业
        Map<String,String> profession = new HashMap<>();
        profession.put("1","0");//1、机关/事业 → 0:事业单位
        profession.put("2","0");//2、国有 → 0:事业单位
        profession.put("3","1");//3、股份制 → 1:白领上班族
        profession.put("4","1");//4、外商独资 → 1:白领上班族
        profession.put("5","1");//5、中外合作企业 → 1:白领上班族
        profession.put("6","1");//6、私营/集体 → 1:白领上班族
        profession.put("7","5");//7、个体 → 5:个体户
        String cprofession = profession.get(model.getInatureofunit());

        //信用卡使用
        String isuse = "";
        if (StringUtils.isEmpty(model.getChavebankcard())){
            isuse = "0";//0:没
        }else{
            isuse = "5";//5持他行信用卡6个月以上
        }

        //社保 0:无,1:3个月以上,2:6个月以上,3:1年以上
        int itimeinjob = Integer.parseInt(model.getItimeinjob());
        String isocialpay = itimeinjob<1?"1":"3";//
        //学历
        Map<String,String> degree = new HashMap<>();
        degree.put("1","1");//1、博士及以上 → 1本科及以上
        degree.put("2","1");//2、硕士 → 1本科及以上
        degree.put("3","1");//3、本科 → 1本科及以上
        degree.put("4","0");//4、大专 → 0专科，1本科及以上，2高中及以下
        degree.put("5","2");//5、高中、中专一下 → 2高中及以下
        String idegree = degree.get(model.getIdegree());

        String icitycode = "";
        String icountycode = "";
        String detailaddress = "";
        if ("1".equals(bean.getIpostaddr())){//单位
            icitycode = model.getIcompany_cid();
            icountycode = model.getIcompany_did();
            detailaddress = model.getCcompany_detailaddress();
        }else{//家庭
            icitycode = model.getIhome_cid();
            icountycode = model.getIhome_did();
            detailaddress = model.getChome_detailaddress();
        }
        //根据code查询名称
        String cityName = "";
        String countyName = "";
        String bizName = "";
//        String sql = "select t.iareaid,t.careaname from tb_area t where t.adcode=? and t.iareatype=1 union all\n" +
//                "select t.iareaid,t.careaname from tb_area t where t.adcode=? and t.iareatype=2 union all\n" +
//                "select t.iareaid,t.careaname from tb_area t where t.iareaid=?";
//        JdbcRecordSet jrs = jcn.executeQuery(sql, new Object[]{icitycode,icountycode,bean.getIbizid()});
        Map<String,String> para=new HashMap<>();
        para.put("icitycode",icitycode);
        para.put("icountycode",icountycode);
        para.put("iareaid",bean.getIbizid());
        List<Map<String,Object>> areaList=materialCardMapper.queryArea(para);
        if(areaList!=null&&areaList.size()>0){
            for(Map<String,Object> tmp:areaList){
                String iareaid = tmp.get("iareaid")==null?"":tmp.get("iareaid").toString();
                String careaname = tmp.get("careaname")==null?"":tmp.get("careaname").toString();
                if (iareaid.length()==3){
                    cityName = careaname;
                }else if (iareaid.length()==5){
                    countyName = careaname;
                }else if (iareaid.length()==7){
                    bizName = careaname;
                }
            }
        }
        String cworkplace = cityName +"-"+countyName+"-"+bizName+"-"+detailaddress;//市-区-商圈-详细地址
        String sexAndBirthday1 = getSexAndBirthday(model.getCidcard());
        String[] sexAndBirthday = sexAndBirthday1.split("\\|");
        String igender = sexAndBirthday[0];
        String birthday = sexAndBirthday[1].substring(0,4);
        int current = new Date().getYear();
        int iage = current-Integer.parseInt(birthday);

        String iworkprove = "0";//默认：0工牌
        //0租房，1自有住房无贷款，2本市按揭房贷，3其它
        String ihouse = "0";
        Map<String,String> house = new HashMap<>();
        house.put("1","2");//1、自购有贷款房 → 2本市按揭房贷
        house.put("2","1");//2、自有无贷款房 → 1自有住房无贷款
        house.put("3","0");//3、租用 → 0租房
        house.put("4","3");//4、与父母同住 → 3其它
        house.put("5","3");//5、其它 → 3其它
        ihouse = house.get(model.getResidencestatus());
        model.getResidencestatus();

        String icar = "0";
        //判断资格
        int isuccess = 0;//0申请成功。1申请失败，资料不符合。2申请失败，不在规定城市内
        String istatus = "0";//0待出售1已出售2已下架
        Map<String, String> aptitude = o2oCondition(isuse, "0", cprofession, isocialpay, ihouse, icar, iworkprove,idegree);
        isuccess = Integer.parseInt(aptitude.get("isuccess"));
        istatus = aptitude.get("istatus");

        //去重,每个手机号每个银行每月最多申请2次
        String[] banks = bean.getCbankids().split(",");//用户申请的银行
        Map<String,String> bankids = new HashMap<>();//去重后的银行
        List<Map<String,Object>> bankList=materialCardMapper.queryBank(model.getCtmpphone());
//        sql = "select t.ibankid,t.isuccess from tb_card_apply t where t.cphonenum=? and t.cadddate>=sysdate-30";
//        jrs = jcn.executeQuery(sql, new Object[]{model.getCtmpphone()});
        int count = 0;
        if (null!=bankList && bankList.size()>0){
           for(Map<String,Object> bank:bankList){
                String bankIdDB = bank.get("ibankid")==null?"":bank.get("ibankid").toString();
                isuccess = bank.get("isuccess")==null?0:Integer.parseInt(bank.get("ibankid").toString());
                if (!StringUtils.isEmpty(bankIdDB)&&isuccess==0){
                    for (String bankId : banks){
                        if (!bankIdDB.equals(bankId)){//已经申请成功的银行30天内不能再次申请
                            bankids.put(bankId,bankId);
                        }
                    }
                }
                if (isuccess!=0){
                    count++;
                }
            }
            if (count>2){//30天内有两次失败，暂时无法申请。
                result.put("code","1001");
                result.put("desc","您暂时无法预约，建议您去银行柜台");
                result.put("data","fail");
                return result;
            }
            if (bankids.size()==0){
                result.put("code","1000");
                result.put("desc","您已申请过该行信用卡，30天内不能再次申请!");
                result.put("data","fail");
                return result;
            }
        }else{
            for (String bankId : banks){
                bankids.put(bankId,bankId);
            }
        }
        int ret = 0;
//        sql = "insert into tb_card_apply (iapplyid,cprofession,isuse,ioverdue,isocialpay,iworkprove,cname,cphonenum,idegree,cworkorg,cworkplace,iage,icardid,ibankid,icitycode,icountycode,cgroupcode,igender,isuccess,istatus,ihouse,icar) values " +
//                "(seq_applyid.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        para.clear();
        para.put("cprofession",cprofession);
        para.put("isuse",isuse);
        para.put("isocialpay",isocialpay);
        para.put("iworkprove",iworkprove);
        para.put("cname",model.getCname());
        para.put("ctmpphone",model.getCtmpphone());
        para.put("idegree",idegree);
        para.put("ccompanyname",model.getCcompanyname());
        para.put("cworkplace",cworkplace);
        para.put("iage",iage+"");
        para.put("cardid",model.getCardid());
        para.put("icitycode",icitycode);
        para.put("icountycode",icountycode);
        para.put("ibizid",bean.getIbizid());
        para.put("igender",igender);
        para.put("isuccess",isuccess+"");
        para.put("istatus",istatus);
        para.put("ihouse",ihouse);
        para.put("icar",icar);
        for (String bankId : bankids.keySet()){
            para.put("bankId",bankId);
//            ret = jcn.executeUpdate(sql, new Object[]{cprofession,isuse,0,isocialpay,iworkprove,model.getCname(),model.getCtmpphone(),idegree,model.getCcompanyname(),
//                    cworkplace,iage,model.getCardid(),bankId,icitycode,icountycode,bean.getIbizid(),igender,isuccess,istatus,ihouse,icar});
            ret = materialCardMapper.saveApplyCard(para);
        }
        if (ret<1){
            result.put("code",ret+"");
            result.put("desc","预约失败");
            result.put("data","fail");
            return result;
        }else {
            if ("1".equals(isuccess)){
                result.put("code","0");
                result.put("desc","预约失败了,您的资料不符合");
                result.put("data","fail");
            }else{
                result.put("code","1");
                result.put("desc","成功预约"+ret+"家银行");
                result.put("data","success");
            }
        }
        return result;
    }

    /**
     * 查询推广银行列表
     * @param para
     * @return
     */
    public List<Map<String,Object>> spreadBank(Map<String,String> para){
        List<Map<String,Object>> list=materialCardMapper.query_spreadBank(para);
        if(list!=null){
            Map<String,Object> tmp=new HashMap<>();
            tmp.put("ibankid",-1);
            tmp.put("cbankname","全部银行");
            list.add(0,tmp);
        }
        return list;
    }
    /**
     * 推广统计
     * @param para
     * @return
     */
    public Map<String,String> spreadCount(Map<String,String> para){
        Map<String,String> map=new HashMap<>();
        if(para==null){
            map.put("code","0");
            map.put("desc","参数错误");
            return map;
        }
        String strPara="";
        Integer zz=-1;
        if(!CheckUtil.isNullString(para.get("ltype"))){
            if("4".equals(para.get("ltype"))||"5".equals(para.get("ltype"))){
                strPara=para.get("ichannelid");
            }else{
                strPara=para.get("ispreadid");
            }
            switch (para.get("ltype")){
                case "1":
                    zz= materialCardMapper.updateCardListCount(strPara);
                    break;
                case "2":
                    zz=  materialCardMapper.updateCardDetailPageCount(strPara);
                    break;
                case "3":
                    zz=  materialCardMapper.updateCardDetailCount(strPara);
                    break;
                case "4":
                    zz= materialCardMapper.updateCardH5Count(strPara);
                    break;
                case "5":
                    zz=  materialCardMapper.updateCardAPPCount(strPara);
                    break;
                default:
                    break;
            }
        }
        if(zz>=0){
            map.put("code","1");
            map.put("desc","更新成功");
        }else{
            map.put("code","0");
            map.put("desc","更新失败");
        }
        return map;
    }

    public  List<Map<String,Object>> spreadCard(Map<String,String> map){
//        Map<String,String> result=new HashMap<>();
        Map<String, String> where = new HashMap<String, String>();
        where.put("citysql", "1=1");
        where.put("banksql", "and 1=1");

        if (MaterialModel.isInteger(map.get("icityid"))) {
            where.put("citysql", "iareaid=" + map.get("icityid"));
        }else{
//            result.put("code","-1");
//            result.put("desc","城市id错误");
//            result.put("data","fail");
            return null;
        }
        String ibankid = map.get("ibankid").trim();

        if (MaterialModel.isNotNull(ibankid) && MaterialModel.isInteger(ibankid)) {
            where.put("banksql", " and t.ibankid =" + ibankid);
        }
        int pn = Integer.parseInt(map.get("pn"));
        pn = pn > 0 ? pn : 1;
        int ps = Integer.parseInt(map.get("ps"));
        ps = ps > 0 ? ps : 4;
        where.put("ichannelid",map.get("ichannelid"));
        PageHelper.offsetPage(ps*(pn-1),ps);
//        JdbcRecordSet jrs = JdbcSqlMapping.executeQuery("query_spreadCard", bean, where, jcn);
        List<Map<String,Object>> mapList=materialCardMapper.query_spreadCard(where);
        for (int i=0;i<mapList.size();i++){
            String cprivilege = "";
            Map<String,Object> tmp = mapList.get(i);
            if(tmp.get("cprivilege")==null){
                continue;
            }
            String str = tmp.get("cprivilege").toString();
            JSONObject json = JSONObject.parseObject(str);
            int size = json.size();
            int j = 0;
            if (size>2){
                size=2;
            }
            for(String key:json.keySet()){
                j++;
                cprivilege += json.getJSONObject(key).getJSONArray("title").get(0);
                if (j==size){
                    break;
                }else {
                    cprivilege += "|";
                }
            }
            tmp.put("cprivilege", cprivilege);
        }
//        JSONObject data = new JSONObject();
//        result.put("cards",JSON.toJSONString(mapList));
//        data.put("pn", pn);
//        data.put("tp", tp);
//        data.put("ps", ps);
//        data.put("cardstotal", count);
//        bean.setBusiJSON(data.toJSONString());
        return mapList;
    }
    /**
     * 根据身份证获取性别和出生日期
     * @param idCard
     * @return 性别(M/F)|生日yyyyMMdd
     */
    public  String getSexAndBirthday(String idCard){
        String birthday = "";
        String sex = "";

        int idCard_length = idCard.length();
        if(idCard_length==15){
            birthday = idCard.substring(6,12);
            int year = Integer.parseInt(birthday.substring(0, 2));
            if(year>13){
                birthday = "19"+birthday;
            }else{
                birthday = "20"+birthday;
            }
            sex = idCard.substring(14,15);
            if(sex.equals("1")||sex.equals("3")||sex.equals("5")||sex.equals("7")||sex.equals("9")){
                sex = "1";
            }else{
                sex = "0";
            }
        }else{
            birthday = idCard.substring(6,14);
            sex = idCard.substring(16,17);
            if(sex.equals("1")||sex.equals("3")||sex.equals("5")||sex.equals("7")||sex.equals("9")){
                sex = "1";
            }else{
                sex = "0";
            }
        }
        return sex+"|"+birthday;
    }
    /**
     * 判断是否符合o2o办卡资质
     * @param use
     * @param overdue
     * @param profession
     * @param socialpay
     * @param ihouse
     * @param icar
     * @param workprove
     * @param idegree
     */
    private  Map<String,String> o2oCondition(String use, String overdue, String profession, String socialpay, String ihouse, String icar, String workprove, String idegree) {
        Map<String,String> result = new HashMap<>();
        result.put("isuccess","0");//0申请成功。1申请失败，资料不符合。2申请失败，不在规定城市内
        result.put("istatus","0");//0待出售1已出售2已下架

        if (StringUtils.isEmpty(use)||StringUtils.isEmpty(overdue)||StringUtils.isEmpty(profession)||StringUtils.isEmpty(socialpay)||StringUtils.isEmpty(ihouse)||StringUtils.isEmpty(icar)||StringUtils.isEmpty(workprove)||StringUtils.isEmpty(idegree)){
            result.put("busiErrCode","-1");
            result.put("busiErrDesc","请选择您的资料!");
            return result;
        }

        //1）有本行信用卡,一票否决
        if ("1".equals(use)){
            result.put("isuccess","1");//1申请失败，资料不符合
            result.put("istatus","2");//2下架
            return result;
        }
        //2）逾期3次或3个月以上,一票否决
        if("2356".contains(overdue)){
            result.put("isuccess","1");//1申请失败，资料不符合
            result.put("istatus","2");//2下架
            return result;
        }
        //3）职业为美容美发、保安保洁、小型餐饮、房产经纪,一票否决
        if ("2346".contains(profession)){
            result.put("isuccess","1");//1申请失败，资料不符合
            result.put("istatus","2");//2下架
            return result;
        }
//			第二关：
//			第一关通过后，满足以下任意一条可申请成功：
        if ("0".equals(result.get("isuccess"))){
            if ("5".equals(use)){//1）有他行卡6个月以上；

            }else if ("123".contains(socialpay)){//2）有本市社保3个月以上；

            }else if ("12".contains(ihouse)){//3）自有住房无贷款或有本市按揭房贷；

            }else if ("1".equals(icar)){//4）有本市牌照5年以内汽车；

            }else if ("017".contains(profession)&&"01".contains(idegree)){//5）事业单位或白领或其它+大专或本科以上学历；

            }else if ("017".contains(profession)&&"4".equals(use)){//6）事业单位或白领或其它+持他行信用卡6个月以内；

            }else if ("017".contains(profession)&&"6".equals(workprove)){//7）事业单位或白领或其它+银行流水/税单。

            }else{
                result.put("isuccess","1");//1申请失败，资料不符合
                result.put("istatus","2");//2下架
            }
        }
        return result;
    }
    public  MaterialBean applyCreditQueryConversion(MaterialBean bean){
        if(StringUtils.isEmpty(bean.getOrderid())){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请id不能为空");
            bean.setBusiJSON("fail");
            return bean;
        }
        String ibankid = "";
        String idcardid = "";
        String cname = "";
        Map<String,String> materialLog=materialCardMapper.queryMaterialLogByApplyId(bean.getOrderid());
        if(materialLog!=null&&materialLog.size()>0){
            ibankid=materialLog.get("ibankid");
            idcardid=materialLog.get("idcardid");
            cname=materialLog.get("cname");

            bean.setIbankid(ibankid);
            bean.setIdcardid(idcardid);
            bean.setCname(cname);
        }else{
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("没有查到申请记录");
            bean.setBusiJSON("fail");
            return bean;
        }
        if("7".equals(bean.getIbankid())){
            bean.setBusiErrCode(1);
            String cardKey = bean.getIdcardid() + "_schedule_pingAn_cookieStore_cardType";
            if(client.get(cardKey)!=null) {
                logger.info("apply_credit_query_conversion 平安银行申卡进度查询转换数据已保存到MemCache:" + client.get(cardKey));
            }else {
                //申请卡号
                logger.info("查询申卡记录参数,申卡银行id=="+bean.getIbankid()+";申卡记录卡id=="+bean.getCardid()+";记录id=="+bean.getOrderid());
                //申卡时间
                Map<String,Object> date=materialCardMapper.queryApplyCreditDate(bean.getOrderid());
                String capplydate = "";
                String cardid = "";
                if(date!=null){
                    capplydate = date.get("capplydate")==null?null:date.get("capplydate").toString();
                    cardid = date.get("icreditid")==null?null:date.get("icreditid").toString();
                    date.clear();
                }else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("申卡记录未匹配");
                    bean.setBusiJSON("fail");
                    return bean;
                }
                if(StringUtils.isEmpty(cardid)){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("申卡记录未匹配");
                    bean.setBusiJSON("fail");
                    logger.info("icreditid=="+cardid+";申卡记录参数错误");
                    return bean;
                }
                StringBuilder bank_card_msg = new StringBuilder("");
                Map<String,String>  cardMap=materialCardMapper.queryBankCardId(bean.getIbankid(),cardid);
                if(cardMap!=null){
                    bank_card_msg.append(cardMap.get("cbankcardid"));
                    cardMap.clear();
                }else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("申卡记录未匹配");
                    bean.setBusiJSON("fail");
                    return bean;
                }
                bank_card_msg.append("@");
                bank_card_msg.append(capplydate);
                client.set(cardKey, bank_card_msg.toString(), Constant.TIME_HOUR);
            }
        }else if("2".equals(bean.getIbankid())) {//中信银行
            //查询信用卡名
            Map<String,Object> card=materialCardMapper.queryCreditName(bean.getOrderid());
            if(card!=null&&card.get("ccardname")!=null){
                bean.setCname(card.get("ccardname").toString());
            }
        }else if ("16".equals(bean.getIbankid())) {
            Map<String,Object> card=materialCardMapper.queryCreditName(bean.getOrderid());
            if(card!=null&&card.get("cothername")!=null){
                bean.setApplyBankCardId(card.get("cothername").toString());
            }
        }
        return  bean;
    }
    public Integer updateApplyCreditLog(MaterialBean bean){
        Integer rt = 0;
        Map<String,String> map=new HashMap<>();
        map.put("istatus",bean.getCstatus());
        map.put("cresults",bean.getBusiErrDesc());
        map.put("iapplyid",bean.getOrderid());
        rt=materialCardMapper.updateApplyLog(map);
//        String sql = "update tb_apply_credit_log set istatus=?,cresults=? where iapplyid=?";
//        rt = jcn.executeUpdate(sql, new Object[]{bean.getCstatus(), bean.getBusiErrDesc(), bean.getOrderid()});
        if (rt == 1) {
            logger.info("更新查询记录成功 istate=" + bean.getCstatus() + " cresults=" + bean.getBusiErrDesc() + " iapplyid=" + bean.getOrderid());
        } else {
            logger.info("更新查询记录失败 istate=" + bean.getCstatus() + " cresults=" + bean.getBusiErrDesc() + " iapplyid=" + bean.getOrderid());
        }
        return rt;
    }
}
