#!/usr/bin/env bash
echo "信用卡优惠模块打包上传"
##信用卡优惠模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.discount.ccard
modelPath=${path}.model
jarName=nirvana.discount.ccard-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskCcard

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh