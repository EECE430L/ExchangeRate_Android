package com.eece430L.inflaterates.statistics

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.FluctuationModel
import com.eece430L.inflaterates.api.models.RatesPercentChangesModel
import com.eece430L.inflaterates.api.models.TransactionsNumberModel
import com.eece430L.inflaterates.utilities.ContentDescriptionUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment(), DatePickerDialog.OnDateSetListener  {

    private var headerTextView: TextView? = null

    private var lbpToUsdPercentChangeContainer: LinearLayout? = null
    private var usdToLbpPercentChangeContainer: LinearLayout? = null
    private var lbpToUsdTransactionsNumberContainer: LinearLayout? = null
    private var usdToLbpTransactionsNumberContainer: LinearLayout? = null

    private var chart: LineChart? = null
    private var lbpToUsdPercentChangeTextView: TextView? = null
    private var usdToLbpPercentChangeTextView: TextView? = null
    private var usdToLbpTransactionsNumberTextView: TextView? = null
    private var lbpToUsdTransactionsNumberTextView: TextView? = null

    private var setStatisticsTimeFrameButton: Button? = null

    private var dates: ArrayList<String> = ArrayList()
    private var usdToLbpFluctuations: ArrayList<Float> = ArrayList()
    private var lbpToUsdFluctuations: ArrayList<Float> = ArrayList()

    private var selectedStartYear = -1
    private var selectedStartMonth = -1
    private var selectedStartDay = -1

    private var selectedEndYear = -1
    private var selectedEndMonth = -1
    private var selectedEndDay = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_statistics, container, false)

        // obtain a reference to different ui elements
        // configure some elements by setting their on click listeners

        headerTextView = view.findViewById(R.id.header_TextView)

        lbpToUsdPercentChangeContainer = view.findViewById(R.id.lbp_to_usd_percent_change_container)
        usdToLbpPercentChangeContainer = view.findViewById(R.id.usd_to_lbp_percent_change_container)
        lbpToUsdTransactionsNumberContainer = view.findViewById(R.id.lbp_to_usd_transactions_number_container)
        usdToLbpTransactionsNumberContainer = view.findViewById(R.id.usd_to_lbp_transactions_number_container)

        setStatisticsTimeFrameButton = view.findViewById(R.id.set_statistics_time_frame_Button)
        setStatisticsTimeFrameButton?.setOnClickListener { _ -> setStatisticsTimeFrame() }

        chart = view.findViewById(R.id.line_chart)
        lbpToUsdPercentChangeTextView = view.findViewById(R.id.lbp_to_usd_percent_change_textview)
        usdToLbpPercentChangeTextView = view.findViewById(R.id.usd_to_lbp_percent_change_textview)

        lbpToUsdTransactionsNumberTextView = view.findViewById(R.id.lbp_to_usd_transactions_number_textview)
        usdToLbpTransactionsNumberTextView = view.findViewById(R.id.usd_to_lbp_transactions_number_textview)

        setDefaultStatisticsTimeFrame()
        updateTheHeaderToMatchSelectedDates()

        updatePercentChange()
        updateTransactionsNumber()
        updateFluctuations()

        return view
    }

    private fun setDefaultStatisticsTimeFrame() {
        val calendar = Calendar.getInstance()
        selectedEndYear = calendar.get(Calendar.YEAR)
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH)

        // decrement the current date by 1 week
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        selectedStartYear = calendar.get(Calendar.YEAR)
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun setStatisticsTimeFrame() {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        // configure the datePickerDialog to start with the current date selected
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            // save the chosen date
            selectedStartYear = year
            selectedStartMonth = month + 1
            selectedStartDay = dayOfMonth
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            // open another dialog so that the user can choose the End Date
            openEndDatePickerDialog(selectedCalendar.time)
        }, year, month, day)
        datePickerDialog.setTitle("Select Start Date")
        datePickerDialog.show()
    }

    private fun openEndDatePickerDialog(startDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        // configure the datePickerDialog to render with the start date (chosen previously) selected
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            // save user's input
            selectedEndYear = year
            selectedEndMonth = month + 1
            selectedEndDay = dayOfMonth

            updateTheHeaderToMatchSelectedDates()
            updatePercentChange()
            updateTransactionsNumber()
            updateFluctuations()

        }, year, month, day)
        // start the dialog from the start date so that the user can not choose an end date that comes
        // before the start date s/he previously chose
        datePickerDialog.datePicker.minDate = startDate.time
        datePickerDialog.setTitle("Select End Date")
        datePickerDialog.show()
    }

    private fun updateTheHeaderToMatchSelectedDates() {
        headerTextView?.text = "Displaying statistics between " +
                "$selectedStartYear-$selectedStartMonth-$selectedStartDay and " +
                "$selectedEndYear-$selectedEndMonth-$selectedEndDay"
    }

    private fun updatePercentChange() {
        InflateRatesService.inflateRatesApi().getTransactionNumbers(
            startYear = selectedStartYear,
            startMonth = selectedStartMonth,
            startDay = selectedStartDay,
            endYear = selectedEndYear,
            endMonth = selectedEndMonth,
            endDay= selectedEndDay
        ).enqueue(object: Callback<TransactionsNumberModel> {
            override fun onResponse(
                call: Call<TransactionsNumberModel>,
                response: Response<TransactionsNumberModel>
            ) {
                if(response.isSuccessful) {
                    val lbpToUsdTransactionsNumber = response.body()?.lbpToUsdTransactionsNumber
                    lbpToUsdTransactionsNumberTextView?.text = lbpToUsdTransactionsNumber?.toString() ?: "N/A"
                    ContentDescriptionUtils.setTransactionsNumberContentDescription(lbpToUsdTransactionsNumber, lbpToUsdTransactionsNumberContainer, "LBP to USD")

                    val usdToLbpTransactionsNumber = response.body()?.usdToLbpTransactionsNumber
                    usdToLbpTransactionsNumberTextView?.text = usdToLbpTransactionsNumber?.toString() ?: "N/A"
                    ContentDescriptionUtils.setTransactionsNumberContentDescription(usdToLbpTransactionsNumber, usdToLbpTransactionsNumberContainer, "USD to LBP")

                }
            }
            override fun onFailure(call: Call<TransactionsNumberModel>, t: Throwable) {}
        })
    }



    private fun updateTransactionsNumber() {
        InflateRatesService.inflateRatesApi().getPercentChanges(
            startYear = selectedStartYear,
            startMonth = selectedStartMonth,
            startDay = selectedStartDay,
            endYear = selectedEndYear,
            endMonth = selectedEndMonth,
            endDay= selectedEndDay
        ).enqueue(object: Callback<RatesPercentChangesModel> {
            override fun onResponse(
                call: Call<RatesPercentChangesModel>,
                response: Response<RatesPercentChangesModel>
            ) {
                if(response.isSuccessful) {
                    val lbpToUsdPercentChange = response.body()?.lbpToUsdPercentChange
                    if(lbpToUsdPercentChange == null) {
                        lbpToUsdPercentChangeTextView?.text = "N/A"
                    }
                    else {
                        lbpToUsdPercentChangeTextView?.text = "$lbpToUsdPercentChange %"
                    }
                    ContentDescriptionUtils.setPercentChangeContentDescription(lbpToUsdPercentChange, lbpToUsdPercentChangeContainer, "LBP to USD")

                    val usdToLbpPercentChange = response.body()?.usdToLbpPercentChange
                    if(usdToLbpPercentChange == null) {
                        usdToLbpPercentChangeTextView?.text = "N/A"
                    }
                    else {
                        usdToLbpPercentChangeTextView?.text = "$usdToLbpPercentChange %"
                    }
                    ContentDescriptionUtils.setPercentChangeContentDescription(usdToLbpPercentChange, usdToLbpPercentChangeContainer, "USD to LBP")
                }
            }
            override fun onFailure(call: Call<RatesPercentChangesModel>, t: Throwable) {}
        })
    }

    private fun convertDateStringToTimestamp(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0L
    }

    //    ChatGPT
    private fun updateChart() {

        // fill the entries for the 2 y-axes
        val lbpToUsdFluctuationEntries = mutableListOf<Entry>()
        val usdToLbpFluctuationEntries = mutableListOf<Entry>()
        for (i in dates.indices) {
            val timestamp = convertDateStringToTimestamp(dates[i])
            lbpToUsdFluctuationEntries.add(Entry(timestamp.toFloat(), lbpToUsdFluctuations[i]))
            usdToLbpFluctuationEntries.add(Entry(timestamp.toFloat(), usdToLbpFluctuations[i]))
        }

        // create the datasets that will be graphed
        val lbpToUsdFluctuationsDataSet = LineDataSet(lbpToUsdFluctuationEntries, "LBP to USD")
        lbpToUsdFluctuationsDataSet.color = Color.BLUE

        val usdToLbpFluctuationsDataSet = LineDataSet(usdToLbpFluctuationEntries, "USD to LBP")
        usdToLbpFluctuationsDataSet.color = Color.GREEN

        // graph the datasets
        val lineData = LineData(lbpToUsdFluctuationsDataSet, usdToLbpFluctuationsDataSet)

        // render the graph on the chart with customizations
        chart?.data = lineData
        chart?.description = null
        chart?.setDrawGridBackground(false)
        chart?.invalidate()

        chart?.xAxis?.isEnabled = false

        val yAxisLeft = chart?.axisLeft
        yAxisLeft?.setDrawGridLines(false)

        chart?.axisRight?.isEnabled = false

        // show a snackbar when the user clicks on a point on the graph
        // the snackbar contains the x and y values of the chosen point
        chart?.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val timestamp = e.x.toLong()
                    val date = Date(timestamp)
                    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)

                    Snackbar.make(
                        requireView(),
                        "Date: $formattedDate | Exchange Rate: ${e.y}" ,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onNothingSelected() {}
        })
    }

    private fun updateFluctuations() {

        dates.clear()
        lbpToUsdFluctuations.clear()
        usdToLbpFluctuations.clear()

        InflateRatesService.inflateRatesApi().getFluctuations(
            startYear = selectedStartYear,
            startMonth = selectedStartMonth,
            startDay = selectedStartDay,
            endYear = selectedEndYear,
            endMonth = selectedEndMonth,
            endDay= selectedEndDay).enqueue(object: Callback<List<FluctuationModel>> {

                override fun onResponse(call: Call<List<FluctuationModel>>, response: Response<List<FluctuationModel>>) {
                    if(response.isSuccessful) {
                        for(fluctuation in response.body()!!) {
                            dates.add(fluctuation.date!!)
                            if(fluctuation.lbpToUsdRate == null) {
                                lbpToUsdFluctuations.add(0F)
                            }
                            else {
                                lbpToUsdFluctuations.add(fluctuation.lbpToUsdRate!!)
                            }
                            if(fluctuation.usdToLbpRate == null) {
                                usdToLbpFluctuations.add(0F)
                            }
                            else {
                                usdToLbpFluctuations.add(fluctuation.usdToLbpRate!!)
                            }
                        }
                        updateChart()
                    }
                }

                override fun onFailure(call: Call<List<FluctuationModel>>, t: Throwable) {
                    Snackbar.make(
                        setStatisticsTimeFrameButton as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {}
}