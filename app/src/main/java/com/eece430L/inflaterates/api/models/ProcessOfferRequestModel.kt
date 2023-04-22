package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class ProcessOfferRequestModel {

    @SerializedName("offer_id")
    var offerId: Int? = null

    @SerializedName("accepted")
    var accepted: Boolean? = null

    constructor(offerId: Int?, accepted: Boolean?) {
        this.offerId = offerId
        this.accepted = accepted
    }
}