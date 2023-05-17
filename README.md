# Short

A simple URL shortener.

## Prerequisites

1. sbt
2. npm

## Running the App

This application has two subprojects, one has the backend built using
AkkaHttp, the other using Http4s. Note: Http4s is currently in active
development and does not have documentation for running it.

### Running the AkkaHttp Application

The backend and frontend can be run using two separate commands:

1. `cd short-akka-http/web` and `npm i` to install the http-server 
dependency.
2. `sbt shortAkkaHttp/run` to start the backend.
3. Open a new terminal window and `npm --prefix=web start`
to start the frontend.

The backend is served on port 8081 while the frontend is served
on port 8080. To get to the webpage, navigate to http://localhost:8080.

## API Guide

Create new short url mapping:
```json
POST /urls
BODY: { "url": "http://google.com" }
RESP:
201 CREATED
{
  "message": "(o7iFyQz) -> (http://google.com) successfully created",
  "url": {
    "originalUrl": "http://google.com",
    "shortUrl": "o7iFyQz"
  }
}
```

Get all url mappings:
```json
GET /urls
RESP:
200 OK
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
```json
GET /urls/o7iFyQz
RESP:
200 OK
{
  "message": "(o7iFyQz) -> (http://google.com) successfully retrieved",
  "url": {
    "originalUrl": "http://google.com",
    "shortUrl": "o7iFyQz"
  }
}
```

Delete a single url mapping:
```json
DELETE /urls/o7iFyQz
RESP:
200 OK
{
  "message": "Short URL o7iFyQz successfully deleted."
}
```
