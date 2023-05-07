package com.eece430L.inflaterates.api

import com.eece430L.inflaterates.api.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// inspired by EECE430L mobile labs
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

        @GET("/statistics/number-transactions")
        fun getTransactionNumbers(@Query("startYear") startYear: Int,
                                  @Query("startMonth") startMonth: Int,
                                  @Query("startDay") startDay: Int,
                                  @Query("endYear") endYear: Int,
                                  @Query("endMonth") endMonth: Int,
                                  @Query("endDay") endDay: Int): Call<TransactionsNumberModel>

        @GET("/statistics/rates-percent-change")
        fun getPercentChanges(@Query("startYear") startYear: Int,
                              @Query("startMonth") startMonth: Int,
                              @Query("startDay") startDay: Int,
                              @Query("endYear") endYear: Int,
                              @Query("endMonth") endMonth: Int,
                              @Query("endDay") endDay: Int): Call<RatesPercentChangesModel>

        @GET("/fluctuations")
        fun getFluctuations(@Query("startYear") startYear: Int,
                                    @Query("startMonth") startMonth: Int,
                                    @Query("startDay") startDay: Int,
                                    @Query("endYear") endYear: Int,
                                    @Query("endMonth") endMonth: Int,
                                    @Query("endDay") endDay: Int
        ): Call<List<FluctuationModel>>

        @GET("/transaction")
        fun getMyTransactions(@Header("Authorization") authorization: String?) : Call<List<MyTransactionModel>>

        @GET("/transaction/excel-transactions")
        fun exportMyTransactions(@Header("Authorization") authorization: String?) : Call<Any>

        @POST("/transaction")
        fun addTransaction(@Body transaction: TransactionModel,
                           @Header("Authorization") authorization: String?) : Call<Any>

        @GET("/offer/sent")
        fun getOffersISent(@Header("Authorization") authorization: String?): Call<List<OfferModel>>

        @GET("/offer/received")
        fun getOffersIReceived(@Header("Authorization") authorization: String?): Call<List<OfferModel>>

        @POST("/offer")
        fun createOffer(@Body createOfferRequestModel: CreateOfferRequestModel,
                         @Header("Authorization") authorization: String?) : Call<Any>

        @POST("/offer/process-offer")
        fun processOffer(@Body processOfferRequest: ProcessOfferRequestModel,
                           @Header("Authorization") authorization: String?) : Call<Any>
    }
}
