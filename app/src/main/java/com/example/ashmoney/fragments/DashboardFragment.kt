package com.example.ashmoney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ashmoney.databinding.FragmentDashboardBinding
import com.example.ashmoney.viewmodels.DashboardViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel.ViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding

    private lateinit var navController: NavController

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
    }

    private fun setupTotalSumField() {
        lifecycleScope.launch {
            viewModel.outputs.totalSum().collect {
                binding.dashboardFragmentTotalSumTextView.text = roundDouble(it).toString()
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

    private fun setupPieChart() {
        lifecycleScope.launch {
            viewModel.outputs.pieChartOperationList().collect { operationList ->
                //val map = operationList.associateBy({it.toName}, {it.sum})
                val pieEntryList = operationList.map {  PieEntry(it.sum.toFloat(), it.toName) }
                val pieDataSet = PieDataSet(pieEntryList, "Hello")
                val pieData = PieData(pieDataSet)
                with (binding.dashboardFragmentPieChart) {
                    data = pieData
                    invalidate()
                }

            }
        }
        //binding.dashboardFragmentPieChart.
    }


    private fun roundDouble(value: Double): Double {
        return (value * 100).roundToInt() / 100.0
    }
}