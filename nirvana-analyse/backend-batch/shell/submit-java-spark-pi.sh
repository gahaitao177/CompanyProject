#!/usr/bin/env bash
./bin/spark-submit \
  --class com.caiyi.nirvana.analyse.demo.JavaSparkPi \
  deploy/analyse-backend-batch-1.0-SNAPSHOT.jar \
  spark://been.local:7077 2


