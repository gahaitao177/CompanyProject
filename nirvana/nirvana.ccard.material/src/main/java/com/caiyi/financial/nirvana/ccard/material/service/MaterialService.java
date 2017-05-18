package com.caiyi.financial.nirvana.ccard.material.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.mapper.MaterialMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/13.
 */
@Service
public class MaterialService extends AbstractService {
    @Autowired
    MaterialMapper materialMapper;

    public Map<String, Object> filterCard(MaterialBean bean){
        Page<Map<String, Object>> mapList=null;

        Map<String, Object> result=new HashMap<>();

        Integer isnormal=-1;
        // dele by lcs 20161206  删除冗余代码
//        try {
//            isnormal=materialMapper.queryBankIsOpen();
//        }catch (Exception e){
//            logger.info("查询tb_apply_credit_material_p总开关异常:{}",e.toString());
//            return  null;
//        }
        MaterialModel model = bean.getModel();
        Map<String, String> where = new HashMap<String, String>();
        where.put("citysql", "1=1");
        where.put("banksql", "and 1=1");
        where.put("usesql", "1=1");
        where.put("icooperation", "0");//大于0合作银行
        where .put("isnormal", " and 1=1 ");
        where .put("totalsearch", "1=1 ");

        int ltype = bean.getLtype();//0 筛选列表，1匹配卡类别，2成功失败等推荐列表
        if(ltype==0){
            where.put("icooperation", "-1");
        }

        if (ltype==1){
            // dele by lcs 20161206  删除冗余代码
//            if (isnormal==0){ //关闭后只查询合作银行icooperation=9
//                where.put("icooperation", "8");
//            }

            if (!StringUtils.isEmpty(bean.getIshot())){
                if (!"1".equals(bean.getIshot())){
                    bean.setIshot("0");
                }
            }
            if (1==isnormal){//开启后只查询可以模拟办卡
                //ltype=1，只查询能模拟申卡的卡。
                where.put("isnormal"," and t1.isnormal = 1 and t1.cardnormal = 1");
            }
        }else{
            bean.setIshot(null);
        }

        //add by lcs
        if (MaterialModel.isNotNull(bean.getCname() )){
            String sqlWheere = " (ccardname like '%" +  bean.getCname() + "%' or ccardname like  '%" + bean.getCname() + "%'  or ctag like '%"+ bean.getCname()   +"%')";
            where.put("totalsearch", sqlWheere);
        }

        if (MaterialModel.isInteger(model.getIcityid())) {
            where.put("citysql", "iareaid=" + model.getIcityid());
        }
        String theme = model.getCtheme();
        if (MaterialModel.isNotNull(theme)) {
            String use = " 1=0 ";
            String[] themes = theme.split(",");
            for (String t : themes) {
                if (MaterialModel.isInteger(t)) {
                    use += " or iuseid=" + t;
                }
            }
            where.put("usesql", use);
        }
        String ibankid = model.getIbankid();

        String exibankid = bean.getExibankid();

        if (MaterialModel.isNotNull(ibankid) && MaterialModel.isInteger(ibankid)) {
            where.put("banksql", " and ibankid=" + ibankid);
        }
        logger.info("exibankid:" + exibankid + ",allNormal:" + isnormal);
        //排除已有的银行
        if (MaterialModel.isNotNull(exibankid)&&isnormal!=0) {
            String bs[] = exibankid.split(",");
            String tmpbankid = "";
            for(String i:bs){
                if(MaterialModel.isInteger(i)){
                    tmpbankid+=(" and ibankid !=" + i+" ");
                }
            }
            logger.info("tmpbankid:" + tmpbankid );

            if(!StringUtils.isEmpty(tmpbankid)){
                where.put("banksql", tmpbankid);
            }
        }
        logger.info("where:" + where);
        where.put("ishot",bean.getIshot());
        int pn = bean.getPn();
        pn = pn > 0 ? pn : 1;
        int ps = bean.getPs();
        ps = ps > 0 ? ps : 4;
        try {
            PageHelper.offsetPage(ps*(pn-1), ps);
            mapList=  materialMapper.query_filterCard(where);
        }catch (Exception e){
            mapList=null;
            logger.info("查询合作银行的卡,异常{}",e.toString());
            return null;
        }
        for(Map<String,Object> map1:mapList.getResult()){
            String applyUrl = map1.get("cardaddr")==null?"":map1.get("cardaddr").toString();
            String applyUrlIOS = map1.get("cardaddrios")==null?"":map1.get("cardaddrios").toString();
            //如果配置的有IOS办卡链接，IOS客户端返回IOS办卡链接
                /*if (bean.getSource()>=6000 && !StringUtils.isEmpty(applyUrlIOS)){//IOS客户端
                    applyUrl = applyUrlIOS;
                    obj.put("cardaddr", applyUrl);
                }
                if (applyUrl == null || applyUrl.trim().length() == 0) {
                    int bankId = obj.getIntValue("ibankid");
                    sql = "select thc.cardaddr from tb_handle_credit thc where thc.ibankid = ? ";
                    JdbcRecordSet jsApplyAdr = jcn.executeQuery(sql, new Object[]{bankId});
                    if (jsApplyAdr != null && jsApplyAdr.size() > 0 && jsApplyAdr.first()) {
                        applyUrl = jsApplyAdr.get("cardaddr");
                            obj.put("cardaddr", applyUrl);
                    }
                }*/
            //查询办卡地址
            String cityCode = "";
            if (StringUtils.isEmpty(bean.getHskcityid())) {
                cityCode = bean.getCitycode();
            } else {
                cityCode = bean.getHskcityid();
            }
            // update by lcs 20161020
//            String cardAddr = getApplyUrl(applyUrl, applyUrlIOS, bean.getIclient(), map1.get("ibankid").toString(), cityCode);
            String cardAddr = getApplyUrl(applyUrl, applyUrlIOS, bean.getIclient(), map1.get("ibankid").toString(), bean.getPackagename());
            map1.put("cardaddr", cardAddr);
            map1.remove("cardaddrios");

            //是否支持模拟办卡
            String isnormal0 = map1.get("isnormal")==null?null:map1.get("isnormal").toString();
            String cardnormal = map1.get("cardnormal")==null?null:map1.get("cardnormal").toString();
            if ("1".equals(isnormal0)){
                map1.put("isnormal", cardnormal);
            }
            //add two privilege
            String privileges = null;
            if(map1.get("cprivilege")!=null)
                privileges=map1.get("cprivilege").toString();
            StringBuffer sbpr = new StringBuffer();
            if(!StringUtils.isEmpty(privileges)) {
                getPrivilege(privileges, sbpr);
            }
            map1.put("cprivilege",sbpr.toString());
        }
        double k=filterCard_score(model);
        result.put("cards",mapList.getResult());
        result.put("cardstotal",mapList.getTotal());
        result.put("pageNum",mapList.getPageNum());
        result.put("totalPage",mapList.getPages());
        result.put("pageSize",mapList.getPageSize());
        result.put("score",k);
        result.put("score_tag",filterCard_score_tag(k));
        return  result;
    }
    /**
     * 信用卡附加字段
     */
    public List<Map<String,Object>> field_p(){
        List<Map<String,Object>>  mapList=materialMapper.queryMaterialBank();
        return  mapList;
    }
    /**
     *查询地区的信息
     */
    public  List<Map<String,Object>> queryCreditArea(MaterialBean bean){
        List<Map<String,Object>> maps=null;
        String icityid = bean.getIcityid();
        // update by lcs 20160406 将ibankid设为0
        bean.setIbankid("0");

        String bankid = bean.getIbankid();
        if(!StringUtils.isEmpty(bankid) && !bankid.equals("0")){
            Map<String,String> map=new HashMap<>();
            map.put("ibankid",bankid);
            map.put("itype",bean.getItype()+"");
            map.put("icityid",icityid);
            maps=materialMapper.queryAreaByIpcityIdAndBankid(map);
        }
        if(maps==null || maps.size()==0 || StringUtils.isEmpty(bankid) || bankid.equals("0") ){
            maps=materialMapper.queryAreaByIpcityId(icityid);
        }
        if(maps==null){
            logger.info("查询地区信息失败，icityid={}",icityid);
        }else {
            logger.info("查询地区信息成功，icityid={}",icityid);
        }
        return maps;
    }

