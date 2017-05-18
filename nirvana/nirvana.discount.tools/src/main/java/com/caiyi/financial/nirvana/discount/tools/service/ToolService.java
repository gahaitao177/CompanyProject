package com.caiyi.financial.nirvana.discount.tools.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolBean;
import com.caiyi.financial.nirvana.discount.tools.bean.ToolVersionBean;
import com.caiyi.financial.nirvana.discount.tools.dto.TbHomePageDto;
import com.caiyi.financial.nirvana.discount.tools.mapper.TbHomePageMapper;
import com.caiyi.financial.nirvana.discount.tools.mapper.ToolMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lizhijie on 2016/8/9.
 */
@Service
public class ToolService extends AbstractService {
    @Autowired
    ToolMapper toolMapper;
    @Autowired
    private TbHomePageMapper tbHomePageMapper;

    /**
     * 根据citycode 和 type 获取tbhometble里面的摸个类型的列表
     * @param adcode 高德城市码
     * @param type 类型（banner pact..）
     * @return
     */
    public List<TbHomePageDto> selectHomePage(String adcode,String type){
        adcode = "%"+adcode+"%";
        List<TbHomePageDto> list = tbHomePageMapper.selectHomePage(adcode,type);
        return list;
    }



    public  Map<String,Object> test(){
       Map<String,Object> map=  toolMapper.queryTest();
        return  map;
    }
    public Map<String,Object> qualitySpread(String typeid){
        Map<String,Object> map=new HashMap<>();
//        if(StringUtils.isEmpty(typeid)) {
//            map.put("code","0");
//            map.put("desc","参数错误");
//            return map;
//        }
        List<Map<String,Object>> list=  toolMapper.queryQualitySpread(typeid);
        if(list!=null&&list.size()>0){
            map.put("code","1");
            map.put("desc","请求成功");
            map.put("data", list);
        }else{
            map.put("code","1");
            map.put("desc","没有得到有效数据");
            map.put("data", null);
        }
        return  map;
    }

