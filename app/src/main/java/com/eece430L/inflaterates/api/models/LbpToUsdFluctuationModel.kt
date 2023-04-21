package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class LbpToUsdFluctuationModel {

    @SerializedName("StartDate")
    var startDate: String? = null

    @SerializedName("EndDate")
    var endDate: String? = null

    @SerializedName("lbpToUsdRate")
    var lbpToUsdRate: String? = null
}