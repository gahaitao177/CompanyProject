package com.caiyi.financial.nirvana.ccard.bill.mail.service;

import com.caiyi.financial.nirvana.ccard.bill.bean.MailBill;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.MailBillDto;
import com.caiyi.financial.nirvana.ccard.bill.mail.mapper.MailMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/5/9.
 * 邮箱导入
 */
@Service
public class MailImportService extends AbstractService {
    @Autowired
    MailMapper mapper;


    public List<Map<String,Object>> select(MailBill mailBillBean){
        logger.info("MailImportService select");
        List<Map<String,Object>> list = mapper.select(mailBillBean);
        System.out.println("MailImportService END");
//        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }

    /**
     *
     * @param billId
     * @param cuserId
     * @return
     */
    public MailBillDto getUserBankBill(String billId, String cuserId) {
        try {
            logger.info("MailImportService getUserBankBill start" + billId + "," + cuserId);
            return mapper.getMailBillByBillId(billId, cuserId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("getUserBankBill 异常", e);
        }
        return null;
    }

    /**
     *
     * @param type
     * @param cuserid
     * @param ibankid
     * @param caccountname
     * @return
     */
    public ImportTaskDto queryTaskByUser(String type, String cuserid, int ibankid, String caccountname){
        try {
            return mapper.queryTaskByUser(Integer.valueOf(type), cuserid, ibankid, caccountname);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("queryTaskByUser 异常", e);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Integer querySeqIdFormTask(){
        try {
            return mapper.querySeqIdFormTask();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("querySeqIdFormTask 异常", e);
        }
        return null;
    }

    /**
     *  最后更新时间
     * @return
     */
    public Integer updateOnlyDate(String billId, String cuserId){
        try {
            return mapper.updateOnlyDate(billId,cuserId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("querySeqIdFormTask 异常", e);
        }
        return null;
    }
    /**
     *
     * @param taskDto
     * @return
     */
    public int createTask(ImportTaskDto taskDto){
        try {
            return mapper.createTask(taskDto);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("createTask 异常", e);
        }
        return 0;
    }
}
