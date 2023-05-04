# syntax=docker/dockerfile:1

FROM openjdk:17
WORKDIR /testbackend
COPY . .
RUN chmod 755 ./mvnw
RUN ./mvnw clean package
RUN cp ./target/demo*.jar ./demo.jar

FROM openjdk:17
WORKDIR /root
COPY --from=0 /testbackend/demo.jar .
CMD ["java", "-jar", "demo.jar"]
EXPOSE 8080
