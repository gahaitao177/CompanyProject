package com.caiyi.financial.nirvana.ccard.bill.bank.quartz;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.EmailUtil;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillCountDayDto;
import com.danga.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vi on 2016/8/19.
 */
@Service
public class CheckEveryDaySuccBill {

    private static Logger logger = LoggerFactory.getLogger(CheckEveryDaySuccBill.class);

    @Autowired
    private BankImportService bankImportService;

    @Autowired
    private MemCachedClient memCachedClient;


    public void run() {
        //每天晚上7点到7点30之间发邮件。
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(calendar.HOUR_OF_DAY) != 7 || calendar.get(Calendar.MINUTE) > 30) {
            return;
        }
        //检测是否已经发送过邮件
        Object obj = memCachedClient.get("CHECK_EVERY_DAY_SUCC");
        if (obj != null) {
            return;
        }
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yesterday = sdf.format(calendar.getTime());
        BillCountDayDto dto = bankImportService.queryBillCountDay(yesterday);
        if (dto == null) {
            logger.info("查询不到" + yesterday + "账单导入记录");
            if (calendar.get(Calendar.MINUTE) > 25) {
                //保存1小时
                memCachedClient.set("CHECK_EVERY_DAY_SUCC", 1, new Date(1000 * 60 * 60));
                EmailUtil.sendEmail("账单记录统计失败", yesterday + "请人为查看！");
            }
        } else {
            //广发400，中信80，光大50，农业80，花旗1，平安250，华夏10，浦发100，兴业900，民生50，建设4500，工商50，交通1000，上海2，招商1000
            int succCount = 0;
            int failCount = 0;
            String title = "";
            String content = "";
            //广发
            succCount = dto.getIgfs();
            failCount = dto.getIgfe();
            if (succCount < 400) {
                sendEmail("广发", yesterday, succCount, failCount, 400);
            }
            //中信
            succCount = dto.getIzxs();
            failCount = dto.getIzxe();
            if (succCount < 80) {
                sendEmail("中信", yesterday, succCount, failCount, 80);
            }
            //光大
            succCount = dto.getIgds();
            failCount = dto.getIgde();
            if (succCount < 50) {
                sendEmail("光大", yesterday, succCount, failCount, 50);
            }
            // 农业
            succCount = dto.getInys();
            failCount = dto.getInye();
            if (succCount < 80) {
                sendEmail("农业", yesterday, succCount, failCount, 80);
            }
            succCount = dto.getIhqs();
            failCount = dto.getIhqe();
            if (succCount < 1) {
                sendEmail("花旗", yesterday, succCount, failCount, 1);
            }
            succCount = dto.getIpas();
            failCount = dto.getIpae();
            if (succCount < 250) {
                sendEmail("平安", yesterday, succCount, failCount, 250);
            }
            succCount = dto.getIhxs();
            failCount = dto.getIhxe();
            if (succCount < 10) {
                sendEmail("华夏", yesterday, succCount, failCount, 10);
            }
            succCount = dto.getIpfs();
            failCount = dto.getIpfe();
            if (succCount < 100) {
                sendEmail("浦发", yesterday, succCount, failCount, 100);
            }
            succCount = dto.getIxys();
            failCount = dto.getIxye();
            if (succCount < 900) {
                sendEmail("兴业", yesterday, succCount, failCount, 900);
            }
            succCount = dto.getImss();
            failCount = dto.getImse();
            if (succCount < 50) {
                sendEmail("民生", yesterday, succCount, failCount, 50);
            }
            succCount = dto.getIjss();
            failCount = dto.getIjse();
            if (succCount < 4500) {
                sendEmail("建设", yesterday, succCount, failCount, 4500);
            }
            succCount = dto.getIgss();
            failCount = dto.getIgse();
            if (succCount < 50) {
                sendEmail("工商", yesterday, succCount, failCount, 50);
            }
            succCount = dto.getIjts();
            failCount = dto.getIjte();
            if (succCount < 1000) {
                sendEmail("交通", yesterday, succCount, failCount, 1000);
            }
            succCount = dto.getIshs();
            failCount = dto.getIshe();
            if (succCount < 2) {
                sendEmail("上海", yesterday, succCount, failCount, 2);
            }
            succCount = dto.getIzss();
            failCount = dto.getIzse();
            if (succCount < 1000) {
                sendEmail("招商", yesterday, succCount, failCount, 1000);
            }
            memCachedClient.set("CHECK_EVERY_DAY_SUCC", 1, new Date(1000 * 60 * 60));
        }
    }

    private void sendEmail(String bankNmae, String date, int succ, int fail, int average) {
        String title = bankNmae + "银行账单";
        String content = bankNmae + "银行账单" + date + "成功数" + succ + "失败数" + fail + "低于每日平均成功数" + average + "请核查。";
        EmailUtil.sendEmail(title, content);
    }

    public static void main(String[] args) throws InterruptedException {
        // MemCachedClient memCachedClient = new MemCachedClient();
        // memCachedClient.set("a","b", new Date(5000));
        // String a = (String)memCachedClient.get("a");
        // System.out.println("前" + a);
        // Thread.sleep(10000);
        // a = (String) memCachedClient.get("a");
        // System.out.println("后" + a);
        // Calendar calendar = Calendar.getInstance();
        // System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        // System.out.println(calendar.get(Calendar.MINUTE));
        //     CheckEveryDaySuccBill aaa = new CheckEveryDaySuccBill();
        //     aaa.run();
        //EmailUtil.sendEmail("测试邮件","测试");

        // System.out.println("Test start.");
        // System.out.println(DateUtil.getCurrentDate());
        // System.out.println(getCurrentDate(-1));
        // System.out.print("Test end..");
    }

}
