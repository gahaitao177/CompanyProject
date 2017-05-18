# Kafka测试工具 ,Zookeeper测试工具
## Kafka测试工具


### 示例(默认测试正式环境地址):
`
    java -jar nirvana.tools.kafka.jar topic
`
#### 可选项:
1. -zks Zookeeper Servers
2. -kfks kafka brokers list
3. -kfkr kafka Zk RootPath(默认空)
4. -msg 自定义发送消息
    
### 示例:
`
    java -jar nirvana.tools.kafka.jar topic -msg 测试消息不要有空格 -zks 192.168.1.1:2181,192.168.1.2:2181 -kfks 192.168.1.1:9092,192.168.1.2:9092 -kfkr kafkaZkRootPath
`

## Zookeeper测试工具


### 示例(默认测试正式环境地址):
`
    java -jar nirvana.tools.zookeeper.jar
`
### 可选项 附加参数:
1.Zookeeper Servers
     
### 示例:
`
    java -jar nirvana.tools.zookeeper.jar 192.168.1.55:2181
`
    