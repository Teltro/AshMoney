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
import com.example.ashmoney.adapters.AccountRadioAdapter
import com.example.ashmoney.adapters.CurrencyRadioAdapter
import com.example.ashmoney.adapters.OperationCategoryRadioAdapter
import com.example.ashmoney.adapters.OperationTypeRadioAdapter
import com.example.ashmoney.databinding.FragmentOperationBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.utils.round100
import com.example.ashmoney.utils.setEnabledForAll
import com.example.ashmoney.utils.setItemDecoration
import com.example.ashmoney.viewmodels.OperationViewModel
import kotlinx.coroutines.launch

class OperationFragment : Fragment() {

    private companion object {
        const val OPERATION_ID_KEY = "operationId"
    }

    private val viewModel: OperationViewModel.ViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentOperationBinding

    private lateinit var operationTypeAdapter: OperationTypeRadioAdapter
    private lateinit var fromAccountAdapter: AccountRadioAdapter
    private lateinit var toAccountAdapter: AccountRadioAdapter
    private lateinit var operationCategoryAdapter: OperationCategoryRadioAdapter
    private lateinit var currencyAdapter: CurrencyRadioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOperationBinding.inflate(inflater)
        navController = findNavController()

        viewModel.start(arguments?.getInt(OPERATION_ID_KEY))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOperationTypeList()
        setupAccountFromList()
        setupAccountToList()
        setupOperationCategoryList()
        setupCurrencyList()
        setupNameField()
        setupSumField()
        setupNoteField()
        setupConverterCurrenciesFields()
        setupCurrencyExchangeRateField()
        setupConverteredSumField()

        setupButtons()
        setupLeavePage()
        setupConvertLayoutEnable()

