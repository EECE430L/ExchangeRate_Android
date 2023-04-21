package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class UsdToLbpFluctuationModel {

    @SerializedName("StartDate")
    var startDate: String? = null

    @SerializedName("EndDate")
    var endDate: String? = null

    @SerializedName("usdToLbpRate")
    var usdToLbpRate: String? = null
}