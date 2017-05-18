package kafka;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.bean.BillImportRecord;
import com.caiyi.financial.nirvana.cassandra.CassandraCQL;
import com.caiyi.financial.nirvana.conf.ConfigurationManager;
import com.caiyi.financial.nirvana.constant.Constants;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka.OffsetRange;
import scala.Tuple2;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Socean on 2016/11/29.
 */
public class BankSessionAnalysis {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName(Constants.SPARK_APP_NAME).setMaster("local[2]");

        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(3));

        CassandraCQL cassandraCQL = new CassandraCQL();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //获取zookeeper的地址
        String zkList = ConfigurationManager.getProperty(Constants.ZOOKEEPER_LIST);
        String kafkaTopicName = ConfigurationManager.getProperty(Constants.KAFKA_TOPIC_NAME);

        //Map of (topic_name -> numPartitions) to consume. Each partition is consumed in its own thread
        Integer numThread = ConfigurationManager.getInteger(Constants.KAFKA_NUM_THREADS);

        Map<String, Integer> kafkaParams = new HashMap<String, Integer>();
        kafkaParams.put(kafkaTopicName, numThread);

        JavaPairReceiverInputDStream<String, String> linesDStream = KafkaUtils.createStream(jsc, zkList, Constants
                .KAFKA_GROUP_ID, kafkaParams);

        System.out.println("--------------------------------1");

        JavaDStream<String> lineJSONDStream = linesDStream.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> tuple2) throws Exception {

                return tuple2._2();
            }
        });

        System.out.println("--------------------------------2");

        JavaDStream<BillImportRecord> sessionDetailDStream = lineJSONDStream.map(new Function<String,
                BillImportRecord>() {
            @Override
            public BillImportRecord call(String lineJson) throws Exception {

                BillImportRecord billImportRecord = JSON.parseObject(lineJson, BillImportRecord.class);

                return billImportRecord;
            }
        });

        System.out.println("--------------------------------3");


        sessionDetailDStream.foreachRDD(new VoidFunction<JavaRDD<BillImportRecord>>() {
            @Override
            public void call(JavaRDD<BillImportRecord> billImportRecordJavaRDD) throws Exception {

                billImportRecordJavaRDD.foreachPartition(new VoidFunction<Iterator<BillImportRecord>>() {

                    @Override
                    public void call(Iterator<BillImportRecord> billImportRecordIterator) throws Exception {

                        while (billImportRecordIterator.hasNext()) {

                            BillImportRecord billImportRecord = billImportRecordIterator.next();

                            String time = dateFormat.format(billImportRecord.getRequestDate());

                            System.out.println("-----------------------" + time);

                            String cql = "insert into bill_bank_test(sessionId,type,bank,userName,result,detail,time)"
                                    + "values ('"
                                    + billImportRecord.getSessionId()
                                    + "','"
                                    + billImportRecord.getAction()
                                    + "','"
                                    + billImportRecord.getBankCode()
                                    + "','"
                                    + billImportRecord.getUserName()
                                    + "','"
                                    + billImportRecord.getResultCode()
                                    + "','"
                                    + billImportRecord.getResultDesc()
                                    + "','"
                                    + time
                                    + "');";

                            System.out.println("CQL = " + cql);

                            cassandraCQL.insertBankCQL(cql);

                        }

                    }
                });
            }
        });

        try {
            jsc.start();

            jsc.awaitTermination();

            jsc.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
