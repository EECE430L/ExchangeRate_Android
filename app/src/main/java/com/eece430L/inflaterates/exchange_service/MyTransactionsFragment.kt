package com.eece430L.inflaterates.exchange_service

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.MyTransactionModel
import com.eece430L.inflaterates.utilities.Authentication
import com.eece430L.inflaterates.utilities.HttpStatusCodesUtil
import com.eece430L.inflaterates.utilities.ProgressBarManager
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTransactionsFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var myTransactionsPlaceHolderContainer: LinearLayout? = null
    private var linkToRecordExchangeTextView: TextView? = null
    private var linkToCreateOfferTextView: TextView? = null
    private var exportMyTransactionsButton: Button? = null
    private var listview : ListView? = null
    private var myTransactions : ArrayList<MyTransactionModel>? = ArrayList()
    private var adapter : MyTransactionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_my_transactions, container, false)

        // obtain a reference to different ui elements
        // configure some elements by setting their on click listeners

        myTransactionsPlaceHolderContainer = view.findViewById(R.id.transactions_placeholder_LinearLayout)
        myTransactionsPlaceHolderContainer?.visibility = View.GONE

        linkToRecordExchangeTextView = view.findViewById(R.id.link_to_record_an_exchange_TextView)
        linkToRecordExchangeTextView?.setOnClickListener { _ ->
            (activity as MainActivity).switchToRecordAnExchangeFragment()
        }
        // underline "Record an Exchange!"
        val linkToRecordExchange = SpannableString("Record an Exchange!")
        linkToRecordExchange.setSpan(UnderlineSpan(), 0, linkToRecordExchange.length, 0)
        linkToRecordExchangeTextView?.text = linkToRecordExchange

        linkToCreateOfferTextView = view.findViewById(R.id.link_to_create_an_offer_TextView)
        linkToCreateOfferTextView?.setOnClickListener { _ ->
            (activity as MainActivity).switchToOfferATransactionFragment()
        }
        // underline "Create an Offer!"
        val linkToCreateOffer = SpannableString("Create an Offer!")
        linkToCreateOffer.setSpan(UnderlineSpan(), 0, linkToCreateOffer.length, 0)
        linkToCreateOfferTextView?.text = linkToCreateOffer

        exportMyTransactionsButton = view.findViewById(R.id.export_my_transactions_Button)
        exportMyTransactionsButton?.visibility = View.GONE
        exportMyTransactionsButton?.setOnClickListener { _ -> exportMyTransactions() }

        listview = view.findViewById(R.id.my_transactions_ListView)
        adapter = MyTransactionsAdapter(layoutInflater, myTransactions!!)
        listview?.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillMyTransactionsList()
    }

    private fun fillMyTransactionsList() {

        if(Authentication.getToken() == null) {
            (requireActivity() as MainActivity).logout()
        }

        progressBarManager.showProgressBar(requireActivity())
//        Inspired by 430L mobile labs
        InflateRatesService.inflateRatesApi().getMyTransactions(
            "Bearer ${Authentication.getToken()}")
            .enqueue(object: Callback<List<MyTransactionModel>> {

                override fun onResponse(call: Call<List<MyTransactionModel>>,
                                        response: Response<List<MyTransactionModel>>) {

                    progressBarManager.hideProgressBar()
                    var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())

                    if(response.isSuccessful) {
                        message = "Fetched ${response.body()?.size} transactions!"
                        myTransactions?.addAll(response.body()!!)
                        if(myTransactions?.isEmpty() == true) {
                            myTransactionsPlaceHolderContainer?.visibility = View.VISIBLE
                            listview?.visibility = View.GONE
                            exportMyTransactionsButton?.visibility = View.GONE
                        }
                        else {
                            myTransactionsPlaceHolderContainer?.visibility = View.GONE
                            listview?.visibility = View.VISIBLE
                            exportMyTransactionsButton?.visibility = View.VISIBLE
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    else if (response.code() == 401 || response.code() == 403) {
                        (activity as MainActivity).logout()
                    }
                    else if(message == "") { message = response.code().toString() }
                    Snackbar.make(
                        requireView(),
                        message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                override fun onFailure(call: Call<List<MyTransactionModel>>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        requireView(),
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun exportMyTransactions() {

        if(Authentication.getToken() == null) {
            (requireActivity() as MainActivity).logout()
        }

        progressBarManager.showProgressBar(requireActivity())
        InflateRatesService.inflateRatesApi().exportMyTransactions(
            "Bearer ${Authentication.getToken()}")
            .enqueue(object: Callback<Any> {

                override fun onResponse(call: Call<Any>, response: Response<Any>) {

                    progressBarManager.hideProgressBar()
                    var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())

                    if(response.isSuccessful) {
                        message = "Please check your email inbox shortly for an Excel sheet containing your transaction details."
                    }
                    else if (response.code() == 401 || response.code() == 403) {
                        (activity as MainActivity).logout()
                    }
                    else if(message == "") { message = response.code().toString() }
                    Snackbar.make(
                        requireView(),
                        message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        requireView(),
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }
}