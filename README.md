# ai-jokes


This is a simple Spring boot application for a hello world for Open AI

There are three different endpoints in the application:

** /joke ** will return a joke related with the path parameter passed as subject


To run the application:

0- Pre-requisites (might work with other versions)
```
You'll need Java 17 and Maven 3 installed
```

1- To run the test ( by default in a random port)
```
mvn clean test
```

2- To run the application (by default in port 8080)
```
mvn spring-boot:run
```

To test it:

```
curl --location 'http://localhost:8080/joke?subject=dogs'
```