FROM azul/zulu-openjdk:11
ARG JAR_FILE=build/libs/*.jar
ARG AGENT_APPINSIGHT=agent/applicationinsights*.*

COPY ${JAR_FILE} expediente-service-0.0.1-SNAPSHOT.jar 
COPY ${AGENT_APPINSIGHT} /agent/

ENTRYPOINT  ["java", "-javaagent:/agent/applicationinsights-agent-3.1.1.jar", "-jar","-Dserver.port=80", "/expediente-service-0.0.1-SNAPSHOT.jar"]