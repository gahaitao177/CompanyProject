package bank;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.bean.BillImportRecord;
import com.caiyi.financial.nirvana.conf.ConfigurationManager;
import com.caiyi.financial.nirvana.constant.Constants;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by root on 2016/12/12.
 */
public class demo2 {

     public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("GaoHaitaoAppName").setMaster("local[2]");

        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(5));

        SQLContext sqlContext = new SQLContext(jsc.sparkContext());

        Map<String,String> cassandraTable = new HashMap<String,String>();
        cassandraTable.put("keyspace","demospace");
        cassandraTable.put("table", "bill");

        Dataset<Row> sessionDF = sqlContext.read().format("org.apache.spark.sql.cassandra")
                .options(cassandraTable)
                .load();

        //获取zookeeper的地址
        String zkList = ConfigurationManager.getProperty(Constants.ZOOKEEPER_LIST);
        String kafkaTopicName = ConfigurationManager.getProperty(Constants.KAFKA_TOPIC_NAME);

        //Map of (topic_name -> numPartitions) to consume. Each partition is consumed in its own thread
        Integer numThread = ConfigurationManager.getInteger(Constants.KAFKA_NUM_THREADS);

        Map<String, Integer> kafkaParams = new HashMap<String, Integer>();
        kafkaParams.put(kafkaTopicName, numThread);

        JavaPairReceiverInputDStream<String, String> linesDStream = KafkaUtils.createStream(jsc, zkList, Constants
                .KAFKA_GROUP_ID, kafkaParams);

        JavaDStream<String> lineJSONDStream = linesDStream.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> tuple2) throws Exception {

                return tuple2._2();
            }
        });

        JavaDStream<BillImportRecord> sessionDetailDStream = lineJSONDStream.map(new Function<String,
                BillImportRecord>() {
            @Override
            public BillImportRecord call(String lineJson) throws Exception {

                BillImportRecord billImportRecord = JSON.parseObject(lineJson, BillImportRecord.class);

                return billImportRecord;
            }
        });

        sessionDetailDStream.foreachRDD(new VoidFunction<JavaRDD<BillImportRecord>>() {
            @Override
            public void call(JavaRDD<BillImportRecord> billImportRecordJavaRDD) throws Exception {
                billImportRecordJavaRDD.foreachPartition(new VoidFunction<Iterator<BillImportRecord>>() {
                    @Override
                    public void call(Iterator<BillImportRecord> billSessionIterator) throws Exception {
                        while (billSessionIterator.hasNext()) {
                            BillImportRecord billImportRecord = billSessionIterator.next();

                            String cql = "insert into session(sessionId,type,bank,userName,result,detail,time)"
                                    + "values ('"
                                    + billImportRecord.getAction()
                                    + "','"
                                    + billImportRecord.getBankCode()
                                    + "','"
                                    + billImportRecord.getSessionId()
                                    + "','"
                                    + billImportRecord.getUserName()
                                    + "','"
                                    + billImportRecord.getUserName()
                                    + "','"
                                    + billImportRecord.getResultCode()
                                    + "','"
                                    + billImportRecord.getResultDesc()
                                    + "','"
                                    + billImportRecord.getRequestDate()
                                    + ")";
                        }
                    }
                });
            }
        });

        jsc.start();

        try {
            jsc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        jsc.stop();

    }
}
