package com.eece430L.inflaterates.exchange_service

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.OfferModel
import com.eece430L.inflaterates.api.models.ProcessOfferRequestModel
import com.eece430L.inflaterates.utilities.Authentication
import com.eece430L.inflaterates.utilities.ContentDescriptionUtils
import com.eece430L.inflaterates.utilities.ProgressBarManager
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// https://www.tutorialspoint.com/android/android_list_view.htm
// an adapter bridges between UI components and the data source that fill data into UI Components
// OffersAdapter bridges between the offers list view and the data obtained from the backend
class OffersAdapter(private val activity: Activity,
                    private val inflater: LayoutInflater,
                    private val progressBarManager: ProgressBarManager,
                    private val dataSource: List<OfferModel>,
                    private val isViewingReceivedOffers: Boolean) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = inflater.inflate(R.layout.offer_list_item, parent, false)

        if(isViewingReceivedOffers) {
            view.findViewById<TextView>(R.id.sender_or_receiver_TextView).text = dataSource[position].offerer
            view.findViewById<TextView>(R.id.sender_label_TextView).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.receiver_label_TextView).visibility = View.GONE

        }
        else {
            view.findViewById<TextView>(R.id.sender_or_receiver_TextView).text = dataSource[position].receiver
            view.findViewById<TextView>(R.id.sender_label_TextView).visibility = View.GONE
            view.findViewById<TextView>(R.id.receiver_label_TextView).visibility = View.VISIBLE
        }

        val offerItemContainer: LinearLayout? = view.findViewById(R.id.offer_item_container)
        ContentDescriptionUtils.setOfferItemContentDescription(offerItemContainer,
            offerItem=dataSource[position], isViewingReceivedOffers
        )

        view.findViewById<TextView>(R.id.offered_amount_TextView).text =
            dataSource[position].offeredAmount.toString()

        view.findViewById<TextView>(R.id.requested_amount_TextView).text =
            dataSource[position].requestedAmount.toString()

        view.findViewById<TextView>(R.id.type_TextView).text = dataSource[position].let {
            if(it.usdToLbp == null) "N/A" else if (it.usdToLbp!!) "USD to LBP" else "LBP to USD"
        }

        val acceptRejectContainer: LinearLayout =
            view.findViewById(R.id.accept_reject_container_LinearLayout)

        acceptRejectContainer.visibility = if (isViewingReceivedOffers) View.VISIBLE else View.GONE

        val acceptButton: Button = view.findViewById(R.id.accept_offer_Button)
        acceptButton.setOnClickListener {
            processOffer(ProcessOfferRequestModel(offerId = dataSource[position].id, accepted = true),
            view)
        }

        val rejectButton: Button = view.findViewById(R.id.reject_offer_Button)
        rejectButton.setOnClickListener {
            processOffer(ProcessOfferRequestModel(offerId = dataSource[position].id, accepted = false),
            view)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    private fun processOffer(processOfferRequest: ProcessOfferRequestModel, view: View) {
        progressBarManager.showProgressBar(activity)
        InflateRatesService.inflateRatesApi().processOffer(processOfferRequest,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null)
            .enqueue(object: Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        if(processOfferRequest.accepted!!) {
                            (activity as MainActivity).switchToMyTransactionsFragment()
                        }
                        else {
                            (activity as MainActivity).switchToOffersIReceivedFragment()
                        }
                    }
                    else {
                        Snackbar.make(
                            view,
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
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