#!/usr/bin/env bash
./bin/spark-submit \
  --class com.caiyi.nirvana.analyse.env.SimpleApp \
  --master spark://been.local:7077  \
  deploy/analyse-backend-batch-1.0-SNAPSHOT.jar \
  1  spark://been.local:7077


