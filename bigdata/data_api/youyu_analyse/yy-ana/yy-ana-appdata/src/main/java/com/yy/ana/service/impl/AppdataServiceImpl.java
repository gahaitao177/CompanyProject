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

    private Dto checkDto(Dto dtoByProductKey, String productKey, String platform, List<Dto> list, String pkg, boolean isDistinPlatform, String day) {
        if (dtoByProductKey == null) {
            dtoByProductKey = new BaseDto();
            dtoByProductKey.put("product_key", productKey);
            dtoByProductKey.put("platform", platform);
            dtoByProductKey.put("pkg", pkg);
            dtoByProductKey.put("new_user", 0l);
            dtoByProductKey.put("active_user", 0l);
            dtoByProductKey.put("start_times", 0l);
            dtoByProductKey.put("today_total", 0l);
            dtoByProductKey.put("data_date", day);

            if (isDistinPlatform) {
                dtoByProductKey.put("platform", PlatformDict.getPlatformName(pkg));
                dtoByProductKey.put("app_name", PlatformDict.getAppName(pkg));
            }
            if ((isDistinPlatform && !StringUtils.isEmpty(pkg.trim())) || !isDistinPlatform) {
                list.add(dtoByProductKey);
            }

        }
        return dtoByProductKey;
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

    public List<Dto> getProductSummaryData(boolean distinguishPlatform, String days, String products) throws Exception {
        String[] dayArray = days.split(",");

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

        //截止到昨天的总数
        String totalType = "total_user";

        List<Dto> list = new ArrayList<Dto>();
        for (String day : dayArray) {
            List<Dto> dayList = getProductByDate(DateUtil.parseString2Date(day), products, dataTypes, tableName);
            dealDayList(dayList, list, distinguishPlatform, platform, day);
        }

        //处理产品名
        dealProductName(list);

        return list;
    }

    /**
     * 求两个字符串的交集
     *
     * @param products
     * @param productAll
     * @return
     * @throws Exception
     */
    private String intersectionStr(String products, String productAll) throws Exception {
        List<String> array = Arrays.asList(products.split(","));
        List<String> allArray = Arrays.asList(productAll.split(","));
        StringBuilder sb = new StringBuilder();
        for (String product : array) {
            if (allArray.contains(product)) {
                sb.append(product).append(",");
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 查询当天以前的总数
     *
     * @param day
     * @param distinguishPlatform
     * @return
     * @throws Exception
     */
    public List<Dto> getProductsTotalData(boolean distinguishPlatform, String day) throws Exception {
        Date date = DateUtil.parseString2Date(day);
        String products = "yy_gjj,yy_jz,yy_chexian,yy_ss,jz_new";
        String totalType = "total_user";
        String tableName = "app_data_daily";
        List<Dto> totalList = getProductByDate(date, products, totalType, tableName);

        List<Dto> list = new ArrayList<Dto>();

        if (totalList.size() > 0) {
            for (Dto dto : totalList) {
                addTotal(list, dto, checkTotalDto(list, dto, distinguishPlatform), distinguishPlatform);
            }
        }

        return list;
    }

    private void addTotal(List<Dto> list, Dto dto, boolean b, boolean distinguishPlatform) throws Exception {
        if (!b) {
            Dto dto1 = new BaseDto();
            dto1.put("product_key", dto.getAsString("key").split("#")[0]);
            dto1.put("total", dto.getAsLong("value"));
            dto1.put("platform", PlatformDict.getPlatformName(dto.getAsString("key").split("#")[3]));
            dto1.put("app_name", PlatformDict.getAppName(dto.getAsString("key").split("#")[3]));
            list.add(dto1);
        }

    }

    private boolean checkTotalDto(List<Dto> list, Dto dto, boolean distinguishPlatform) throws Exception {
        if (list.size() == 0) {
            return false;
        }
        for (Dto dto1 : list) {
            if (dto.getAsString("key").split("#")[0].equals(dto1.getAsString("product_key"))) {
                if (distinguishPlatform) {
                    if (PlatformDict.getPlatformName(dto.getAsString("key").split("#")[3]).equals(dto1.getAsString("platform"))) {
                        dto1.put("total", dto1.getAsLong("total") + dto.getAsLong("value"));
                        return true;
                    }
                } else {
                    dto1.put("total", dto1.getAsLong("total") + dto.getAsLong("value"));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理产品名
     *
     * @param list
     * @throws Exception
     */
    private void dealProductName(List<Dto> list) throws Exception {
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
    }

    private void dealDayList(List<Dto> dayList, List<Dto> list, boolean distinguishPlatform, String platform, String day) throws Exception {
        if (dayList.size() > 0) {
            for (Dto dto : dayList) {
                String productKey = dto.getAsString("key").split("#")[0];
                String pkg = dto.getAsString("key").split("#")[3];

                Dto dtoByProductKey = checkDto(getDtoByProductKey(list, productKey, pkg, distinguishPlatform, day), productKey, platform, list, pkg, distinguishPlatform, day);
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
}
