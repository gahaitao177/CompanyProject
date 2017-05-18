package com.caiyi.financial.nirvana.ccard.material.mapper;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/26.
 */
public interface MaterialCardMapper {

    //查询银行的办卡id
   @Select("select cbankcardid \"cbankcardid\",ccardlevel \"ccardlevel\"  from tb_bank_card where ibankid=#{ibankid,jdbcType=VARCHAR} and " +
           "icardid=#{icardid,jdbcType=VARCHAR} and cbankcardid is not null")
   Map<String,String> queryBankCardId(@Param("ibankid") String ibankid,@Param("icardid") String icardid);

    //查询银行对应的省、市
    Map<String,String> query_bank_ProOrCity(Map<String,String> map);

    //查询银行对应的县(区)
    @Select("select cname \"cname\"  from tb_district where " +
            "icityid=#{icityid,jdbcType=VARCHAR} and ipcityid=#{ipcityid,jdbcType=VARCHAR}")
    String queryBankDistrict(@Param("icityid") String icityid,@Param("ipcityid") String ipcityid);

    //转换对应银行的关系，如行业性质、单位性质等
    Map<String,String> apply_credit_relation(Map<String,String> map);
    //查询行业性质
    @Select("select t.cbankid \"cbankid\" from tb_applycredit_relation t where t.ibankid=1 and t.itype=2 " +
            "and t.clocalid=#{clocalid,jdbcType=VARCHAR}")
    String queryIniatureofbusiness(@Param("clocalid") String clocalid);

    //查询发证机关
    @Select("select cname \"cname\" from tb_district where icityid=?")
    String queryDepartment();

    //查询银行对应的县(区)
   @Select("select cname \"cname\"  from tb_district where " +
         "icityid=#{icityid,jdbcType=VARCHAR}")
   String queryBankDistrict2(@Param("icityid") String icityid);

    //不完整资料表是否存在该手机的资料
    @Select("select count(1) from tb_apply_credit_material_dirty where ctmpphone=#{phone,jdbcType=VARCHAR}")
    Integer findCreditMaterialDirty(@Param("phone") String phone);
    //保存不完整数据
    Integer save_apply_credit_material_dirty(MaterialModel model);

    //更新不完整数据
    Integer update_apply_credit_material_dirty(MaterialModel model);
   //根据城市查询支持O2O办卡的银行
    @Select("select t1.ccode \"ccode\",t1.cname \"cname\",t2.hotdeal \"hotdeal\" from " +
            "( select t.ccode, t.cname from tb_loan_dictionary t where t.itype = 2 and t.ipdicid =" +
            "(select idicid from tb_loan_dictionary where ccode =#{ccode,jdbcType=VARCHAR} " +
            "and itype = 1 and istatus=1 ))t1 left join tb_card_bank_sort t2 on t1.ccode=t2.ibankid")
    List<Map<String,Object>> queryO2OBank(@Param("ccode") String ccode);
    //根据区查询商业圈
    @Select("select t.iareaid \"iareaid\",t.careaname \"careaname\" from tb_area t where t.ipareaid=" +
            "(select iareaid from tb_area where adcode=#{adcode,jdbcType=VARCHAR} and iareatype=2)")
    List<Map<String,Object>> queryBusiness(@Param("adcode") String adcode);

   //根据县区或者镇区、商圈的id查询地区名称
   @Select(" select t.iareaid,t.careaname from tb_area t where t.adcode=#{adcode，jdbcType=VARCHAR} and t.iareatype=1 union all " +
         " select t.iareaid,t.careaname from tb_area t where t.adcode=#{icountycode,jdbcType=VARCHAR} and t.iareatype=2 union all " +
         " select t.iareaid,t.careaname from tb_area t where t.iareaid=#{iareaid,jdbcType=VARCHAR}")
    List<Map<String,Object>> queryArea(Map<String,String> map);

   //查询用户成功申请的银行
   @Select("select t.ibankid,t.isuccess from tb_card_apply t where " +
           "t.cphonenum=#{phone,jdbcType=VARCHAR} and t.cadddate>=sysdate-30")
   List<Map<String,Object>> queryBank(@Param("phone") String phone);

