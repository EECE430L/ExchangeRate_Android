package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class RatesPercentChangesModel {

    @SerializedName("percent_change_USD_to_LBP")
    var usdToLbpPercentChange: Float? = null

    @SerializedName("percent_change_LBP_to_USD")
    var lbpToUsdPercentChange: Float? = null
}