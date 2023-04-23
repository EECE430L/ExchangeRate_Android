package com.eece430L.inflaterates.utilities

import android.app.Activity
import android.view.View
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.OffersAdapter
import com.eece430L.inflaterates.api.models.OfferModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OffersUtil {

     fun getOffersAndUpdateView(activity: Activity,
                                view: View,
                                apiCall: Call<List<OfferModel>>,
                                placeHolderView: View,
                                progressBarManager: ProgressBarManager,
                                offers: ArrayList<OfferModel>,
                                adapter: OffersAdapter) {

        apiCall.enqueue(object : Callback<List<OfferModel>> {

            override fun onResponse(call: Call<List<OfferModel>>, response: Response<List<OfferModel>>) {

                var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                progressBarManager.hideProgressBar()

                if (response.isSuccessful) {

                    if(response.body()?.isNotEmpty() == true) {
                        message = "Fetched ${response.body()?.size} offers!"
                    }
                    offers.addAll(response.body()!!)
                    if(offers.isEmpty()) {
                        placeHolderView.visibility = View.VISIBLE
                    }
                    else {
                        placeHolderView.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
                else if (response.code() == 401 || response.code() == 403) {
                    (activity as MainActivity).logout()
                }
                else if(message == "") { message = response.code().toString() }

                if(message != "") {
                    Snackbar.make(
                        view,
                        message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<List<OfferModel>>, t: Throwable) {
                progressBarManager.hideProgressBar()
                Snackbar.make(
                    view,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }
}