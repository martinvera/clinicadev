FROM stazdx/openjdk11-jre-slim-buster

RUN mkdir /opt/app
COPY build/libs/sed-backend-0.0.1-SNAPSHOT.jar /opt/app

RUN java -version

CMD ["java", "-jar","-Dserver.port=80","/opt/app/sed-backend-0.0.1-SNAPSHOT.jar"]
