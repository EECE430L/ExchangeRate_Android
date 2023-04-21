package com.eece430L.inflaterates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.ExchangeRatesModel
import com.eece430L.inflaterates.utilities.ProgressBarManager
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var buyUsdTextView: TextView? = null
    private var sellUsdTextView: TextView? = null
    private var lastRefreshedDateTimeTextView: TextView? = null
    private var refreshButton: Button? = null

    private var moneyAmountEditText : EditText? = null
    private var conversionTypeRadioGroup : RadioGroup? = null
    private var convertButton : Button? = null
    private var converterResultTextView : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchRates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View =  inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        buyUsdTextView = view.findViewById(R.id.lbp_to_usd_textview)
        sellUsdTextView = view.findViewById(R.id.usd_to_lbp_textview)
        lastRefreshedDateTimeTextView = view.findViewById(R.id.lastRefreshedDateTimeTextView)

        refreshButton = view.findViewById(R.id.refresh_button)
        refreshButton?.setOnClickListener { _ -> fetchRates() }

        moneyAmountEditText = view.findViewById(R.id.moneyAmount)
        conversionTypeRadioGroup = view.findViewById(R.id.conversionType)
        convertButton = view.findViewById(R.id.convertButton)
        converterResultTextView = view.findViewById(R.id.converterResult)

        convertButton?.setOnClickListener { view -> convert() }

        return view
    }

    private fun fetchRates() {

        progressBarManager.showProgressBar(requireActivity())

        InflateRatesService.inflateRatesApi().getExchangeRates().enqueue(object: Callback<ExchangeRatesModel> {
            override fun onResponse(call: Call<ExchangeRatesModel>,
                                    response: Response<ExchangeRatesModel>) {
                progressBarManager.hideProgressBar()
                if(response.isSuccessful) {
                    val lbpToUsd = response.body()?.lbpToUsd
                    val usdToLbp = response.body()?.usdToLbp
                    lbpToUsd?.let {
                        println(it.toDouble())
                        buyUsdTextView?.text = if (it.toDouble() != 0.0) it.toString() else "N/A"
                    }
                    usdToLbp?.let {
                        sellUsdTextView?.text = if (it.toDouble() != 0.0) it.toString() else "N/A"
                    }
                    lastRefreshedDateTimeTextView?.text =
                        SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                }
                else {
                    Snackbar.make(
                        convertButton as View,
                        response.code().toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ExchangeRatesModel>, t: Throwable) {
                progressBarManager.hideProgressBar()
                Snackbar.make(
                    convertButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun convert() {

        val moneyAmount = moneyAmountEditText?.text.toString().toDoubleOrNull() ?: return

        when(conversionTypeRadioGroup?.checkedRadioButtonId){

            R.id.convertUsdToLbp -> {
                val sellUsdRate = sellUsdTextView?.text.toString().toDoubleOrNull()
                if(sellUsdRate != null){
                    val result =  (moneyAmount * sellUsdRate).toString()
                    converterResultTextView?.text = "$moneyAmount USD = $result LBP"
                }
                else {
                    converterResultTextView?.text = "Couldn't convert, sell USD rate is not available!"
                }
            }

            R.id.convertLbpToUsd -> {
                val buyUsdRate = buyUsdTextView?.text.toString().toDoubleOrNull()
                if(buyUsdRate != null){
                    val result =  (moneyAmount * buyUsdRate).toString()
                    converterResultTextView?.text = "$moneyAmount LBP = $result USD"
                }
                else {
                    converterResultTextView?.text = "Couldn't convert, buy USD rate is not available!"
                }
            }
        }
    }
}