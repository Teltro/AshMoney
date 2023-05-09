package com.example.ashmoney.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.adapters.CurrencyExchangeRateAdapter
import com.example.ashmoney.adapters.CurrencyRadioAdapter
import com.example.ashmoney.databinding.FragmentCurrencyExchangeRateBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.viewmodels.CurrencyExchangeRateViewModel
import kotlinx.coroutines.launch

class CurrencyExchangeRateFragment : Fragment() {

    private val viewModel: CurrencyExchangeRateViewModel.ViewModel by viewModels()

    private lateinit var binding: FragmentCurrencyExchangeRateBinding

    private lateinit var currencyAdapter: CurrencyRadioAdapter
    private lateinit var currencyExchangeRateAdapter: CurrencyExchangeRateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyExchangeRateBinding.inflate(inflater, container, false)

        viewModel.start()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCurrencyList()
        setupCurrencyExchangeRateList()
        setupSumField()
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.inputs.currency(it)
        }
        setupDefaultHorizontalList(binding.fragmentCurrencyExchangeRateCurrencyRecyclerView, currencyAdapter)

        lifecycleScope.launch {
            launch {
                viewModel.outputs.currencyList().collect(currencyAdapter::submitList)
            }
            launch {
                viewModel.outputs.currency().collect { currencyAdapter.selectedItem = it }
            }
        }
    }

    private fun setupCurrencyExchangeRateList() {
        currencyExchangeRateAdapter = CurrencyExchangeRateAdapter()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        with(binding) {
            fragmentCurrencyExchangeRateRateRecyclerView.layoutManager = layoutManager
            fragmentCurrencyExchangeRateRateRecyclerView.adapter = currencyExchangeRateAdapter
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.currencyExchangeRateList().collect(currencyExchangeRateAdapter::submitList)
            }
            launch {
                viewModel.outputs.convertingSum().collect { currencyExchangeRateAdapter.convertingSum = it }
            }
        }
    }

    private fun setupSumField() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.outputs.convertingSum().collect {
                    if (!fragmentCurrencyExchangeRateSumEditText.hasFocus())
                        fragmentCurrencyExchangeRateSumEditText.setText(it.toString())
                }
            }
            fragmentCurrencyExchangeRateSumEditText.doAfterTextChanged {
                val sum = it.toString().toDoubleOrNull() ?: 1.0
                currencyExchangeRateAdapter.convertingSum = sum
                viewModel.inputs.convertingSum(sum)
            }
        }
    }

    private fun setupDefaultHorizontalList(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    ) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        setItemDecoration(recyclerView, layoutManager.orientation)
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

