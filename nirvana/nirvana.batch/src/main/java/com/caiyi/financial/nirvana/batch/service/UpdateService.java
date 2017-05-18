package com.caiyi.financial.nirvana.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lichuanshun on 16/10/20.
 */
@Service
public class UpdateService {

    public Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UpdateService() {
    }

    public UpdateService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void update(String areaId, String phone) {
        String sql = "update tb_user t set t.iphoneattribution=? where t.cphone=?";
        jdbcTemplate.update(sql, new Object[]{areaId, phone});
    }

    /**
     * @param sql
     * @return
     */
    public List<String> query(String sql) {
        List<String> userPhones = jdbcTemplate.queryForList(sql, String.class);
        return userPhones;
    }

    /**
     * @param phone
     * @param areaId
     * @return
     */
    public String updateUser(String areaId, String phone) {
        String result = "0";
        String sql = "update tb_user t set t.iphoneattribution=? where t.cphone=?";
        jdbcTemplate.update(sql, new Object[]{areaId, phone});
        return result;
    }
}
