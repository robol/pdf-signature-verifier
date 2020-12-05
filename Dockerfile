FROM java:8

COPY ./server/target/pdfsignatureverifier-1.0-jar-with-dependencies.jar /server.jar
COPY ./cert.pem /cert.pem

EXPOSE 8081

CMD PSV_PORT=8081 PSV_CERT_PATH=/ \
    java -cp /server.jar it.unipi.dm.pdfsignatureverifier.Server
