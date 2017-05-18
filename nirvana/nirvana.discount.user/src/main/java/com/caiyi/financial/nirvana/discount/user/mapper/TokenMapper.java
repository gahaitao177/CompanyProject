package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import org.apache.ibatis.annotations.*;

/**
 * Created by wenshiliang on 2016/11/18.
 */
public interface TokenMapper extends BaseDao {

    @Insert("insert into tb_token (ACCESSTOKEN,EXPIRESIN,MOBILETYPE,CUSERID,CPASSWORD,APPID,PARAMJSON,CPWD9188,ILOGINFROM) values" +
            "(#{accessToken, jdbcType=VARCHAR}," +
            "#{expiresin, jdbcType=INTEGER}," +
            "#{mobiletype, jdbcType=INTEGER}," +
            "#{cuserId, jdbcType=VARCHAR}," +
            "#{cpassword, jdbcType=VARCHAR}," +
            "#{appid, jdbcType=VARCHAR}," +
            "#{paramJson, jdbcType=VARCHAR}," +
            "#{cpwd9188, jdbcType=VARCHAR}, " +
            "#{iloginfrom, jdbcType=INTEGER})")
    int saveToken(TokenDto dto);

    @Delete("delete from tb_token where cuserid=#{cuserid,jdbcType=VARCHAR} and iloginfrom=#{iloginfrom, jdbcType=INTEGER}")
    int deleteToken(@Param("cuserid")String cuserid,@Param("iloginfrom")Integer iloginfrom);

    @Select("select lastTime,expiresin,ISTATE,mobiletype,cuserId,cpassword,paramJson,cpwd9188,iloginfrom from tb_token where accesstoken = #{accessToken,jdbcType=VARCHAR} and appid = #{appId,jdbcType=VARCHAR} ")
    TokenDto query_token(@Param("accessToken")String accessToken,@Param("appId")String appId);

    @Update("update tb_token set LASTTIME = sysdate  where accesstoken = #{accessToken,jdbcType=VARCHAR} and appid = #{appId,jdbcType=VARCHAR}")
    int updateTokenLastTime(@Param("accessToken")String accessToken,@Param("appId")String appId);

    @Update("update tb_token set DEADTIME = sysdate ,ISTATE=1,CAUSE = #{cause, jdbcType=VARCHAR}  where accesstoken = #{accessToken,jdbcType=VARCHAR} and appid = #{appId,jdbcType=VARCHAR}")
    int logoutToken(@Param("cause")String cause,@Param("accessToken")String accessToken,@Param("appId")String appId);
}
