# Mamba

Mamba is a collection of tools aimed to gather event information from different sources into a database.

This project is part of the [Trivago Tech Camp 2019](https://techcamp.trivago.com/).

## Contents

- [Mamba](#mamba)
  - [Contents](#contents)
  - [Installation](#installation)
    - [Node Crawlers](#node-crawlers)

## Installation

### Node Crawlers

- Install using `npm install`
- eventbride-mamba
  - local
    - set environment variables for
      - elastic db url: `ELASTIC_URI`
      - eventbrite secret: `TOKEN_EVENTBRITE`
    - run using `npm start`
  - docker
    - build docker file
    - run with `--env-file` (important for string formatting)
