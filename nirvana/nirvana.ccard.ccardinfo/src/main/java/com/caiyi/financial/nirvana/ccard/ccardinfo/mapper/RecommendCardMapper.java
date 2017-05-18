package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RecommendCardBean;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.RecommendCardDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by lizhijie on 2017/1/11.
 */
public interface RecommendCardMapper extends BaseDao {

    //推荐卡列表
    @Select("select cr.icr_id cardId,cr.cr_pic_url picURL,cr.cr_action_url actionURL ,cr.cr_card_name cardName" +
            "  from tb_card_recommend cr where cr.is_del=0 and cr.is_hidden=0  and " +
            "  cr.cr_type = #{key,jdbcType=VARCHAR} and（cr.city_code='all' or instr(cr.city_code,#{adcode,jdbcType=VARCHAR})>0 ) " +
            " order by cr.cr_card_order desc")
    Page<RecommendCardDto> getRecommendCardsByCityCode(RecommendCardBean bean);

    @Select("select * from (select cr.cr_card_id cardId,cr.cr_card_name cardName,cr.cr_prc_url picURL," +
            "cr.city_code cityCode,cr.cr_card_order orderNum  from tb_card_recommend cr  where " +
            "cr.cr_card_id=#{cardId,jdbcType=VARCHAR} order by cr.cr_card_order desc) card where  rownum=1")
    RecommendCardDto getRecommendCardDetailById(@Param("cardId") String cardId);
    //推荐卡点击量
    @Update("update tb_card_recommend  set cr_click_count=cr_click_count+1 where icr_id=#{cardId,jdbcType=INTEGER}")
    int updatePicClickCount(@Param("cardId") int cardId);

}
