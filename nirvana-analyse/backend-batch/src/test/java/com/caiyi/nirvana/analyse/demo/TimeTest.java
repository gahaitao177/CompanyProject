package com.caiyi.nirvana.analyse.demo;

import org.apache.spark.sql.catalyst.expressions.Concat;

import java.util.Date;
import java.util.Random;

/**
 * Created by root on 2017/1/23.
 */
public class TimeTest {

    public static void main(String[] args) {
        Random random = new Random();

        for (int i = 0; i < 5; i++) {

            int prefix = random.nextInt();

            System.out.println("prefix=" + prefix);
        }



    }
}
