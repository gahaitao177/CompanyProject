#!/usr/bin/env bash
echo "贷款模块打包上传"
##贷款模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.loan
modelPath=${path}.model
jarName=nirvana.ccard.loan-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskLoan

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh
