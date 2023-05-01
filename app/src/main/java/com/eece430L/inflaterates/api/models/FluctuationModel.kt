package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class FluctuationModel {

    @SerializedName("Date")
    var date: String? = null

    @SerializedName("lbpToUsdRate")
    var lbpToUsdRate: Float? = null

    @SerializedName("usdToLbpRate")
    var usdToLbpRate: Float? = null
}