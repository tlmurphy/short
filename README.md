# Short

A simple URL shortener.

## Prerequisites

1. sbt
2. npm

## Running the App

This application has multiple subprojects using different frameworks and
libraries for the http server.

> Note: Start the frontend and backend in separate terminal windows
> I've had issues when attempting to start the akka-http backend
> in the background, probably due to the process forking.

Starting the frontend:

1. `npm --prefix=web i`
2. `npm --prefix=web start`

Starting the backend:

* akkahttp: `sbt shortAkkaHttp/run`
* http4s: `sbt shortHttp4s/run`
* play: `sbt shortPlay/run`
* zio: `sbt shortZio/run`

The backend is served on port 8081 while the frontend is served
on port 8080. To get to the webpage, navigate to http://localhost:8080.

## API Guide

Create new short url mapping:

```
POST /urls
BODY: { "url": "http://google.com" }
RESP: 201 CREATED
```

```json
{
  "message": "(o7iFyQz) -> (http://google.com) successfully created",
  "url": {
    "originalUrl": "http://google.com",
    "shortUrl": "o7iFyQz"
  }
}
```

Get all url mappings:

```
GET /urls
RESP: 200 OK
```

```json
{
  "urls": [
    {
      "originalUrl": "http://google.com",
      "shortUrl": "o7iFyQz"
    }
  ]
}
```

Get a single url mapping:

```
GET /urls/o7iFyQz
RESP: 200 OK
```

```json
{
  "message": "(o7iFyQz) -> (http://google.com) successfully retrieved",
  "url": {
    "originalUrl": "http://google.com",
    "shortUrl": "o7iFyQz"
  }
}
```

Delete a single url mapping:
```
DELETE /urls/o7iFyQz
RESP: 200 OK
```

```json
{
  "message": "Short URL o7iFyQz successfully deleted."
}
```
