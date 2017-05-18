#!/usr/bin/env bash
echo "模拟办卡模块打包上传"
##模拟办卡模块快捷打包sh脚本
cd ..
rootPath=`pwd`
echo ${rootPath}
path=${rootPath}/nirvana.ccard.material
modelPath=${path}.model
jarName=nirvana.ccard.material-1.0.0-SNAPSHOT.jar
jarPath=${path}/target/${jarName}

topologyId=hskCardMaterial

echo "打包"
cd ${rootPath}/nirvana.core
mvn clean install -DskipTests
cd ${modelPath}
mvn clean install -DskipTests
cd ${path}

source ${rootPath}/start/SUBMIT.sh
