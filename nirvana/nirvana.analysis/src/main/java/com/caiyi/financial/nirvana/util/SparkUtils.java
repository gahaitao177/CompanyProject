package com.caiyi.financial.nirvana.util;

import com.caiyi.financial.nirvana.bank.SecondarySortKey;
import com.caiyi.financial.nirvana.bank.ThirdSortKey;

/**
 * Spark工具类
 *
 * @author Administrator
 */
public class SparkUtils {

    /**
     * @param param1 二次排序的第一个参数param1
     * @param param2 二次排序的第二个参数param2
     * @return SecondarySortKey
     */
    public static SecondarySortKey getSecondarySortKey(String param1, String param2) {
        SecondarySortKey secondKey = new SecondarySortKey(param1, param2);
        return secondKey;
    }

    /**
     * @param param1 三次排序的第一个参数param1
     * @param param2 三次排序的第二个参数param2
     * @param param3 三次排序的第三个参数param3
     * @return ThirdSortKey
     */
    public static ThirdSortKey getThirdSortKey(String param1, String param2, String param3) {
        ThirdSortKey thirdKey = new ThirdSortKey(param1, param2, param3);
        return thirdKey;
    }

}
