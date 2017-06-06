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

    public List<Dto> getProductsStatsDetails(boolean isDistinPlatform) throws Exception {
        Date today = new Date();
        Date yesday = DateUtil.getLastDay(today, 1);

        String products = "yy_gjj,yy_jz";
        String dataTypes = "new_user,active_user,start_times";
        String tableName = "app_data_daily";

        //平台
        String platform = "全平台";

        //截止到昨天的总数
        String totalType = "total_user";

        //获取今天的数据
        List<Dto> todayList = getProductByDate(today, products, dataTypes, tableName);
        //获取昨天的数据
        List<Dto> yesList = getProductByDate(yesday, products, dataTypes, tableName);
        //昨日累计用户
        List<Dto> totalList = getProductByDate(yesday, products, totalType, tableName);

        //合并两天的数据
        List<Dto> list = new ArrayList<Dto>();
        if (todayList.size() > 0) {
            for (Dto dto : todayList) {
                String productKey = dto.getAsString("key").split("#")[0];
                String pkg = dto.getAsString("key").split("#")[3];

                Dto dtoByProductKey = checkDto(getDtoByProductKey(list, productKey, pkg, isDistinPlatform), productKey, platform, list, pkg, isDistinPlatform);
                List<String> keys = Arrays.asList(dto.getAsString("key").split("#"));

                //处理新增用户
                if (keys.contains("new_user")) {
                    dtoByProductKey.put("todayNewUser", dtoByProductKey.getAsLong("todayNewUser") + dto.getAsLong("value"));
                }
                //处理活跃用户
                if (keys.contains("active_user")) {
                    dtoByProductKey.put("todayActiveUser", dtoByProductKey.getAsLong("todayActiveUser") + dto.getAsLong("value"));
                }
                //处理启动次数
                if (keys.contains("start_times")) {
                    dtoByProductKey.put("todayStartTimes", dtoByProductKey.getAsLong("todayStartTimes") + dto.getAsLong("value"));
                }
            }

        }
        if (yesList.size() > 0) {
            for (Dto dto : yesList) {
                String productKey = dto.getAsString("key").split("#")[0];
                String pkg = dto.getAsString("key").split("#")[3];

                Dto dtoByProductKey = checkDto(getDtoByProductKey(list, productKey, pkg, isDistinPlatform), productKey, platform, list, pkg, isDistinPlatform);
                List<String> keys = Arrays.asList(dto.getAsString("key").split("#"));

                //处理新增用户
                if (keys.contains("new_user")) {
                    dtoByProductKey.put("yesNewUser", dtoByProductKey.getAsLong("yesNewUser") + dto.getAsLong("value"));
                }
                //处理活跃用户
                if (keys.contains("active_user")) {
                    dtoByProductKey.put("yesActiveUser", dtoByProductKey.getAsLong("yesActiveUser") + dto.getAsLong("value"));
                }
                //处理启动次数
                if (keys.contains("start_times")) {
                    dtoByProductKey.put("yesStartTimes", dtoByProductKey.getAsLong("yesStartTimes") + dto.getAsLong("value"));
                }
            }
        }

        //处理产品名
        if (list.size() > 0) {
            for (Dto dto : list) {
                List<KeyValue> keyValues = hbaseService.getByRowkeyColumn("product_dic", dto.getAsString("productKey"), "info:product_name,info:app_platform");
                if (keyValues != null && keyValues.size() > 0) {
                    for (KeyValue keyValue : keyValues) {
                        dto.put("productName", Bytes.toString(keyValue.getValue()).replaceAll("\"", ""));
                    }
                }
            }
        }

        //处理今日累计用户
        if (totalList.size() > 0) {
            for (Dto dto : totalList) {
                if (list.size() > 0) {
                    for (Dto dto1 : list) {
                        if (dto.getAsString("key").split("#")[0].equals(dto1.getAsString("productKey"))) {
                            if (isDistinPlatform) {
                                if (PlatformDict.getPlatformName(dto.getAsString("key").split("#")[3]).equals(dto1.getAsString("platform"))) {
                                    dto1.put("todayTotal", dto.getAsLong("value") + dto1.getAsLong("todayTotal"));
                                }
                            } else {
                                dto1.put("todayTotal", dto.getAsLong("value") + dto1.getAsLong("todayTotal"));
                            }

                        }
                    }
                }
            }
        }
        //加上今日新增的
        if (list.size() > 0) {
            for (Dto dto : list) {
                dto.put("todayTotal", dto.getAsLong("todayTotal") + dto.getAsLong("todayNewUser"));
            }
        }

        return list;
    }


    private Dto checkDto(Dto dtoByProductKey, String productKey, String platform, List<Dto> list, String pkg, boolean isDistinPlatform) {
        if (dtoByProductKey == null) {
            dtoByProductKey = new BaseDto();
            dtoByProductKey.put("productKey", productKey);
            dtoByProductKey.put("platform", platform);
            dtoByProductKey.put("todayNewUser", 0l);
            dtoByProductKey.put("todayActiveUser", 0l);
            dtoByProductKey.put("todayStartTimes", 0l);
            dtoByProductKey.put("yesNewUser", 0l);
            dtoByProductKey.put("yesActiveUser", 0l);
            dtoByProductKey.put("yesStartTimes", 0l);
            dtoByProductKey.put("todayTotal", 0l);

            if (isDistinPlatform) {
                dtoByProductKey.put("platform", PlatformDict.getPlatformName(pkg));
            }
            if ((isDistinPlatform && !StringUtils.isEmpty(pkg.trim())) || !isDistinPlatform) {
                list.add(dtoByProductKey);
            }

        }
        return dtoByProductKey;
    }

    private Dto getDtoByProductKey(List<Dto> list, String productKey, String pkg, boolean isDistinPlatform) {
        if (list == null || list.size() == 0) {
            return null;
        }
        for (Dto dto : list) {
            if (productKey.equals(dto.getAsString("productKey"))) {
                if (!isDistinPlatform) {
                    return dto;
                }
                if (PlatformDict.getPlatformName(pkg).equals(dto.getAsString("platform"))) {
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
                }
            }
        }
        return dtos;
    }

    public Dto getProductsStatsTotal() throws Exception {
        Dto totalDto = initTotalDto();
        List<Dto> dtoList = getProductsStatsDetails(false);

        if (dtoList != null && dtoList.size() > 0) {
            for (Dto dto : dtoList) {
                totalDto.put("todayAdd", totalDto.getAsLong("todayAdd") + dto.getAsLong("todayNewUser"));
                totalDto.put("todayActive", totalDto.getAsLong("todayActive") + dto.getAsLong("todayActiveUser"));
                totalDto.put("todayStarttimes", totalDto.getAsLong("todayStarttimes") + dto.getAsLong("todayStartTimes"));
                totalDto.put("yesAdd", totalDto.getAsLong("yesAdd") + dto.getAsLong("yesNewUser"));
                totalDto.put("yesActive", totalDto.getAsLong("yesActive") + dto.getAsLong("yesActiveUser"));
                totalDto.put("yesStarttimes", totalDto.getAsLong("yesStarttimes") + dto.getAsLong("yesStartTimes"));
                totalDto.put("todayTotal", totalDto.getAsLong("todayTotal") + dto.getAsLong("todayTotal"));
                totalDto.put("yesTotal", totalDto.getAsLong("todayTotal") - dto.getAsLong("todayNewUser"));
            }
        }

        return totalDto;
    }

    private Dto initTotalDto() {
        Dto dto = new BaseDto();
        dto.put("todayAdd", 0);
        dto.put("todayActive", 0);
        dto.put("todayStarttimes", 0);
        dto.put("todayTotal", 0);
        dto.put("yesAdd", 0);
        dto.put("yesActive", 0);
        dto.put("yesStarttimes", 0);
        dto.put("yesTotal", 0);

        return dto;
    }
}
