package com.caiyi.nirvana.analyse.akka;

import akka.actor.Actor;
import akka.actor.Props;
import akka.japi.Creator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.dto.CityDto;
import com.caiyi.nirvana.analyse.model.AppProfile;
import com.caiyi.nirvana.analyse.service.AppProfileService;
import com.caiyi.nirvana.analyse.service.RedisService;
import com.caiyi.nirvana.analyse.utils.IpUtil;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;


/**
 * Created by been on 2017/1/11.
 */
public class CassandraActor extends BaseActor {
    private final AppProfileService service;
    private final RedisService redisService;

    public static Props props(final Tuple tuple,
                              final BasicOutputCollector collector,
                              final AppProfileService service,
                              final RedisService redisService) {
        return Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Actor create() throws Exception {

                return new CassandraActor(tuple, collector, service, redisService);
            }
        });
    }

    public CassandraActor(Tuple tuple, BasicOutputCollector collector, AppProfileService service, RedisService redisService) {
        super(tuple, collector);
        this.service = service;
        this.redisService = redisService;
    }

    @Override
    protected void doBusiness(Tuple tuple, BasicOutputCollector collector) {
        String msg = tuple.getString(0);
        if (msg.equals("demo")) {
            logger.info("#############test###############");
        } else {
            persist(msg);
//            logger.info(msg);
        }
    }

    private void persist(String msg) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(msg);
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    AppProfile appProfile = buildAppProfile(jsonArray.getJSONObject(i));
                    logger.warn("appIp  " + appProfile.getAppIp());
                    service.save(appProfile);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    private AppProfile buildAppProfile(JSONObject jsonObject) {
        String data = jsonObject.toJSONString();
        AppProfile appProfile = JSONObject.parseObject(data, AppProfile.class);
        String ip = appProfile.getAppIp();
        CityDto cityDto = getCity(ip);
        if (cityDto != null) {
            appProfile.setCityCode(cityDto.getCityCode());
            appProfile.setCity(cityDto.getCity());
            appProfile.setProvince(cityDto.getProvince());
        }
        return appProfile;
    }

    public CityDto getCity(String ip) {
        CityDto cityDto = null;
        try {
            String key = "ip_" + ip;
            String cityDtoStr = redisService.get(key);
            if (cityDtoStr == null) {
                JSONObject jsonObject = IpUtil.getCityJson(ip);
                JSONObject address_detail = jsonObject.getJSONObject("content").getJSONObject("address_detail");
                cityDto = new CityDto();
                cityDto.setCity(address_detail.getString("city"));
                cityDto.setCityCode(address_detail.getString("city_code"));
                cityDto.setProvince(address_detail.getString("province"));
                redisService.set(key, JSONObject.toJSONString(cityDto));
            } else {
                cityDto = JSONObject.parseObject(cityDtoStr, CityDto.class);
            }
        } catch (Exception e) {

        }
        return cityDto;

    }
}
