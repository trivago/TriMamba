package com.trivago.spiderman.event

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trivago.spiderman.domain.*
import com.trivago.spiderman.parsers.event.football.BundesligaParser
import kong.unirest.Unirest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder


@RestController
@RequestMapping("/api/v1")
class EventController {

    @GetMapping("/events")
    fun getEvents(): List<DomainEvent> {
        var domainEvents = mutableListOf<DomainEvent>()
        var addresses = HashMap<String, JsonObject>()
        BundesligaParser().fetchEvents(2)
                .mergeWith(BundesligaParser().fetchEvents(1))
                .map { s ->
                    run {
                        var address = s.location.address
                        println(addresses.containsKey(address))
                        var result = if (addresses.containsKey(address)) addresses[address] else getAddressData(address)
                        result = result as JsonObject
                        addresses[address] = result
                        var event = updateEvent(s, result)
                        event
                    }
                }
                .subscribe {
                    run {
                        domainEvents.add(convertToDomainEvent(it))
                        postToElasticSearch(convertToDomainEvent(it))
                    }
                }

        return domainEvents
    }

    private fun getAddressData(address: String): JsonObject {
        var url = "https://api.tomtom.com/search/2/search/${URLEncoder.encode(address, "UTF-8")}.json?limit=1&countrySet=DE&idxSet=POI&key=c6U1YkgaBfTTBZSS16q5DmH5HfxUVPgy";
        var response = Unirest.get(url)
                .header("Content-Type", "application/json")
                .header("cache-control", "no-cache")
                .header("Postman-Token", "3ed1c64a-8098-413e-a5a4-5691f1ea650f")
                .asString()
        Thread.sleep(1000)
        var json = Gson().fromJson(response.body, JsonObject::class.java)
        println("Called json $json")
        return json.getAsJsonArray("results").get(0).asJsonObject
    }

    private fun updateEvent(event: Event, result: JsonObject): Event {
        var addressData = result.get("address").asJsonObject
        var tempEvent = event
        tempEvent.location.city = addressData.get("municipality").asString
        tempEvent.location.country = addressData.get("country").asString
        tempEvent.location.coordinates = doubleArrayOf(result.get("position").asJsonObject.get("lat").asDouble,
                result.get("position").asJsonObject.get("lon").asDouble)
        tempEvent.location.address = addressData.get("freeformAddress").asString
        return tempEvent
    }

    private fun postToElasticSearch(domainEvent: DomainEvent) {
        val response = Unirest.post("http://elastic.fabian-fritzsche.de/event-bundesliga/_doc/")
                .header("Content-Type", "application/json")
                .header("cache-control", "no-cache")
                .header("Postman-Token", "22e4bf63-ae08-4d76-823b-e37d285a108d")
                .body(Gson().toJson(domainEvent))
                .asString()
        println("Added event ${domainEvent.name}")
        println(response.body)
    }

    private fun convertToDomainEvent(event: Event): DomainEvent {

        var domainDateTime = DomainDateTime()
        domainDateTime.start = event.start.toString().replace("[UTC]", "")
        domainDateTime.end = event.end.toString().replace("[UTC]", "")

        var domainAddress = DomainAddress()
        domainAddress.street = event.location.address
        domainAddress.number = 0

        var domainGeo = DomainGeo()
        domainGeo.lat = event.location.coordinates[0]
        domainGeo.lon = event.location.coordinates[1]

        var domainLocation = DomainLocation()
        domainLocation.city = event.location.city
        domainLocation.address = domainAddress
        domainLocation.geo = domainGeo
        domainLocation.country = event.location.country

        var domainEvent = DomainEvent()
        domainEvent.name = event.name
        domainEvent.datetime = domainDateTime
        domainEvent.location = domainLocation


        return domainEvent
    }

}