        startUiStateManaging()
        startMemberUIStateManaging()
        startConverterUIStateManaging()
    }

    private fun setupOperationTypeList() {
        operationTypeAdapter = OperationTypeRadioAdapter {
            viewModel.inputs.operationType(it)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentOperationOperationTypeRecyclerView.let {
            it.layoutManager = layoutManager
            it.adapter = operationTypeAdapter
            it.setItemDecoration(layoutManager.orientation)
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.operationTypeList().collect(operationTypeAdapter::submitList)
            }
            launch {
                viewModel.outputs.operationType().collect { operationTypeAdapter.selectedItem = it }
            }
        }
    }

    private fun setupAccountFromList() {
        fromAccountAdapter = AccountRadioAdapter {
            //viewModel.data.accountFrom = it
            viewModel.inputs.fromAccount(it)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentOperationFromAccountRecyclerView.let {
            it.layoutManager = layoutManager
            it.adapter = fromAccountAdapter
            it.setItemDecoration(layoutManager.orientation)
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.accountList().collect(fromAccountAdapter::submitList)
            }
            launch {
                viewModel.outputs.fromAccount().collect { fromAccountAdapter.selectedItem = it }
            }
        }
    }

    private fun setupAccountToList() {
        toAccountAdapter = AccountRadioAdapter {
            viewModel.inputs.toAccount(it)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentOperationToAccountRecyclerView.let {
            it.layoutManager = layoutManager
            it.adapter = toAccountAdapter
            it.setItemDecoration(layoutManager.orientation)
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.accountList().collect(toAccountAdapter::submitList)
            }
            launch {
                viewModel.outputs.toAccount().collect { toAccountAdapter.selectedItem = it }
            }
        }
    }

    private fun setupOperationCategoryList() {
        operationCategoryAdapter =
            OperationCategoryRadioAdapter {
                viewModel.inputs.operationCategory(it)
            }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentOperationOperationCategoryRecyclerView.let {
            it.layoutManager = layoutManager
            it.adapter = operationCategoryAdapter
            it.setItemDecoration(layoutManager.orientation)
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.operationCategoryList()
                    .collect(operationCategoryAdapter::submitList)
            }
            launch {
                viewModel.outputs.operationCategory()
                    .collect { operationCategoryAdapter.selectedItem = it }
            }
        }
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.inputs.currency(it)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentOperationOperationCurrencyRecyclerView.let {
            it.layoutManager = layoutManager
            it.adapter = currencyAdapter
            it.setItemDecoration(layoutManager.orientation)
        }

        lifecycleScope.launch {
            launch {
                viewModel.outputs.currencyList().collect(currencyAdapter::submitList)
            }
            launch {
                viewModel.outputs.currency().collect { currencyAdapter.selectedItem = it }
            }
        }
    }

    private fun setupNameField() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.outputs.name().collect {
                    if (!fragmentOperationOperationNameEditText.hasFocus())
                        fragmentOperationOperationNameEditText.setText(it)
                }
            }
            fragmentOperationOperationNameEditText.doAfterTextChanged {
                viewModel.inputs.name(it.toString())
            }
        }
    }

    private fun setupSumField() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.outputs.sum()
                    .collect {
                        if (!fragmentOperationSumFromValueEditText.hasFocus())
                            fragmentOperationSumFromValueEditText.setText(it.toString())
                    }
            }
            fragmentOperationSumFromValueEditText.doAfterTextChanged {
                val inputValue = if (it != null && it.isNotEmpty())
                    it.toString().toDouble()
                else
                    0.0
                viewModel.inputs.sum(inputValue)
            }
        }
    }

    private fun setupNoteField() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.outputs.note()
                    .collect {
                        if (!fragmentOperationOperationNoteEditText.hasFocus())
                            fragmentOperationOperationNoteEditText.setText(it)
                    }
            }
            fragmentOperationOperationNoteEditText.doAfterTextChanged {
                viewModel.inputs.note(it.toString())
            }
        }
    }

    private fun setupConverterCurrenciesFields() {
        with(binding) {
            lifecycleScope.launch {
                launch {
                    viewModel.outputs.converterFromCurrency().collect {
                        fragmentOperationOperationConvertFromCurrencyTextView.text = it
                        fragmentOperationOperationSumFromCurrencyTextView.text = it
                    }
                }

                launch {
                    viewModel.outputs.converterToCurrency().collect {
                        fragmentOperationOperationConvertToCurrencyTextView.text = it
                        fragmentOperationOperationSumToCurrencyTextView.text = it
                    }
                }
            }
        }
    }

    private fun setupCurrencyExchangeRateField() {
        with(binding) {
            lifecycleScope.launch {
                launch {
                    viewModel.outputs.currencyExchangeRate()
                        .collect {
                            if (!fragmentOperationConverterToValueEditText.hasFocus())
                                fragmentOperationConverterToValueEditText.setText(it.round100().toString())
                        }
                }
            }
            fragmentOperationConverterToValueEditText.doAfterTextChanged {
                viewModel.inputs.currencyExchangeRate(it.toString().toDouble())
            }
        }
    }

    private fun setupConverteredSumField() {
        lifecycleScope.launch {
            viewModel.outputs.converteredSum().collect {
                binding.fragmentOperationSumToValueEditText.run {
                    if (!hasFocus())
                        setText(it.round100().toString())
                }
            }
        }
    }

    private fun setupButtons() {
        with(binding) {
            lifecycleScope.launch {
                launch {
                    fragmentOperationPrimaryActionButton.setOnClickListener {
                        viewModel.inputs.primaryActionClicked()
                    }
                }
                launch {
                    fragmentOperationSecondaryActionButton.setOnClickListener {
                        viewModel.inputs.secondaryActionClicked()
                    }
                }
            }
        }
    }

    private fun setupLeavePage() {
        lifecycleScope.launch {
            viewModel.outputs.leavePage().collect {
                navController.popBackStack()
            }
        }
    }

    private fun startUiStateManaging() {
        lifecycleScope.launch {
            viewModel.outputs.uiState().collect {
                with(binding) {
                    fragmentOperationPrimaryActionButton.text = it.primaryActionButtonText
                    fragmentOperationSecondaryActionButton.text = it.secondaryActionButtonText
                }
            }
        }
    }

    private fun startMemberUIStateManaging() {
        lifecycleScope.launch {
            viewModel.outputs.membersUIState().collect {
                with(binding) {
                    fragmentOperationFromAccountLayout.visibility =
                        memberUIVisibleConvert(it.fromAccountVisible)
                    fragmentOperationToAccountLayout.visibility =
                        memberUIVisibleConvert(it.toAccountVisible)
                    fragmentOperationOperationCategoryLayout.visibility =
                        memberUIVisibleConvert(it.operationCategoryVisible)
                }
            }
        }
    }

    private fun memberUIVisibleConvert(visible: Boolean) = if (visible) View.VISIBLE else View.GONE

    private fun setupConvertLayoutEnable() {
        with(binding) {
            fragmentOperationConverterFromLayout.setEnabledForAll(false)
            fragmentOperationConverterToLayout.setEnabledForAll(true)
            fragmentOperationSumFromLayout.setEnabledForAll(true)
            fragmentOperationSumToLayout.setEnabledForAll(false)
        }
    }

    private fun startConverterUIStateManaging() {
        lifecycleScope.launch {
            viewModel.outputs.converterUIState().collect {
                with(binding) {
                    when (it) {
                        OperationViewModel.ConverterState.NONE,
                        OperationViewModel.ConverterState.WITHOUT_CONVERTER -> {
                            fragmentOperationConverterLayout.visibility = View.GONE
                            fragmentOperationSumToLayout.visibility = View.GONE
                        }
                        OperationViewModel.ConverterState.WITH_CONVERTER -> {
                            fragmentOperationConverterLayout.visibility = View.VISIBLE
                            fragmentOperationSumToLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

}