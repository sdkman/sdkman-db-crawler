FROM java:8

WORKDIR /work

COPY *.jar /work/application.jar

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "application.jar"]
