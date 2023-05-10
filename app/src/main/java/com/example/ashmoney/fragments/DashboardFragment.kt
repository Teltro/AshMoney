package com.example.ashmoney.fragments

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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
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

    private fun setupPieChart() {
        with(binding.dashboardFragmentPieChart) {
            setDrawCenterText(false)
            setDrawEntryLabels(false)
            setDrawMarkers(false)
            setDrawRoundedSlices(false)
        }

        lifecycleScope.launch {
            viewModel.outputs.pieChartOperationList().collect { operationList ->
                //val map = operationList.associateBy({it.toName}, {it.sum})
                val pieEntryList = operationList.map {  PieEntry(it.sum.toFloat(), it.targetName) }
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

    private fun setupPieChartOperationList() {
        operationPieCharItemAdapter = OperationPieChartItemAdapter()
        setupDefaultHorizontalList(
            binding.dashboardFragmentPieChartDataRecyclerView,
            operationPieCharItemAdapter
        )

        lifecycleScope.launch {
            launch {
                viewModel.outputs.pieChartOperationList().collect(operationPieCharItemAdapter::submitList)
            }
        }
    }

    private fun setupDefaultHorizontalList(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    ) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        //setItemDecoration(recyclerView, layoutManager.orientation)
    }

    private fun setItemDecoration(recyclerView: RecyclerView, orientation: Int) {
        requireContext().run {
            val drawable = ContextCompat.getDrawable(this, R.drawable.item_decoration)
            drawable?.let {
                val itemDecoration = RadioItemDecoration(drawable, orientation)
                recyclerView.addItemDecoration(itemDecoration)
            }
        }
    }

}