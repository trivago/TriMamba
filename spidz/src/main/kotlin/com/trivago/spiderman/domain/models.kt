package com.trivago.spiderman.domain

import com.google.gson.annotations.SerializedName

class DomainEvent {
    var name = ""

    lateinit var datetime: DomainDateTime

    @SerializedName("location")
    lateinit var location: DomainLocation
}

class DomainDateTime {
    var start: String? = null
    var end: String? = null
}

class DomainLocation {
    @SerializedName("adress")
    lateinit var address: DomainAddress

    var city = ""
    var country = ""
    var district = ""
    var state = ""

    @SerializedName("geo")
    lateinit var geo: DomainGeo
}

class DomainAddress {
    var street = ""
    var number = 0
}

class DomainGeo {
    var lat = 0.0
    var lon = 0.0
}
