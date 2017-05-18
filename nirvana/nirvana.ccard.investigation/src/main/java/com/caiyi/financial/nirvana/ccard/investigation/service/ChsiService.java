package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiEducationDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.ChsiMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by shaoqinghua on 2017/1/24.
 */
@Service
public class ChsiService extends AbstractService {
    @Autowired
    private ChsiMapper chsiMapper;

    /**
     * 新增学信网账号
     *
     * @param chsiAccount
     * @return
     */
    public int addChsiAccount(ChsiAccountDto chsiAccount) {
        return chsiMapper.addChsiAccount(chsiAccount);
    }

    /**
     * 新增学信网账号并返回主键id
     *
     * @param chsiAccount
     * @return
     */
    public ChsiAccountDto addChsiAccountBackId(ChsiAccountDto chsiAccount) {
        chsiMapper.addChsiAccount(chsiAccount);
        return chsiAccount;
    }

    /**
     * 添加学历信息
     *
     * @param degrees
     * @return
     */
    public int addChsiEducation(List<ChsiEducationDto> degrees) {
        return chsiMapper.addChsiEducationList(degrees);
    }

    /**
     * 查询学信网账号信息
     *
     * @return
     */
    public ChsiAccountDto queryChsiAccount(String cuserId) {
        return chsiMapper.queryChsiAccount(cuserId);
    }


    /**
     * 查询学历信息
     *
     * @param chsiAccountId
     * @return
     */
    public List<ChsiEducationDto> queryChsiEducation(int chsiAccountId) {
        return chsiMapper.queryChsiEducationList(chsiAccountId);
    }

    /**
     * 更新学信账号信息
     *
     * @param chsiAccount
     * @return
     */
    public int updateChsiAccount(ChsiAccountDto chsiAccount) {
        return chsiMapper.updateChsiAccount(chsiAccount);
    }

    /**
     * 更新学历信息
     *
     * @param chsiEducation
     * @return
     */
    public int updateChsiEducation(ChsiEducationDto chsiEducation) {
        return chsiMapper.updateChsiEducation(chsiEducation);
    }


    /**
     * 删除学历信息
     *
     * @param chsiAccountId
     * @return
     */
    public int deleteChsiEducation(int chsiAccountId) {
        return chsiMapper.deleteChsiEducation(chsiAccountId);
    }
}
