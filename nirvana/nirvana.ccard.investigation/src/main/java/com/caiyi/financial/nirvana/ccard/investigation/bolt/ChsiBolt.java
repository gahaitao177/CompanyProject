package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ChsiEducationDto;
import com.caiyi.financial.nirvana.ccard.investigation.service.ChsiService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by shaoqinghua on 2017/1/24.
 */
@Bolt(boltId = "chsi", parallelismHint = 1, numTasks = 1)
public class ChsiBolt extends BaseBolt {
    @Autowired
    private ChsiService chsiService;

    /**
     * 新增学信账号信息
     *
     * @param chsiAccount
     */
    @BoltController
    public void addChsiAccount(ChsiAccountDto chsiAccount) {
        chsiService.addChsiAccount(chsiAccount);
    }

    /**
     * 新增学信网账号并返回主键id
     *
     * @param chsiAccount
     * @return
     */
    @BoltController
    public ChsiAccountDto addChsiAccountBackId(ChsiAccountDto chsiAccount) {
        return chsiService.addChsiAccountBackId(chsiAccount);
    }

    /**
     * 新增学信学历信息
     *
     * @param degrees
     */
    @BoltController
    public void addChsiEducation(List<ChsiEducationDto> degrees) {
        chsiService.addChsiEducation(degrees);
    }

    /**
     * 查询学信网账号信息
     *
     * @param cuserId
     * @return
     */
    @BoltController
    public ChsiAccountDto queryChsiAccount(String cuserId) {
        return chsiService.queryChsiAccount(cuserId);
    }

    /**
     * 查询学历信息
     *
     * @param chsiAccountId
     * @return
     */
    @BoltController
    public JSONObject queryChsiEducation(int chsiAccountId) {
        JSONObject jsonObject = new JSONObject();
        List<ChsiEducationDto> degreeList = chsiService.queryChsiEducation(chsiAccountId);
        jsonObject.put("degrees", degreeList);
        return jsonObject;
    }

    /**
     * 更新学信账号信息
     *
     * @param chsiAccount
     */
    @BoltController
    public void updateChsiAccount(ChsiAccountDto chsiAccount) {
        chsiService.updateChsiAccount(chsiAccount);
    }

    /**
     * 更新学历信息
     *
     * @param chsiEducation
     */
    @BoltController
    public void updateChsiEducation(ChsiEducationDto chsiEducation) {
        chsiService.updateChsiEducation(chsiEducation);
    }

    /**
     * 删除学历信息
     *
     * @param chsiAccountId
     */
    @BoltController
    public void deleteChsiEducation(int chsiAccountId) {
        chsiService.deleteChsiEducation(chsiAccountId);
    }
}
