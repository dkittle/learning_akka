# Learning Akka

This project is used to teach the basics of _core_ Akka.

## Chapter 1
We have discussed 
Create a database actor called `DbActor` that stores values of type Any in a Map, indexed by a key of type String.
The actor should be created in the `actor.db` package.

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

## Chapter 3
The goal of this chapter is to have your application retrieve an RSS feed and store the contents of each article in the feed in the `DbActor`.

#### Requirement 1
Create a REST endpoint that accepts a URL of content that should be downloaded via an RSS feed. The application will
retrieve the RSS feed from the URL provided, parse out the articles (items) and store the content of those articles in
the `DbActor`.

**Add only ONE actor to the system for this implementation.** Call the actor `RssActor`.

When storing content in the AkkaDb, you should use the `<guid>` element as the key for each `<item>` in the RSS feed. The value
stored in the AkkaDb could be the `<description>` element of the item or you may opt to download the content from the
item's URL found in the `<link>` of the item. The description element is often wrapped in a CDATA which you’ll need to discard.

If you grab the content from the URL in the `<link>` element, you can use the BoilerPipe library included in the Learning
Akka project to retrieve the content stripped of extraneous HTML elements/tags from it’s specific URL. For example:
`de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(new java.net.URL( \
"http://www.cbc.ca/news/canada/ottawa/ottawa-weather-forecast-october-5-1.3791908?cmp=rss"))`

The endpoint should be
```
POST http://localhost:9000/contents/url
```

The request body should be JSON indicating the URL to fetch the RSS feed from.
```
{
    "url", "[RSS feed url]"
}
```

If content cannot be downloaded from the URL ,return a 404 error, Not Found.

If the URL has content but it’s not in the format you expect, return a 400 error, Bad Request. If the supplied URL is invalid, return a 400 error, Bad Request.

#### Requirement 2
Create a REST endpoint that will return the content for a given guid.
```
GET http://localhost:9000/content/guid/[guid]
```

If the guid (key) is not in the AkkaDB, return a 404 error, Not Found.

#### Requirement 3
Create a REST endpoint that will return the guids for all content in the system. You do not need to paginate this data.
```
GET http://localhost:9000/contents/guids
```

#### Example Curls

Parse an RSS feed
```
curl -i -H "Content-Type: application/json" -X POST -d '{"url":"http://www.cbc.ca/cmlink/rss-world"}' http://localhost:9000/contents/url
```

List the guid keys in the DB
```
curl -i http://localhost:9000/contents/guids
```

Retrieve a piece of content
```
curl -i http://localhost:9000/contents/guid/[a guid from the content in the DB]
```

## Chapter 4
If you are grabbing articles and cleaning them up using `boilerpipe`, you likely needed to increase your `ask Timeout`
in `Main` to 10 seconds or so. This is because the RssActor is downloading articles from a URL and stripping out tags
in a serial fashion: one article at a time.

Start by refactoring the code to download an article, strip it of tags and store it in the `DbActor` to a separtate 
actor. Then, change that actor into a `Router` (with a pool of actors created by it). What happens to the RSS fetching
time with 2 actors in the pool? How about with 5 actors in the pool?

## Chapter 5
Alas, when you shut down the application, the `DbActor` looses it's state. One can preserve state by using Akka
persistence.

Refactor the `DbActor` to use Akka persistence. We'll be using leveldb to store state for testing and development.
Use **event** sourcing for your solution. Add a `Remove` message to the `DbActor` that removes a key, if it exists.
Have the remove message cause a snapshot of the actor state.

## Chapter 6
Having some historical stats on RSS processing would make the application a lot more useful. We will create a 
`Bookkeeper` actor that will track the status of a fetch as it's running and retain stats on the results of each
 fetch after the fact.
 
Implement the `Bookkepper` as a persistent actor that does a snapshot after each RSS fetch is completely processed 
(after all items have been fetched and stored in the `DbActor`).

### Requirement 6.1
`RssActor` should inform `Bookkeeper` of the status of each RSS processing.
It should send a message when it retrieves an RSS file along with the results of the fetch (404, not an RSS
feed or fetched). It should also send a message after each article fetch request has been submitted to `FetchActor`
and include the GUID of each article.
Finally, it should send a message when all article fetch messages have been sent to the `FetchActor` including the
the number of articles in the RSS file.

### Requirement 6.2
`FetchActor` should send a message to `Bookkeeper` with the results of each article fetch.

### Requirement 6.3
When an RSS feed is processed, the `RSSActor` should aggregate metrics for the processing of the feed including number 
of articles in the feed, the number that were successfully fetched and the number of articles that could not be fetched.

### Requirement 6.4
--- blurb about snapshotting aggregate metrics


For each RSS file processing request, the `Bookkeeper` w