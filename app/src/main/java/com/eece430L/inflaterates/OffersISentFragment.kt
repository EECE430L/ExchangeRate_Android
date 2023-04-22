package com.eece430L.inflaterates

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.OfferModel
import com.eece430L.inflaterates.utilities.Authentication
import com.eece430L.inflaterates.utilities.OffersUtil
import com.eece430L.inflaterates.utilities.ProgressBarManager

class OffersISentFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var sentOffersPlaceHolderContainer: LinearLayout? = null
    private var linkToCreateOfferTextView: TextView? = null
    private var listview : ListView? = null
    private var offers : ArrayList<OfferModel>? = ArrayList()
    private var adapter : OffersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_offers_i_sent, container, false)

        sentOffersPlaceHolderContainer = view.findViewById(R.id.sent_offers_placeholder_LinearLayout)
        sentOffersPlaceHolderContainer?.visibility = View.GONE

        linkToCreateOfferTextView = view.findViewById(R.id.linkToCreateOffer_TextView)
        linkToCreateOfferTextView?.setOnClickListener { _ ->
            (activity as MainActivity).switchToOfferATransactionFragment()
        }
        val linkToCreateOffer = SpannableString("Create an Offer!")
        linkToCreateOffer.setSpan(UnderlineSpan(), 0, linkToCreateOffer.length, 0)
        linkToCreateOfferTextView?.text = linkToCreateOffer

        listview = view.findViewById(R.id.offers_i_sent_ListView)
        adapter = OffersAdapter(requireActivity(), layoutInflater, progressBarManager,
            offers!!, isViewingReceivedOffers = false)
        listview?.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillOffersList()
    }

    private fun fillOffersList() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            (requireActivity() as MainActivity).logout()
        }

        val getOffersISentApiCall = InflateRatesService.inflateRatesApi().getOffersISent(
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null
        )

        offers?.let { offers ->
            adapter?.let { adapter ->
                sentOffersPlaceHolderContainer?.let { sentOffersPlaceHolderContainer ->
                    OffersUtil.getOffersAndUpdateView(
                        requireActivity(),
                        requireView(),
                        getOffersISentApiCall,
                        sentOffersPlaceHolderContainer,
                        progressBarManager,
                        offers,
                        adapter)
                }
            }
        }
    }
}