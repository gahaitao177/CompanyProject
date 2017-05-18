package com.caiyi.financial.nirvana.ccard.investigation.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.TestSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxingyu on 2017/3/17.
 */
public class YouyuCreditServiceTest extends TestSupport {

    @Autowired
    private YouyuCreditService service;

   /* @Test
    public void creditScoreIndex() throws Exception {
//<<<<<<< HEAD
        //String cuserId1 = "abd85cda9fae48d5a54ed4bd373315f6";
        //service.creditScoreIndex(cuserId1);
        //String cuserId2 = "07ab10b0219";
        //service.creditScoreIndex(cuserId2);
        //String cuserId3 = "797666a0104";
        //service.creditScoreIndex(cuserId3);
//=======
//        String cuserId1 = "abd85cda9fae48d5a54ed4bd373315f6";
//        service.creditScoreIndex(cuserId1);
//        String cuserId2 = "07ab10b0219";
//        service.creditScoreIndex(cuserId2);
//        String cuserId3 = "797666a0104";
//        service.creditScoreIndex(cuserId3);
//>>>>>>> upstream/dev
    }*/

    @Test
    public void calcMonthRepayTest(){
        List<Integer> cards = new ArrayList<>();
        cards.add(3526);
        cards.add(3172);
    }
}