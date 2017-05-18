package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CreditLifeMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import test.TestSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by jianghao on 2016/12/13.
 * 征信service的test
 */
public class CreditScoreServiceTest extends TestSupport {
    @Autowired
    private CreditScoreService scoreService;
    @Autowired
    private CreditScoreCalculateService scoreCalculateService;
    @Autowired
    private  CreditLifeService lifeService;

    @Autowired
    private CreditLifeMapper lifeMapper;
    //获取信用特权
    @Test
    public void queryCreditPrivilege() throws Exception {
        System.out.println("queryCreditPrivilege:"+ JSON.toJSON(scoreService.queryCreditPrivilege(4)));

    }
    //获取战胜用户百分比
    @Test
    public void queryTopRate() throws Exception {
        System.out.println("queryTopRate:"+ JSON.toJSON(scoreService.queryTopRate("test1")));
    }
    /**
     * 获得最大值积分
     */
    @Test
    public void getMaxScoresByCreditScoreBean() {
        CreditScoreBean bean=new CreditScoreBean();
        //bean.setGjjId(1);
        //bean.setXykId("13|14|15");
        bean.setZxId(27);
        System.out.println("最大积分:" + JSON.toJSON(scoreCalculateService.getMaxScoresByCreditScoreBean(bean)));
    }
    //更新征信
    @Test
    @Commit
    public void updatezx() throws Exception {
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setCuserId("84d45b82c303");
        System.out.println("queryTopRate:"+ JSON.toJSON(scoreService.creditInvestigation(creditScoreBean,"50")));
    }
    //更新信用卡
    @Test
    @Commit
    public void updatexyk() throws Exception {
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setCuserId("07ab10b0219");
//        System.out.println("queryTopRate:"+ JSON.toJSON(scoreService.creditCard(creditScoreBean)));
    }
    //查询用户数据
    @Test
    public void queryCreditScore() throws Exception {
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setCuserId("8dba725f215");
        System.out.println("queryCreditScore:"+ JSON.toJSON(scoreService.queryCreditScoreIndex(creditScoreBean)));
    }
    /**
     * 测试公积金接口
     */
    @Test
    public void getGjjBean() {
        CreditScoreBean scoreBean=new CreditScoreBean();
        scoreBean.setAppId("lcD2016OI1V227OVST0413HYF0L53MJ58");
        String  accessToken="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtFc+vSczuSZ/C5CKEu2AxMHPKaFyQooy2mxxPLt1dCCD14iuWvGNj5W09oszSzhLiPOHnsRb7T2AE24g1o3XZKuWztw692M3WRNp6wTzJbqsdMClFsyLG6bS9HGVgkTvZvtsZg955I6g==";
        scoreBean.setAccessToken(accessToken);
        System.out.println("公积金:" + JSON.toJSON(lifeService.getGjjBean(scoreBean)));
    }
    //更新公积金
    @Test
    @Commit
    public void updategjj() throws Exception {
        CreditScoreBean scoreBean =new CreditScoreBean();
        scoreBean.setAppId("lc20170103B09VFUNKQXV5PIUZ30239G6");
        String  accessToken="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsCA0FjABdTrtnlpSHMhSCOLc+T0iagcC+TrAvDDeDWT1IhaH1zGbzd9t2hCSOa3oNHvrKUWC5I7W1l5RuFNnMp2M3na67FA679lkdYgSStlGgB/BuxCUtCHMZ/oucjBBq3lQ++5rTRRA==";
        scoreBean.setAccessToken(accessToken);
        scoreBean.setCuserId("586f0117a690");
        System.out.println("updategjj:"+ JSON.toJSON(scoreService.providentFund(scoreBean)));
    }
    /**
     * 测试信用生活
     */
    @Test
    public void creditLife() {
        CreditScoreBean scoreBean=new CreditScoreBean();
        scoreBean.setAdcode("310100");
        scoreBean.setCuserId("65865f07a690");
        System.out.println("信用特权和banner:" + JSON.toJSON(lifeService.queryCreditLife(scoreBean)));
    }
    //更新社保
    @Test
    @Commit
    public void updateSb() throws Exception {
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setAccessToken("+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtFc+vSczuSZ/C5CKEu2AxMHPKaFy" +
                "Qooy2mxxPLt1dCCD14iuWvGNj5W09oszSzhLiPOHnsRb7T2AE24g1o3XZKuWztw692M3WRNp6wTzJbqsdMClFsyLG6bS9HG" +
                "VgkTvZvtsZg955I6g==");
        creditScoreBean.setAppId("lcD2016OI1V227OVST0413HYF0L53MJ58");
        creditScoreBean.setCuserId("8dba725f215");
        System.out.println("updategSb:"+ JSON.toJSON(scoreService.socalInsurance(creditScoreBean)));
    }
    //查询历史积分
    @Test
    public void getHistoryScores(){
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setCuserId("abd85cda9fae48d5a54ed4bd373315f6");
        System.out.println("获得历史积分:"+ JSON.toJSON(scoreService.getHistoryScores(creditScoreBean)));
    }
    //获得社保信息
    @Test
    public  void getSbBean(){
        CreditScoreBean creditScoreBean =new CreditScoreBean();
        creditScoreBean.setSource(13011);
        //可以获得社保的token
        creditScoreBean.setAccessToken("+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtFc+vSczuSZ/C5CKEu2AxMHPKaFy" +
                "Qooy2mxxPLt1dCCD14iuWvGNj5W09oszSzhLiPOHnsRb7T2AE24g1o3XZKuWztw692M3WRNp6wTzJbqsdMClFsyLG6bS9HG" +
                "VgkTvZvtsZg955I6g==");
        creditScoreBean.setAppId("lcD2016OI1V227OVST0413HYF0L53MJ58");
        creditScoreBean.setCuserId("8dba725f215");

        //可以获得社保的token
//        creditScoreBean.setAccessToken("+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsCA0FjABdTrlhNK+MGDQC7eGmRT5bZ" +
//                "Zverwkxl1txKCEsFOAmY3oLd/SwpmfnAjVIbPi6RG/In3P7DOoa22MpeBpE/iwGctIrXjvWOYRWbUpMPcJvo4YavJQj868" +
//                "9Lx4pi4b9DxiZgPA==");
//        creditScoreBean.setAppId("lc2016RZD0CVX12003O120I8RON27GXM5");
        System.out.println("获得社保信息:"+ JSON.toJSON(lifeService.getSbBean(creditScoreBean)));
    }
    @Test
    public void querySwitch(){
        List<Map<String,String>> maps=lifeMapper.querySwitchs();
        String sb1="";
        String zx1="";
        String xyk1="";
        String gjj1="";
        System.out.println("maps:"+JSON.toJSON(maps));
        for(Map<String,String> map: maps){
            for (String key:map.keySet()){
                if("name".equals((key))) {
                    if ("sb".equals(map.get(key))) {
                        sb1 = map.get("isOpen");
                    }
                    if ("zx".equals(map.get(key))) {
                        zx1 = map.get("isOpen");
                    }
                    if ("xyk".equals(map.get(key))) {
                        xyk1 = map.get("isOpen");
                    }
                    if ("gjj".equals(map.get(key))) {
                        gjj1 = map.get("isOpen");
                    }
                }
            }
        }
        System.out.println("获得社保信息:"+sb1+zx1+gjj1+xyk1 );
    }

}