package com.caiyi.financial.nirvana.ccard.bill.bank.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by terry on 2016/8/19.
 */
public class BankImportServiceTest extends TestSupport  {
//    @Autowired
//    BankImportService service;
//
//    @Autowired
//    MessageService messageService;
//    @Autowired
//    BankImportService bankImportService;

//    @Test
//    public void testQueryBankErrorByDesc() throws Exception {
//        System.out.println(111111);
//        System.out.println(111111);
//        List<HashMap<String, Object>> list=service.queryBankErrorByBankid("2015-08-05 00:00:00", "2015-08-05 23:59:59");
//        for (int i = 0; i < list.size(); i++) {
//            HashMap<String, Object> map=list.get(i);
//            for (String key:map.keySet()){
//                System.out.println("key="+key+" value="+map.get(key));
//            }
//        }
//    }
//    @Test
//    @Commit
//    public void testQueryBankErrorByBankid() throws Exception {
//        System.out.println(messageService.deleteMessageByBillId("797666a0104","3492"));
//    }

//    public void testQueryBankSuccessByBankid() throws Exception {
//
//    }

//    @Test
//    @Rollback(false)
//    public void saveBillCountDay() throws Exception {
//
//        String yesterday="2016-08-22";
//
//        String sd=yesterday+" 00:00:00";
//        String ed=yesterday+" 23:59:59";
//
//        System.out.println(service.deleteBillCount(yesterday));
////        BillCountDayDto bcd=new BillCountDayDto();
////        List<HashMap<String, Object>> result=service.queryBankErrorByBankid(sd, ed);
////        for (int i = 0; i < result.size(); i++) {
////            HashMap<String, Object> map=result.get(i);
////            int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
////            int nums=((BigDecimal)map.get("NUMS")).intValue();
////            bcd.setEValue(ibankid, nums);
////            logger.info(yesterday+" error ibankid=" + ibankid + "  nums=" + nums);
////        }
////        result=service.queryBankSuccessByBankid(sd, ed);
////        for (int i = 0; i < result.size(); i++) {
////            HashMap<String, Object> map=result.get(i);
////            int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
////            int nums=((BigDecimal)map.get("NUMS")).intValue();
////            bcd.setSValue(ibankid, nums);
////            logger.info(yesterday+" success ibankid="+ibankid+"  nums="+nums);
////        }
////        bcd.setCcountday(yesterday);
////        service.saveBillCountDay(bcd);
////        System.out.println(service.deleteBillCountDetail(yesterday));
////
////        result=service.queryBankErrorByDesc(sd, ed);
////        for (int i = 0; i < result.size(); i++) {
////            HashMap<String, Object> map=result.get(i);
////            String cdesc=(String)map.get("CDESC");
////            int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
////            int nums=((BigDecimal)map.get("NUMS")).intValue();
////            BillDetailCountDayDto dto=new BillDetailCountDayDto();
////            dto.setCcountday(yesterday);
////            dto.setCdesc(cdesc);
////            dto.setIbankid(ibankid);
////            dto.setIfailcount(nums);
////            dto.setItype(0);
////            service.saveBillDetailCountDay(dto);
////            logger.info(yesterday+" cdesc="+cdesc+" ibankid="+ibankid+" nums="+nums);
////        }
//    }
//    @Test
//    public void testQueryBankPoint() throws Exception {
//        System.out.println("bankPoints:"+bankImportService.queryBankPoint("-33.86881","151.208977","3","101",10,1));
//    }

}