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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.adapters.CurrencyRadioAdapter
import com.example.ashmoney.adapters.IconColorRadioAdapter
import com.example.ashmoney.adapters.IconRadioAdapter
import com.example.ashmoney.databinding.FragmentAccountBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.viewmodels.AccountViewModel2
import kotlinx.coroutines.launch

class AccountFragment2 : Fragment() {

    private companion object {
        const val ACCOUNT_ID_KEY = "accountId"
    }

    private val viewModel: AccountViewModel2.ViewModel by viewModels()

    private lateinit var binding: FragmentAccountBinding

    private lateinit var navController: NavController

    private lateinit var currencyAdapter: CurrencyRadioAdapter
    private lateinit var iconAdapter: IconRadioAdapter
    private lateinit var iconColorAdapter: IconColorRadioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        navController = findNavController()

        viewModel.start(arguments?.getInt(ACCOUNT_ID_KEY))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIconList()
        setupIconColorList()
        setupCurrencyList()

        setupFieldListeners()
        startUiStateManaging()
    }

    private fun setupIconList() {
        iconAdapter = IconRadioAdapter {
            viewModel.inputs.icon(it)
        }
        setupDefaultHorizontalList(binding.fragmentAccountIconRecyclerView, iconAdapter)
        lifecycleScope.launch {
            launch {
                viewModel.outputs.iconList().collect(iconAdapter::submitList)
            }
            launch {
                viewModel.outputs.selectedIcon().collect { iconAdapter.selectedItem = it }
            }
        }
        //viewModel.iconList.observe(viewLifecycleOwner, iconAdapter::submitList)
    }

    private fun setupIconColorList() {
        iconColorAdapter = IconColorRadioAdapter {
            viewModel.inputs.iconColor(it)
        }
        setupDefaultHorizontalList(binding.fragmentAccountIconColorRecyclerView, iconColorAdapter)
        lifecycleScope.launch {
            launch {
                viewModel.outputs.iconColorList().collect(iconColorAdapter::submitList)
            }
            launch {
                viewModel.outputs.selectedIconColor().collect { iconColorAdapter.selectedItem = it }
            }

        }
        //viewModel.iconColorList.observe(viewLifecycleOwner, iconColorAdapter::submitList)
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.inputs.currency(it)
        }
        setupDefaultHorizontalList(binding.fragmentAccountCurrencyRecyclerView, currencyAdapter)

        lifecycleScope.launch {
            viewModel.outputs.currencyList().collect(currencyAdapter::submitList)
        }

        //viewModel.currencyList.observe(viewLifecycleOwner, currencyAdapter::submitList)
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

    private fun setupFieldListeners() {
        binding.fragmentAccountNameTextView.doAfterTextChanged {
            viewModel.inputs.name(it.toString())
        }
        binding.fragmentAccountStartAmountTextView.doAfterTextChanged {
            val inputValue = if (it != null && it.isNotEmpty())
                it.toString().toDouble()
            else
                0.0
            viewModel.inputs.sum(inputValue)
        }
        binding.fragmentAccountNoteTextView.doAfterTextChanged {
            viewModel.inputs.note(it.toString())
        }
    }

    private fun startUiStateManaging() {
        lifecycleScope.launch {
            viewModel.outputs.uiState().collect {
                with(binding) {
                    fragmentAccountPrimaryActionButton.text = it.primaryActionButtonText
                    fragmentAccountSecondaryActionButton.text = it.primaryActionButtonText
                }
            }
        }
    }

}