    /**
     *  装修工具app数据同步  lcs 20160628
     * @param bean
     * @return
     */
    public Map<String,String> mergerDecoration(ToolBean bean){
        logger.info("mergerDecoration start");
        logger.info("mergerDecoration userId:" + bean.getCuserId() + ",type:" + bean.getAction());
        logger.info("mergerDecoration data:"  + bean.getData());
        Map<String,String> queryResult =new HashMap<>();
        bean.setMediatype("json");
        JSONObject json=new JSONObject();
        if (StringUtils.isEmpty(bean.getCuserId())){
            queryResult.put("code","0");
            queryResult.put("desc","用户id不能为空");
            return queryResult;
        }
        getDecorationData(json,bean.getCuserId());
        JSONObject dataJson =  JSONObject.parseObject(bean.getData());
        queryResult.put("code","1");
        queryResult.put("desc","获得数据成功");
        queryResult.put("data",json.toJSONString());
        mergerDecorationData(dataJson,bean.getCuserId());
        return  queryResult;
    }
    private  void getDecorationData(JSONObject object,String cuserid){
        List<Map<String,Object>> listBook=toolMapper.queryBooks(cuserid);
        if(listBook!=null&&listBook.size()>0){
            object.put("book",JSON.toJSONString(listBook));
        }
        List<Map<String,Object>> listSubCategory=toolMapper.queryCategory(cuserid);
        if(listSubCategory!=null&&listSubCategory.size()>0){
            object.put("subCategory",JSON.toJSONString(listSubCategory));
        }
        List<Map<String,Object>> listRecord=toolMapper.queryRecord(cuserid);
        if(listRecord!=null&&listRecord.size()>0){
            object.put("record",JSON.toJSONString(listRecord));
        }
    }
    private void mergerDecorationData(JSONObject json,String cuserid){
        mergerDecorationBooks(json,cuserid);
        mergerDecorationCategory(json,cuserid);
        mergerDecorationRecord(json,cuserid);
        logger.info("mergerDecorationData:success");
    }
    private  void mergerDecorationBooks(JSONObject json,String cuserid){
        String bookStr = json.getString("book");
        logger.info("book:", bookStr);
        JSONArray bookArr = JSON.parseArray(bookStr);
        if (bookArr != null && bookArr.size() > 0) {
            for (Object bookObj : bookArr) {
                JSONObject bookJson = JSONObject.parseObject(bookObj.toString());
                String bookid = bookJson.getString("bookId");
                String bookname = bookJson.getString("bookName");
                String bookColor = bookJson.getString("bookColor");
                String updateTime = bookJson.getString("updateTime");
                String budget = bookJson.getString("budget");
                String exparam01 = bookJson.getString("exparam01");
                String exparam02 = bookJson.getString("exparam02");
                String delete = bookJson.getString("isDelete");
                if (StringUtils.isEmpty(budget)) {
                    budget = "0";
                }
                if (StringUtils.isEmpty(bookid)) {
                    logger.info("bookid:" + bookid + " 非法数据:");
                    continue;
                }
//                String[] checkParam = {cuserid, bookid};
//                JdbcRecordSet jrs = jcn.executeQuery(CHECK_BOOK_EXIST, checkParam);
//
                int nums=toolMapper.queryBookCount(cuserid,bookid);
                logger.info("bookid:" + bookid + " 存在数量:" + nums);
                Map<String ,String> map =new HashMap<>();
                map.put("bookname",bookname);
                map.put("bookcolor",bookColor);
                map.put("updateTime",updateTime);
                map.put("budget",budget);
                map.put("exparam01",exparam01);
                map.put("exparam02",exparam02);
                map.put("isdelete",delete);
                map.put("cuserid",cuserid);
                map.put("bookid",bookid);
                if (nums > 0) {
                    logger.info("bookid:" + bookid + " 已存在,更新");
//                    String[] updateParams = {bookname, bookColor, updateTime, budget, exparam01, exparam02, delete, updateTime, cuserid, bookid};
//                    int updateCounts = jcn.executeUpdate(UPDATE_BOOK, updateParams);
                    int updateCounts =toolMapper.updateBookInfo(map);
                    if (updateCounts == 1) {
                        logger.info("bookid:" + bookid + " 更新成功");
                    } else {
                        logger.info("bookid:" + bookid + " 更新失败" + ",updateCounts" + updateCounts);
                    }
                    map.clear();
                } else {
                    logger.info("bookid:" + bookid + " 不存在,插入");
//                    String[] addParams = {bookid, uid, bookname, bookColor, updateTime, budget, exparam01, exparam02, delete};
//                    int addCounts = jcn.executeUpdate(INSERT_BOOK, addParams);
                    int addCounts=toolMapper.saveBookInfo(map);
                    if (addCounts == 1) {
                        logger.info("bookid:" + bookid + " 插入成功");
                    } else {
                        logger.info("bookid:" + bookid + " 插入失败" + ",addCounts" + addCounts);
                    }
                    map.clear();
                }
            }
        }
    }
    private  void mergerDecorationCategory(JSONObject json,String cuserid){
        String categoryStr = json.getString("subCategory");
        logger.info("categoryArr:", categoryStr);
        JSONArray categoryArr = JSON.parseArray(categoryStr);
        if (categoryArr != null && categoryArr.size() > 0){
//            int deleteCounts = jcn.executeUpdate(DELETE_CATEGORY, new String[]{uid});
//            logger.info("uid:" +  uid + "删除数量:" +deleteCounts ) ;
            for (Object categoryObj: categoryArr){
                JSONObject categoryJson = JSONObject.parseObject(categoryObj.toString());
                String categoryId = categoryJson.getString("categoryId");
                String subCategoryId = categoryJson.getString("subCategoryId");
                String name = categoryJson.getString("name");
                String updateTime = categoryJson.getString("updateTime");
                String exparam01 = categoryJson.getString("exparam01");
                String exparam02 = categoryJson.getString("exparam02");
                String delete = categoryJson.getString("isDelete");
                if (StringUtils.isEmpty(subCategoryId)){
                    logger.info("subCategoryId:" +  subCategoryId + " 非法数据:" );
                    continue;
                }
//                String[] checkParam = {uid, subCategoryId};
//                JdbcRecordSet jrs = jcn.executeQuery(CHECK_CATEGORY_EXIST, checkParam);
                int nums = toolMapper.queryCategoryCount(cuserid,subCategoryId);
                Map<String ,String> map =new HashMap<>();
                map.put("categoryid",categoryId);
                map.put("name",name);
                map.put("updateTime",updateTime);
                map.put("isdelete",delete);
                map.put("exparam01",exparam01);
                map.put("exparam02",exparam02);
                map.put("subcategoryid",subCategoryId);
                map.put("cuserid",cuserid);
                if(nums > 0){
                    logger.info("subCategoryId:" +  subCategoryId + " 已存在,更新") ;
//                    String[] updateParams = {categoryId,name,updateTime,delete,exparam01,exparam02,updateTime,subCategoryId,uid};
//                    int updateCounts = jcn.executeUpdate(UPDATE_CATEGORY, updateParams);
                    int updateCounts=toolMapper.updateCategoryInfo(map);
                    if (updateCounts == 1){
                        logger.info("subCategoryId:" +  subCategoryId + " 更新成功") ;
                    } else {
                        logger.info("subCategoryId:" +  subCategoryId + " 更新失败" + ",updateCounts" + updateCounts) ;
                    }

                }else {
                    logger.info("categoryId:" +  categoryId + "subCategoryId:" +  subCategoryId + "插入") ;
//                    String[] addParams = {categoryId,subCategoryId,uid,name,updateTime,exparam01,exparam02,delete};
//                    int addCounts = jcn.executeUpdate(INSERT_CATEGORY, addParams);
                    int addCounts=toolMapper.saveCategoryInfo(map);
                    if (addCounts == 1){
                        logger.info("categoryId:" +  categoryId + "subCategoryId:" +  subCategoryId + "插入成功") ;
                    } else {
                        logger.info("categoryId:" +  categoryId + "subCategoryId:" +  subCategoryId + "插入失败" + ",addCounts" + addCounts) ;
                    }
                }
                map.clear();
            }
        }
    }
    private  void mergerDecorationRecord(JSONObject json,String cuserid){
        String recordStr = json.getString("record");
        logger.info("recordStr:" + recordStr);
        JSONArray recordArr = JSON.parseArray(recordStr);
        if (recordArr != null && recordArr.size() > 0){
//            int deleteCounts = jcn.executeUpdate(DELETE_RECORD, new String[]{uid});
//            logger.info("uid:" +  uid + "删除数量:" +deleteCounts ) ;
            for (Object recordObj: recordArr){
                JSONObject recordJson = JSONObject.parseObject(recordObj.toString());
                String recordId = recordJson.getString("recordId");
                String bookId = recordJson.getString("bookId");
                String categoryId = recordJson.getString("categoryId");
                String subCategoryId = recordJson.getString("subCategoryId");
                String updateTime = recordJson.getString("updateTime");
                String expenditure = recordJson.getString("expenditure");
                String remark = recordJson.getString("remark");
                String exparam01 = recordJson.getString("exparam01");
                String exparam02 = recordJson.getString("exparam02");
                String exparam03 = recordJson.getString("exparam03");
                String exparam04 = recordJson.getString("exparam04");
                String delete = recordJson.getString("isDelete");
                if (StringUtils.isEmpty(recordId)){
                    logger.info("mergerDecorationRecord recordId:" +  recordId + " 非法数据:" );
                    continue;
                }
//                String[] checkParam = {uid, recordId};
//                JdbcRecordSet jrs = jcn.executeQuery(CHECK_RECORD_EXIST, checkParam);
                int nums = toolMapper.queryRecordCount(cuserid,recordId);
                Map<String ,String> map =new HashMap<>();
                map.put("bookid",bookId);
                map.put("categoryid",categoryId);
                map.put("subcategoryid",subCategoryId);
                map.put("expenditure",expenditure);
                map.put("remark",remark);
                map.put("updateTime",updateTime);
                map.put("isdelete",delete);
                map.put("exparam01",exparam01);
                map.put("exparam02",exparam02);
                map.put("exparam03",exparam03);
                map.put("exparam04",exparam04);
                map.put("cuserid",cuserid);
                map.put("irecordid",recordId);
                if(nums > 0){
                    logger.info("recordId:" +  subCategoryId + " 已存在,更新") ;
//                    String[] updateParams = {bookId,categoryId,subCategoryId,expenditure,remark,updateTime,delete,exparam01,exparam02,exparam03,exparam04,updateTime,uid,recordId};
//                    int updateCounts = jcn.executeUpdate(UPDATE_RECORD, updateParams);
                    int updateCounts=toolMapper.updateRecordInfo(map);
                    if (updateCounts == 1){
                        logger.info("recordId:" +  subCategoryId + " 更新成功") ;
                    } else {
                        logger.info("recordId:" +  subCategoryId + " 更新失败" + ",updateCounts" + updateCounts) ;
                    }

                } else {
                    logger.info("recordId:" +  recordId + "插入") ;
//                    String[] addParams = {recordId,bookId,categoryId,subCategoryId,uid,updateTime,expenditure,remark,delete,exparam01,exparam02,exparam03,exparam04};
//
//                    int addCounts = jcn.executeUpdate(INSERT_RECORD, addParams);
                    int addCounts=toolMapper.saveRecordInfo(map);
                    if (addCounts == 1){
                        logger.info("recordId:" +  recordId +  "插入成功") ;
                    } else {
                        logger.info("recordId:" +  recordId + "插入失败" + ",addCounts" + addCounts) ;
                    }
                }

            }
        }
    }

   public List<ToolVersionBean> query_tool_version(ToolVersionBean bean){
        List<ToolVersionBean> toolVersionBeanList =  new CopyOnWriteArrayList<>();
        try {
            toolVersionBeanList  =   toolMapper.query_tool_version(bean);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询版本Bean",e);
        }
        return toolVersionBeanList;
    }
}