    /**
     * 信息保存
     * @param model
     * @return
     */
    public Map<String,String> saveMaterial(MaterialModel model){
        String prePhone=model.getPrecphone();
        Map<String,String> map=new HashMap<>();
        String phone=model.getCphone();

        Integer count=-1;
        if(!StringUtils.isEmpty(prePhone)){
            model.setPrecphone(phone);
            count=materialMapper.queryMaterialByPhone(model.getPrecphone());
            if(count>0){
                map.put("code","-1");
                map.put("desc","用户的资料已存在，phone="+phone);
                logger.info("用户的资料已存在，phone={}",phone);
                return map;
            }
            model.setPrecphone(prePhone);
        }else {
            model.setPrecphone(phone);
        }
        if(count==-1){
            count=materialMapper.queryMaterialByPhone(model.getPrecphone());
        }
        int r=-1;
        boolean mark=false;
       if (count==0){
           r=materialMapper.save_apply_credit_material(model);
        }else {
           mark=true;
           r=materialMapper.update_apply_credit_material(model);
       }
        if(r>0&&mark){
            map.put("code","1");
            map.put("desc","用户的资料更新，phone="+phone);
            map.put("data","资料更新成功");
            logger.info("用户的资料更新，phone={}",phone);
        }else if(r>0&&!mark){
            map.put("code","1");
            map.put("desc","用户的资料保存，phone="+phone);
            map.put("data","资料保存成功");
            logger.info("用户的资料保存，phone={}",phone);
        }else {
            map.put("code","0");
            map.put("desc","用户的资料保存失败，phone="+phone);
            logger.info("用户的资料保存失败，phone={}",phone);
        }
        return  map;
    }

