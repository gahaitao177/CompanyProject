package com.caiyi.financial.nirvana.discount.ccard.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.bean.Commodity;
import com.caiyi.financial.nirvana.discount.ccard.dto.BankCommodityDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.CommodityDto;
import com.caiyi.financial.nirvana.discount.ccard.mapper.CommodityMapper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by A-0106 on 2016/5/31.
 */
@Service
public class CommodityService extends AbstractService {
    @Autowired
    CommodityMapper commodityMapper;
    //建行的  招行 广发的 是有的 平安 中信 兴业 没有
    private static int[] DefBank=new int[]{21,13,1,2,10,7};//未关注为导卡的用户显示积分的银行
    public List<CommodityDto> queryPointsByUser(String userId){
       return commodityMapper.queryPointsByUserId(userId);
    }
    public  List<String> fetchComm(String cuserId,int len,String ibankids,List<CommodityDto> commodityDtoList){
        Commodity bean = new Commodity();
        bean.setCuserId(cuserId);
        Set<Integer> ibanks = new TreeSet<Integer>();
        if(!StringUtils.isEmpty(ibankids)){
            String[] arr = ibankids.split("#");
            for(String a:arr){
                if(!StringUtils.isEmpty(a)){
                    ibanks.add(Integer.parseInt(a));
                }
            }
        }
        Map<Integer, Double> ubankjf = userjf(cuserId);
        Map<Integer, List<String>> result = new TreeMap<>();
        if (ubankjf != null) {
            // 查询和用户积分相近的商品
            Set<Map.Entry<Integer, Double>> set = ubankjf.entrySet();
            int index=0;
            for (Map.Entry<Integer, Double> s : set) {
                int key = s.getKey();
                double val = s.getValue();
                ibanks.add(key);
                if (val > 0&&index<6) {// 小于他的从大到小排序
                    // 设置参数
                    bean.setCminscore(val + "");
                    bean.setIbankid(key+"");
                    List<BankCommodityDto> bankCommodityDtoList  = commodityMapper.queryBankCommodity(key+"",val+"",len);
                    for(BankCommodityDto bankCommodityDto:bankCommodityDtoList){
                        String cminscore =bankCommodityDto.getCminscore();
                        if (!StringUtils.isEmpty(cminscore)) {
                            int v = (int) Double.parseDouble(cminscore);
                            int x = (int) (val - v);
                            List<String> ljf = result.get(x);
                            if (ljf == null) {
                                ljf = new ArrayList<String>();
                            }
                            ljf.add(commodity2str(bankCommodityDto, ubankjf));
                            result.put(x, ljf);
                            index++;
                        }
                    }
                }
            }
        } else {
            for (CommodityDto commodityDto:commodityDtoList ) {
                ibanks.add(commodityDto.getIbankid());
            }
        }
        List<String> lastResult = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> re : result.entrySet()) {
            lastResult.addAll(re.getValue());
        }
        int ihave = len - lastResult.size();
        if(ihave<0){
            //Collections.shuffle(lastResult);
        }
        if (ihave > 0) {
            Map<String, String> parmm = new HashMap<String, String>();
            String order = " cminscore,to_number(regexp_replace(nvl(coriginprice,'0'), '[^0-9.]')),cmaxscore"; //order by
            String sqlwhere = "";
            if (ibanks.size() > 0) {
                sqlwhere += "and ( 1=0";
                for (Integer bk : ibanks) {
                    sqlwhere += " or ibankid=" + bk;
                }
                sqlwhere += ")";
            }
//            parmm.put("order", order);
            if(sqlwhere.equals("")){
                //没有导入卡，没有关注银行
                sqlwhere += "and (1=0";
                for(int i:DefBank){
                    sqlwhere += " or icommid=(select icommid from (select * from  tb_bank_jf_commodity where ibankid="+i+"order by "+order+" ) where rownum=1 ) ";
                }
                sqlwhere += ")";
            }
            parmm.put("sqlwhere", sqlwhere);
            PageHelper.offsetPage(0,ihave,order);
            List<BankCommodityDto> bankCommodityDtoList2=commodityMapper.query_index_commodity(parmm);
            for(BankCommodityDto bankCommodity:bankCommodityDtoList2) {
                lastResult.add(commodity2str(bankCommodity, ubankjf));
            }
        }
        return lastResult;
    }
    private Map<Integer,Double> userjf(String userId){
        Map<Integer, Double> jfs = new HashMap<Integer, Double>();
        List<CommodityDto> commodityDtoList=commodityMapper.queryPointsByUserId(userId);
        for (CommodityDto commodityDto:commodityDtoList){
            int ibankid = commodityDto.getIbankid();
            Double ipoint =0.00D;
            try {
                ipoint =Double.parseDouble(commodityDto.getIpoint()) ;
            }catch (Exception e){
                ipoint =0.00D;
            }
            Double ot = jfs.get(ibankid);
            if (ot == null || ot < ipoint) {
                jfs.put(ibankid, ipoint);
            }
        }
        return jfs;
    }
    private  String commodity2str(BankCommodityDto bankCommodityDto,Map<Integer,Double> map){
        JSONObject json=new JSONObject();
        Element fontaitem=new DOMElement("font");
        int ibankid = bankCommodityDto.getIbankid();
        Double c = map.get(ibankid);// 现有积分
        String cminscore = bankCommodityDto.getCminscore();
        cminscore = StringUtils.isEmpty(cminscore)?"0":cminscore;
        Double price = Double.parseDouble(cminscore);// 需要积分
        Double required = 0d;
        if (c != null && price != null && c != 0 && price != 0 && price > c) {
            // 相差多少
            required = (price - c);
        }
        json.put("icommid", bankCommodityDto.getIcommid() + "");
        json.put("clistimg", bankCommodityDto.getClistimg());
        json.put("required", required + "");
        json.put("cfetchurl", bankCommodityDto.getCfetchurl());
        json.put("ctitle", bankCommodityDto.getCtitle());
        json.put("cname", bankCommodityDto.getCname());
        json.put("ibankid", ibankid + "");
        json.put("cbankname", bankCommodityDto.getCbankname());
        json.put("coriginprice", bankCommodityDto.getCoriginprice());

        String ccash1 = bankCommodityDto.getCcash1();
        String ccash2 = bankCommodityDto.getCcash2();

        String[] ctype = ccash1.split("\\|");
        // 交换规则1
        JSONArray exchangetypeitemList=new JSONArray();
        for (String cty : ctype) {
            if (!StringUtils.isEmpty(cty)) {
                JSONObject ccashJson=new JSONObject();
                ccashJson.put("ccash",cty);
                exchangetypeitemList.add(ccashJson);
            }
        }
        // 交换规则2
        if (!StringUtils.isEmpty(ccash2)) {
//            ccashJson.put("ccash",ccash2.split("\\|")[0]);
        }
        json.put("exchangetypeitem",exchangetypeitemList);
        return json.toString();
    }
    public List<CommodityDto> queryPointsAndBanks(){
        logger.info("查询银行积分");
       return commodityMapper.queryPointsAndBanks();
    }
    public Map<String,Object>  queryPointsList(Commodity commodity){
        Map<String,Object> resultData=new HashMap<>();
        if (commodity.getPs() <= 0) {
            commodity.setPs(5);
        }
        if(commodity.getPn()<=0){
            commodity.setPn(1);
        }
        int tp = commodity.getTp();
        if (tp == 0) {
            int count = 0;
            long s = System.currentTimeMillis();
            count = commodityMapper.count_jf_commodity(commodity.getIbankid(),commodity.getCcategory());
            long e = System.currentTimeMillis();
            logger.info("jf_commodity_count耗时>" + (e - s));
            commodity.setRc(count);
            if (count % commodity.getPs() == 0) {
                commodity.setTp(count / commodity.getPs());
            } else {
                commodity.setTp(count / commodity.getPs() + 1);
            }
        }
        long s = System.currentTimeMillis();
        int start=commodity.getPs()*(commodity.getPn()-1);
        if (start<0)
            start=0;
        Map<String,String> map=new HashMap<>();
        map.put("ibankid",commodity.getIbankid());
        map.put("ccategory",commodity.getCcategory());
        map.put("start",start+"");
        map.put("size1",commodity.getPs()+"");
        List<BankCommodityDto> commodityDtoList = commodityMapper.query_jf_commodity(map);
        long e = System.currentTimeMillis();
        logger.info(" jf_commodity_query耗时>" + (e - s));
        Map<Integer, Double> bankjfs = userjf(commodity.getCuserId());

        List<String> list=new ArrayList<>();
        for (BankCommodityDto commodityDto:commodityDtoList){
                // 定制解析商品
            list.add(commodity2str(commodityDto,bankjfs));
        }
        logger.info("查询到的积分记录数量:"+list.size());
        resultData.put("data",list);
        resultData.put("tp",commodity.getTp());
        resultData.put("ps",commodity.getPs());
        resultData.put("pn",commodity.getPn());
        resultData.put("rc",commodity.getRc());
        return resultData;
    }
    public JSONObject queryCommodityDetail(String icommid){
        BankCommodityDto bankCommodityDto=commodityMapper.queryCommodityDetail(icommid);
        if(bankCommodityDto!=null){
            JSONObject json=new JSONObject();
            String exchangeurl = bankCommodityDto.getCfetchurl();
            String cname = bankCommodityDto.getCname();
            String ctitle = bankCommodityDto.getCtitle();
            String money = bankCommodityDto.getCoriginprice();
            String points = bankCommodityDto.getCcash1();
            String cdetailimg = bankCommodityDto.getCdetailimg();
            String cattr = "";
            String exchangetype = bankCommodityDto.getCcash2();
            if(StringUtils.isNotEmpty(bankCommodityDto.getCattr())){
                cattr = bankCommodityDto.getCattr().replaceAll("[?？]","");
            }
            json.put("cfetchurl", exchangeurl);
            json.put("cname", cname);
            json.put("ctitle", ctitle);
            if(StringUtils.isNotEmpty(money)) {
                json.put("money", "￥" + money);
            }else{
                json.put("money", "");
            }
            json.put("points", points);
            json.put("exchangetype", exchangetype);
            json.put("commoditydesc", cattr);
            json.put("commoditypic", cdetailimg);
            json.put("bankname",bankCommodityDto.getCbankname());

            List<String> urlList=commodityMapper.queryCommodityImgUrl(icommid);
            JSONObject jsonUrl=new JSONObject();
            int index=1;
            for (String url:urlList){
                index++;
                jsonUrl.put("detailpic"+index,url);
            }
            json.put("detailpic", jsonUrl);
            logger.info("积分详情查询成功:"+icommid);
            return json;
        }else {
            logger.info("积分详情查询失败:"+icommid);
            return null;
        }

    }
}
