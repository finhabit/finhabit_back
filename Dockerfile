FROM scratch as build_context_copy
COPY build/libs/*.jar app.jar

FROM eclipse-temurin:17-jre
ENV TZ=Asia/Seoul \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Duser.timezone=Asia/Seoul"
WORKDIR /opt/app

COPY --from=build_context_copy /app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]