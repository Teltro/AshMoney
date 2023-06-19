package com.example.ashmoney.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.adapters.OperationPieChartItemAdapter
import com.example.ashmoney.adapters.OperationTypeRadioAdapter
import com.example.ashmoney.databinding.FragmentDashboardBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.utils.round100
import com.example.ashmoney.viewmodels.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel.ViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var navController: NavController

    private lateinit var operationPieCharItemAdapter: OperationPieChartItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTotalSumField()
        setupTotalSumCurrency()
        setupPieChart()
        setupPieChartOperationList()
        setupLineChart()
    }

    private fun setupTotalSumField() {
        lifecycleScope.launch {
            viewModel.outputs.totalSum().collect {
                binding.dashboardFragmentTotalSumTextView.text = it.round100().toString()
            }
        }
    }

    private fun setupTotalSumCurrency() {
        lifecycleScope.launch {
            viewModel.outputs.globalCurrency().collect {
                binding.dashboardFragmentTotalSumCurrencyTextView.text = it?.name ?: ""
            }
        }
    }

    // TODO: rewrite chart creation
    private fun setupPieChart() {
        with(binding.dashboardFragmentPieChart) {
            setDrawCenterText(false)
            setDrawEntryLabels(false)
            setDrawMarkers(false)
            setDrawRoundedSlices(false)
            setDrawSlicesUnderHole(false)
            legend.isEnabled = false
            description.isEnabled = false
            setHoleColor(Color.TRANSPARENT)
        }

        lifecycleScope.launch {
            viewModel.outputs.pieChartOperationList().collect { operationList ->
                //val map = operationList.associateBy({it.toName}, {it.sum})
                val pieEntryList = operationList.map {  PieEntry(it.sum.toFloat(), it.targetName) }
                val pieDataSet = PieDataSet(pieEntryList, "Hello")
                pieDataSet.colors = operationList.map { Color.parseColor(it.targetIconColorValue) }
                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(false)
                with (binding.dashboardFragmentPieChart) {
                    data = pieData
                    invalidate()
                }

            }
        }
        //binding.dashboardFragmentPieChart.
    }

    private fun setupLineChart() {
        with(binding.dashboardFragmentLineChart) {
            setDrawBorders(false)
            setDrawGridBackground(false)
            setDrawMarkers(false)
            legend.isEnabled = false
            description.isEnabled = false
            xAxis.valueFormatter = object: ValueFormatter() {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                override fun getFormattedValue(value: Float): String {
                    return formatter.format(LocalDate.ofEpochDay(value.toLong()))
                }
            }
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = 330f
            xAxis.isGranularityEnabled = true
            xAxis.setCenterAxisLabels(false)

            right
        }

        lifecycleScope.launch {
            viewModel.outputs.lineChartOperationList().collect { operationList ->
                //val map = operationList.associateBy({it.toName}, {it.sum})
                val lineEntryList = operationList.map { Entry((it.dateTime.getLong(ChronoField.DAY_OF_YEAR)).toFloat(), it.sum.toFloat()) }
                //val pieEntryList = operationList.map {  PieEntry(it.sum.toFloat(), it.targetName) }
                val lineDataSet = LineDataSet(lineEntryList, "Hello")
                lineDataSet.setDrawValues(false)
                //lineDataSet.colors = operationList.map { Color.parseColor(it) }
                val lineData = LineData(lineDataSet)
                with (binding.dashboardFragmentLineChart) {
                    data = lineData
                    invalidate()
                }

            }
        }
        //binding.dashboardFragmentPieChart.
    }

    private fun setupPieChartOperationList() {
        operationPieCharItemAdapter = OperationPieChartItemAdapter()
        with(binding.dashboardFragmentPieChartDataRecyclerView) {
            val l = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = operationPieCharItemAdapter
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.pieChartOperationList().collect(operationPieCharItemAdapter::submitList)
            }
        }
    }

}