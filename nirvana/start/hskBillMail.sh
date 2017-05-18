#!/usr/bin/env bash
echo "邮箱模块打包上传"
##邮箱模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.bill
modelPath=${path}.model
jarName=nirvana.ccard.bill.mail-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskBillMail

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}.mail

source ${rootPath}/start/SUBMIT.sh

