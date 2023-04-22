package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class CreateOfferRequestModel {

    @SerializedName("receiver")
    var receiver: String? = null

    @SerializedName("amount_offered")
    var offeredAmount: Float? = null

    @SerializedName("amount_requested")
    var requestedAmount: Float? = null

    @SerializedName("usd_to_lbp")
    var usdToLbp: Boolean? = null

    constructor(
        receiver: String?,
        offeredAmount: Float?,
        requestedAmount: Float?,
        usdToLbp: Boolean?
    ) {
        this.receiver = receiver
        this.offeredAmount = offeredAmount
        this.requestedAmount = requestedAmount
        this.usdToLbp = usdToLbp
    }
}