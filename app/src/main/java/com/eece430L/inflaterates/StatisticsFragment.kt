package com.eece430L.inflaterates

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.LbpToUsdFluctuationModel
import com.eece430L.inflaterates.api.models.RatesPercentChangesModel
import com.eece430L.inflaterates.api.models.TransactionsNumberModel
import com.eece430L.inflaterates.api.models.UsdToLbpFluctuationModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var lbpToUsdPercentChangeContainer: LinearLayout? = null
    private var usdToLbpPercentChangeContainer: LinearLayout? = null
    private var lbpToUsdTransactionsNumberContainer: LinearLayout? = null
    private var usdToLbpTransactionsNumberContainer: LinearLayout? = null

    private var chart: LineChart? = null
    private var lbpToUsdPercentChangeTextView: TextView? = null
    private var usdToLbpPercentChangeTextView: TextView? = null
    private var usdToLbpTransactionsNumberTextView: TextView? = null
    private var lbpToUsdTransactionsNumberTextView: TextView? = null

    private var dates: ArrayList<String> = ArrayList()
    private var usdToLbpFluctuations: ArrayList<Float> = ArrayList()
    private var lbpToUsdFluctuations: ArrayList<Float> = ArrayList()

    private val calendar = Calendar.getInstance()
    private var currentYear = calendar.get(Calendar.YEAR)
    private var currentMonth = calendar.get(Calendar.MONTH) + 1
    private val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_statistics, container, false)

        lbpToUsdPercentChangeContainer = view.findViewById(R.id.lbp_to_usd_percent_change_container)
        usdToLbpPercentChangeContainer = view.findViewById(R.id.usd_to_lbp_percent_change_container)
        lbpToUsdTransactionsNumberContainer = view.findViewById(R.id.lbp_to_usd_transactions_number_container)
        usdToLbpTransactionsNumberContainer = view.findViewById(R.id.usd_to_lbp_transactions_number_container)

        chart = view.findViewById(R.id.line_chart)
        lbpToUsdPercentChangeTextView = view.findViewById(R.id.lbp_to_usd_percent_change_textview)
        usdToLbpPercentChangeTextView = view.findViewById(R.id.usd_to_lbp_percent_change_textview)

        lbpToUsdTransactionsNumberTextView = view.findViewById(R.id.lbp_to_usd_transactions_number_textview)
        usdToLbpTransactionsNumberTextView = view.findViewById(R.id.usd_to_lbp_transactions_number_textview)

        updatePercentChange()
        updateTransactionsNumber()
        updateChart()

        return view
    }

    private fun updatePercentChange() {

        InflateRatesService.inflateRatesApi().getTransactionNumbers().enqueue(object: Callback<TransactionsNumberModel> {
            override fun onResponse(
                call: Call<TransactionsNumberModel>,
                response: Response<TransactionsNumberModel>
            ) {
                if(response.isSuccessful) {
                    val lbpToUsdTransactionsNumber = response.body()?.lbpToUsdTransactionsNumber
                    lbpToUsdTransactionsNumberTextView?.text = lbpToUsdTransactionsNumber?.toString() ?: "N/A"
                    setTransactionsNumberContentDescription(lbpToUsdTransactionsNumber, lbpToUsdTransactionsNumberContainer, "LBP to USD")

                    val usdToLbpTransactionsNumber = response.body()?.usdToLbpTransactionsNumber
                    usdToLbpTransactionsNumberTextView?.text = usdToLbpTransactionsNumber?.toString() ?: "N/A"
                    setTransactionsNumberContentDescription(usdToLbpTransactionsNumber, usdToLbpTransactionsNumberContainer, "USD to LBP")

                }
            }
            override fun onFailure(call: Call<TransactionsNumberModel>, t: Throwable) {}
        })
    }

    private fun setTransactionsNumberContentDescription(transactionsNumber: Int?, transactionsNumberContainer: LinearLayout?, type: String) {
        if(transactionsNumber == null) {
            transactionsNumberContainer?.contentDescription = "Number of $type transactions is not available"
        }
        else {
            transactionsNumberContainer?.contentDescription = "$transactionsNumber $type transactions were performed"
        }
    }

    private fun updateTransactionsNumber() {
        InflateRatesService.inflateRatesApi().getPercentChanges().enqueue(object: Callback<RatesPercentChangesModel> {
            override fun onResponse(
                call: Call<RatesPercentChangesModel>,
                response: Response<RatesPercentChangesModel>
            ) {
                if(response.isSuccessful) {
                    val lbpToUsdPercentChange = response.body()?.lbpToUsdPercentChange
                    lbpToUsdPercentChangeTextView?.text = lbpToUsdPercentChange?.toString() ?: "N/A"
                    setPercentChangeContentDescription(lbpToUsdPercentChange, lbpToUsdPercentChangeContainer, "LBP to USD")

                    val usdToLbpPercentChange = response.body()?.usdToLbpPercentChange
                    usdToLbpPercentChangeTextView?.text = usdToLbpPercentChange?.toString() ?: "N/A"
                    setPercentChangeContentDescription(usdToLbpPercentChange, usdToLbpPercentChangeContainer, "USD to LBP")
                }
            }
            override fun onFailure(call: Call<RatesPercentChangesModel>, t: Throwable) {}
        })
    }

    private fun setPercentChangeContentDescription(percentChange: Float?, percentChangeContainer: LinearLayout?, type: String) {
        if(percentChange == null) {
            percentChangeContainer?.contentDescription = "$type exchange rate percent change is not available"
        }
        else if(percentChange > 0) {
            percentChangeContainer?.contentDescription = "$type exchange rate has decreased by ${-percentChange} percent"
        }
        else if(percentChange.toInt() == 0) {
            percentChangeContainer?.contentDescription = "$type exchange rate has not changed"
        }
        else {
            percentChangeContainer?.contentDescription = "$type exchange rate has increased by $percentChange percent"
        }
    }

    //    ChatGPT
    private fun updateChart() {

        getFluctuations()

        val lbpToUsdFluctuationEntries = mutableListOf<Entry>()
        for (i in dates.indices) {
            lbpToUsdFluctuationEntries.add(Entry(i.toFloat(), lbpToUsdFluctuations[i]))
        }

        val usdToLbpFluctuationEntries = mutableListOf<Entry>()
        for (i in dates.indices) {
            usdToLbpFluctuationEntries.add(Entry(i.toFloat(), usdToLbpFluctuations[i]))
        }

        val lbpToUsdFluctuationsDataSet = LineDataSet(lbpToUsdFluctuationEntries, "LBP to USD")
        lbpToUsdFluctuationsDataSet.color = Color.BLUE

        val usdToLbpFluctuationsDataSet = LineDataSet(usdToLbpFluctuationEntries, "USD to LBP")
        usdToLbpFluctuationsDataSet.color = Color.GREEN

        val lineData = LineData(lbpToUsdFluctuationsDataSet, usdToLbpFluctuationsDataSet)

        chart?.data = lineData
        chart?.description = null
        chart?.setDrawGridBackground(false)
        chart?.invalidate()

        val xAxis: XAxis? = chart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return dates[value.toInt()]
            }
        }
        xAxis?.setDrawGridLines(false)

        val yAxisLeft = chart?.axisLeft
        yAxisLeft?.setDrawGridLines(false)

        val yAxisRight = chart?.axisRight
        yAxisRight?.setDrawGridLines(false)
    }

    private fun getFluctuations() {

        InflateRatesService.inflateRatesApi().getUsdToLbpFluctuations(
            startYear = currentYear,
            startMonth = currentMonth,
            startDay = 1,
            endYear = currentYear,
            endMonth = currentMonth,
            endDay= daysInMonth
        ).enqueue(
            object: Callback<List<UsdToLbpFluctuationModel>> {
                override fun onResponse(call: Call<List<UsdToLbpFluctuationModel>>,
                                        response: Response<List<UsdToLbpFluctuationModel>>
                ) {
                    println("-----------------------------------------------------------------------------------")
                    println(response.body()?.size)
                    println("-----------------------------------------------------------------------------------")
                    if(response.isSuccessful) {
                        for(fluctuation in response.body()!!) {
                            dates.add(fluctuation.startDate!!)
//                            usdToLbpFluctuations.add(fluctuation.usdToLbpRate!!)
                        }
                    }
                }

                override fun onFailure(call: Call<List<UsdToLbpFluctuationModel>>, t: Throwable) {
                    println("-----------------------------------------------------------------------------------")
                    println(t.message)
                    println("-----------------------------------------------------------------------------------")
                }
        })

        InflateRatesService.inflateRatesApi().getLbpToUsdFluctuations(
            startYear = currentYear,
            startMonth = currentMonth,
            startDay = 1,
            endYear = currentYear,
            endMonth = currentMonth,
            endDay= daysInMonth
        ).enqueue(
            object: Callback<List<LbpToUsdFluctuationModel>> {
                override fun onResponse(call: Call<List<LbpToUsdFluctuationModel>>,
                                        response: Response<List<LbpToUsdFluctuationModel>>
                ) {
                    println("-----------------------------------------------------------------------------------")
                    println(response.body()?.size)
                    println("-----------------------------------------------------------------------------------")
                    if(response.isSuccessful) {
                        for(fluctuation in response.body()!!) {
//                            usdToLbpFluctuations.add(fluctuation.lbpToUsdRate!!)
                        }
                    }
                }

                override fun onFailure(call: Call<List<LbpToUsdFluctuationModel>>, t: Throwable) {
//                    println("-----------------------------------------------------------------------------------")
//                    print(t.message)
//                    println("-----------------------------------------------------------------------------------")
                }
        })

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = cal.time
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = cal.time
        val datesList = ArrayList<String>()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var currentDate = startDate
        while (currentDate <= endDate) {
            datesList.add(dateFormatter.format(currentDate))
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            currentDate = calendar.time
        }

        val usdToLbpList = ArrayList<Float>()
        for (i in 0 until datesList.size) {
            usdToLbpList.add((Random().nextFloat() * (5000.0 - 1500.0) + 1500.0).toFloat())
        }
        val lbpToUsdList = ArrayList<Float>()
        for (i in 0 until datesList.size) {
            lbpToUsdList.add((Random().nextFloat() * (1000 - 500) + 1000).toFloat())
        }

        println("-----------------------------------------------------------------------------------")
        println("Dates ArrayList: $datesList")
        println("USD to LBP ArrayList: $usdToLbpList")
        println("LBP to USD ArrayList: $lbpToUsdList")
        dates = datesList
        usdToLbpFluctuations = usdToLbpList
        lbpToUsdFluctuations = lbpToUsdList
    }
}