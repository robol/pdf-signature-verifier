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
   "date": timestamp,
   "DN": "C=UK,O=My Organization,CN=John Smith,SERIALNUMBER=1421348dfg7",
   "issuerDN": "CN=...",
   "notBefore": timestamp,
   "notAfter": timestamp,
   "valid": "true"
}, {
   "name": "Elizabeth White",
   "date": timestamp,
   "DN": "C=US,O=Some other Company,CN=Elizabeth White,SERIALNUMBER=quuuseiFish1",
   "issuerDN": "CN=...",
   "notBefore": timestamp,
   "notAfter: timestamp,
   "valid": "false"
}]
```
All the timestamps above are UNIX timestamps, as integer numbers. DN is the DN of the
signing entity, while issuerDN contains the DN of the entity that issued that 
certificate. 
The endpoint the validates the PDF is ```/validate```, and the service is open
on the port 8081. 

## Running the server

The recommended way to run the server is with Docker, using the provided
```docker-compose.yml``` file. With Docker installed, you can just run 
```
docker-compose up
```
and point your browser to ```http://localhost:8081/```. You may want to 
add some certificates in the ```certs``` folder that is created at the 
first Docker start. 

## Running the server manually

To build the server you need Maven, and then to run 
```
cd server && mvn package
```
and then it can be run with
```
mvn exec:java
```
or ```java -cp target/pdfsignatureverifier-1.0-jar-with-dependencies.jar it.unipi.dm.pdfsignatureverifier.Server```
The environment variable ```PSV_CERT_PATH``` is used to read additional certificates in PEM format, which are looked
for in the specified folder, and will be used for the validation. 

## Using the PHP client

A sample PHP client, to be used from command line, is included in ```client/```, anche can
be used as follows:
```
php client.php /path/to/file.pdf
```
To send the request to a remote API endpoint (make sure to use HTTPS, this
sends the entire file BASE64-encoded!), you can use
```
php client.php https://my-endpoint.com/validate /path/to/file.pdf
```

## Building the Docker image

To build the docker image first compile the server, running 
```
cd server && mvn package
```
and then run
```
sudo docker build -t robol/pdf-signature-verifier server
```
You may want to specify the environment variable ```PSV_CERT_PATH``` to
load additional certificates. 

An easier alternative, the provided ```docker-compose.yml``` file can be used, 
which pulls the image from Docker Hub. By default, this includes the local 
folder ```./certs/``` as a volume mounted on ```/certs/``` inside the 
container, which is used to provide additional certificates. 
