# syntax=docker/dockerfile:1

FROM openjdk:17
WORKDIR /testbackend/
COPY . .
RUN ./mvnw clean package

FROM openjdk:17
WORKDIR /root/
COPY --from=0 /testbackend/target/* ./
CMD ["java", "-jar ./target/*.jar"]
EXPOSE 8080
