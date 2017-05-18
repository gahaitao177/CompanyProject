#!/usr/bin/env bash
echo "账单模块打包上传"
##账单模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.bill
modelPath=${path}.model
jarName=nirvana.ccard.bill.bank-1.0.0-SNAPSHOT.jar
jarPath=${path}.bank/target/${jarName}

topologyId=hskBillBank

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}.bank


source ${rootPath}/start/SUBMIT.sh

