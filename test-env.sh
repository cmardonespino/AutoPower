#!/usr/bin/env bash

NORMAL=$(tput sgr0)
GREEN=$(tput setaf 2; tput bold)
YELLOW=$(tput setaf 3)
RED=$(tput setaf 1)

function red() {
  echo -e "$RED$*$NORMAL"
}

function green() {
  echo -e "$GREEN$*$NORMAL"
}

function yellow() {
  echo -e "$YELLOW$*$NORMAL"
}

function status() {
  green "TEST ENVIRONMENT STATUS"
  docker-compose ps
}

case "$1" in
  up)
    cd docker
    green "CREATING TEST ENVIRONMENT WITH DOCKER-COMPOSE UP"
    docker-compose up -d
    status
    ;;
  test)
    green "RUNNING TESTS WITH GRADLE"
    ./gradlew clean test jacocoTestReport
    ;;
  down)
    cd docker
    green "DESTROYING TEST ENVIRONMENT WITH DOCKER-COMPOSE DOWN"
    docker-compose down
    status
    ;;
  status)
    cd docker
    status
    ;;
esac
