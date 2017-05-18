package com.caiyi.nirvana.analyse.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Util {

    /**
     * @param clientDate:客户端操作时间
     * @param serverDate:服务器中保存的操作时间
     * @return true:有效数据  false:无效数据
     */
    public static boolean dataEffectCheck(String clientDate, String serverDate) {
        if (CheckUtil.isNullString(serverDate)) {
            return true;
        }
        clientDate = clientDate.replaceAll("-", "").replaceAll(":", "").replaceAll("\\.", "").replaceAll(" ", "");
        serverDate = serverDate.replaceAll("-", "").replaceAll(":", "").replaceAll("\\.", "").replaceAll(" ", "");
        long client = Long.parseLong(clientDate);
        long server = Long.parseLong(serverDate);
        return client > server;

    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }


    public static Map<String, String> insertLog(String cuserid, String itype, String cdetails) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cuserid", cuserid);
        map.put("itype", itype);
        map.put("cdetails", cdetails);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(dataEffectCheck("2015-12-02 10:00:59.234", "2015-12-02 09:00:59.234"));
    }


    public static int getAppVersion(String appVersion) {
        int version = 100;
        if (!CheckUtil.isNullString(appVersion)) {
            String[] appVersions = appVersion.split("\\.");
            if (appVersions.length == 3) {
                int number = 0;
                if (appVersions[2].length() != 1) {
                    number = Integer.parseInt(appVersions[2].substring(0, 1));
                }
                version = Integer.parseInt(appVersions[0]) * 100 + Integer.parseInt(appVersions[1]) * 10 + number;
            } else {
                throw new RuntimeException("appVersion异常");
            }
        }
        return version;

    }
}
