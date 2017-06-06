FROM 192.168.0.1/base/docker-java-base:jdk_8

COPY dubbo-demo-consumer-1.0-SNAPSHOT.jar /

VOLUME "/opt/export"

RUN mkdir /logs \
&& cd / \

EXPOSE 20888

CMD ["java","-jar","dubbo-demo-consumer-1.0-SNAPSHOT.jar"]