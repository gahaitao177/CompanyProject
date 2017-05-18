package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.YouYuNewsDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lichuanshun on 16/12/23.
 */
public interface YouyuNewsMapper extends BaseDao {
    /**
     * 检查收藏接口
     *
     * @param cuserid
     * @param newsid
     * @return
     */
    int checkNewsCollect(@Param("cuserid") String cuserid, @Param("newsid") String newsid);

    /**
     * 有鱼金融资讯接口
     *
     * @return
     */
    Page<YouYuNewsDto> queryYouYuNews(Card card);

    /**
     * 收藏接口
     *
     * @param cuserid
     * @param newsid
     * @return
     */
    int newsCollect(@Param("cuserid") String cuserid, @Param("newsid") String newsid);

    /**
     * 删除收藏接口
     *
     * @param cuserid
     * @param newsid
     * @return
     */
    int delNewsCollect(@Param("cuserid") String cuserid, @Param("newsid") String newsid);

    /**
     * 查询资讯收藏
     *
     * @param card
     * @return
     */
    Page<YouYuNewsDto> queryNewsCollect(Card card);

}
