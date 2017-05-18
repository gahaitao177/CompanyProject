package com.caiyi.financial.nirvana.ccard.bill.bank.mapper;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillConsumeAnalysisDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillDetailDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillMonthDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/8/17.
 * 卡管理相关
 */
public interface BillMapper extends BaseDao {

    int updateBillInfo(String username,String card4num, String cash,String billid,String uid);

    /**
     * 查询账单流水最近的六个月
     * @param billId
     * @return
     */
    List<BillMonthDto> queryLatestBillMonth(String billId);

    /**
     * 查询每月账单流水
     * @param monthid
     * @return
     */
    List<BillDetailDto> queryMonthlyBill(Integer monthid);


    /**
     * 针对特殊账单流水作查询(跨年)
     *
     * @param monthid
     * @return
     */
    List<BillDetailDto> queryMonthlyBillSpecial(Integer monthid);

    /**
     * 查询总消费，排除还款和一般，按money倒序
     * @param billId
     * @return
     */
    List<BillConsumeAnalysisDto> queryTotalConsumeBill(String billId);

//    List<BillConsumeAnalysisDto> queryTotalConsumeBillNew(String billId);

    /**
     * 根据月份查询当月消费类型流水
     * @param monthId
     * @return
     */
    List<BillConsumeAnalysisDto> queryConsumeBillByMonthId(Integer monthId);

    /**
     * 查询起止时间段
     * @param billId
     * @return
     */
    Map<String,String> queryMonthTime(String billId);
}
