package com.yy.ana.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.yy.ana.bean.KeyValue;
import com.yy.ana.dict.PlatformDict;
import com.yy.ana.domain.BaseDto;
import com.yy.ana.domain.Dto;
import com.yy.ana.service.appdata.IAppdataService;
import com.yy.ana.service.hbase.IHbaseService;
import com.yy.ana.tools.Bytes;
import com.yy.ana.tools.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by sunqm on 2017/5/27.
 */
@Service
public class AppdataServiceImpl implements IAppdataService {
    @Reference
    private IHbaseService hbaseService;


    /**
     * 查询产品列表
     *
     * @param distinguishPlatform
     * @param dateList
     * @param products
     * @return
     * @throws Exception
     */
    public List<Dto> getProductSummaryData(boolean distinguishPlatform, String dateList, String products) throws Exception {
        String[] dateArray = dateList.split(",");

        String productAll = "yy_gjj,yy_jz,yy_chexian,yy_ss,jz_new";
        if (StringUtils.isEmpty(products)) {
            products = productAll;
        } else {
            products = intersectionStr(products, productAll);
        }

        String dataTypes = "new_user,active_user,start_times";
        String tableName = "app_data_daily";
        //平台
        String platform = "全平台";

        List<Dto> list = new ArrayList<Dto>();
        for (String day : dateArray) {
            /**1、查询产品列表并封装成Dto*/
            List<Dto> dataList = getProductByDate(DateUtil.parseString2Date(day), products, dataTypes, tableName);
            if (dataList.size() > 0) {
                for (Dto dto : dataList) {
                    String productKey = dto.getAsString("key").split("#")[0];
                    String pkg = dto.getAsString("key").split("#")[3];

                    Dto dtoByProductKey = getDtoByProductKey(list, productKey, pkg, distinguishPlatform, day);
                    if (dtoByProductKey == null) {
                        dtoByProductKey = createDto(productKey, platform, pkg, distinguishPlatform, day);

                        //处理pkg为空的情况
                        if ((distinguishPlatform && !StringUtils.isEmpty(pkg.trim())) || !distinguishPlatform) {
                            list.add(dtoByProductKey);
                        }
                    }

                    List<String> keys = Arrays.asList(dto.getAsString("key").split("#"));
                    //处理新增用户
                    if (keys.contains("new_user")) {
                        dtoByProductKey.put("new_user", dtoByProductKey.getAsLong("new_user") + dto.getAsLong("value"));
                    }
                    //处理活跃用户
                    if (keys.contains("active_user")) {
                        dtoByProductKey.put("active_user", dtoByProductKey.getAsLong("active_user") + dto.getAsLong("value"));
                    }
                    //处理启动次数
                    if (keys.contains("start_times")) {
                        dtoByProductKey.put("start_times", dtoByProductKey.getAsLong("start_times") + dto.getAsLong("value"));
                    }
                }
            }
        }

        /**处理产品名*/
        return dealProductName(list);
    }


    /**
     * 查询截止到今天的总数
     *
     * @param day
     * @param distinguishPlatform
     * @return
     * @throws Exception
     */
    public List<Dto> getProductsTotalData(boolean distinguishPlatform, String day, String products) throws Exception {
        Date date = DateUtil.parseString2Date(day);

        String productAll = "yy_gjj,yy_jz,yy_chexian,yy_ss,jz_new";
        if (StringUtils.isEmpty(products)) {
            products = productAll;
        } else {
            products = intersectionStr(products, productAll);
        }

        String totalType = "total_user";
        String tableName = "app_data_daily";
        List<Dto> totalList = getProductByDate(date, products, totalType, tableName);

        List<Dto> list = new ArrayList<Dto>();

        if (totalList.size() > 0) {
            for (Dto dto : totalList) {
                Dto dto1 = new BaseDto();
                dto1.put("product_key", dto.getAsString("key").split("#")[0]);
                dto1.put("total", dto.getAsLong("value"));
                dto1.put("platform", PlatformDict.getPlatformName(dto.getAsString("key").split("#")[3]));
                dto1.put("pkg", dto.getAsString("key").split("#")[3]);
                dto1.put("app_name", PlatformDict.getAppName(dto.getAsString("key").split("#")[3]));
                dto1.put("total", dto.getAsLong("value"));
                list.add(dto1);

            }
        }

        return list;
    }

