package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class TransactionsNumberModel {

    @SerializedName("num_usd_to_lbp_transactions")
    var usdToLbpTransactionsNumber: Int? = null

    @SerializedName("num_lbp_to_usd_transactions")
    var lbpToUsdTransactionsNumber: Int? = null
}