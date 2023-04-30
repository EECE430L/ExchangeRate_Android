package com.eece430L.inflaterates.utilities

import android.widget.LinearLayout
import com.eece430L.inflaterates.api.models.MyTransactionModel
import com.eece430L.inflaterates.api.models.OfferModel

object ContentDescriptionUtils {

    fun setTransactionItemContentDescription(transactionItemContainer: LinearLayout?,
                                             transactionItem: MyTransactionModel) {

        val localDateTime = transactionItem.addedDate?.let {
            DateTimeFormatUtils.iSODateTimeStringToLocalDateTimeString(
                it
            )
        }

        var contentDescription = "On $localDateTime you exchanged"

        contentDescription += if(transactionItem.usdToLbp == true) {
            " ${transactionItem.usdAmount} United States Dollars" +
                    " for ${transactionItem.lbpAmount} Lebanese Pounds"
        } else {
            " ${transactionItem.lbpAmount} Lebanese Pounds" +
                    " for ${transactionItem.usdAmount} United States Dollars"
        }

        contentDescription += if(transactionItem.secondParty != "Third Party") {
            " with user ${transactionItem.secondParty}"
        } else {
            " with a third party client"
        }

        transactionItemContainer?.contentDescription = contentDescription
    }

    fun setOfferItemContentDescription(offerItemContainer: LinearLayout?,
                                       offerItem: OfferModel,
                                       viewingReceivedOffers: Boolean) {

        var contentDescription = ""

        contentDescription += if(viewingReceivedOffers) {
            "You have received an offer from user ${offerItem.offerer}"
        } else {
            "You have sent an offer to user ${offerItem.receiver}"
        }

        contentDescription += if(offerItem.usdToLbp == true) {
            " requesting ${offerItem.requestedAmount} United States Dollars for ${offerItem.offeredAmount} Lebanese Pounds"
        } else {
            " requesting ${offerItem.requestedAmount} Lebanese Pounds for ${offerItem.offeredAmount} United States Dollars"
        }

        offerItemContainer?.contentDescription = contentDescription
    }
}