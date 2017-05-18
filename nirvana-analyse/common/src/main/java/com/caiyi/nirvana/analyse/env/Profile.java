package com.caiyi.nirvana.analyse.env;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by been on 2017/1/13.
 */
public class Profile {
    public static Properties props = new Properties();
    public final static Profile instance = new Profile();
    public Profile() {
        if (props.size() == 0 ) {
            loadConf();
        }
    }

    public boolean isProd() {
        if (props.size() == 0) {
            props = new Properties();
            loadConf();
        }
        return props.get("env").equals("prod");
    }

    private synchronized static void loadConf() {
        try {
            props.load(Profile.class.getResourceAsStream("/env.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
