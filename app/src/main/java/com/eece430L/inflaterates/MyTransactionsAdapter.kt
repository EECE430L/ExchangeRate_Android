package com.eece430L.inflaterates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.eece430L.inflaterates.api.models.MyTransactionModel
import com.eece430L.inflaterates.utilities.ContentDescriptionUtils
import com.eece430L.inflaterates.utilities.DateTimeFormatUtils

// ChatGPT
class MyTransactionsAdapter (private val inflater: LayoutInflater,
                             private val dataSource: List<MyTransactionModel>)
    : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.transaction_list_item, parent, false)

        view.findViewById<TextView>(R.id.exchanged_with_TextView).text =
            dataSource[position].secondParty

        view.findViewById<TextView>(R.id.usd_amount_TextView).text =
            dataSource[position].usdAmount.toString()

        view.findViewById<TextView>(R.id.lbp_amount_TextView).text =
            dataSource[position].lbpAmount.toString()

        view.findViewById<TextView>(R.id.type_TextView).text = dataSource[position].let {
            if(it.usdToLbp == null) "N/A" else if (it.usdToLbp!!) "USD to LBP" else "LBP to USD"
        }

        val transactionItemContainer: LinearLayout? = view.findViewById(R.id.transaction_item_container)
        ContentDescriptionUtils.setTransactionItemContentDescription(
            transactionItemContainer, transactionItem=dataSource[position])

        view.findViewById<TextView>(R.id.date_and_time_TextView).text =
            dataSource[position].addedDate?.let {
                DateTimeFormatUtils.iSODateTimeStringToLocalDateTimeString(
                    it
                )
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
}