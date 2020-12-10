FROM java:8-alpine

MAINTAINER xggz "yyimba@qq.com"

ENV TZ=Asia/Shanghai

ARG DB_FILE
ARG JAR_FILE

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp

ADD ${DB_FILE} /db/mqr.db
ADD ${JAR_FILE}  mqr-rest.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "/mqr-rest.jar"]