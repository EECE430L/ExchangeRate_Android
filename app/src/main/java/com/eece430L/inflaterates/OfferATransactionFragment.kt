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
import com.eece430L.inflaterates.api.models.CreateOfferRequestModel
import com.eece430L.inflaterates.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfferATransactionFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var receiverEditText: TextInputLayout? = null
    private var offeredAmountEditText: TextInputLayout? = null
    private var requestedAmountEditText: TextInputLayout? = null
    private var transactionTypeRadioGroup: RadioGroup? = null
    private var createOfferButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_offer_a_transaction, container, false)

        receiverEditText = view.findViewById(R.id.receiver_Layout)
        TextChangeListenerUtils.setTextChangeListener(receiverEditText!!)

        offeredAmountEditText = view.findViewById(R.id.offered_amount_Layout)
        TextChangeListenerUtils.setTextChangeListener(offeredAmountEditText!!)

        requestedAmountEditText = view.findViewById(R.id.requested_amount_Layout)
        TextChangeListenerUtils.setTextChangeListener(requestedAmountEditText!!)

        transactionTypeRadioGroup = view.findViewById(R.id.transaction_type_RadioGroup)
        transactionTypeRadioGroup?.check(R.id.usd_to_lbp_RadioButton)

        createOfferButton = view.findViewById(R.id.create_offer_Button)
        createOfferButton?.setOnClickListener { _ -> createOffer() }

        return view
    }

    private fun createOffer() {

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(createOfferButton?.windowToken, 0)

        receiverEditText?.clearFocus()
        offeredAmountEditText?.clearFocus()
        requestedAmountEditText?.clearFocus()

        val receiver = receiverEditText?.editText?.text?.toString()
        val offeredAmount = offeredAmountEditText?.editText?.text?.toString()
        val requestedAmount = requestedAmountEditText?.editText?.text?.toString()

        val inputIsValid = ValidatorUtils.validateOfferInput(receiver, offeredAmount, requestedAmount,
        receiverEditText, offeredAmountEditText, requestedAmountEditText)
        if(!inputIsValid) {
            return
        }

        val usdToLbp: Boolean =
            transactionTypeRadioGroup?.checkedRadioButtonId == R.id.usd_to_lbp_RadioButton

        val createOfferRequest = CreateOfferRequestModel(
            receiver,
            offeredAmount?.toFloatOrNull(),
            requestedAmount?.toFloatOrNull(),
            usdToLbp
        )

        progressBarManager.showProgressBar(requireActivity())

        InflateRatesService.inflateRatesApi().createOffer(
            createOfferRequest,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null)
            .enqueue(object: Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        (activity as MainActivity).switchToOffersISentFragment()
                    }
                    else if (response.code() == 401 || response.code() == 403) {
                        (activity as MainActivity).logout()
                    }
                    else {
                        var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                        if(response.code() == 404) { message = "A user with username " +
                                "${createOfferRequest.receiver} was not found!" }
                        else if(message == "") { message = response.code().toString() }
                        Snackbar.make(
                            requireView(),
                            message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        createOfferButton as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }
}