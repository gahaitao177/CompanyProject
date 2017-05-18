package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.*;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by heshaohua on 2016/5/20.
 */
public interface UserMapper extends BaseDao {

    /**
     * 更新用户密码
     *
     * @param pwd
     * @param pwd9188
     * @param cuserId
     * @return
     */
    @Update("update tb_user set cpassword=#{pwd,jdbcType=VARCHAR}, cpwd9188=#{pwd9188,jdbcType=VARCHAR} where cuserid=#{cuserId," +
            "jdbcType=VARCHAR}")
    int updateUserPwd(@Param("pwd") String pwd, @Param("pwd9188") String pwd9188, @Param("cuserId") String cuserId);

    /**
     * 更新用户手机号
     *
     * @param mobileNo
     * @param cuserId
     * @return
     */
    @Update("update tb_user set cphone=#{mobileNo,jdbcType=VARCHAR} where cuserid=#{cuserId,jdbcType=VARCHAR}")
    int updateMobileNo(@Param("mobileNo") String mobileNo, @Param("cuserId") String cuserId);


    /**
     * 查询用户信息
     **/
//    @Select("select CPASSWORD pwd,CPWD9188 pwd9188,ISTATE, cuserid from tb_user where cuserId = #{cuserId,jdbcType=VARCHAR}")
//    UserDto query_user_byId(@Param("cuserId") String cuserId, @Param("mobileNo") String mobileNo);


//    /** 查询用户信息 **/
    @Select("select CPASSWORD pwd,CPWD9188 pwd9188,istate, cuserid cuserId from tb_user where cuserId = #{cuserId,jdbcType=VARCHAR}")
    UserDto query_user_byId2(@Param("cuserId") String cuserId);


    /**
     * 查询用户类型
     **/
    int query_user_type(User user);

    /**
     * 登录
     **/
    void user_login(java.util.Map params);

    /**
     * 查询9188密码
     **/
    @Select("select t.cpwd9188 from tb_user t where (t.cphone=#{uid,jdbcType=VARCHAR} or t.cusername=#{uid,jdbcType=VARCHAR})and t" +
            ".cpassword=#{pwd,jdbcType=VARCHAR}")
    String query_user_pwd9188(@Param("uid") String uid, @Param("pwd") String pwd);

    /**  **/
    @Update("update tb_user t set t.cpassword=#{pwd,jdbcType=VARCHAR}, t.cpwd9188=#{pwd9188,jdbcType=VARCHAR} where t" +
            ".cuserid=#{cuserId,jdbcType=VARCHAR}")
    int update_pwd(@Param("pwd") String pwd, @Param("pwd9188") String pwd9188, @Param("cuserId") String cuserId);

    @Update("update tb_user t set t.itype=3,t.cusername=#{username,jdbcType=VARCHAR},t.cpwd9188=#{pwd9188,jdbcType=VARCHAR} where t" +
            ".cphone=#{mobileNo,jdbcType=VARCHAR}")
    int update_userType(@Param("username") String username, @Param("pwd9188") String pwd9188, @Param("mobileNo") String mobileNo);

    @Select("select SEQ_CUSERID.nextVal num from dual")
    int query_user_nextVal();

    /**
     * @Insert("insert into tb_user (cnickid,cpassword,cpwd9188,cusername,cphone,cregisterdate,cregip,imobbind,cuserid,isource,
     * crealname,cidcard,itype) values" +
     * "(#{cnickname,jdbcType=VARCHAR},#{pwd,jdbcType=VARCHAR},#{pwd9188,jdbcType=VARCHAR},#{cusername,jdbcType=VARCHAR},#{mobileNo,
     * jdbcType=VARCHAR},sysdate," +
     * "#{ipAddr,jdbcType=VARCHAR},#{imobbind,jdbcType=INTEGER},#{cuserid,jdbcType=VARCHAR},#{source,jdbcType=VARCHAR},#{realname,
     * jdbcType=VARCHAR},#{idcard,jdbcType=VARCHAR},#{itype,jdbcType=INTEGER})")
     **/
    int insert_user_record(User user);

