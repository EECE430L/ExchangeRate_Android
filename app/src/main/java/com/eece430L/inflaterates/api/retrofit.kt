package com.eece430L.inflaterates.api

import com.eece430L.inflaterates.api.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

        @GET("/statistics/todays-transactions")
        fun getTransactionNumbers(): Call<TransactionsNumberModel>

        @GET("/statistics/rates-percent-change")
        fun getPercentChanges(): Call<RatesPercentChangesModel>

        @GET("/fluctuations/usd-to-lbp")
        fun getUsdToLbpFluctuations(@Query("startYear") startYear: Int,
                                    @Query("startMonth") startMonth: Int,
                                    @Query("startDay") startDay: Int,
                                    @Query("endYear") endYear: Int,
                                    @Query("endMonth") endMonth: Int,
                                    @Query("endDay") endDay: Int
        ): Call<List<UsdToLbpFluctuationModel>>

        @GET("/fluctuations/lbp-to-usd")
        fun getLbpToUsdFluctuations(@Query("startYear") startYear: Int,
                                    @Query("startMonth") startMonth: Int,
                                    @Query("startDay") startDay: Int,
                                    @Query("endYear") endYear: Int,
                                    @Query("endMonth") endMonth: Int,
                                    @Query("endDay") endDay: Int
        ): Call<List<LbpToUsdFluctuationModel>>
    }
}
