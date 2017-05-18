package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiEducationDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by shaoqinghua on 2017/1/24.
 */
public interface ChsiMapper extends BaseDao {

    /**
     * 新增学信网账号
     *
     * @param chsiAccount
     * @return
     */
    int addChsiAccount(ChsiAccountDto chsiAccount);

    /**
     * 新增学历信息
     *
     * @param degrees
     * @return
     */
    int addChsiEducationList(@Param("degrees") List<ChsiEducationDto> degrees);

    /**
     * 查询学信网账号信息
     *
     * @param cuserId
     * @return
     */
    ChsiAccountDto queryChsiAccount(@Param("cuserId") String cuserId);

    /**
     * 查询学历信息
     *
     * @param chsiAccountId
     * @return
     */
    List<ChsiEducationDto> queryChsiEducationList(@Param("chsiAccountId") int chsiAccountId);

    /**
     * 更新学信账号信息
     *
     * @param chsiAccount
     * @return
     */
    int updateChsiAccount(ChsiAccountDto chsiAccount);

    /**
     * 更新学历信息
     *
     * @param chsiEducation
     * @return
     */
    int updateChsiEducation(ChsiEducationDto chsiEducation);

    /**
     * 删除学历信息
     *
     * @param chsiAccountId
     * @return
     */
    int deleteChsiEducation(@Param("chsiAccountId") int chsiAccountId);
}
