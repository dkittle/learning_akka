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
