package com.eece430L.inflaterates.exchange_rate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.ExchangeRatesModel
import com.eece430L.inflaterates.utilities.HttpStatusCodesUtil
import com.eece430L.inflaterates.utilities.ProgressBarManager
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var usdToLbpExchangeRateContainer: LinearLayout? = null
    private var lbpToUsdExchangeRateContainer: LinearLayout? = null
    private var lastRefreshedDateTimeContainer: LinearLayout? = null

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
        val view: View =  inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        // obtain a reference to different ui elements
        // configure some elements by setting their on click / text change listeners

        lbpToUsdExchangeRateContainer = view.findViewById(R.id.lbp_to_usd_exchange_rate_container)
        usdToLbpExchangeRateContainer = view.findViewById(R.id.usd_to_lbp_exchange_rate_container)
        lastRefreshedDateTimeContainer = view.findViewById(R.id.last_refreshed_date_time_container)

        buyUsdTextView = view.findViewById(R.id.lbp_to_usd_textview)
        sellUsdTextView = view.findViewById(R.id.usd_to_lbp_textview)
        lastRefreshedDateTimeTextView = view.findViewById(R.id.lastRefreshedDateTimeTextView)

        refreshButton = view.findViewById(R.id.refresh_button)
        refreshButton?.setOnClickListener { _ -> fetchRates() }

        moneyAmountEditText = view.findViewById(R.id.moneyAmount)
        moneyAmountEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moneyAmountEditText?.error = null
            }
        })

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
                        if(it.toDouble() != 0.0) {
                            buyUsdTextView?.text = it.toString()
                            lbpToUsdExchangeRateContainer?.contentDescription = "LBP to USD exchange rate is ${it.toDouble()}"
                        }
                        else {
                            buyUsdTextView?.text = "N/A"
                            lbpToUsdExchangeRateContainer?.contentDescription = "LBP to USD exchange rate is not available"
                        }
                    }
                    usdToLbp?.let {
                        if(it.toDouble() != 0.0) {
                            sellUsdTextView?.text = it.toString()
                            usdToLbpExchangeRateContainer?.contentDescription = "USD to LBP exchange rate is ${it.toDouble()}"
                        }
                        else {
                            sellUsdTextView?.text = "N/A"
                            usdToLbpExchangeRateContainer?.contentDescription = "USD to LBP exchange rate is not available"
                        }
                    }
                    val lastRefreshedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                    lastRefreshedDateTimeTextView?.text = lastRefreshedDateTime
                    lastRefreshedDateTimeContainer?.contentDescription = "Exchange rates were last refreshed on $lastRefreshedDateTime"

                }
                else {
                    var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                    if(message == "") { message = response.code().toString() }
                    Snackbar.make(
                        convertButton as View,
                        message,
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

        if(moneyAmountEditText?.text?.isBlank() == true) {
            moneyAmountEditText?.error = "Please enter a number"
            return
        }

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