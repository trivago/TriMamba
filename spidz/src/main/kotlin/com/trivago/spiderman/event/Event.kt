package com.trivago.spiderman.event

import com.trivago.spiderman.location.Location
import java.time.ZonedDateTime

class Event {
    lateinit var name: String
    var start: ZonedDateTime? = null
    var end: ZonedDateTime? = null
    lateinit var type: EventType
    lateinit var location: Location
}