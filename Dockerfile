# 指定构成镜像的基础镜像源,如需要jdk:镜像名,8-alpine:版本号
# From java:8-alpine
# 指定从网易的镜像网站中下载镜像
FROM hub.c.163.com/library/java:8-alpine
# 标签信息,可写多个,key=value形式
LABEL Author=DreamFlyingFlower
LABEL Version=0.0.1
# 复制需要制作的源文件到docker容易中,名字自定义
ADD dream-study-microservice.jar spring-cloud-test.jar
# 容器对外映射的端口号
EXPOSE 55555
# 启动命令
ENTRYPOINT ["java","-jar","/spring-cloud-test.jar","--spring.profiles.active=prod"]
# 配置java环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_171
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH
# 进入容器命令
CMD /bin/bash