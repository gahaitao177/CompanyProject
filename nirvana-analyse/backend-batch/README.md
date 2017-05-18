package com.caiyi.nirvana.analyse;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by been on 2017/1/16.
 */
public class SparkDemo {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("demo").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = sc.parallelize(data);
        JavaRDD<String> distFile = sc.textFile("pom.xml");
        distFile.map(s -> s.length()).reduce((a, b) -> (a + b));

    }
}
