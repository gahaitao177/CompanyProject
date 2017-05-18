package com.caiyi.nirvana.analyse.cassandra;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.CassandraTestSupport;
import com.caiyi.nirvana.analyse.cassandra.test.Bean;
import com.caiyi.nirvana.analyse.cassandra.test.Been;
import com.caiyi.nirvana.analyse.cassandra.test.User;
import com.caiyi.nirvana.analyse.cassandra.test.UserAccessor;
import com.caiyi.nirvana.analyse.model.AppProfile;
import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by been on 2016/12/26.
 */
public class DBSetup extends CassandraTestSupport {
    @Test
    public void test1() {
        Cluster cluster = null;
        try {
            cluster = getCluster("127.0.0.1", 9042);
            Session session = cluster.connect("nirvana");
//            ResultSet rs = session.execute("select release_version from system.local");
//            Row row = rs.one();
//            System.out.println(row.getString(0));
            ResultSet rs = session.execute("select * from app_profile");
            MappingManager mappingManager = new MappingManager(session);
            Mapper<AppProfile> appProfileMapper = mappingManager.mapper(AppProfile.class);
            Result<AppProfile> appProfiles = appProfileMapper.map(rs);
            AppProfile appProfile = appProfiles.iterator().next();
            System.out.println(JSONObject.toJSONString(appProfile, true));
        } finally {
            cluster.close();
        }
    }

    @Test
    public void testMapper() {
        Cluster cluster = null;
        try {
            cluster = getCluster("127.0.0.1", 9042);
            Session session = cluster.connect("demo");
            MappingManager mappingManager = new MappingManager(session);
            Mapper<User> userDao = mappingManager.mapper(User.class);

            UUID uuid = UUID.randomUUID();
            User user = new User(uuid, "been1");
            userDao.save(user);
            User queryResult = userDao.get(uuid);

            System.out.println(queryResult.getName());

            ResultSet rs = session.execute("select * from user");
            Result<User> users = userDao.map(rs);
            for (User user1 : users) {
                System.out.println(user1.getName());
            }

            //测试时间
            Mapper<Been> beenMapper = mappingManager.mapper(Been.class);
            uuid = UUID.randomUUID();
            String name = "been1";
            Date now = new Date();
            Set<Bean> beans = new HashSet<>();
            Bean bean1 = new Bean(22, "beijing");
            Bean bean2 = new Bean(23, "shanghai");
            beans.add(bean1);
            beans.add(bean2);

            Been been = new Been(uuid, name, now);
            been.setBeans(beans);
            beenMapper.save(been);

            rs = session.execute("select * from been");
            Result<Been> beens = beenMapper.map(rs);
            for (Been _been : beens) {
                if (_been.getBeans().size() > 0) {
                    System.out.println(_been.getBeans().size());
                }
//                System.out.println(_been.getCtime());
            }


        } finally {
            cluster.close();
        }
    }


    @Test
    public void testAccessor() {
        Cluster cluster = null;
        try {
            cluster = getCluster("127.0.0.1", 9042);
            Session session = cluster.connect("demo");
            MappingManager mappingManager = new MappingManager(session);
            UserAccessor userAccessor = mappingManager.createAccessor(UserAccessor.class);
            Result<User> users = userAccessor.getAll();
            for (User user : users) {
                System.out.println(user.getName());
            }
        } finally {
            cluster.close();
        }
    }


}
