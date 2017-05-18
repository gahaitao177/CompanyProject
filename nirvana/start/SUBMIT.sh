mvn clean install -DskipTests -P remotetest,storm

stormIp=192.168.1.207
stormPath=/opt/cluster/storm
echo "执行上传"
scp ${jarPath} root@${stormIp}:${stormPath}/deploy
echo "停止旧拓扑"
echo "stop ${topologyId}"
ssh  root@${stormIp} "${stormPath}/bin/storm kill ${topologyId} -w 3"

echo "等待10s"
sleep 10 ##在此处调整等待时间##

echo "启动新拓扑"
startPath="${stormPath}/bin/storm jar ${stormPath}/deploy/${jarName} com.caiyi.financial.nirvana.core.Main"
echo "start ${topologyId} : ${startPath}"
ssh  root@${stormIp} "${startPath}"
