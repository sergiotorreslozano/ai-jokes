# ai-jokes


This is a simple Spring boot application with a hello world for Open AI

There are different endpoints in the application:

**/joke?subject=dogs** 
will return a joke related with the path parameter passed as subject

**/ask?question=what is the default armor class** will return what the AI has understood from the AD&D guide 

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

To test the jokes:

```
curl --location 'http://localhost:8080/joke?subject=dogs'
```

To test the embeddings model (AD&D Guide)
```
curl --location 'http://localhost:8080/ask?question=what%20is%20the%20default%20armor%20class'
```
Note:


If you want to use the Hearavon guide and make questions like this:
```
curl --location 'http://localhost:8080/ask?question=what%20are%20the%20strongest%20classes'
```
You need to take 2 actions:

1- Change the property files to point to the guide:
```
app.documentResource=classpath:icewind-dale-guide-haeravon.txt
```
2- Change the embedding model to ada: 

current: text-embedding-3-small

change to: text-embedding-ada-002

Please be aware of the cost of the different models. 


