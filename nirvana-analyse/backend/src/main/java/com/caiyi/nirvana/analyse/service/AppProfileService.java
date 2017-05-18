package com.caiyi.nirvana.analyse.service;

import com.caiyi.nirvana.analyse.env.Profile;
import com.caiyi.nirvana.analyse.model.AppProfile;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by been on 2017/1/12.
 * 将AppProfile modle 存入cassandra 数据库
 */
public class AppProfileService {
    private Mapper<AppProfile> _service;
    private static final String KEYSPACE = "nirvana";

    public AppProfileService() {
        Properties properties = new Properties();
        try {
            if (Profile.instance.isProd()) {
                properties.load(getClass().getResourceAsStream("/cassandra_prod.properties"));
            } else {
                properties.load(getClass().getResourceAsStream("/cassandra_dev.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String contractPoints = properties.getProperty("contractPoints", "127.0.0.1");
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(contractPoints)
                .build();
        Session session = cluster.connect(KEYSPACE);
        MappingManager mappingManager = new MappingManager(session);
        _service = mappingManager.mapper(AppProfile.class);
    }

    public void save(AppProfile appProfile) throws Exception {
        UUID uuid = UUID.randomUUID();
        appProfile.setCtime(new Date());
        appProfile.setId(uuid);
        _service.save(appProfile);
    }

    public void iterate() {
        ResultSet rs = _service.getManager().getSession().execute("select * from app_profile");
        Result<AppProfile> list = _service.map(rs);
        list.forEach(appProfile -> System.out.println(appProfile.getCtime()));
    }
}
