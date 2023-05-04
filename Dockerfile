# syntax=docker/dockerfile:1

FROM openjdk:17
WORKDIR /testbackend/
COPY . .
RUN chmod 755 ./mvnw
RUN ./mvnw clean package

FROM openjdk:17
WORKDIR /root/
COPY --from=0 /testbackend/target/* ./
CMD ["java", "-jar", "./*.jar"]
EXPOSE 8080
