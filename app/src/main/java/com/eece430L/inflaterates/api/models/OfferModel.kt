package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class OfferModel {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("offerer")
    var offerer: String? = null

    @SerializedName("receiver")
    var receiver: String? = null

    @SerializedName("offered_amount")
    var offeredAmount: Float? = null

    @SerializedName("requested_amount")
    var requestedAmount: Float? = null

    @SerializedName("usd_to_lbp")
    var usdToLbp: Boolean? = null
}