    private Dto createDto(String productKey, String platform, String pkg, boolean isDistinPlatform, String day) {
        Dto dto = new BaseDto();
        dto.put("product_key", productKey);
        dto.put("platform", platform);
        dto.put("pkg", pkg);
        dto.put("new_user", 0l);
        dto.put("active_user", 0l);
        dto.put("start_times", 0l);
        dto.put("today_total", 0l);
        dto.put("data_date", day);

        if (isDistinPlatform) {
            dto.put("platform", PlatformDict.getPlatformName(pkg));
            dto.put("app_name", PlatformDict.getAppName(pkg));
        }
        return dto;
    }

    private Dto getDtoByProductKey(List<Dto> list, String productKey, String pkg, boolean isDistinPlatform, String day) {
        if (list == null || list.size() == 0) {
            return null;
        }
        for (Dto dto : list) {
            if (productKey.equals(dto.getAsString("product_key")) && day.equals(dto.getAsString("data_date"))) {
                if (!isDistinPlatform) {
                    return dto;
                }
                if (pkg.equals(dto.getAsString("pkg"))) {
                    return dto;
                }

            }
        }
        return null;
    }

    /**
     * 处理产品名
     *
     * @param list
     * @throws Exception
     */
    private List<Dto> dealProductName(List<Dto> list) throws Exception {
        //处理产品名
        if (list.size() > 0) {
            for (Dto dto : list) {
                List<KeyValue> keyValues = hbaseService.getByRowkeyColumn("product_dic", dto.getAsString("product_key"), "info:product_name,info:app_platform");
                if (keyValues != null && keyValues.size() > 0) {
                    for (KeyValue keyValue : keyValues) {
                        dto.put("product_name", Bytes.toString(keyValue.getValue()).replaceAll("\"", ""));
                    }
                }
            }
        }
        return list;
    }


    /**
     * 求两个字符串以逗号分隔后字符数组的交集
     *
     * @param str1
     * @param str2
     * @return
     * @throws Exception
     */
    private String intersectionStr(String str1, String str2) throws Exception {
        String[] array1 = str1.split(",");
        String[] array2 = str2.split(",");
        
        return intersectionStrArray(array1, array2);
    }

    private String intersectionStrArray(String[] array1, String[] array2) throws Exception {
        List<String> list1 = Arrays.asList(array1);
        List<String> list2 = Arrays.asList(array2);
        StringBuilder sb = new StringBuilder();
        for (String str : list1) {
            if (list2.contains(str)) {
                sb.append(str).append(",");
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * @param date
     * @param products
     * @param dataTypes
     * @param tableName
     * @return
     * @throws Exception
     */
    private List<Dto> getProductByDate(Date date, String products, String dataTypes, String tableName) throws Exception {
        String startDate = DateUtil.formatDate(date, "yyyy-MM-dd");
        String endDate = DateUtil.formatDate(DateUtil.getLastDay(date, -1), "yyyy-MM-dd");

        return getProductByDate(startDate, endDate, products, dataTypes, tableName);

    }

    /**
     * @param startDate
     * @param endDate
     * @param products
     * @param dataTypes
     * @param tableName
     * @return
     * @throws Exception
     */
    private List<Dto> getProductByDate(String startDate, String endDate, String products, String dataTypes, String tableName) throws Exception {
        List<Dto> dtos = new ArrayList<Dto>();
        for (String product : products.split(",")) {
            for (String dataType : dataTypes.split(",")) {
                String startRowKey = product + "#" + dataType + "#";
                String endRowKey = product + "#" + dataType + "#";
                if (StringUtils.isNotEmpty(startDate)) {
                    startRowKey = startRowKey + startDate;
                }
                if (StringUtils.isNotEmpty(endDate)) {
                    endRowKey = endRowKey + endDate;
                }

                Map<String, List<KeyValue>> results = hbaseService.scanByFilter(tableName, startRowKey, endRowKey, "", "data", "data:all");

                for (String key : results.keySet()) {
                    Dto dto = new BaseDto();
                    dto.put("key", key);
                    dto.put("value", Bytes.tolong(results.get(key).get(0).getValue()));
                    dtos.add(dto);

                    System.out.println(PlatformDict.getAppName(key.split("#")[3]));
                }
            }
        }
        return dtos;
    }
}
