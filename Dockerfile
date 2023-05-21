# Docker image for springboot application

FROM bellsoft/liberica-runtime-container:jre-17-slim-stream-musl

#系统编码
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

#应用构建成功后的jar文件被复制到镜像内，名字也改成了app.jar
ADD target/ChatGptManage-0.0.1-SNAPSHOT.jar app.jar

#启动容器时的进程
ENTRYPOINT ["java","-Dsun.jnu.encoding=UTF-8","-Dfile.encoding=UTF-8","-Duser.timezone=GMT+8","-jar","/app.jar"]

EXPOSE 8081