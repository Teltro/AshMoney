package com.example.ashmoney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.adapters.CurrencyExchangeRateAdapter
import com.example.ashmoney.adapters.CurrencyRadioAdapter
import com.example.ashmoney.databinding.FragmentCurrencyExchangeRateBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.utils.toEditable
import com.example.ashmoney.viewmodels.AccountViewModel
import com.example.ashmoney.viewmodels.CurrencyExchangeRateViewModel
import kotlinx.coroutines.launch

class CurrencyExchangeRateFragment : Fragment() {

    private val viewModel: CurrencyExchangeRateViewModel by viewModels()

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
        setupSumEditTextListener()

        startStateManaging()
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.selectedCurrency.value = it
        }
        setupDefaultHorizontalList(binding.fragmentCurrencyExchangeRateCurrencyRecyclerView, currencyAdapter)

        lifecycleScope.launch {
            viewModel.currencyList.collect(currencyAdapter::submitList)
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
            viewModel.currencyExchangeRateList.collect(currencyExchangeRateAdapter::submitList)
        }
    }

    private fun setupSumEditTextListener() {
        binding.fragmentCurrencyExchangeRateSumEditText.doAfterTextChanged {
            val sum = it.toString().toDoubleOrNull() ?: 1.0
            currencyExchangeRateAdapter.sum = sum
            viewModel.data.sum = sum
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

    private fun startStateManaging() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state == CurrencyExchangeRateViewModel.State.INFO) {
                    setVisualDataFromViewModel()
                }
            }
        }
    }

    private fun setVisualDataFromViewModel() {
        viewModel.data.let { data ->
            with(binding) {
                currencyAdapter.selectedItem = data.selectedCurrency
                data.sum?.let {
                    fragmentCurrencyExchangeRateSumEditText.text = it.toString().toEditable()
                } ?: {
                    fragmentCurrencyExchangeRateSumEditText.text = "1.0".toEditable()
                }
            }
        }
    }

}