    /**
     * 获取某个昵称数量
     **/
    @Select("select count(1) as num from tb_user tu where tu.cnickid = #{cnickid, jdbcType=VARCHAR}")
    int query_user_cnickid(@Param("cnickid") String cnickid);

    /**
     * 修改昵称
     ***/
    @Update("update tb_user t set t.CACTIVEDATE =#{activeDate,jdbcType=DATE },t.CNICKID =#{cnickid,jdbcType=VARCHAR} where t.cuserid" +
            " =#{cuserid, jdbcType=VARCHAR}")
    int update_user_cnickid(@Param("activeDate") Date activeDate, @Param("cnickid") String cnickid, @Param("cuserid") String cuserid);

    /***判断（微信）用户是否绑定过手机**/
    @Select("select t.cphone from tb_user t where t.cuserid=#{cuserid, jdbcType=VARCHAR}")
    String query_user_phone(@Param("cuserid") String cuserid);

    /**
     * 短信验证  检测
     **/
    void registerCheckYZM(Map<String, String> map);

    /**
     * 修改绑定 状态
     */
    @Update("update tb_user t set t.imobbind=1, t.cphone=#{cphone,jdbcType=VARCHAR} where t.cuserid=#{cuserid,jdbcType=VARCHAR}  and" +
            " t.cphone is null")
    Integer update_user_imobbind(@Param("cphone") String phone, @Param("cuserid") String cuserid);

    /**
     * 关注银行---删除此用户之前绑定的银行卡
     **/
    @Delete("delete from tb_user_bank t where t.cnickid =#{cnickid,jdbcType=VARCHAR}")
    int delete_user_bank(@Param("cnickid") String cnickid);

    /***绑定银行卡**/
    Map<String, String> bankBind(Map<String, String> map);


    /**
     * 更新超市优惠
     **/
    @Update("update tb_market_cheap tmc set tmc.isavetimes = tmc.isavetimes - 1 where tmc.icheapid =#{icheapid,jdbcType=INTEGER }")
    int update_market_cheap(@Param("icheapid") Integer icheapid);

    /**
     * 手机验证码发送
     **/
    Map<String, Object> Send_PWD_YZM(Map<String, String> map);

    /***修改用户密码**/
    @Update(" update tb_user set cpassword =#{newPwd,jdbcType=VARCHAR}, cpwd9188=#{cpwd9188,jdbcType=VARCHAR} where  cuserid = " +
            "#{cuserid,jdbcType=VARCHAR} and cpassword = #{oldPwd,jdbcType=VARCHAR}")
    Integer update_user_pwd0(@Param("newPwd") String newPwd, @Param("cpwd9188") String cpwd9188, @Param("cuserid") String cuserid,
                             @Param("oldPwd") String oldPwd);

    @Update(" update tb_user set cpassword = #{cpassword,jdbcType=VARCHAR} where  cuserid =#{cuserid,jdbcType=VARCHAR} ")
    Integer update_user_pwd1(@Param("cpassword") String cpassword, @Param("cuserid") String cuserid);

    @Update("update tb_user t set t.cpassword=#{cpassword,jdbcType=VARCHAR}, t.cpwd9188=#{cpwd9188,jdbcType=VARCHAR} where t" +
            ".cuserid=#{cuserid,jdbcType=VARCHAR}")
    Integer update_usr_pwd2(@Param("cpassword") String cpassword, @Param("cpwd9188") String cpwd9188, @Param("cuserid") String
            cuserid);

    /****/
    @Select("select count(1) as num from tb_user where cphone=#{phone ,jdbcType=VARCHAR}")
    Integer query_user_byPhone(@Param("phone") String phone);

    /***修改密码 by 电话号码**/
    @Update("update tb_user set cpassword=#{cpassword,jdbcType=VARCHAR},cpwd9188=#{cpwd9188,jdbcType=VARCHAR} where cphone=#{cphone," +
            "jdbcType=VARCHAR}")
    Integer update_user_pwdbyPhone(@Param("cpassword") String cpassword, @Param("cpwd9188") String cpwd9188, @Param("cphone") String
            cphone);

