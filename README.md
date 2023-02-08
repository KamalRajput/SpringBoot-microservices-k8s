# SpringBoot-microservices-k8s
 Spring boot app with Devops

Docker file 


docker build -t kamalrajput755/configserver:0.0.1 .
docker build -t kamalrajput755/serviceregistry:0.0.1 .
docker build -t kamalrajput755/cloudgateway:0.0.1 .

docker run -d -p8761:8761 --name serviceregistry imageId
now to make other microservices register on eureka server running in a separate container, we have to do networking.
We have to pass on environment variable used in application yaml. Instead of using localhost, we have to use host.docker.internal
to make docker containers connect to their internal network.

docker run -d -p9296:9296 -e EUREKA_SERVER_ADDRESS=http://host.docker.internal:8761/eureka --name configserver imageId


docker run -d -p9090:9090 -e CONFIG_SERVER_URL=host.docker.internal  -e EUREKA_SERVER_ADDRESS=http://host.docker.internal:8761/eureka --name cloudgateway imageId

docker run -d -p8080:9090 -e CONFIG_SERVER_URL=host.docker.internal  -e EUREKA_SERVER_ADDRESS=http://host.docker.internal:8761/eureka --name productservice imageId
