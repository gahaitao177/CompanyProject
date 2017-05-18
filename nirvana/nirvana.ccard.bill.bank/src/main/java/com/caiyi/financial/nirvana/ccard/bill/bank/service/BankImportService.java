package com.caiyi.financial.nirvana.ccard.bill.bank.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.mapper.BankMapper;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.*;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaoruikang on 16/5/26.
 * 网银导入
 */
@Service
public class BankImportService extends AbstractService {
    @Autowired
    BankMapper mapper;


    public List<Map<String, Object>> select(Channel channelBean) {
        logger.info("BankImportService select");
        List<Map<String, Object>> list = mapper.select(channelBean);
        System.out.println("MailImportService END");
//        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }

    public BankBillDto getUserBankBill(String card4Num, Integer bankId, String cuserId) {
        try {
            return mapper.getUserBankBill(card4Num, bankId, cuserId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBill 异常", e);
        }
        return null;
    }

    /**
     * 根据Billid获取Bill
     *
     * @param billId
     * @return
     */
    public BankBillDto getUserBankBillById(String billId) {
        try {
            return mapper.getUserBankBillById(billId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBillById 异常", e);
        }
        return null;
    }

    public ImportTaskDto queryTaskByUser(String type, String cuserid, int ibankid, int isend, String caccountname) {
        try {
            return mapper.queryTaskByUser(Integer.valueOf(type), cuserid, ibankid, isend, caccountname);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryTaskByUser 异常", e);
        }
        return null;

    }

    public Integer querySeqIdFormTask() {
        try {
            return mapper.querySeqIdFormTask();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("querySeqIdFormTask 异常", e);
        }
        return null;
    }

    public int createTask(ImportTaskDto taskDto) {
        try {
            return mapper.createTask(taskDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createTask 异常", e);
        }
        return 0;
    }

    /**
     * 根据userId ,billId 获取用户卡数据
     *
     * @param bean
     * @return
     */
    public List<BankBillDto> getUserCardInfo(Channel bean) {
        try {
            return mapper.getUserCardInfo(bean);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBillByUser 异常", e);
        }
        return null;
    }

    /**
     * 获取seq_repayment_order.nextval,用于订单号自增长
     *
     * @return
     */
    public Integer getSeqRepayOrderNextVal() {
        try {
            return mapper.getSeqRepayOrderNextVal();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getSeqRepayOrderNextVal 异常", e);
        }
        return null;
    }

    /**
     * 新增还款订单
     *
     * @param rod
     */
    public int createRepaymentOrder(RepaymentOrderDto rod) {
        try {
            return mapper.createRepaymentOrder(rod);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createRepaymentOrder 异常", e);
        }
        return 0;
    }

    /**
     * 根据partnerID获取还款订单
     *
     * @param partnerId
     * @return
     */
    public RepaymentOrderDto getRepaymentOrderByPartnerId(String partnerId) {
        try {
            return mapper.getRepaymentOrderByPartnerId(partnerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("RepaymentOrderDto 异常", e);
        }
        return null;
    }

    /**
     * 更新还款订单
     *
     * @param rod
     * @return
     */
    public int updateRepayOrder(RepaymentOrderDto rod) {
        try {
            return mapper.updateRepayOrder(rod);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("updateRepayOrder 异常", e);
        }
        return 0;
    }

    /**
     * @param type
     * @param cuserid
     * @param ibankid
     * @param isend
     * @param caccountname
     * @return
     */
    public ConsumeTaskDto queryConsumeTaskByUser(String type, String cuserid, String ibankid, String isend, String caccountname) {
        try {
            return mapper.queryConsumeTaskByUser(type, cuserid, ibankid, isend, caccountname);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryConsumeTaskByUser 异常", e);
        }
        return null;
    }

    /**
     * 获取seq_consume_task.nextval 用于网银登录taskid自增长
     *
     * @return
     */
    public Integer querySeqIdFromConsumeTask() {
        try {
            return mapper.querySeqIdFromConsumeTask();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("querySeqIdFromConsumeTask 异常", e);
        }
        return null;
    }

    /**
     * 创建网银登录分析任务
     *
     * @param taskDto
     * @return
     */
    public int createConsumeTask(ConsumeTaskDto taskDto) {
        try {
            return mapper.createConsumeTask(taskDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createConsumeTask 异常", e);
        }
        return 0;
    }

    /**
     * 更新网银登录状态
     *
     * @param taskDto
     * @return
     */
    public int updateConsumeTask(ConsumeTaskDto taskDto) {
        try {
            return mapper.updateConsumeTask(taskDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createConsumeTask 异常", e);
        }
        return 0;
    }


    /**
     * 根据账单id，获取账单月信息
     *
     * @param ibillid
     * @return
     */
    public List<BillMonthDto> getBillMonthByBillId(String ibillid) {
        try {
            return mapper.getBillMonthByBillId(ibillid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getBillMonthByBillId 异常", e);
        }
        return null;
    }

    /**
     * 根据账单id，获取所有账单 已出/未出
     *
     * @param ibillid
     * @return
     */
    public List<BillMonthDto> getAllBillMonthByBillId(String ibillid) {
        try {
            return mapper.getAllBillMonthByBillId(ibillid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getBillMonthByBillId 异常", e);
        }
        return null;
    }

    /**
     * 根据银行id，获取银行信息
     *
     * @param ibankid
     * @return
     */
    public BankDto getBankById(String ibankid) {
        try {
            return mapper.getBankById(ibankid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getBankById 异常", e);
        }
        return null;
    }

    /**
     * 根据账单月份id获取流水信息,最新的一条
     *
     * @param imonthid
     * @return
     */
    public BillDetailDto getLatestBillDetailByMonthId(String imonthid) {
        try {
            List<BillDetailDto> listRes = mapper.getLatestBillDetailByMonthId(imonthid);
            if (listRes != null && listRes.size() > 0) {
                return listRes.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getLatestBillDetailByMonthId 异常", e);
        }
        return null;
    }

    /**
     * 查询所有月份账单信息
     *
     * @param ibillid
     * @return
     */
    public List<BillMonthDto> getAllBillMonths(String ibillid) {
        try {
            return mapper.getAllBillMonths(ibillid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getAllBillMonths 异常", e);
        }
        return null;
    }

    /**
     * 查询所有账单详细
     *
     * @param imonthid
     * @return
     */
    public List<BillDetailDto> getBillDetailsByMonthId(String imonthid) {
        try {
            return mapper.getBillDetailsByMonthId(imonthid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getBillDetailsByMonthId 异常", e);
        }
        return null;
    }

    /**
     * 将costtype转化为name
     *
     * @param icostid
     * @return
     */
    public String getCostTypeName(String icostid) {
        try {
            return mapper.getCostTypeName(icostid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getCostTypeName 异常", e);
        }
        return null;
    }

    /**
     * 删除账单（账单改为已删除状态）
     *
     * @param billId
     * @return
     */
    public int deleteBankBill(String billId) {
        try {
            return mapper.deleteBankBill(billId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("deleteBankBill 异常", e);
        }
        return 0;
    }

    /**
     * 更新还款状态
     *
     * @param billId
     * @param repaymentStatus
     * @return
     */
    public int changeRepaymentStatus(String billId, String repaymentStatus) {
        try {
            return mapper.changeRepaymentStatus(billId, repaymentStatus);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("changeRepaymentStatus 异常={}", e.toString());
        }
        return 0;
    }

    /**
     * 计算总消费
     *
     * @param imonthid
     * @return
     */
    public String calcTotalConsume(String imonthid) {
        try {
            return mapper.calcTotalConsume(imonthid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("calcTotalConsume 异常", e);
        }
        return null;
    }

    /**
     * 分类型计算总消费
     *
     * @param imonthid
     * @return
     */
    public List<Map<String, Object>> queryConsumeByCostType(String imonthid) {
        try {
            return mapper.queryConsumeByCostType(imonthid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryConsumeByCostType 异常", e);
        }
        return null;
    }

    /**
     * 查询账单
     *
     * @param bean
     * @return
     */
    public BankBillDto queryBillByUserIdBillId(Channel bean) {
        try {
            return mapper.queryBillByUserIdBillId(bean);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBillBy4NumIdUser 异常", e);
        }
        return null;
    }

    /**
     * 根据用户查询相关Task
     *
     * @param type
     * @param cuserId
     * @param bankId
     * @param isSend
     * @param isSend2
     * @param accountname
     * @return
     */
    public ImportTaskDto queryTaskByUser2(String type, String cuserId, String bankId, int isSend, int isSend2, String accountname) {
        try {
            return mapper.queryTaskByUser2(type, cuserId, bankId, isSend, isSend2, accountname);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryTaskByUser2 异常", e);
        }
        return null;
    }

    /**
     * @param type
     * @param cuserId
     * @param isend
     * @param isend2
     * @param accountname
     * @return
     */
    public ImportTaskDto queryTaskByUser3(String type, String cuserId, int isend, int isend2, String accountname) {
        try {
            return mapper.queryTaskByUser3(type, cuserId, isend, isend2, accountname);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryTaskByUser2 异常", e);
        }
        return null;
    }

    /**
     * 根据TaskID 查询Task
     *
     * @param taskid
     * @return
     */
    public ImportTaskDto queryTaskById(String taskid) {
        try {
            return mapper.queryTaskById(taskid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryTaskById 异常", e);
        }
        return null;
    }

    /**
     * @return
     */
    public List<HashMap<String, Object>> queryCreditHandle() {
        try {
            return mapper.queryCreditHandle();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryCreditHandle 异常", e);
        }
        return null;
    }

    /**
     * 计算网点总数
     *
     * @param bankId
     * @param cityId
     * @return
     */
    public int sumBankPoint(String bankId, String cityId) {
        try {
            return mapper.sumBankPoint(bankId, cityId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sumBankPoint 异常", e);
        }
        return 0;
    }

    /**
     * 查询经纬度
     *
     * @param cityId
     * @return
     */
    public List<Map<String, Object>> queryLatLngByAreaId(String cityId) {
        try {
            return mapper.queryLatLngByAreaId(cityId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryCreditHandle 异常", e);
        }
        return null;
    }

    /**
     * 查询网点信息
     *
     * @param clat
     * @param clng
     * @param bankId
     * @param cityId
     * @return
     */
    public List<HashMap<String, Object>> queryBankPoint(String clat, String clng, String bankId, String cityId, int pageSize, int pageNum) {
        try {
            List<HashMap<String, Object>> resTemp = mapper.queryBankPoint(clat, clng, bankId, cityId);
            List<HashMap<String, Object>> res = new ArrayList<>();
            for (int i = (pageNum - 1) * pageSize; i < pageNum * pageSize; i++) {
                //防止数组越界
                if (i < resTemp.size()) {
                    res.add(resTemp.get(i));
                }
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankPoint 异常", e);
        }
        return null;
    }

    /**
     * 查询办卡2
     *
     * @param bankId
     * @return
     */
    public List<CreditHandleDto> queryCreditHandle2(String bankId) {
        return mapper.queryCreditHandle2(bankId);
    }

    /**
     * 查询办卡3
     *
     * @return
     */
    public List<CreditHandleDto> queryCreditHandle3() {
        return mapper.queryCreditHandle3();
    }

    /**
     * 查询办卡状态
     *
     * @return
     */
    public List<CreditHandleDto> queryProgress() {
        return mapper.queryProgress();
    }

    /**
     * 查询银行导入失败次数，分银行类型
     */

    public List<HashMap<String, Object>> queryBankErrorByDesc(String sd, String ed) {
        try {
            return mapper.queryBankErrorByDesc(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankErrorByDesc 异常", e);
        }
        return null;
    }


    /**
     * 查询银行导入失败次数，分银行类型
     */

    public List<HashMap<String, Object>> queryBankErrorByDescConsume(String sd, String ed) {
        try {
            return mapper.queryBankErrorByDescConsume(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankErrorByDescConsume 异常", e);
        }
        return null;
    }

    /**
     * 查询银行导入失败次数，分银行
     */
    public List<HashMap<String, Object>> queryBankErrorByBankid(String sd, String ed) {
        try {
            return mapper.queryBankErrorByBankid(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankErrorByBankid 异常", e);
        }
        return null;
    }

    /**
     * 查询银行导入失败次数，分银行
     */
    public List<HashMap<String, Object>> queryBankErrorByBankidConsume(String sd, String ed) {
        try {
            return mapper.queryBankErrorByBankidConsume(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankErrorByBankidConsume 异常", e);
        }
        return null;
    }


    /**
     * 查询银行导入成功过次数，分银行
     */
    public List<HashMap<String, Object>> queryBankSuccessByBankid(String sd, String ed) {
        try {
            return mapper.queryBankSuccessByBankid(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankSuccessByBankid 异常", e);
        }
        return null;
    }

    /**
     * 查询银行导入成功过次数，分银行
     */
    public List<HashMap<String, Object>> queryBankSuccessByBankidConsume(String sd, String ed) {
        try {
            return mapper.queryBankSuccessByBankidConsume(sd, ed);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankSuccessByBankidConsume 异常", e);
        }
        return null;
    }

    /**
     * 导入日统计
     *
     * @param dto
     * @return
     */
    public int saveBillCountDay(BillCountDayDto dto) {
        try {
            return mapper.saveBillCountDay(dto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveBillCountDay 异常", e);
        }
        return 0;
    }

    public int saveBillDetailCountDay(BillDetailCountDayDto dto) {
        try {
            return mapper.saveBillDetailCountDay(dto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveBillDetailCountDay 异常", e);
        }
        return 0;
    }


    public int deleteBillCountDetail(String ccountday) {
        try {
            return mapper.deleteBillCountDetail(ccountday);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("deleteBillCountDetail 异常", e);
        }
        return 0;
    }

    public int deleteBillCount(String ccountday) {
        try {
            return mapper.deleteBillCount(ccountday);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("deleteBillCount 异常", e);
        }
        return 0;
    }

    /**
     * 根据银行id,查询一段时间内未做更新的网银账户总数量
     *
     * @param ibankid
     * @return
     */
    public long queryCountNoUpdateTimer(String ibankid, String time) {
        try {
            return mapper.queryCountNoUpdateTimer(ibankid, time);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankCountTimer 异常", e);
        }
        return 0;
    }

    /**
     * 根据银行id,查询一段时间内未做更新的网银账户信息
     *
     * @param ibankid
     * @return
     */
    public List<BankBillDto> queryBillNoUpdateTimer(String ibankid, String time, int start, int end) {
        try {
            return mapper.queryBillNoUpdateTimer(ibankid, time, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryBankBillTimer 异常", e);
        }
        return null;
    }

    /**
     * 银行服务大厅
     *
     * @param cuserId
     * @return
     */
    public BoltResult bankServiceIndex(String cuserId) {
        BoltResult result = new BoltResult("0", "请求失败");
        try {
            JSONObject data = new JSONObject();
            List<CreditHandleDto> collection = mapper.queryUserCollectionBank(cuserId);
            if (null != collection && collection.size() > 0) {
                data.put("collection", collection);
            } else {
                data.put("collection", new int[0]);
            }
            List<CreditHandleDto> other = mapper.queryUserOtherBank(cuserId);
            if (null != other && other.size() > 0) {
                data.put("other", other);
            } else {
                data.put("other", new int[0]);
            }
            result.setCode("1");
            result.setData(data);
            result.setDesc("请求成功");
        } catch (Exception e) {
            logger.error("银行服务大厅出错" + e.getMessage());
        }
        return result;
    }

    /**
     * 查询昨天账单导入信息
     * @param yesterday
     * @return
     */
    public BillCountDayDto queryBillCountDay(String yesterday){
       return mapper.queryBillCountDay(yesterday);
    }

}
