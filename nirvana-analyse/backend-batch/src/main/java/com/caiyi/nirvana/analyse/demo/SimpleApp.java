//package com.caiyi.nirvana.analyse.demo;
//
///**
// * Created by been on 2017/1/16.
// */
//
//import org.apache.spark.api.java.*;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.function.Function;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class SimpleApp {
//    public static void main(String[] args) {
//        Logger logger = LoggerFactory.getLogger(SimpleApp.class);
//        String logFile = "backend-batch/README.md"; // Should be some file on your system
//        String master = "local";
//        if (args != null && args.length == 2) {
//            logFile = "README.md";
//            master = args[1];
//        }
//        SparkConf conf = new SparkConf().setAppName("Simple Application").setMaster(master);
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        JavaRDD<String> logData = sc.textFile(logFile).cache();
//
//        long numAs = logData.filter((Function<String, Boolean>) s -> s.contains("a")).count();
//
//        long numBs = logData.filter((Function<String, Boolean>) s -> s.contains("b")).count();
//
//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
//        logger.info("Lines with a: " + numAs + ", lines with b: " + numBs);
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        sc.stop();
//    }
//}