    /*****/
    @Select("select count(*) as count from tb_user t where t.cphone=#{cphone,jdbcType=VARCHAR}")
    Integer query_user_countByPhone(@Param("cphone") String cphone);

    /**
     * 用户收藏添加
     **/
    Map<String, String> user_collect_add(Map<String, String> map);


    /**
     * 用户点赞
     **/
    Map<String, String> user_praise(Map<String, String> map);

    /**
     * 查询关注的银行卡信息
     **/
    @Select(" select t1.IBANKID from tb_user_bank t1 where t1.CNICKID = #{cnickid , jdbcType=VARCHAR}")
    List<String> query_user_bankId(@Param("cnickid") String cnickid);

    /**
     * 获取商家logo
     **/
    @Select("select  ts.istoreid, tbs.clogo,  ts.ibankids from tb_business tbs, tb_store ts,tb_user_collection tuc where ts" +
            ".ibussinessid = tbs.ibusinessid and tuc.istoreid = ts.istoreid and  tuc.cuserid =#{cuserid,jdbcType=VARCHAR} and tuc" +
            ".idel = 0 and tuc.iexpire = 0  and tuc.itype = 0  order by tuc.cadddate desc")
    List<StoreLogo> query_store_logo(@Param("cuserid") String cuserid);

    /**
     * 优惠信息
     ***/
    @Select(" select c.icheapid,c.cbankid,c.ctitle,c.cptype,bk.ishortname, s.ibankids,s.istoreid,s.cstorename,c.iexpire" +
            "             from tb_cheap c left join  tb_city_cheap cc on cc.icheapid=c.icheapid" +
            "             left join tb_store s on c.ibussinessid=s.ibussinessid and (s.icityid=cc.icityid or cc.icityid is null)  " +
            "and instr(','||s.ibankids||',', ','||c.cbankid||',')>0 " +
            "             left join tb_bank bk on bk.ibankid=c.cbankid               " +
            "             where  c.istate > 0 and c.istate!=2 and s.istoreid =#{storeId , jdbcType=VARCHAR} ")
    List<StoreCheapBean> query_store_cheap(@Param("storeId") String storeId);

    /**
     * 微信匿名用户
     **/
    @Select("select tu.cpassword,tu.cuserid,tu.cnickid from tb_user tu ,tb_wechat_user twu where tu.cuserid = twu.cuserid and twu" +
            ".cunionid = #{cunionid, jdbcType=VARCHAR }")
    weChatUnionid query_weChat_Union(@Param("cunionid") String cunionid);

    @Select("select count(tu.cnickid) as num from tb_user tu where tu.cnickid = #{cnickid, jdbcType=VARCHAR}")
    Integer query_user_nickidNum(@Param("cnickid") String cnickid);

    /**
     * 获取 随机数字
     **/
    @Select("select SEQ_CUSERID.nextVal num from dual")
    Integer query_dual_num();

    /**
     * 微信用户注册
     **/
    @Insert(" insert into tb_user (cnickid,cpassword,cregip,cuserid,isource,cregisterdate, ctinyurl) values" +
            " (#{nickname, jdbcType=VARCHAR}," +
            " #{pwd, jdbcType=VARCHAR}," +
            "#{ipAddr, jdbcType=VARCHAR}," +
            "#{cuserId, jdbcType=VARCHAR}," +
            "#{source, jdbcType=VARCHAR},sysdate," +
            "#{icon, jdbcType=VARCHAR})")
    Integer Insert_user_weChat(WeChatBean bean);

    /**
     * 微信用户注册
     **/
    @Insert(" insert into tb_wechat_user (cunionid,cuserid,cnickname,isex,cprovince,ccity,ccountry,cheadimgurl,cprivilege) values" +
            " (#{unionid, jdbcType=VARCHAR}," +
            " #{cuserId, jdbcType=VARCHAR}," +
            " #{wxNickName, jdbcType=VARCHAR}," +
            " #{sex, jdbcType=VARCHAR}," +
            " #{province, jdbcType=VARCHAR}," +
            " #{city, jdbcType=VARCHAR}," +
            " #{country, jdbcType=VARCHAR}," +
            " #{headimgurl, jdbcType=VARCHAR}," +
            " #{privilege, jdbcType=VARCHAR})")
    Integer Insert_user_weChatBind(WeChatBean bean);

