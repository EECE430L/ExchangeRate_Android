package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class MyTransactionModel {

    @SerializedName("second_party")
    var secondParty: String? = null

    @SerializedName("usd_amount")
    var usdAmount: Float? = null

    @SerializedName("lbp_amount")
    var lbpAmount: Float? = null

    @SerializedName("usd_to_lbp")
    var usdToLbp: Boolean? = null

    @SerializedName("added_date")
    var addedDate: String? = null
}