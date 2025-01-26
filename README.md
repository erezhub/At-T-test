# At-T-test
this project is a simple movie-theater management system.
it allows to:
1.  create user
2.  CRUD movies
3.  CRUD screening
4.  make a booking for a movie.

prerequisits:

Java 21 and above

Maven 3.9.6 and above

DB - H2

steps to run the application:
1.  pull project
2.  run 'mvn clean install'
3.  cd to 'target' foler
4.  run 'java -jar .\atnt-test-0.0.1-SNAPSHOT.jar'

in order to make API calls, use 'Basic Auth'.
credentials of default admin user can be set in the application.propertis file.  
also, in the application.propertis file the number of theaters and their seats can be set.

API documentation:
1.  create a user:
    POST http://localhost:8080/user
    body:
    {
        "name": String,
        "password": String,
        "email": String
        "role": "ROLE_CUSTOMER"/"ROLE_ADMIN"
    }
    response:  user-id

3.  create a movie:
   POST http://localhost:8080/movie
   body:
   {
    "title": String,
    "rating": int,
    "genre": String,
    "duration": int,
    "releaseYear": int
}

4.  update a movie:
   PUT http://localhost:8080/movie/{movie-title}
   same body as above
5. delete a movie:
   DELETE http://localhost:8080/movie/{movie-title}
6. get all movies:
   GET http://localhost:8080/movie
   response:
   [
     {
       "title": String,
      "rating": int,
      "genre": String,
      "duration": int,
      "releaseYear": int
     }
   ]
 7. create a screening:
    POST http://localhost:8080/screening
    body:
    {
      "movieTitle": String,
      "theaterId": int,
      "startTime": "dd/MM/YYYY HH:mm",
      "endTime": "dd/MM/YYYY HH:mm"
  }
  response: screening-id
8.  update a screening  
    PUT http://localhost:8080/screening
    body:
    {
      "id" int,
      "movieTitle": String,
      "theaterId": int,
      "startTime": "dd/MM/YYYY HH:mm",
      "endTime": "dd/MM/YYYY HH:mm"
  }
9. delete a screening:
   DELETE http://localhost:8080/screening/{screening-id}
10.  get screenings by title
    GET  http://localhost:8080/screening/title/{movie-title}
    response:
    [
    {
        "id": int,
        "movieTitle": String,
        "theaterId": int,
        "startTime": "dd/MM/YYYY HH:mm",
        "endTime": "dd/MM/YYYY HH:mm"
    }
  ]
11. get screenings by theater
    GET  http://localhost:8080/screening/theater/{theater-id}
    response:
    [
    {
        "id": int,
        "movieTitle": String,
        "theaterId": int,
        "startTime": "dd/MM/YYYY HH:mm",
        "endTime": "dd/MM/YYYY HH:mm"
    }
  ]
12.  make a booking
     POST http://localhost:8080/booking
     body:
     {
        "userId": int,
        "movieTitle": String,
        "theaterId": int,
        "showTime": "dd/MM/YYYY HH:mm",
        "seatNumber": int,
        "price": double
    }    
