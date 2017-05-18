package com.caiyi.nirvana.analyse.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.model.AppProfile;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by been on 2017/1/12.
 */
public class AppProfileServiceTest {
    @Test
    public void save() throws Exception {
        byte[] data = IOUtils.toByteArray(new FileInputStream(new File("data-template.json")));
        String json = new String(data);
        AppProfile appProfile = JSONObject.parseObject(json, AppProfile.class);
        AppProfileService service = new AppProfileService();
        service.save(appProfile);
    }

    @Test
    public void testIterate() throws Exception {
        AppProfileService service = new AppProfileService();
        service.iterate();
    }

}