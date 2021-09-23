FROM azul/zulu-openjdk:11
ARG JAR_FILE=build/libs/*.jar
ARG AGENT_APPINSIGHT=agent/applicationinsights*.*

COPY ${JAR_FILE} clinicalrecord-service-0.0.1-SNAPSHOT.jar 
COPY ${AGENT_APPINSIGHT} /agent/

ENTRYPOINT  ["java", "-javaagent:/agent/applicationinsights-agent-3.1.1.jar", "-jar","-Dserver.port=80", "/clinicalrecord-service-0.0.1-SNAPSHOT.jar"]