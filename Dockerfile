# Multi-stage build for PTFMS
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM tomcat:10.1-jdk21
# Remove default ROOT
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/FinalProject-PTFMS-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
# Environment variables for DB configuration
ENV DB_HOST=database \
    DB_PORT=3306 \
    DB_NAME=ptfms \
    DB_USER=ptfms_user \
    DB_PASS=changeMe \
    TZ=UTC
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 CMD curl -f http://localhost:8080/ || exit 1

