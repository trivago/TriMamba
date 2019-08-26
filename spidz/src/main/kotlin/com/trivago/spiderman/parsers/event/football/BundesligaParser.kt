package com.trivago.spiderman.parsers.event.football

import com.trivago.spiderman.event.Event
import com.trivago.spiderman.event.EventType
import com.trivago.spiderman.location.Location
import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class BundesligaParser {

    fun fetchEvents(leagueNumber: Number): Observable<Event> {
        return Observable.create { emitter ->
            for (i in 3 until 35) {
                var prefix = if (leagueNumber == 2) "2" else ""
                var doc = Jsoup.connect("https://www.bundesliga.com/de/${prefix}bundesliga/spieltag/2019-2020/$i").get()
                var matchInfos = doc.select(".matchInfos.ng-star-inserted") as List<Element>
                matchInfos.forEach {
                    var event = parseEvent(it)
                    emitter.onNext(event)
                }
            }
        }
    }

    var date = ""
    private fun parseEvent(matchInfo: Element): Event {
        var kickoffDate = matchInfo.select(".row.kickoffDatetime.ng-star-inserted").text()
        if (kickoffDate != "") {
            date = kickoffDate
        }

        val teamOne = matchInfo.select(".clubName")[0].text()
        val teamTwo = matchInfo.select(".clubName")[1].text()
        val eventName = "$teamOne vs $teamTwo"

        var stadium = matchInfo.select(".stadium-name").text()

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
        return event
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
