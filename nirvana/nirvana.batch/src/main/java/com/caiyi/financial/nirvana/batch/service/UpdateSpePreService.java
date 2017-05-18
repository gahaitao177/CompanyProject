package com.caiyi.financial.nirvana.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhukai on 16/12/1.
 */
@Service
public class UpdateSpePreService {

    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UpdateSpePreService() {
    }

    public UpdateSpePreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * @param ccontent preferentialid
     * @return i
     */
    public int update(String ccontent, String preferentialid) {
        String sql = " update tb_special_preferential t set t.ccontent=? where t.preferentialid=?";
        int result = jdbcTemplate.update(sql, new Object[]{ccontent, preferentialid});
        return result;
    }

    /**
     * @return 返回一个IidAndUrlList
     */
    public List<Map<String, Object>> query() {
        String sql = "select PREFERENTIALID AS ID,CURL from tb_special_preferential WHERE rownum < 2000";
        List<Map<String, Object>> idAndUrlList = jdbcTemplate.queryForList(sql);
        return idAndUrlList;
    }

    /**
     * 查询 CSUMMARY
     *
     * @param icontactid
     * @return result 里面是 CSUMMARY
     */
    public List<String> query(String icontactid) {
        String sql = "select CSUMMARY from tb_contact tc where tc.itype  = 0 and tc.ipublished =1 and tc.icontactid =?";
        List<String> result = jdbcTemplate.queryForList(sql, new Object[]{icontactid}, String.class);
        return result;
    }
}
