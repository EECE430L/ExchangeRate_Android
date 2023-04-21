package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class ExchangeRatesModel {

    @SerializedName("usd_to_lbp")
    var usdToLbp: Float? = null

    @SerializedName("lbp_to_usd")
    var lbpToUsd: Float? = null
}