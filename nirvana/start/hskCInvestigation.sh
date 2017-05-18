#!/usr/bin/env bash
echo "征信模块打包上传"
##征信模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.investigation
modelPath=${path}.model
jarName=nirvana.ccard.investigation-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskCInvestigation

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh
