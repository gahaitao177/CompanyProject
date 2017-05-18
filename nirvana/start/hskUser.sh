#!/usr/bin/env bash
echo "用户模块打包上传"
##用户模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.discount.user
modelPath=${path}.model
jarName=nirvana.discount.user-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

##execPath= ${stormPath}/hsk-deploy-discount-user.sh
topologyId=hskUser

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh
