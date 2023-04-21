package com.eece430L.inflaterates.api

import com.eece430L.inflaterates.api.models.ExchangeRatesModel
import com.eece430L.inflaterates.api.models.TokenModel
import com.eece430L.inflaterates.api.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object InflateRatesService {

    private const val API_URL: String = "https://exchangeapp.azurewebsites.net/"

    fun inflateRatesApi(): InflateRates {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(InflateRates::class.java);
    }

    interface InflateRates {

        @POST("/user")
        fun signup(@Body user: UserModel) : Call<UserModel>

        @POST("/authentication")
        fun login(@Body user: UserModel) : Call<TokenModel>

        @GET("/exchangeRate")
        fun getExchangeRates(): Call<ExchangeRatesModel>

    }
}
