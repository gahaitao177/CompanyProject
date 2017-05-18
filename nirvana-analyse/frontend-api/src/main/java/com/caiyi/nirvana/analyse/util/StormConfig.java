package com.caiyi.nirvana.analyse.util;

import com.caiyi.nirvana.analyse.env.Profile;

/**
 * Created by general on 2016/1/25.
 */
public class StormConfig {
    public static String STORM_SERVCE = Profile.instance.isProd() ? "192.168.83.40" : "192.168.1.88";//正式，测试

}
