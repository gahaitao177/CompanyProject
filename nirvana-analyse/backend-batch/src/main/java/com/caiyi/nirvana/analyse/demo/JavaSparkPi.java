///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.caiyi.nirvana.analyse.demo;
//
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.sql.SparkSession;
//import redis.clients.jedis.Jedis;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Computes an approximation to pi
// * Usage: JavaSparkPi [slices]
// */
//public final class JavaSparkPi {
//
//    public static void main(String[] args) throws Exception {
//        String appName = "JavaSparkPi";
//
//        SparkSession spark = getSparkSession(appName, args);
//
//
//        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
//
//
//        int slices = (args.length == 2) ? Integer.parseInt(args[1]) : 2;
//        int n = 100000 * slices;
//        List<Integer> l = new ArrayList<>(n);
//        for (int i = 0; i < n; i++) {
//            l.add(i);
//        }
//
//
//        JavaRDD<Integer> dataSet = jsc.parallelize(l, slices);
//
//        int count = dataSet.map((Function<Integer, Integer>) integer -> {
//            double x = Math.random() * 2 - 1;
//            double y = Math.random() * 2 - 1;
//            return (x * x + y * y <= 1) ? 1 : 0;
//        }).reduce((Function2<Integer, Integer, Integer>) (integer, integer2) -> integer + integer2);
//
//        double result = 4.0 * count / n;
//        System.out.println("Pi is roughly " + result);
//        //just for test
//        Jedis jedis = new Jedis("192.168.1.51", 10105);
//        jedis.set("been", new String(result + ""));
//        jedis.close();
//        spark.stop();
//    }
//
//    private static SparkSession getSparkSession(String appName, String[] args) throws Exception {
//        if (args.length > 2) {
//            throw new Exception("arg exception!");
//        }
//        if (args.length == 2) {
//            System.out.println("deploy remote " + args[0]);
//            String master = args[0];
//            if (master.equals("dcos")) {
//                return SparkSession
//                        .builder()
//                        .appName(appName)
//                        .getOrCreate();
//
//            } else {
//                return SparkSession
//                        .builder()
//                        .appName(appName)
//                        .master(master)
//                        .getOrCreate();
//            }
//
//        } else {
//            System.out.println("deploy on local");
//            return SparkSession
//                    .builder()
//                    .appName(appName)
//                    .master("local")
//                    .getOrCreate();
//        }
//    }
//}
