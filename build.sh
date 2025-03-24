#!/bin/bash

mvn clean package
curl --output postgres.jar https://jdbc.postgresql.org/download/postgresql-42.7.5.jar
docker build -t fishmaps-payara .
docker-compose up