 //保存O2O申卡资料
  int saveApplyCard(Map<String ,String> para);

 //查询推广银行列表
 List<Map<String,Object>> query_spreadBank(Map<String,String> para);

 //更新列表点击申卡统计
 @Update("update tb_spread_card t set t.iapplynum = t.iapplynum+1 where t.ispreadid=#{ispreadid,jdbcType=VARCHAR}")
 Integer updateCardListCount(@Param("ispreadid") String ispreadid);

 //更新详情页点击申卡统计
 @Update("update tb_spread_card t set t.idetailapplynum = t.idetailapplynum+1 " +
         "where t.ispreadid=#{ispreadid,jdbcType=VARCHAR}")
 Integer updateCardDetailPageCount(@Param("ispreadid") String ispreadid);

 //更新点击详情统计
 @Update("update tb_spread_card t set t.idetailnum = t.idetailnum+1 where" +
         " t.ispreadid=#{ispreadid,jdbcType=VARCHAR}")
 Integer updateCardDetailCount(@Param("ispreadid") String ispreadid);
 //更新跳转H5链接浏览量
 @Update("update tb_spread t set t.iclicks = t.iclicks+1 where t.ichannelid=#{ispreadid,jdbcType=VARCHAR}")
 Integer updateCardH5Count(@Param("ispreadid") String ispreadid);

 //更新APP下载点击量
 @Update("update tb_spread t set t.idownload = t.idownload+1 where t.ichannelid=#{ispreadid,jdbcType=VARCHAR}")
 Integer updateCardAPPCount(@Param("ispreadid") String ispreadid);
 //推广列表
 List<Map<String,Object>> query_spreadCard(Map<String,String> map);
 //查询进度 查询申卡的卡名称
@Select("select t2.ccardname,t2.cothername from(select * from tb_apply_credit_log t where " +
        "t.iapplyid=#{iapplyid,jdbcType=VARCHAR})t1 left join tb_bank_card t2 on t1.ICREDITID=t2.icardid")
 Map<String,Object> queryCreditName(@Param("iapplyid") String iapplyid);
 //平安银行 查询进度 查询申卡 时间和卡id
 @Select("select icreditid,to_char(capplydate,'yyyy-mm-dd') capplydate from tb_apply_credit_log " +
         "where iapplyid = #{iapplyid,jdbcType=VARCHAR}")
 Map<String,Object> queryApplyCreditDate(@Param("iapplyid") String iapplyid);
 //更新申请日志的状态
 @Select("update tb_apply_credit_log set istatus=#{istatus,jdbcType=VARCHAR}," +
         "cresults=#{cresults,jdbcType=VARCHAR} where iapplyid=#{iapplyid,jdbcType=VARCHAR}")
 Integer updateApplyLog(Map<String,String> map);

 //通过身份证号和手机号查询资料id
 @Select("select imaterialid from tb_apply_credit_material where " +
         "cphone=#{cphone,jdbcType=VARCHAR} and cidcard=#{cidcard,jdbcType=VARCHAR}")
 String queryMaterialIdByPhoneAndId(@Param("cphone") String cphone,@Param("cidcard") String cidcard);
 //申卡资料的序列id
 @Select("select SEQ_A_C_MATERIAL.nextval id from dual")
 Integer queryMaterialId();
 //保存申请信息的log日志
 Integer save_apply_credit_log(Map<String,String> map);
 //查询城市的O2O办卡是否打开
@Select("select t.ccode \"ccode\",t.ciso2o \"ciso2o\" from tb_loan_dictionary t where t.itype=1")
 List<Map<String,Object>> queryO2OCityIsOpen();
 //查询申请记录
 Map<String,Object> apply_credit_log_by_id(@Param("iapplyid") String iapplyid);

 @Select(" select a.imaterialid  \"imaterialid\",a.icreditid \"icreditid\",a.ibankid \"ibankid\"," +
         "a.cidcard \"cidcard\",a.cname \"cname\" from tb_apply_credit_log  a  where " +
         "a.iapplyid =#{iapplyid,jdbcType=VARCHAR} ")
 Map<String,String> queryMaterialLogByApplyId(@Param("iapplyid") String iapplyid);
}
