package com.eece430L.inflaterates

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioGroup
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.TransactionModel
import com.eece430L.inflaterates.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordAnExchangeFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var lbpAmountEditText: TextInputLayout? = null
    private var usdAmountEditText: TextInputLayout? = null
    private var addTransactionButton: Button? = null
    private var transactionTypeRadioGroup: RadioGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_record_an_exchange, container, false)

        lbpAmountEditText = view.findViewById(R.id.lbp_amount_Layout)
        TextChangeListenerUtils.setTextChangeListener(lbpAmountEditText!!)

        usdAmountEditText = view.findViewById(R.id.usd_amount_Layout)
        TextChangeListenerUtils.setTextChangeListener(usdAmountEditText!!)

        addTransactionButton = view.findViewById(R.id.add_transaction_button)
        addTransactionButton?.setOnClickListener { _ -> addTransaction() }

        transactionTypeRadioGroup = view.findViewById(R.id.transaction_type_RadioGroup)
        transactionTypeRadioGroup?.check(R.id.usd_to_lbp_RadioButton)

        return view
    }

    private fun addTransaction() {

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(addTransactionButton?.windowToken, 0)

        usdAmountEditText?.clearFocus()
        lbpAmountEditText?.clearFocus()

        val usdAmount = usdAmountEditText?.editText?.text?.toString()
        val lbpAmount = lbpAmountEditText?.editText?.text?.toString()

        val inputIsValid = ValidatorUtils.validateTransactionInput(usdAmount, lbpAmount,
            usdAmountEditText, lbpAmountEditText)
        if(!inputIsValid) {
            return
        }

        usdAmountEditText?.editText?.setText("")
        lbpAmountEditText?.editText?.setText("")

        val usdToLbp: Boolean =
            transactionTypeRadioGroup?.checkedRadioButtonId == R.id.usd_to_lbp_RadioButton

        val transaction = TransactionModel(
            usdAmount?.toFloatOrNull(), lbpAmount?.toFloatOrNull(), usdToLbp)

        progressBarManager.showProgressBar(requireActivity())

        InflateRatesService.inflateRatesApi().addTransaction(
            transaction,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null)
            .enqueue(object: Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        Snackbar.make(
                            addTransactionButton as View,
                            "Transaction Added!",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else {
                        var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                        if(message == "") { message = response.code().toString() }
                        Snackbar.make(
                            addTransactionButton as View,
                            message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        addTransactionButton as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }
}