    /**
     * 申卡记录列表
     * @param phone
     * @return
     */
    public List<Map<String,Object>> queryCreditOrder(String phone){
        return materialMapper.queryCreditOrder(phone);
    }

    /**
     * 删除申请记录
     * @param map
     * @return
     */
    public  Map<String,String> deleteApplyCreditLog(Map<String,String> map){
        Map<String,String>  result=new HashMap<>();
        Integer count=materialMapper.queryMaterialCount(map);
        if(count!=1){
            result.put("code","0");
            result.put("desc","参数错误");
            logger.info("参数错误,cphone={}",map.get("cphone"));
            return  result;
        }
        Integer delete=materialMapper.updateMaterialState(map.get("iapplyid"));
        if(delete==1){
            result.put("code","1");
            result.put("desc","更新成功");
            logger.info("更新成功,cphone={}",map.get("cphone"));
        }else if (delete>0){
            result.put("code","1");
            result.put("desc","更新成功了"+delete+"条");
            logger.info("更新成功了"+delete+"条,cphone={}",map.get("cphone"));
        }else {
            result.put("code","0");
            result.put("desc","更新失败");
            logger.info("更新失败,cphone={}",map.get("cphone"));
        }
        return  result;
    }

    /**
     * 发送手机验证码
     * @param data
     * @return
     */
    public  Map<String,String> sendMessage(Map<String,String> data){
        Map<String,String> map=new HashMap<>();
        if(data==null){
            map.put("code","0");
            map.put("desc","参数错误");
            logger.info("参数错误");
            return  map;
        }
        String yzm=CheckUtil.randomNum();
        data.put("yzm",yzm);
        data.put("yzmType","1");
        materialMapper.sendYZM(data);
        map.put("code",data.get("busiErrCode"));
        map.put("desc",data.get("busiErrDesc"));
        logger.info("code={},desc={},cphone={}",data.get("busiErrCode"),data.get("busiErrDesc"),data.get("mobileNo"));
        return  map;
    }

