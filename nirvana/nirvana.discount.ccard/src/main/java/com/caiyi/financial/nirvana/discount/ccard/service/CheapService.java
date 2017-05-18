package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.ccard.dto.*;
import com.caiyi.financial.nirvana.discount.ccard.mapper.CheapMapper;
import com.caiyi.financial.nirvana.discount.util.DistanceUtil;
import com.caiyi.financial.nirvana.discount.util.LuceneUtil;
import com.caiyi.financial.nirvana.discount.util.MyModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.QueryParser;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by heshaohua on 2016/5/3.
 */
@Service
public class CheapService extends AbstractService {
    private static final int LIMITCOUNT = 100;
	@Autowired
    CheapMapper cheapMapper;

    public List<AreaDto> query_area(Cheap cheap){
        List<AreaDto> areaList = null;
        int ipareaid = cheap.getIpareaid() == null ? 0:cheap.getIpareaid();
        String citycode = cheap.getCitycode();

        cheap.setIpareaid(ipareaid);
        cheap.setCitycode(citycode);
        if (!StringUtils.isEmpty(cheap.getCitycode())&&"loan".equals(cheap.getCitycode())){
            areaList = cheapMapper.query_area2(cheap.getIpareaid(),cheap.getBankid().intValue());
        }else if(ipareaid == 0){//查城市
            areaList = cheapMapper.query_area3(cheap.getIpareaid());
        }else if(String.valueOf(ipareaid).length() <4 ){//查区及商圈级联
            areaList = cheapMapper.query_area(cheap.getIpareaid());
            List<AreaDto> resultList = new ArrayList<AreaDto>();

            Iterator<AreaDto> iter = areaList.iterator();
            while(iter.hasNext()){
                AreaDto areDto = iter.next();
                if(Integer.parseInt(areDto.getIareatype()) == 2) {
                    resultList.add(areDto);
                }
            }

            Iterator<AreaDto> iter2 = resultList.iterator();
            while(iter2.hasNext()){
                AreaDto areDto = iter2.next();

                List<AreaDto> aList = new ArrayList<AreaDto>();
                for(AreaDto area : areaList){
                    if(area.getIpareaid().equals(areDto.getIareaid())){
                        aList.add(area);
                    }
                }

                if(aList != null && aList.size() > 0){
                    areDto.setChild(aList);
                    aList = null;
                }
            }
            areaList.clear();
            areaList.addAll(0,resultList);
        }else{//通过区查商圈或无数据
            areaList = cheapMapper.query_area3(cheap.getIpareaid());
        }
        return areaList;
    }
    public WindowDto qpage(Window window){
        WindowDto windowDto=new WindowDto();
        // 所在城市
        if (StringUtils.isEmpty(window.getCityId())) {
            window.setCityId("101");
        }
        // 经度
        if (StringUtils.isEmpty(window.getUserLot())) {
            window.setUserLot("0");
        }
        // 纬度
        if (StringUtils.isEmpty(window.getUserLat())) {
            window.setUserLat("0");
        }
        if(window.getPn()==1){
            windowDto.setTopicList(cheapMapper.queryTopic());
        }
        HashMap<String, String> maps = new HashMap<String, String>();
        // 取得专题总数
        int num = 0;
        // 关注银行
        String focusBankIds = window.getBankid();
        if (!StringUtils.isEmpty(focusBankIds)) {
            if (!focusBankIds.contains("#")) {
                window.setInfo1(focusBankIds);
                if (focusBankIds.length() == 1) {
                    focusBankIds = "0" + focusBankIds;
                }
            } else {
                window.setInfo1(focusBankIds.replaceAll("#", ","));
                String[] banksArr = focusBankIds.split("#");
                focusBankIds = "";
                for (String strBkId : banksArr) {
                    if (!StringUtils.isEmpty(strBkId)) {
                        if (strBkId.length() == 1) {
                            focusBankIds = focusBankIds + "0" + strBkId	+ "#";
                        } else {
                            focusBankIds = focusBankIds + strBkId + "#";
                        }

                    }
                }
            }
            window.setBankid(focusBankIds);
        } else {
            window.setBankid("999");
            window.setInfo1("999");
        }
        int windowNum=cheapMapper.queryWindowCount(window);
        if(windowNum>LIMITCOUNT){
            windowNum=LIMITCOUNT;
        }
        /* 总记录数 */
        window.setRc(num);
        int tp = (num + window.getPs() - 1) / window.getPs();
        if (tp == 0) {
            tp = 1;
        }
        /* 总页数 */
        window.setTp(tp);
        /*
        if (window.getPn() > tp) {
        window.setBusiErrCode(1000);
        window.setBusiErrDesc("页数超出限制！");
        return;
        }
        */
        List<TopicBussiDto> list=cheapMapper.window_query_distance(window).subList(0,5);
        windowDto.setTopicBussiDtoList(list);
        return windowDto;
    }

