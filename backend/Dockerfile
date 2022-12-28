FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./target/dscatalog-0.0.1-SNAPSHOT.jar dscatalog-jameseng.jar
ENTRYPOINT ["java","-jar","/dscatalog-jameseng.jar"]