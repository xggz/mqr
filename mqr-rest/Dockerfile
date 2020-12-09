FROM java:8-alpine

MAINTAINER xggz "yyimba@qq.com"

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp

ADD https://molicloud.oss-cn-hangzhou.aliyuncs.com/mqr/db/mqr-last.db /db/mqr.db
COPY ./target/mqr-rest.jar /mqr-rest.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "mqr-rest.jar"]