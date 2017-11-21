# Learning Akka

This project is used to teach the basics of _core_ Akka.

## Chapter 1

Create a database actor called `DbActor` that stores values of type `Any` in a Map, indexed by a key of type `String`.
The actor should be created in the `actor.db` package.

What happens when you send a message with a key of type `Int` and a value of type `String`. Write a test that logs an error.

This is all pretty poor practice. We should be defining a Command that we send as a message to the actor and the actor should likely emit an event as a result of the Command being accepted. Refactor to add a `StoreValue` command and a `ValueStored` event to your DbActor implementation. Refactor your tests.
