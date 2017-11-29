# Learning Akka

This project is used to teach the basics of _core_ Akka.

## Chapter 1
Create a database actor called `DbActor` that stores values of type `Any` in a Map, indexed by a key of type `String`.
The actor should be created in the `actor.db` package.

What happens when you send a message with a key of type Int and a value of type String. Write a test that causes the actor to log an error.

This is all pretty poor practice. 
We should be defining a Command that we send as a message to the actor and the actor should likely emit an event as a result of an action that the sender might be interested in. 
Refactor to add a StoreValue command and a RetrieveValue command to the `DbActor`. 
A RetrieveValue command should result in either a KeyNotFound or a KeyFound event being emmitted by your DbActor implementation.
The KeyFound event would wrap the value being returned.
Refactor your tests.

## Chapter 2
There is a simply REST service for the application now. The goal of this chapter is to add some endpoints to interact
with the `DbActor`.

#### Requirement 2.1
Create a REST endpoint that will return the content stored in the `DbActor` for a given key.
```
GET http://localhost:9000/db?key=[key]
```

If the guid (key) is not in the AkkaDB, return a 404 error, Not Found.

#### Requirement 2.2
Create a REST endpoint that will store a value in the `DbActor`. Specify the key and value to be stored in `key` and 
`value` query parameters
```
POST http://localhost:9000/db?key=foo&value=cheese
```


#### Sample Curls
Store a piece of content
```
curl -i -X POST "http://localhost:9000/db?key=foo&value=cheese"
```

Retrieve a piece of content
```
curl -i http://localhost:9000/db?key=foo
```
