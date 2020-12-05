# PDF Signature Verifier

This repository contains a Web Service that verifies signatures contained
in a PDF file. The service can be used by sending requests in JSON format
like the following:
```
{
  "data": "..."
}
```
where the field ```data``` contains a base64 encoded version of the PDF file
to check. The response is again in JSON format, as follows:
```
[{
   "name": "John Smith",
   "date": "...",
   "valid": "true"
}, {
   "name": "Betty White",
   "date": "...",
   "valid": "false"
}]
```
The endpoint the validates the PDF is ```/validate```, and the service is open
on the port 8081. 

## Running the server

To build the server you need Maven, and then to run 
```
cd server && mvn package
```
and then it can be run with
```
mvn exec:java
```
or ```java -cp target/pdfsignatureverifier-1.0-jar-with-dependencies.jar it.unipi.dm.pdfsignatureverifier.Server```
The environment variable ```PSV_CERT_PATH``` is used to read an additional certificate in PEM format, 
to be used for the validation. 

## Using the PHP client

A sample PHP client, to be used from command line, is included in ```client/```, anche can
be used as follows:
```
php client.php /path/to/file.pdf
```

## Building the Docker image

To build the docker image first compile the server, running 
```
cd server && mvn package
```
and then run
```
sudo docker build -t pdf-signature-verifier .
```
or use the provided ```docker-compose.yml``` file. 