    //    ll
    @Update("update tb_wechat_user set cuserid=#{cuserId, jdbcType=VARCHAR}," +
            "cnickname=#{wxNickName, jdbcType=VARCHAR}," +
            "isex=#{sex, jdbcType=VARCHAR}," +
            "cprovince=#{province, jdbcType=VARCHAR}," +
            "ccity=#{city, jdbcType=VARCHAR}," +
            "ccountry= #{country, jdbcType=VARCHAR}," +
            "cheadimgurl=#{headimgurl, jdbcType=VARCHAR}," +
            "cprivilege=#{privilege, jdbcType=VARCHAR} where cunionid=#{unionid, jdbcType=VARCHAR}")
    Integer updateWechatUser(WeChatBean bean);

    /***优惠**/
    @Select("select tm.clogo,tm.clogolist, tmc.icheapid,tmc.imarketid, " +
            " tmc.cdiscount,tm.cname,tmc.ctitle,tmci.cimgurl, to_date(tmc.cenddate) " +
            "as cenddate,tuc.iexpire from tb_market_cheap tmc, tb_market tm," +
            " tb_market_cheap_img tmci , tb_user_collection tuc " +
            " where tmc.imarketid = tm.imarketid "
            + "and tmc.icheapid = tmci.icheapid and " +
            "tuc.istoreid = tmc.icheapid and tuc.cuserid = #{cuserid, jdbcType=VARCHAR} " +
            "and tuc.idel = 0 and tuc.iexpire = 0 and tuc.itype =  #{itype, jdbcType=VARCHAR}" +
            "and tm.itype = 1 and tm.istate = 1 and tmc.imarketid = #{imarketid, jdbcType=VARCHAR} " +
            "order by tuc.cadddate desc"
    )
    List<CouponBean> query_user_Coupon(@Param("cuserid") String cuserid, @Param("itype") String itype, @Param("imarketid") String
            imarketid);

    @Select("select distinct(tmc.imarketid),tuc.cadddate from " +
            "tb_market_cheap tmc, tb_user_collection tuc " +
            "where tuc.istoreid = tmc.icheapid and tmc.istate = 1  and " +
            "tuc.cuserid = #{cuserid, jdbcType=VARCHAR}" +
            "and tuc.idel = 0 and tuc.iexpire = 0 and tuc.itype = 1  " +
            "order by tuc.cadddate desc")
    List<MarketBean> query_market(@Param("cuserid") String cuserid);

    /**
     * 收藏过期
     **/
    @Select("select  ts.istoreid from tb_user_collection tuc  ,tb_store ts" +
            " where tuc.istoreid = ts.istoreid and  tuc.cuserid = #{cuserid, jdbcType=VARCHAR} " +
            "and tuc.idel = 0 and tuc.iexpire = 0  and tuc.itype = 0 ")
    List<Integer> query_collect_id(@Param("cuserid") String cuserid);

    /***门店过期**/
    @Update("update tb_user_collection tuc set tuc.iexpire =1 where" +
            " tuc.cuserid = #{cuserid, jdbcType=VARCHAR}" +
            " and tuc.istoreid = #{istoreid, jdbcType=VARCHAR}" +
            " and tuc.idel = 0 and tuc.iexpire = 0  and tuc.itype = 0 ")
    Integer update_user_collection_overdue(@Param("cuserid") String cuserid, @Param("istoreid") String istoreid);

    /***删除优惠价收藏***/
    @Update(" update tb_user_collection tuc set tuc.iexpire = 1   " +
            "where exists (select 1 from tb_market_cheap tmc where tmc.icheapid = tuc.istoreid  and  " +
            "(tmc.cadddate is not null and tmc.cenddate < to_date(sysdate)) )  " +
            " and tuc.cuserid = #{cuserid, jdbcType=VARCHAR} " +
            " and tuc.idel = 0 and tuc.iexpire = 0 and tuc.itype = 1")
    Integer update_collect_expire(@Param("cuserid") String cuserid);


