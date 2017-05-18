package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RankCard;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.YouYuNewsDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.quartz.SimpleTrigger;

import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/10/20.
 */
public interface CardYoyuMapper extends BaseDao {


    /**
     * 福利场地
     * @param adcode
     * @return
     */
    List<Map<String,String>> queryWelfare(@Param("adcode") String adcode);

    /**
     * 福利场地
     * @param adcode
     * @return
     */
    List<Map<String,String>> queryWelfare2(@Param("adcode") String adcode,@Param("bankIds") List<Integer> bankIds);


    /**
     * 查询人气榜单卡片
     * @param cityid
     * @return
     */
    List<RankCard> queryTopTenCards(@Param("cityid") String cityid);

    /**
     * 查询人气榜单卡片
     * @param cityid
     * @return
     */
    List<RankCard> queryTopOneCard(@Param("cityid") String cityid);

    /**
     * 保存银行回调信息
     * @param map
     * @return
     */
    int saveBankCallBackInfo(Map<String ,String> map);

    /**
     * 查询信息
     * @param map
     * @return
     */
    Map<String,Object> queryCallBackInfo(Map<String,String> map);

    /**
     * 查询银行 开启功能的开关 接口 比如bk=1 表示办卡开启
     * @param bankId
     * @return
     */
    @Select("select t.ibk \"bk\",t.izd \"zd\",t.iyh \"yh\",t.itx \"tx\",t.ijsq \"jsq\",t.iznj \"znj\",\n" +
            "t.ijf \"jf\",t.ijd \"jd\",t.iyhfu \"yhfu\",t.ilste \"lste\",t.ikpjh \"kpjh\",t.ibankid \"bankid\"， \n" +
            "t.iwd \"wd\"，t.ite \"te\" from tb_bank_flag t where t.ibankid=#{bankId,jdbcType=VARCHAR} and rownum=1")
    Map<String,Object> getBankFlagByBankId(@Param("bankId") String bankId);

    /**
     * 第十三次迭代 办卡首页卡片推荐
     * @return
     */
    List<Map<String,String>>queryCardRecommendConfig();

    /**
     * 第十三次迭代 办卡首页卡片推荐
     * @param cityid 城市ID
     * @param cardids 配置卡ID(,11,222,33,)
     * @return
     */
    List<Map<String,String>>queryApplyCards(@Param("cityid") String cityid,@Param("cardids") String cardids);

    /**
     * 第十三次迭代 办卡首页快入入口
     * @return
     */
    List<Map<String,Object>> queryQuickInfo(@Param("cityid") String cityid,@Param("type") String type);

    /**
     * 第十三次迭代  获取银行icon
     * @return
     */
    List<Map<String,Object>> getBankIconById(@Param("bankid") String bankid);


    /**
     * 查询登录用户关注的银行id
     * @param userId
     * @return
     */
    List<Integer> queryBankIdByUserId(@Param("userId") String userId);

    }
