# TriMamba

TriMamba is a collection of tools aimed to gather event information from different sources into a database.

This project was part of the [Trivago Tech Camp 2019](https://techcamp.trivago.com/).

## Contents

- [TriMamba](#trimamba)
  - [Contents](#contents)
  - [What is TriMamba](#what-is-trimamba)
  - [Participants](#participants)
  - [Usage](#usage)
    - [Requirenments](#requirenments)
    - [eventbrite-mamba](#eventbrite-mamba)

## What is TriMamba

TriMamba was a short project in the scope of the 2019th [TechCamp](https://techcamp.trivago.com/) by [Trivago](https://company.trivago.com/). It was a two weeks project where students tried to build a small product prototype that could be used in an global application.

## Participants

- [Dzheko Akperov](https://github.com/dzh17)
  - _bundesliga-mamba_ crawler written in Kotlin  
    Crawls event data from the official [Bundesliga website](https://www.bundesliga.com/de/bundesliga/spieltag)
  - Chrome extension for [trivago.com](https://trivago.com) written in JavaSript
- [Fabian Fritzsche](https://github.com/salzian)
  - _eventbrite-mamba_ written in NodeJS  
    Fetches data from the official [Eventbrite API](https://www.eventbrite.com/platform/api)
  - Backend setup (Docker, Elastic stack, Nginx)

## Usage

### Requirenments

TriMamba uses the [Elastick Stack](https://www.elastic.co/products/) to store and visualize data. Both [elasticsearch](https://www.elastic.co/products/elasticsearch) and [Kibana](https://www.elastic.co/products/kibana) need to be setup in advance. For our purposes we dockerized both and ran them linked besides the crawlers.

Before pushing data to the database, make sure to [create an index](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html) on elasticsearch first using the mapping in `common/mappings.json`. Note the chosen index name for later usage.

### eventbrite-mamba

- Install using `npm install`
  - Create a .env file in the node application's root directory and set the environment variables for
    - `ELASTIC_URI`: elasticsearch database url + index name (`https://example.com/events`)
    - `TOKEN_EVENTBRITE`: [Eventbrite API secret](https://www.eventbrite.com/platform)
  - local
    - run using `npm start`
  - docker
    - Build the `Dockerfile` with `docker build --tag eventbrite-mamba .`
    - Run the image with `docker run --env-file .env eventbrite-mamba`
