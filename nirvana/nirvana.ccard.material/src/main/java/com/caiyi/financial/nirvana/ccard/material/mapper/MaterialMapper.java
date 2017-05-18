package com.caiyi.financial.nirvana.ccard.material.mapper;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/13.
 */
public interface MaterialMapper {

    //查询银行的总开关 0表示关闭 1表示开
    @Select("select t.isnormal from tb_apply_credit_material_p t where t.ibankid=0")
    Integer queryBankIsOpen();

    Page<Map<String,Object>> query_filterCard(Map<String, String> map);

    //根据城市的id查询银行的code
    @Select("select ccode from tb_loan_dictionary t where t.istatus = 1 " +
            "   and t.ipdicid = (select t1.idicid from tb_loan_dictionary t1\n" +
            "   right join (select t2.adcode from tb_area t2 where t2.iareaid = #{cityId,jdbcType=VARCHAR}) t3\n" +
            "   on t1.ccode = t3.adcode where t1.istatus=1)")
    List<String> queryBandCodeByCityId(@Param("cityId") String cityId);
    //查询银行的办卡地址
    @Select("select thc.cardaddr \"cardaddr\",thc.cardaddrios \"cardaddrios\",thc.cpackagename \"cpackagename\" from tb_handle_credit thc where thc.ibankid = #{bankId,jdbcType=VARCHAR}")
    Map<String,String> queryBankAddress(@Param("bankId") String bankId);

    //查询申请资料的银行信息
    @Select("select ibankid \"bankid\"，specialcode \"specialcode\"， inum \"num\"，  isnormal \"isnormal\" ，" +
            "  cxml \"cxml\"， cbankname \"bankname\" from TB_APPLY_CREDIT_MATERIAL_P")
    List<Map<String,Object>> queryMaterialBank();

    //直接查询地区信息
    @Select("select icityid \"icityid\",ccitycode \"ccitycode\",cname \"cname\",czipcode \"czipcode\", clevel \"clevel\"," +
            " ipcityid \"ipcityid\" from  TB_DISTRICT where ipcityid= #{ipcityid,jdbcType=VARCHAR} order by icityid")
    List<Map<String,Object>> queryAreaByIpcityId(@Param("ipcityid") String ipcityid);

    //通过绑定的银行查询地区信息
    @Select("select t1.icityid \"icityid\",t1.ccitycode \"ccitycode\",t1.cname \"cname\",t1.czipcode \"czipcode\", " +
            "t1.clevel \"clevel\", t1.ipcityid \"ipcityid\"  from TB_DISTRICT t1,( select icityid from TB_DISTRICT_BANK " +
            "where ibankid=#{ibankid,jdbcType=VARCHAR} and itype = #{itype,jdbcType=VARCHAR}) t2 " +
            "where t1.icityid = t2.icityid and t1.ipcityid=#{ipcityid,jdbcType=VARCHAR} order by t1.icityid")
    List<Map<String,Object>> queryAreaByIpcityIdAndBankid(Map<String,String> map);

    @Select(" select count(*) from tb_apply_credit_material where cphone=#{cphone,jdbcType=VARCHAR}")
    Integer queryMaterialByPhone(@Param("cphone") String cphone);

    Integer save_apply_credit_material(MaterialModel model);

    Integer update_apply_credit_material(MaterialModel model);

    /**申卡记录列表 @param cphone*/
    @Select("select t1.iapplyid \"iapplyid\", t1.icreditid \"icreditid\" ,t1.imaterialid \"imaterialid\"," +
            "t1.cphone \"cphone\",t1.istatus \" istatus\",t1.cresults \" cresults\",\n" +
            "t1.capplydate \" capplydate\",t1.cactivebankpoit \"cactivebankpoit\",t1.cactivebankpoitphone \"cactivebankpoitphone\"," +
            "t1.iactivetype \"iactivetype\",t1.cactivemethod \"cactivemethod\",\n" +
            "t1.cgift \" cgift\",,t1.cname \" cname\",t1.brithday \"brithday\",t3.ccardname \"ccardname\"," +
            "t3.ccardgain \"ccardgain\",t3.ccardimg \"ccardimg\",t3.ctag \"ctag\",t3.ibankid \"ibankid\"" +
            "from tb_apply_credit_log t1,(select imaterialid from tb_apply_credit_material where " +
            "cphone=#{cphone,jdbcType=VARCHAR}) t2,tb_bank_card t3 where t1.istatus>-1 and t1.imaterialid=t2.imaterialid " +
            "and t1.icreditid=t3.icardid  order by t1.CAPPLYDATE desc")
    List<Map<String,Object>> queryCreditOrder(@Param("cphone") String cphone);
    //查询申请资料的数量
    @Select("select count(*) from tb_apply_credit_log where " +
            "cphone=#{cphone,jdbcType=VARCHAR} and iapplyid=#{iapplyid,jdbcType=VARCHAR}")
    Integer queryMaterialCount(Map<String,String> map);

    //更新申请资料的状态为不可见
    @Update("update tb_apply_credit_log set istatus=-2 where iapplyid=#{iapplyid,jdbcType=VARCHAR}")
    Integer updateMaterialState(@Param("iapplyid") String iapplyid);

    //发送短信验证码
    Map<String,Object> sendYZM(Map<String,String> map);
    //校验预约办卡短信验证码
    Integer checkYZM(Map<String,String> map);

    //查询资料
    @Select("select * from tb_apply_credit_material where cphone=#{cphone,jdbcType=VARCHAR}")
    List<Map<String,Object>> findMaterial(@Param("cphone") String cphone);

    /**
     *
     * @param map
     * @return
     * add by lcs 20160806
     */
    Page<Map<String,Object>> totalSearch(Map<String, String> map);
}
