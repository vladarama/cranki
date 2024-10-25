#!/usr/bin/env bash

docker run --name ecse-428-project-db -e MYSQL_ROOT_PASSWORD=lala -e MYSQL_DATABASE=dev -p 3306:3306 -d mysql:latest