    /**
     * 通过手机验证码找回资料
     * @param checkData 包含手机号 mobileNo  验证码  yzm
     * @return
     */
    public List<Map<String,Object>> findMaterial(Map<String,String> checkData){
        if (checkData==null){
            return null;
        }
        checkData.put("yzmType","1");
        materialMapper.checkYZM(checkData);
        if("1".equals(checkData.get("busiErrCode"))){
            logger.info("手机号:"+ checkData.get("mobileNo") +"验证成功");
        }else {
            logger.info("手机号:"+ checkData.get("mobileNo") +"验证失败");
            return null;
        }
        // add by lcs 20160616 start
        if ("1".equals(checkData.get("onlyCheckSms"))){
            return null;
        }
        List<Map<String,Object>> list=materialMapper.findMaterial(checkData.get("mobileNo"));
        return  list;
    }
    /**
     * 初步计算用户的申卡实力
     * @param model
     * @return
     * edit by lcs 20160407
     */
    private double filterCard_score(MaterialModel model) {
        double count = 0;
        try {
            // 在校学生 分数置为2
            if("4".equals(model.getCstartinfo())){
                return count = 2;
            }
            //1、博士及以上 2、硕士 3、本科 4、大专 5、高中、中专一下
            int degree =5 ;
            if(!StringUtils.isEmpty(model.getIdegree())) {
                degree =Integer.parseInt(model.getIdegree());
            }
            count += ((degree == 1 || degree == 2) ? 2 : (degree == 3 ? 1 : 0.5));
            //1、未婚 2、已婚 3、其它
            int marital = 3;
            if(!StringUtils.isEmpty(model.getMaritalstatus())){
                marital = Integer.parseInt(model.getMaritalstatus());
            }
            count += (marital == 2 ? 1 : 0.5);
            //单位性质：1、机关/事业 2、国有 3、股份制 4、外商独资 5、中外合作企业 6、私营/集体 7、个体
            int nunit = 7;
            if(!StringUtils.isEmpty(model.getInatureofunit())){
                nunit = Integer.parseInt(model.getInatureofunit());
            }
//            count += ((nunit == 2 || nunit == 4) ? 2 : ((nunit == 6 || nunit == 7) ? 0.5 : 1));
            count += ((nunit == 2 || nunit == 4) ? 2 : (( nunit == 7) ? 0.5 : 1));
            //职位：1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级
//            int post = Integer.parseInt(model.getIdepartment());
//            count += (post == 1 ? 0.5 : 1);
            count += 2;
            //任职年数：1、一年以下 2、一年 3、两年 4、三年 5、四年 6、五年及以上
            int tjob = 1;
            if(!StringUtils.isEmpty(model.getItimeinjob())){
                tjob = Integer.parseInt(model.getItimeinjob());
            }
//            count += (tjob == 1 ? 0.5 : (tjob < 4 ? 1 : 2));
            count += (tjob == 1 ? 0.5 :1);
            //住宅类型，1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它
            int rstatus = 5;
            if(!StringUtils.isEmpty(model.getResidencestatus())){
                rstatus = Integer.parseInt(model.getResidencestatus());
            }
            count += (rstatus < 3 ? 2 : 0.5);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("程序异常,{}",e.toString());
        }
        return count;
    }
    /**
     * 根据分数计算标语
     *
     * @param score
     * @return
     */
    private String filterCard_score_tag(double score) {
//        if (score < 2.1) {
//            return "实力很苍白，被拒风险高";
//        }
//        if (score < 4.1) {
//            return "综合评分低，申卡靠运气";
//        }
//        if (score < 6.1) {
//            return "表现刚过关，选卡需谨慎";
//        }
//        if (score < 8.1) {
//            return "优秀潜力股，批卡很靠谱";
//        }
//        if (score < 10.1) {
//            return "战斗力爆表，通过率超高";
//        }
        if (score < 2.1) {
            return "实力很苍白，被拒风险高";
        }
        if (score < 4.6) {
            return "表现刚过关 选卡需谨慎";
        }
        if (score < 7.1) {
            return "优秀潜力股，批卡很靠谱";
        }
        if (score < 10.1) {
            return "优秀潜力股，批卡很靠谱";
        }
        return "";
    }
    private static void getPrivilege(String cprivilege , StringBuffer sb) {
        try {
            JSONObject json =JSONObject.parseObject(cprivilege);
//            Iterator<String> keys = json.keys();
            JSONObject jo = null;
            Object o;
           for (String key:json.keySet()){
                o = json.get(key);
                if (o instanceof JSONObject) {
                    jo = (JSONObject) o;
                    JSONArray titleJsa = jo.getJSONArray("title");
                    String title = (String)titleJsa.get(0);
                    if (CheckUtil.isNullString(title)) {
                        getPrivilege(jo.toString(), sb);
                    } else {
                        title = title.replace("[\"", "").replace("\"]", "");
                        if (!CheckUtil.isNullString(title)) {
                            title = title.replaceAll("\\n","");
                            if (sb.length() == 0 || sb.toString().endsWith("|")){
                                sb.append( title );
                            } else {
                                sb.append( "|" + title );
                                break;
                            }
                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
    /**
     * 获取办卡链接
     * @param applyUrl 某信用卡卡办卡地址
     * @param cardaddrios 某信用卡卡IOS端办卡地址
     * @param iclient 新版本设备类型 0 Android，1 iOS
     * @param bankId 银行ID
     * @param packagename 包名
     * @return
     * created by lcs 20161019
     */
    public  String getApplyUrl(String applyUrl, String cardaddrios, int iclient, String bankId, String packagename){
        String cardApplyUrl = "";
        logger.info("request packageName:" + packagename + ",bankId:" + bankId);
        // 从开卡信息表中获取 相关信息
        Map<String,String> cardAddress = materialMapper.queryBankAddress(bankId);
        if (cardAddress == null){
            return applyUrl;
        }
        //包名
        String pname = cardAddress.get("cpackagename");
        logger.info("例外 pname:" + pname);

        // 如果数据库表中设置了例外包名,包含此请求的包名 则直接返回空
//        if (!CheckUtil.isNullString(pname) && !CheckUtil.isNullString(packagename) && pname.contains(packagename)){
//            return cardApplyUrl;
//        }
        if (!CheckUtil.isNullString(pname) && !CheckUtil.isNullString(packagename)){
            packagename = "," + packagename + ",";
            pname = "," + pname + ",";
            if (pname.contains(packagename)){
                return cardApplyUrl;
            }
        }
        // 如果 cardaddrios不为空 并且是ios
        if (1 == iclient && !CheckUtil.isNullString(cardaddrios)) {
            return cardaddrios;
        }
        if (CheckUtil.isNullString(applyUrl)) {
            String addr = cardAddress.get("cardaddr");
            String addrios = cardAddress.get("cardaddrios");
            if (1 == iclient && !StringUtils.isEmpty(addrios)) {
                cardApplyUrl = addrios;
            }else {
                cardApplyUrl = addr;
            }
        } else {
            cardApplyUrl = applyUrl;
        }
        return cardApplyUrl;
    }
//    private String getApplyUrl(String applyUrl, String cardaddrios, int iclient, String bankId,String citycode) {
//        boolean canyybk = false;
//        String sql = "";
//        //查询是否支持预约办卡
//        if (!StringUtils.isEmpty(bankId)&&!StringUtils.isEmpty(citycode)){
//            List<String> listCode=materialMapper.queryBandCodeByCityId(citycode);
//            // add by lcs 20160603 排除空指针
//            if (listCode.size()>0){
//                for (String code :listCode){
//                    if (bankId.equals(code)){
//                        canyybk = true;
//                        break;
//                    }
//                }
//            }
//        }
//
//        //如果某卡的办卡地址为空，去查询银行通用办卡地址
//        if(StringUtils.isEmpty(applyUrl)){
//            Map<String,String> cardAddress=materialMapper.queryBankAddress(bankId);
//            if(cardAddress!=null) {
//                String addr = cardAddress.get("cardaddr");
//                String addrios = cardAddress.get("cardaddrios");
//                if (1 == iclient && !StringUtils.isEmpty(addrios)) {
//                    applyUrl = addrios;
//                } else {
//                    applyUrl = addr;
//                }
//            }
//        }
//        //新版的citycode不为空。
//        //信贷后台开关控制此办卡链接，现在不用了。
//		/*if (!StringUtils.isEmpty(citycode) && !canyybk && applyUrl.contains("huishuaka")){
//			applyUrl = "";
//		}*/
//        return applyUrl;
//    }


    public Map<String, String> totalSearch(MaterialBean bean){
        Page<Map<String, Object>> mapList=null;
        System.out.println("MaterialService totalSearch");
        Map<String, String> result=new HashMap<>();

        Integer isnormal=-1;
        try {
            isnormal=materialMapper.queryBankIsOpen();
        }catch (Exception e){
            logger.info("查询tb_apply_credit_material_p总开关异常:{}",e.toString());
            return  null;
        }
        MaterialModel model = bean.getModel();
        Map<String, String> where = new HashMap<String, String>();
        where.put("citysql", "1=1");
        where.put("banksql", "and 1=1");
        where.put("usesql", "1=1");
        where.put("icooperation", "0");//大于0合作银行
        where.put("isnormal", " and 1=1 ");
        String sqlWheere = " (tbc.ccardname like '%" +  bean.getCname() + "%' or tbc.ccardname like  '%" + bean.getCname() + "%'  or tbc.ctag like '%"+ bean.getCname()   +"%')";

        where.put("totalsearch",sqlWheere);

        int ltype = bean.getLtype();//0 筛选列表，1匹配卡类别，2成功失败等推荐列表
        if(ltype==0){
            where.put("icooperation", "-1");
        }

        if (ltype==1){
            if (isnormal==0){ //关闭后只查询合作银行icooperation=9
                where.put("icooperation", "8");
            }

            if (!StringUtils.isEmpty(bean.getIshot())){
                if (!"1".equals(bean.getIshot())){
                    bean.setIshot("0");
                }
            }
            if (1==isnormal){//开启后只查询可以模拟办卡
                //ltype=1，只查询能模拟申卡的卡。
                where.put("isnormal"," and t1.isnormal = 1 and t1.cardnormal = 1");
            }
        }else{
            bean.setIshot(null);
        }

        if (MaterialModel.isInteger(model.getIcityid())) {
            where.put("citysql", "iareaid=" + model.getIcityid());
        }
        String theme = model.getCtheme();
        String ibankid = model.getIbankid();

        String exibankid = bean.getExibankid();

        if (MaterialModel.isNotNull(ibankid) && MaterialModel.isInteger(ibankid)) {
            where.put("banksql", " and ibankid=" + ibankid);
        }
        logger.info("exibankid:" + exibankid + ",allNormal:" + isnormal);
        //排除已有的银行
        if (MaterialModel.isNotNull(exibankid)&&isnormal!=0) {
            String bs[] = exibankid.split(",");
            String tmpbankid = "";
            for(String i:bs){
                if(MaterialModel.isInteger(i)){
                    tmpbankid+=(" and ibankid !=" + i+" ");
                }
            }
            logger.info("tmpbankid:" + tmpbankid );

            if(!StringUtils.isEmpty(tmpbankid)){
                where.put("banksql", tmpbankid);
            }
        }
        logger.info("where:" + where);
        where.put("ishot",bean.getIshot());
        int pn = bean.getPn();
        pn = pn > 0 ? pn : 1;
        int ps = bean.getPs();
        ps = ps > 0 ? ps : 4;
        PageHelper.offsetPage((pn-1)*ps,ps);
        try {
            mapList=  materialMapper.totalSearch(where);
        }catch (Exception e){
            mapList=null;
            logger.info("查询合作银行的卡,异常{}",e.toString());
            return null;
        }
        for(Map<String,Object> map1:mapList.getResult()){
            String applyUrl = map1.get("cardaddr")==null?"":map1.get("cardaddr").toString();
            String applyUrlIOS = map1.get("cardaddrios")==null?"":map1.get("cardaddrios").toString();
            //查询办卡地址
            String cityCode = "";
            if (StringUtils.isEmpty(bean.getHskcityid())) {
                cityCode = bean.getCitycode();
            } else {
                cityCode = bean.getHskcityid();
            }
            // update by lcs 20161020
//            String cardAddr = getApplyUrl(applyUrl, applyUrlIOS, bean.getIclient(), map1.get("ibankid").toString(), cityCode);
            String cardAddr = getApplyUrl(applyUrl, applyUrlIOS, bean.getIclient(), map1.get("ibankid").toString(), bean.getPackagename());

            map1.put("cardaddr", cardAddr);
            map1.remove("cardaddrios");
            //是否支持模拟办卡
            String isnormal0 = map1.get("isnormal")==null?null:map1.get("isnormal").toString();
            String cardnormal = map1.get("cardnormal")==null?null:map1.get("cardnormal").toString();
            if ("1".equals(isnormal0)){
                map1.put("isnormal", cardnormal);
            }
            //add two privilege
            String privileges = null;
            if(map1.get("cprivilege")!=null)
                privileges=map1.get("cprivilege").toString();
            StringBuffer sbpr = new StringBuffer();
            if(!StringUtils.isEmpty(privileges)) {
                getPrivilege(privileges, sbpr);
            }
            map1.put("cprivilege",sbpr.toString());
        }
        double k=filterCard_score(model);
        result.put("cards",JSONArray.toJSONString(mapList.getResult()));
        result.put("cardstotal",mapList.getTotal()+"");
        result.put("pageNum",mapList.getPageNum()+"");
        result.put("totalPage",mapList.getPages()+"");
        result.put("pageSize",mapList.getPageSize()+"");
        result.put("score",k+"");
        result.put("score_tag",filterCard_score_tag( k));
        return  result;
    }

}
