package com.khattab.payoff.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Market(
    var id: String = "",
    var name: String = "",
    var number: String = "",
    var city: String = "",
    var district: String = "",
    var street: String = "",
    var description: String = "",
    var geoPoint: GeoPoint? = null,
    @ServerTimestamp
    var timestamp: Date? = null,
    var downloadApp: Boolean
)