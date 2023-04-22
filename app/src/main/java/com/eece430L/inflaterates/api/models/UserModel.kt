package com.eece430L.inflaterates.api.models

import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("email")
    var email: String? = null

    @SerializedName("user_name")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    constructor(email: String?, username: String?, password: String?) {
        this.email = email
        this.username = username
        this.password = password
    }
}