    @Select("select icheapid,cptype,ctitle from " +
            "(select t1.icheapid,t1.cptype,t1.ctitle from tb_cheap t1 left join tb_city_cheap t2 on t1.icheapid=t2.icheapid " +
            "                where t1.ibussinessid=#{ibussinessid, jdbcType=VARCHAR} and" +
            " (t1.icityid= #{icityid, jdbcType=VARCHAR} or t1.icityid is null) and t1.iexpire=0 and t1.istate > 0 and t1.istate!=2 " +
            "                ) t group by  icheapid,cptype,ctitle ")
    List<CheapTitleBean> query_cheap_ctitle(@Param("ibussinessid") String ibussinessid, @Param("icityid") String icityid);

    @Update("   update tb_user set" +
            " CTINYURL = #{icon,jdbcType=VARCHAR } " +
            "where  CUSERID =  #{cuserId,jdbcType=VARCHAR }  " +
            "and (CPASSWORD =  #{pwd,jdbcType=VARCHAR }  " +
            "or cpwd9188=  #{pwd9188,jdbcType=VARCHAR } )")
    Integer u_bind_icon(User bean);


    @Select("select t1.cnickid,t1.cphone,t1.ctinyurl,t1.cusername,t1.cusername as username,\n" +
            "       (select count(1) from tb_user_bank t2 where t2.cnickid = t1.cuserid) as banks,\n" +
            "       (select count(1) from tb_user_collection t3 where t3.cuserid = t1.cuserid and t3.idel = 0 and t3.iexpire = 0 and" +
            " t3.itype = 0) as stores,\n" +
            "       (select count(1) from tb_bank_bill tbb where tbb.cuserid = t1.cuserid and tbb.isdel = 0) as cards,\n" +
            "       (select count(1) from tb_user_collection t4 where t4.cuserid = t1.cuserid  and t4.idel = 0 and t4.iexpire = 0 " +
            "and t4.itype = 1) as coupons\n" +
            "  from tb_user t1\n" +
            " where t1.cuserid = #{cuserId,jdbcType=VARCHAR } ")
    UserDto queryUserAccount(@Param("cuserId") String cuserId, @Param("pwd") String pwd, @Param("pwd9188") String pwd9188);

    @Select("select t1.cnickid,t1.cphone,t1.ctinyurl,t1.cusername as username,\n" +
            "       (select count(1) from tb_user_bank t2 where t2.cnickid = t1.cuserid) as banks,\n" +
            "       (select count(1) from tb_user_collection t3 where t3.cuserid = t1.cuserid and t3.idel = 0 and t3.iexpire = 0 and" +
            " t3.itype = 0) as stores,\n" +
            "       (select count(1) from tb_bank_bill tbb where tbb.cuserid = t1.cuserid and tbb.isdel = 0) as cards,\n" +
            "       (select count(1) from tb_user_collection t4 where t4.cuserid = t1.cuserid  and t4.idel = 0 and t4.iexpire = 0 " +
            "and t4.itype = 1) as coupons\n" +
            "  from tb_user t1 " +
            " where t1.cuserid = #{cuserId,jdbcType=VARCHAR }")
    U_AccountBean u_account(@Param("cuserId") String cuserId);


    @Select("select t.cuserid,t.cpassword,t.cpwd9188 from tb_user t where t.cphone=?")
    UserDto queryUserByPhone(@Param("cphone") String cphone);

    @Select("select t.cuserid cuserId,t.cpassword pwd,t.cpwd9188 pwd9188 from tb_user t where t.cuserid = #{cuserId,jdbcType=VARCHAR" +
            " }")
    UserDto queryUserByCuserId(@Param("cuserId") String cuserId);

    @Select("select t.cuserid,t.accesstoken,t.appid from tb_token t where t.accesstoken = #{accessToken,jdbcType=VARCHAR} and t" +
            ".appid = #{appId,jdbcType=VARCHAR}")
    TokenDto queryToken(User user);

}
