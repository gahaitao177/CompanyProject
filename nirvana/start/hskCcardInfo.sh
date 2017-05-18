#!/usr/bin/env bash
echo "卡信息模块打包上传"
##卡信息模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.ccardinfo
modelPath=${path}.model
jarName=nirvana.ccard.ccardinfo-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskCcardInfo

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh