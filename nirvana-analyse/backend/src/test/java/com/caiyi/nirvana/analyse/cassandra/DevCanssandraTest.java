package com.caiyi.nirvana.analyse.cassandra;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.CassandraTestSupport;
import com.caiyi.nirvana.analyse.model.AppProfile;
import com.caiyi.nirvana.analyse.service.AppProfileService;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by been on 2017/1/13.
 */
public class DevCanssandraTest extends CassandraTestSupport {
    @Test
    public void test() {
        Cluster cluster = null;
        try {
            cluster = getCluster("192.168.1.88", 9042);
            Session session = cluster.connect();
            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            System.out.println(row.getString(0));
        } finally {
            cluster.close();
        }
    }

    @Test
    public void initDev() {
        Cluster cluster = null;
        try {
            cluster = getCluster("192.168.1.88", 9042);
            Session session = cluster.connect();
            session.execute("create keyspace nirvana with replication = {'class':'SimpleStrategy', 'replication_factor':1};");
            session.execute("use nirvana;");
            session.execute("create type event(event_id text, time timestamp, extra text);");
            session.execute("create type history(page text, enter_time timestamp, exit_time timestamp);");
            session.execute("create table app_profile(\n" +
                    "    id uuid primary key,\n" +
                    "    app_key text,\n" +
                    "    device_id text,\n" +
                    "    device_type text,\n" +
                    "    device_os text,\n" +
                    "    device_model text,\n" +
                    "    device_brand text,\n" +
                    "    device_res text,\n" +
                    "    app_version text,\n" +
                    "    app_name text,\n" +
                    "    app_source text,\n" +
                    "    app_channel text,\n" +
                    "    app_network text,\n" +
                    "    app_gps text,\n" +
                    "    user_id text,\n" +
                    "    user_name text,\n" +
                    "    app_ip text,\n" +
                    "    ctime timestamp,\n" +
                    "    events set<frozen<event>>,\n" +
                    "    histories set<frozen<history>>\n" +
                    ");");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cluster.close();
        }
    }

    @Test
    public void dropDevKeySpace() {
        Cluster cluster = null;
        try {
            cluster = getCluster("192.168.1.88", 9042);
            Session session = cluster.connect();
            session.execute("drop keyspace nirvana");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cluster.close();
        }
    }

    @Test
    public void initDemo() {
        Cluster cluster = getCluster("192.168.1.88", 9042);
        Session session = cluster.connect();
        session.execute("create keyspace demo with replication = {'class':'SimpleStrategy', 'replication_factor':1};");
        session.execute("use demo");
        session.execute("create table user(user_id uuid primary key, name text);");
        cluster.close();
    }

    @Test
    public void makeRandomData() throws Exception {
        Cluster cluster = getCluster("192.168.1.88", 9042);
        Session session = cluster.connect();
        session.execute("use nirvana");
        String template = IOUtils.toString(new FileInputStream(new File("data-template.json")));
        AppProfile appProfile = JSONObject.parseObject(template, AppProfile.class);
        AppProfileService profileService = new AppProfileService();
        profileService.save(appProfile);

    }

    @Test
    public void testJsonArray() throws Exception {
        String template = IOUtils.toString(new FileInputStream(new File("data-template.json")));
        JSONObject jsonObject1 = JSONObject.parseObject(template);
        JSONObject jsonObject2 = JSONObject.parseObject(template);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject1);
        jsonArray.add(jsonObject2);
        String data = JSONObject.toJSONString(jsonArray, true);
        System.out.println("\n" + data);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        System.out.println("\n" + jsonObject.toJSONString());
        AppProfile appProfile = JSONObject.parseObject(jsonObject.toJSONString(), AppProfile.class);
        System.out.println(appProfile.getAppChannel());
        System.out.println(jsonArray.toJSONString());


    }

    @Test
    public void testProd() throws Exception {
        Cluster cluster = getCluster("192.168.83.26", 9042);
        Session session = cluster.connect("nirvana");

        ResultSet rs = session.execute("select * from app_profile where app_key = 'yy_chexian' allow filtering;");
        MappingManager mappingManager = new MappingManager(session);
        Mapper<AppProfile> mapper = mappingManager.mapper(AppProfile.class);
        AppProfile appProfile = mapper.map(rs).one();
        System.out.println(appProfile.getAppChannel());

    }


    @Test
    public void forFun() throws Exception {
        System.out.println(3 | 2);
    }


}
