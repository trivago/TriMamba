package com.trivago.spiderman.parsers.event.football

import com.trivago.spiderman.event.Event
import com.trivago.spiderman.event.EventType
import com.trivago.spiderman.location.Location
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME


class BundesligaParser {

    fun parseEvents(): List<Event> {
        var allEvents = mutableListOf<Event>()

        for (i in 3 until 34) {
            var doc = Jsoup.connect("https://www.bundesliga.com/de/bundesliga/spieltag/2019-2020/$i").get()
            var date = ""
            var matchInfos = doc.select(".matchInfos.ng-star-inserted") as List<Element>

            var events = matchInfos.map {
                var kickoffDate = it.select(".row.kickoffDatetime.ng-star-inserted").text()
                if (kickoffDate != "") {
                    date = kickoffDate
                }

                val teamOne = it.select(".clubName")[0].text()
                val teamTwo = it.select(".clubName")[1].text()
                val eventName = "$teamOne vs $teamTwo"

                var stadium = it.select(".stadium-name").text()

                var event = Event()
                event.name = eventName

                if (!date.contains("-")) {
                    event.start = fetchStartAndEndDate(date)[0]
                    event.end = fetchStartAndEndDate(date)[1]
                } else {
                    event.start = fetchStartAndEndDate(date.split("-")[0])[0]
                    event.end = fetchStartAndEndDate(date.split("-")[1])[0]
                }


                var location = Location()
                location.country = "Germany"
                location.address = stadium

                event.location = location
                event.type = EventType.SPORT
                println("Parsed Week $i : ${event.name}")
                event
            }

            allEvents.addAll(events)
        }


        return allEvents
    }

    private fun fetchStartAndEndDate(date: String): List<ZonedDateTime> {
        var dateFormat = if (date.contains("|")) "dd.MM.yyyy | HH:mm" else "dd.MM.yyyy"
        var formattedDate = SimpleDateFormat(dateFormat).parse(date.replace(" Uhr", "").split(", ")[1])
        var cestDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .format(formattedDate)
        val utcDate = LocalDateTime.parse(cestDate, ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("CET"))
                .withZoneSameInstant(ZoneId.of("UTC"))

        val startDate = utcDate.minusHours(1L)
        val endDate = utcDate.plusHours(2L)

        return arrayListOf(startDate, endDate)
    }


}
