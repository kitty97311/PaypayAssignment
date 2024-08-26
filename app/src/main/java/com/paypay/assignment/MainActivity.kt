package com.paypay.assignment

import android.app.AlertDialog
import android.content.Context
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paypay.charting.charts.LineChart
import com.paypay.charting.components.XAxis
import com.paypay.charting.data.Entry
import com.paypay.charting.data.LineData
import com.paypay.charting.data.LineDataSet
import com.paypay.charting.formatter.IndexAxisValueFormatter
import com.paypay.charting.utils.ColorTemplate
import com.paypay.assignment.ui.theme.KittyAndroidTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity(), View.OnClickListener {

    private lateinit var loadingDialog: AlertDialog // Define AlertDialog for progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSearch).setOnClickListener(this)

        // Initialize loading dialog
        loadingDialog = createLoadingDialog(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSearch -> {
                val stockSymbol = findViewById<EditText>(R.id.etStockSymbol).text.toString()
                if (stockSymbol.isEmpty()) {
                    Toast.makeText(this, "Stock Symbolを入力します。", Toast.LENGTH_LONG).show()
                    return
                }
                try {
                    MainScope().launch {
                        loadingDialog.show() // Show loading dialog before API call
                        val apiResponse = ApiService.fetchDataFromApi(stockSymbol)
                        Log.e("Response", "$apiResponse")
                        val chartVal = JSONObject(apiResponse).getJSONObject("chart")
                        if (chartVal.isNull("result")) {
                            loadingDialog.dismiss() // Dismiss loading dialog if no result
                            Toast.makeText(this@MainActivity, "データが見つかりません", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                        val metaVal = chartVal.getJSONArray("result").getJSONObject(0).getJSONObject("meta")
                        findViewById<TextView>(R.id.tv52Low).text = "${String.format("%,.2f", metaVal.getDouble("fiftyTwoWeekLow"))}円"
                        findViewById<TextView>(R.id.tv52High).text = "${String.format("%,.2f", metaVal.getDouble("fiftyTwoWeekHigh"))}円"
                        findViewById<TextView>(R.id.tvVolume).text = "${String.format("%,d", metaVal.getLong("regularMarketVolume"))}株"
                        findViewById<TextView>(R.id.tvLowPrice).text = "${String.format("%,.2f", metaVal.getDouble("regularMarketDayLow"))}円"
                        findViewById<TextView>(R.id.tvHighPrice).text = "${String.format("%,.2f", metaVal.getDouble("regularMarketDayHigh"))}円"
                        findViewById<TextView>(R.id.tvBeginPrice).text = "${String.format("%,.2f", metaVal.getDouble("regularMarketPrice"))}円"
                        findViewById<TextView>(R.id.tvPrevClose).text = "${String.format("%,.2f", metaVal.getDouble("previousClose"))}円"

                        val stockChart = findViewById<LineChart>(R.id.stockChart)
                        val timeArray = chartVal.getJSONArray("result").getJSONObject(0).getJSONArray("timestamp")
                        val stockArray = chartVal.getJSONArray("result").getJSONObject(0).getJSONObject("indicators").getJSONArray("quote").getJSONObject(0).getJSONArray("close")
                        val entries = mutableListOf<Entry>()
                        val xLabels = ArrayList<String>()
                        for (i in 0 until timeArray.length()) {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = timeArray.getLong(i) * 1000
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH) + 1
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val min = calendar.get(Calendar.MINUTE)
                            val sec = calendar.get(Calendar.SECOND)
                            val stockVal = if (stockArray.getString(i).equals("null")) 0 else stockArray.getDouble(i)
                            xLabels.add("${min}分")
                            entries.add(Entry(min.toFloat(), stockVal.toFloat()))
                        }
                        val dataSet = LineDataSet(entries, "Stock Data")
                        dataSet.color = ColorTemplate.MATERIAL_COLORS[0]  // Set the color of the line
                        dataSet.valueTextColor = ColorTemplate.MATERIAL_COLORS[1]  // Set the color of the values

                        // Create a LineData object with the dataset
                        val lineData = LineData(dataSet)

                        // Set the data to the chart
                        stockChart.data = lineData
                        stockChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
                        stockChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                        stockChart.xAxis.granularity = 1f
                        // Customize the chart (optional)
                        stockChart.description.text = "Stock Chart"
                        stockChart.animateX(1000)
                        loadingDialog.dismiss() // Dismiss loading dialog after successful response
                    }

                } catch (e: Exception) {
                    loadingDialog.dismiss() // Ensure loading dialog is dismissed on error
                    Toast.makeText(this@MainActivity, "Response is malformed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Function to create a loading dialog with a ProgressBar
    private fun createLoadingDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(view)
        builder.setCancelable(false) // Make dialog non-cancelable
        return builder.create()
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        KittyAndroidTheme {
            Greeting("Android")
        }
    }
}