    // 专题关联商家获取
    public Map<String,Object> getTopicInfo(Window bean) {
        Map<String,Object> mapResult=new HashMap<>();
        // 专题ID
        if (StringUtils.isEmpty(bean.getTopicId())) {
            mapResult.put("code",1000);
            mapResult.put("desc","专题ID 不能为空");
            return mapResult;
        }
        // 所在城市
        if (StringUtils.isEmpty(bean.getCityId())) {
            bean.setCityId("101");
        }
        // 经度
        if (StringUtils.isEmpty(bean.getUserLot())) {
            bean.setUserLot("0");
        }
        // 纬度
        if (StringUtils.isEmpty(bean.getUserLat())) {
            bean.setUserLat("0");
        }
        List<Document> list = null ;
        String topiciQuery = "";
        // 取得专题
        TopicDto topic= cheapMapper.queryTopicById(bean.getTopicId());
        if (topic != null ) {
            if (bean.getPn() == 1) {
                mapResult.put("firstPage",topic);
            }
            topiciQuery = topic.getCkeywords();
            if (!StringUtils.isEmpty(topiciQuery)){
                topiciQuery = topiciQuery.toLowerCase();
            }
            mapResult.put("code",1);
            mapResult.put("desc","获得专题");
        }
        logger.info("topiciQuery----" + topiciQuery);
        // 取得专题总数
        int num = 0;
        Map<String, String> maps = new HashMap<String, String>();
        int listLength = 0;
        if (!StringUtils.isEmpty(topiciQuery)){
            bean.setQuery(topiciQuery);
            List<TopicBussiDto> topicBussiList= cheapMapper.topic_business_query(bean);
            if (topicBussiList != null && topicBussiList.size() > 0) {
                num = topicBussiList.size();
            }
//            windowDto.setTopicBussiDtoList(topicBussiList);
        }
        logger.info(num + "-----" + listLength);
        // 总记录数
        bean.setRc(num + listLength);
        int tp = (num + listLength + bean.getPs() - 1) / bean.getPs();
        if (tp == 0) {
            tp = 1;
        }
        // 总页数
        bean.setTp(tp);
        if (bean.getPn() > tp) {
            mapResult.put("code",1000);
            mapResult.put("desc","页数超出限制");
            return mapResult;
        }
//        String pageXml = "<pageinfo tp=\"" + bean.getTp() + "\" rc=\""
//                + bean.getRc() + "\" pn=\"" + bean.getPn() + "\" ps=\""
//                + bean.getPs() + "\"/>";
//        retXml = pageXml + retXml;
        if (num + listLength == 0) {

        }else if ((bean.getPn() * bean.getPs())  <= num){
            List<TopicBussiDto> topicBussiList0= cheapMapper.topic_business_query(bean);
            if (topicBussiList0 != null && topicBussiList0.size() > 0 ) {
                topicBussiList0.get(0).setTag("row");
//                retXml +=topicBussiList0.get(0).toString();
                mapResult.put("row",topicBussiList0.get(0));
            }
        }
        return mapResult;
    }
    public int topicClick(String topicId){
        return cheapMapper.u_topic_click(topicId);
    }

//    private List<Document> query_result(Window bean, List<TopicBussiDto> topicBussiList ){
//        List<Document> retList =null;
//        try {
//            String query = bean.getQuery();
//            double clng0 = Double.parseDouble(bean.getUserLot());
//            double clat0 = Double.parseDouble(bean.getUserLat());
//            int icityid = Integer.parseInt(bean.getCityId());
//            List<Document> listForAnd =null;
//            List<Document> listForOr =null;
//            List<Document> listForCategoery =null;
//            listForAnd = LuceneUtil.searchByDis(query, clat0, clng0, icityid,new String[]{"cname"}, QueryParser.Operator.AND);
//            System.out.println("listForAnd--" + listForAnd.size());
//            if(listForAnd==null || listForAnd.size() < 10 ){
//                listForOr = LuceneUtil.searchByDis(query, clat0, clng0, icityid,null, QueryParser.Operator.OR);
//                System.out.println("listForOr--" + listForOr.size());
//            }
//            if(MyModel.CATEGOERY.contains(query)){
//                listForCategoery = LuceneUtil.searchByDis(query, clat0, clng0, icityid,new String[]{"ccategoeryname","pccategoeryname"} , QueryParser.Operator.OR);
//            }
//            if((listForAnd==null || listForAnd.size()==0) && (listForOr==null || listForOr.size()==0) && (listForCategoery==null || listForCategoery.size()==0)){
//                return null;
//            }
//            Set<Document> set = new TreeSet<Document>(new Comparator<Document>() {
//                @Override
//                public int compare(Document o1, Document o2) {
//                    return Integer.parseInt(o1.get("distance")) - Integer.parseInt(o2.get("distance"));
//                }
//            });
//            set=fillSet(set,listForAnd,clng0,clat0);
//            set=fillSet(set,listForOr,clng0,clat0);
//            set=fillSet(set,listForCategoery,clng0,clat0);
//            //去重一下
//            List<Integer> ibusinessids = new ArrayList<Integer>();
//            List<Integer> ibusinessidsWinJrs = new ArrayList<Integer>();
//            if (topicBussiList != null && topicBussiList.size() > 0 ){
//                for (TopicBussiDto td:topicBussiList){
//                    ibusinessidsWinJrs.add(Integer.parseInt(td.getIbusinessid()));
//                }
//            }
//            retList = new ArrayList<Document>();
//            for(Document d:set){
//                int bid = Integer.parseInt(d.get("ibussinessid"));
//                if((!ibusinessids.contains(bid) ) && !ibusinessidsWinJrs.contains(bid) ){
//                    ibusinessids.add(bid);
//                    retList.add(d);
//                }
//            }
//        } catch (Exception e) {
//            retList = null;
//        }
//        return retList;
//    }
//    public Set<Document> fillSet(Set<Document> set,List<Document> list,double clng0,double clat0){
//        if (list != null && list.size() > 0) {
//            for(Document d:list){
//                d.removeField("cbus_info");
//                double clat=0;
//                double clng=0;
//                try{
//                    clat=Double.parseDouble(d.get("clat"));
//                    clng=Double.parseDouble(d.get("clng"));
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                int distance = (int) DistanceUtil.GetDistance(clng0, clat0, clng, clat);
//                d.add(new Field("distance", distance+"", Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
//                set.add(d);
//            }
//        }
//        return set;
//    }
    public WindowDto query_searchKeyWorlds(){

        WindowDto windowDto=new WindowDto();
        File file = new File("/opt/export/www/search/hot.xml");
//        File file = new File("C:\\Users\\A-0106\\Desktop\\search/hot.xml");
        SAXReader saxReader = new SAXReader();
        org.dom4j.Document doc=null;
        String xml="";
        if(file.exists()){
            try{
                doc=saxReader.read(file);
            }catch (Exception e){
                windowDto.setCode("0");
                windowDto.setDesc("读取文件异常,hot.xml");
            }
            Element root = doc.getRootElement();
            List<Element> keyword= root.elements("keyword");
            StringBuilder sb = new StringBuilder();
            int i=0;
            for(Element hot:keyword){

                sb.append("<row ");
                sb.append(" ikid ='"+String.valueOf(i));
                sb.append("' ckeyvalue= '"+hot.attributeValue("value"));
                sb.append("' /> ");
                i++;
            }
            xml+=sb.toString();
        }else{
            windowDto.setCode("0");
            windowDto.setDesc("'读取的热词文件hot.xml不存在");
        }

        file = new File("/opt/export/www/search/recommend.xml");
//        file = new File("C:\\Users\\A-0106\\Desktop\\search/recommend.xml");
        if(file.exists()){
            try{
                doc=saxReader.read(file);
            }catch (Exception e){
                windowDto.setCode("0");
                windowDto.setDesc("读取文件异常,读取文件异常recommend.xml");
            }
            Element root = doc.getRootElement();
            List<Element> keyword= root.elements("keyword");
            StringBuilder sb = new StringBuilder();
            int i=0;
            for(Element recommend:keyword){
                sb.append("<recommend ");
                sb.append(" ikid ='"+String.valueOf(i));
                sb.append("' ckeyvalue= '"+recommend.attributeValue("value"));
                sb.append("' /> ");
                i++;
            }
            xml+=sb.toString();
        }else{
            windowDto.setCode("0");
            windowDto.setDesc("读取文件异常recommend不存在");
        }
        windowDto.setCode("1");
        windowDto.setDesc("读取文件成功");
        windowDto.setBusiXml(xml);
        return windowDto;
    }
    public ResultDto query_result(Cheap bean){
        ResultDto resultDto=new ResultDto();
        int pagesize = bean.getPs();
        int pageno =bean.getPn();
        String query = bean.getQuery().toLowerCase();
        double clng0 = bean.getClng();
        double clat0 =bean.getClat();
        //query = new String(query.getBytes("iso8859-1"),"UTF-8");
        int icityid = bean.getCityid();
        List<Document> list =null;
        boolean iscname = false;
        if(MyModel.CATEGOERY.contains(query)){
            list = LuceneUtil.searchByDis(query, clat0, clng0, icityid,new String[]{"ccategoeryname","pccategoeryname"}, QueryParser.Operator.OR);
        }else{
            list = LuceneUtil.searchByDis(query, clat0, clng0, icityid,new String[]{"cname"}, QueryParser.Operator.AND);
            iscname = true;
        }
        if(list==null || list.size()==0){
            list = LuceneUtil.searchByDis(query, clat0, clng0, icityid,null, QueryParser.Operator.OR);
            iscname = false;
        }
        if(list==null || list.size()==0){
            resultDto.setBusiErrCode("0");
            resultDto.setBusiErrDesc("未查询到优惠");
            return resultDto;
        }
        Set<Document> set = new TreeSet<Document>(new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Integer.parseInt(o1.get("distance")) - Integer.parseInt(o2.get("distance"));
            }
        });

        for(Document d:list){
            d.removeField("cbus_info");
            double clat=0;
            double clng=0;
            try{
                clat=Double.parseDouble(d.get("clat"));
                clng=Double.parseDouble(d.get("clng"));
            }catch(Exception e){
                e.printStackTrace();
            }
            int distance = (int) DistanceUtil.GetDistance(clng0, clat0, clng, clat);
            d.add(new Field("distance", (distance+"").toString(), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
            set.add(d);
        }
        //去重一下
        List<Integer> ibusinessids = new ArrayList<Integer>();
        List<Document> olist = new ArrayList<Document>();
        for(Document d:set){
            int bid = Integer.parseInt(d.get("ibussinessid"));
            if(!ibusinessids.contains(bid) || iscname){
                ibusinessids.add(bid);
                olist.add(d);
            }
        }
        int fromIndex = (pageno-1)*pagesize;
        int toIndex = pageno*pagesize;
        if(fromIndex>-1 && fromIndex<olist.size() && fromIndex<toIndex){
            toIndex =toIndex<olist.size()?toIndex:olist.size();
            list = olist.subList(fromIndex, toIndex);
        }else{
            resultDto.setBusiErrCode("0");
            resultDto.setBusiErrDesc("未查询到优惠");
            return resultDto;
        }
        List<Map<String,String>> listData=new ArrayList<>();
        for(Document d:list){
            List<Fieldable> fs = d.getFields();
            Map<String,String> map=new HashMap<>();
            for(Fieldable f:fs){
                String key = f.name();
                String val =d.get(key);
                map.put(key,val);
            }
            listData.add(map);
        }
        if(fromIndex>-1 && fromIndex<olist.size() && fromIndex<toIndex){
            toIndex =toIndex<olist.size()?toIndex:olist.size();
            list = olist.subList(fromIndex, toIndex);
        }else{
            resultDto.setBusiErrCode("0");
            resultDto.setBusiErrDesc("没有查询到优惠");
            return resultDto;
        }
        int count = olist.size();
        int tp =  (count + pagesize -1) / pagesize;
        resultDto.setTp(tp);
        resultDto.setPn(pageno);
        resultDto.setPs(pagesize);
        resultDto.setCcount(list.size());
        resultDto.setBusiErrCode("1");
        resultDto.setBusiErrDesc("查询优惠");
        resultDto.setData(listData);
        resultDto.setRc(count);
        return  resultDto;
    }
}
