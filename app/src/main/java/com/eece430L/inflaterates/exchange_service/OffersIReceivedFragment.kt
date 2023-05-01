package com.eece430L.inflaterates.exchange_service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.OfferModel
import com.eece430L.inflaterates.utilities.Authentication
import com.eece430L.inflaterates.utilities.OffersUtil
import com.eece430L.inflaterates.utilities.ProgressBarManager

class OffersIReceivedFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var receivedOffersPlaceHolderTextView: TextView? = null
    private var listview : ListView? = null
    private var offers : ArrayList<OfferModel>? = ArrayList()
    private var adapter : OffersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_offers_i_received, container, false)

        receivedOffersPlaceHolderTextView = view.findViewById(R.id.received_offers_placeholder_TextView)
        receivedOffersPlaceHolderTextView?.visibility = View.GONE

        listview = view.findViewById(R.id.offers_i_received_ListView)
        adapter = OffersAdapter(requireActivity(), layoutInflater, progressBarManager,
            offers!!, isViewingReceivedOffers = true)
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

        val getOffersIReceivedApiCall = InflateRatesService.inflateRatesApi().getOffersIReceived(
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null
        )

        offers?.let { offers ->
            adapter?.let { adapter ->
                receivedOffersPlaceHolderTextView?.let { receivedOffersPlaceHolderTextView ->
                    OffersUtil.getOffersAndUpdateView(
                        requireActivity(),
                        requireView(),
                        getOffersIReceivedApiCall,
                        receivedOffersPlaceHolderTextView,
                        progressBarManager,
                        offers,
                        adapter)
                }
            }
        }
    }
}