FROM 192.168.0.1/base/docker-java-base:jdk_8

COPY analyse-backend-batch-1.0.jar /

VOLUME "/opt/export"

RUN cd / \



EXPOSE 8080

CMD ["java","-jar","analyse-backend-batch-1.0.jar"]
