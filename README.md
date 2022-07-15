# Short

A simple URL shortener.

## Prerequisites

1. scala
2. sbt
3. npm

## Running the App

The backend and frontend can be run using two separate commands:

1. sbt run
2. npm --prefix=web start

`sbt run` starts the backend service and
`npm --prefix=web start` serves the frontend.

The backend is served on port 8081 while the frontend is served
on port 8080. To get to the webpage, navigate to http://localhost:8080.

## Assumptions

* It's assumed with this being an example exercise, that scalability isn't too big of a concern.
More details on how scalability could be improved in the [Potential Improvements](#potential-improvements) section.
* A database wasn't necessarily needed.
* No authentication is needed for the API.
* It seems like, on redirect, the short URL to original URL is saved as a cookie,
so unless you clear your cookies, it isn't possible to change a short URLs destination address.
* Internet Explorer will not be used.

## Approach

* Decided on using akka-http as the API framework as I've used it in the past.
* Utilized the akka-http-quickstart-scala template to make the API work start faster.
* Since a database was not used, I was going to use some type of mutable data structure,
but akka behaviors worked very nicely as a substitute.
* The "registry" acts like a database and holds a set of url to short url mappings.
* I utilized scala's `Random` utility to generate a unique 7 character alphanumeric string for the short url.

## Issues Faced

* Getting the path matching for any route (to handle when a user wants to access their short url) was a challenge.
* Input validation was a little difficult. I originally began doing the validation using an input
field pattern but quickly realized that was not a great idea as someone could just change the html.
I ended up doing the url validation all in the backend.
  * This led to a bunch of work going into error handling for the API.
* I forgot about CORS being a problem, but found a scala library `akka-http-cors` that made it easy to handle.
* Refactoring in general.

## Potential Improvements

1. Use an actual database for storing the short url to original url mappings.
2. Some route unit tests rely on the POST functionality to work. This is not good
practice and should be refactored. I've put a comment in the test file for more details.
3. The routes file is a little unwieldy and could use some refactoring.
4. I wanted to dockerize the app but ran out of time. Also realized that docker for
desktop now costs money if used in an enterprise setting.
5. Running the app via a script would be nice, however I kept running into issues with
killing the sbt process (I believe because of the forking).
6. The random alphanumeric generator, in scale, could potentially run into conflicts.
That could be resolved using some type of uniquely generated number as the seed for every request.
