# CQRS-DEMO

This repo contains the project used for demonstrating CQRS/ES/DDD using Scala and Akka in practise.

Presentation that uses this project -> <http://slides.com/michaltomanski/cqrs>

# Timer

There is a new wave of speedcubers - people who solve the Rubik's Cube as fast as possible. I decided to implement the most basic version of a timer that they might use to compete online. 

# Entity

`Speedcuber` is the domain entity in this system. It represents a single user of the application and holds the state, which includes a list of his times and and hist best average of 5.

# Usage

#### Requirements

* Sbt is required in order to run the app.

* Cassandra needs to be up and running. It should be available on port 9092

* PostgreSQL needs to be up and running. It should be available on port 5432. The username and the password can be configured in `application.conf`. Database name is `timer`. In order to create schema, execute `1.sql` located in `/evolutions/`.

#### Launching the application

Run `sbt run` to launch the application. Then use your favourite browser and go to `localhost:9000`. The timer is started and stopped with a spacebar.

Go to `localhost:9000/avg/best` to see best averages.

#### Running tests

Tests are under construction...

# About

Author: Michal Tomanski ([@michaltomanski](http://twitter.com/michaltomanski))

This project was used during various conference talks: ScalaUA (Kiev 2017), Kielce Java User Group (Kielce 2017).