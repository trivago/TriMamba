package com.trivago.spiderman.event

import com.google.gson.Gson
import com.trivago.spiderman.domain.*
import com.trivago.spiderman.parsers.event.football.BundesligaParser
import kong.unirest.Unirest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1")
class EventController {

    @GetMapping("/events")
    fun getEvents(): List<Event> {
        return BundesligaParser().parseEvents()
    }

    @GetMapping("/todb")
    fun addEvent(): String {

        var events = BundesligaParser().parseEvents()
        for (i in 0 until events.size) {
            val event = events[i]
            val response = Unirest.put("http://elastic.fabian-fritzsche.de/event/_doc/$i")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Postman-Token", "22e4bf63-ae08-4d76-823b-e37d285a108d")
                    .body(Gson().toJson(convertToDomainEvent(event)))
                    .asString()
            println(response.body)
        }

        return "Done"
    }

    fun convertToDomainEvent(event: Event): DomainEvent {

        var domainDateTime = DomainDateTime()
        domainDateTime.start = event.start.toString().replace("[UTC]", "")
        domainDateTime.end = event.end.toString().replace("[UTC]", "")

        var domainAddress = DomainAddress()
        domainAddress.street = event.location.address
        domainAddress.number = 0

        var domainGeo = DomainGeo()
        domainGeo.lat = 51.2140934
        domainGeo.lon = 6.7406008

        var domainLocation = DomainLocation()
        domainLocation.address = domainAddress
        domainLocation.geo = domainGeo
        domainLocation.country = "DE"

        var domainEvent = DomainEvent()
        domainEvent.name = event.name
        domainEvent.dateTime = domainDateTime
        domainEvent.location = domainLocation


        return domainEvent
    }

}