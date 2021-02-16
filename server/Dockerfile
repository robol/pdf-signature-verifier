FROM openjdk:17

RUN mkdir /certs
COPY ./target/pdfsignatureverifier-1.0-jar-with-dependencies.jar /server.jar

EXPOSE 8081

CMD PSV_PORT=8081 PSV_CERT_PATH=/certs/ \
    java -cp /server.jar it.unipi.dm.pdfsignatureverifier.Server
