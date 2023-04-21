package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class TransactionModel {

    @SerializedName("usd_amount")
    var usdAmount: Float? = null

    @SerializedName("lbp_amount")
    var lbpAmount: Float? = null

    @SerializedName("usd_to_lbp")
    var usdToLbp: Boolean? = null

    constructor(usdAmount: Float?, lbpAmount: Float?, usdToLbp: Boolean?) {
        this.usdAmount = usdAmount
        this.lbpAmount = lbpAmount
        this.usdToLbp = usdToLbp
    }
}