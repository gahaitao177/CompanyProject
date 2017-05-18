package com.caiyi.financial.nirvana.ccard.bill.bank.quartz;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillCountDayDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillDetailCountDayDto;
import com.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by terry on 2016/8/19.
 */
public class StaticBillDay {
    @Autowired
    public BankImportService service;
    private static Logger logger = LoggerFactory.getLogger(StaticBillDay.class);
    private String statDay="";
    public void run(){
        String today=DateUtil.getCurrentDate();
        if (!today.equals(statDay)){

            String yesterday=getCurrentDate(-1);
            String countdate=yesterday.replaceAll("-", "");
            logger.info("begin count statDay["+yesterday+"]================================");

            String sd=yesterday+" 00:00:00";
            String ed=yesterday+" 23:59:59";
            BillCountDayDto bcd=new BillCountDayDto();
            service.deleteBillCount(countdate);
            List<HashMap<String, Object>> result=service.queryBankErrorByBankid(sd, ed);
            List<HashMap<String, Object>> resultConsume=service.queryBankErrorByBankidConsume(sd, ed);

            for (int i = 0; i < result.size(); i++) {
                HashMap<String, Object> map=result.get(i);
                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
                int nums=((BigDecimal)map.get("NUMS")).intValue();
                bcd.setEValue(ibankid, nums);
                logger.info(yesterday+" error ibankid=" + ibankid + "  nums=" + nums);
            }

            for (int i = 0; i < resultConsume.size(); i++) {
                HashMap<String, Object> map=resultConsume.get(i);
                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
                int nums=((BigDecimal)map.get("NUMS")).intValue();
                if (ibankid==4||ibankid==8||ibankid==9||ibankid==11||ibankid==14||ibankid==19){
                    bcd.setEValue(ibankid, nums);
                    logger.info(yesterday+" error ibankid=" + ibankid + "  nums=" + nums);
                }

            }



            result=service.queryBankSuccessByBankid(sd, ed);
//            resultConsume=service.queryBankSuccessByBankidConsume(sd,ed);
            for (int i = 0; i < result.size(); i++) {
                HashMap<String, Object> map=result.get(i);
                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
                int nums=((BigDecimal)map.get("NUMS")).intValue();
                bcd.setSValue(ibankid, nums);
                logger.info(yesterday+" success ibankid="+ibankid+"  nums="+nums);
            }
//            for (int i = 0; i < resultConsume.size(); i++) {
//                HashMap<String, Object> map=resultConsume.get(i);
//                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
//                int nums=((BigDecimal)map.get("NUMS")).intValue();
//                if (ibankid==4||ibankid==8||ibankid==9||ibankid==11||ibankid==14||ibankid==19){
//                    bcd.setSValue(ibankid, nums);
//                    logger.info(yesterday+" success ibankid="+ibankid+"  nums="+nums);
//                }
//            }

            bcd.setCcountday(countdate);
            int rt=service.saveBillCountDay(bcd);
            if (rt==1){
                logger.info("成功统计["+countdate+"] 账单导入数据=========================");
            }else{
                logger.info("失败统计["+countdate+"] 账单导入数据=========================");
            }

            service.deleteBillCountDetail(countdate);
            result=service.queryBankErrorByDesc(sd, ed);
            resultConsume=service.queryBankErrorByDescConsume(sd,ed);

            for (int i = 0; i < result.size(); i++) {
                HashMap<String, Object> map=result.get(i);
                String cdesc=(String)map.get("CDESC");
                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
//                if (ibankid==4||ibankid==8||ibankid==11||ibankid==9||ibankid==19||ibankid==14){
//                    continue;
//                }
                int nums=((BigDecimal)map.get("NUMS")).intValue();
                BillDetailCountDayDto dto=new BillDetailCountDayDto();
                dto.setCcountday(countdate);
                dto.setCdesc(cdesc);
                dto.setIbankid(ibankid);
                dto.setIfailcount(nums);
                dto.setItype(0);
                int rs=service.saveBillDetailCountDay(dto);
                if (rs==1){
                    logger.info(yesterday+" cdesc="+cdesc+" ibankid="+ibankid+" nums="+nums+" succeeded");
                }else{
                    logger.info(yesterday+" cdesc="+cdesc+" ibankid="+ibankid+" nums="+nums+" failed");
                }
            }

            for (int i = 0; i < resultConsume.size(); i++) {
                HashMap<String, Object> map=resultConsume.get(i);
                String cdesc=(String)map.get("CDESC");
                int ibankid=((BigDecimal)map.get("IBANKID")).intValue();
                int nums=((BigDecimal)map.get("NUMS")).intValue();
                if (ibankid==4||ibankid==8||ibankid==11||ibankid==9||ibankid==19||ibankid==14){
                    BillDetailCountDayDto dto=new BillDetailCountDayDto();
                    dto.setCcountday(countdate);
                    dto.setCdesc(cdesc);
                    dto.setIbankid(ibankid);
                    dto.setIfailcount(nums);
                    dto.setItype(0);
                    int rs=service.saveBillDetailCountDay(dto);
                    if (rs==1){
                        logger.info(yesterday+" cdesc="+cdesc+" ibankid="+ibankid+" nums="+nums+" succeeded");
                    }else{
                        logger.info(yesterday+" cdesc="+cdesc+" ibankid="+ibankid+" nums="+nums+" failed");
                    }
                }
            }
            statDay=today;
            logger.info("end count statDay["+yesterday+"]================================");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("Test start.");
        System.out.println(DateUtil.getCurrentDate());
        System.out.println(getCurrentDate(-1));
        System.out.print("Test end..");
    }
    public static String getCurrentDate(int paramInt)
    {
        try{
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.add(5, paramInt);
            return localSimpleDateFormat.format(localCalendar.getTime());
        }catch (Exception e){

        }
        return "";
    }

}
