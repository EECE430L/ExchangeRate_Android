package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("user_name")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }
}