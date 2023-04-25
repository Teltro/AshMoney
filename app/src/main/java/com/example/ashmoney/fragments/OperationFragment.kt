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
import com.example.ashmoney.utils.*
import com.example.ashmoney.viewmodels.OperationViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OperationFragment : Fragment() {

    private companion object {
        const val OPERATION_ID_KEY = "operationId"
    }

    private val viewModel: OperationViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentOperationBinding

    private lateinit var operationTypeAdapter: OperationTypeRadioAdapter
    private lateinit var accountFromAdapter: AccountRadioAdapter
    private lateinit var accountToAdapter: AccountRadioAdapter
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

        bindSelectedOperationTypeForSave()

        setupFieldListeners()

        startStateManaging()
        startOperationTypeStateManaging()

        createTextListenersForConverterFields()
        setupConvertLayoutEnable()
        startConverterStateManaging()
    }

    private fun bindSelectedOperationTypeForSave() {
        lifecycleScope.launch {
            launch {
                viewModel.selectedOperationType.collect { viewModel.data.operationType = it }
            }
            launch {
                viewModel.selectedFromAccount.collect {
                    viewModel.data.accountFrom = it
                    //currencyAdapter.selectedItem = null
                    binding.fragmentOperationOperationConvertFromCurrencyTextView.text =
                        it?.activeCurrencyName
                    binding.fragmentOperationOperationSumFromCurrencyTextView.text =
                        it?.activeCurrencyName
                }
            }
            launch {
                viewModel.selectedToAccount.collect { viewModel.data.accountTo = it }
            }
            launch {
                viewModel.selectedCurrency.collect {
                    viewModel.data.currency = it

                    binding.fragmentOperationOperationConvertToCurrencyTextView.text =
                        it?.name
                    binding.fragmentOperationOperationSumToCurrencyTextView.text =
                        it?.name
                }
            }
            launch {
                viewModel.currencyList.collect {
                    if (!it.contains(viewModel.selectedCurrency.value))
                        viewModel.selectedCurrency.value = null
                }
            }
        }
    }

    private fun setupOperationTypeList() {
        operationTypeAdapter = OperationTypeRadioAdapter {
            viewModel.selectedOperationType.value = it
        }
        setupDefaultHorizontalList(
            binding.fragmentOperationOperationTypeRecyclerView,
            operationTypeAdapter
        )

        lifecycleScope.launch {
            viewModel.operationTypeList.collect { operationTypeList ->
                if (viewModel.state.value == OperationViewModel.State.CREATE) {
                    val operationSelectedType = viewModel.selectedOperationType.value

                    if (operationTypeList.isNotEmpty() && (operationSelectedType == null || !operationTypeList.contains(
                            operationSelectedType
                        ))
                    )
                        operationTypeAdapter.selectedItem = operationTypeList[0]
                }
                operationTypeAdapter.submitList(operationTypeList)
            }
        }
    }

    private fun setupAccountFromList() {
        accountFromAdapter = AccountRadioAdapter {
            //viewModel.data.accountFrom = it
            viewModel.selectedFromAccount.value = it
        }
        setupDefaultHorizontalList(
            binding.fragmentOperationFromAccountRecyclerView,
            accountFromAdapter
        )

        lifecycleScope.launch {
            viewModel.accountList.collect(accountFromAdapter::submitList)
        }

    }

    private fun setupAccountToList() {
        accountToAdapter = AccountRadioAdapter {
            //viewModel.data.accountTo = it
            viewModel.selectedToAccount.value = it
        }
        setupDefaultHorizontalList(binding.fragmentOperationToAccountRecyclerView, accountToAdapter)

        lifecycleScope.launch {
            viewModel.accountList.collect(accountToAdapter::submitList)
        }
    }

    private fun setupOperationCategoryList() {
        operationCategoryAdapter =
            OperationCategoryRadioAdapter {
                viewModel.data.operationCategory = it
            }
        setupDefaultHorizontalList(
            binding.fragmentOperationOperationCategoryRecyclerView,
            operationCategoryAdapter
        )

        lifecycleScope.launch {
            viewModel.operationCategoryList.collect(operationCategoryAdapter::submitList)
        }
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.selectedCurrency.value = it
        }

        setupDefaultHorizontalList(
            binding.fragmentOperationOperationCurrencyRecyclerView,
            currencyAdapter
        )

        lifecycleScope.launch {
            viewModel.currencyList.collect(currencyAdapter::submitList)
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

    private fun setupFieldListeners() {
        binding.fragmentOperationOperationNameEditText.doAfterTextChanged {
            viewModel.data.name = it.toString()
        }

        /*binding.fragmentOperationOperationSumEditText.doAfterTextChanged {
            viewModel.data.run {
                if (it != null && it.isNotEmpty())
                    amountValue = it.toString().toDouble()
                else
                    amountValue = 0.0
            }

        }*/

        binding.fragmentOperationOperationNoteEditText.doAfterTextChanged {
            viewModel.data.note = it.toString()
        }
    }

    private fun startOperationTypeStateManaging() {
        lifecycleScope.launch {
            viewModel.selectedOperationType.collect { operationType ->
                operationType?.let {
                    when (operationType.id) {
                        OperationType.INCOME.id -> handleOperationTypeIncomeState()
                        OperationType.EXPENSE.id -> handleOperationTypeExpenseState()
                        OperationType.TRANSFER.id -> handleOperationTypeTransferState()
                    }

                    if (viewModel.state.value == OperationViewModel.State.CREATE) {
                        accountFromAdapter.selectedItem = null
                        accountToAdapter.selectedItem = null
                        operationCategoryAdapter.selectedItem = null
                        currencyAdapter.selectedItem = null
                    }
                } ?: handleOperationTypeNoneState()
            }
        }
    }

    private fun handleOperationTypeNoneState() {
        binding.fragmentOperationFromAccountLayout.visibility = View.GONE
        binding.fragmentOperationToAccountLayout.visibility = View.GONE
        binding.fragmentOperationOperationCategoryLayout.visibility = View.GONE
    }

    private fun handleOperationTypeIncomeState() {
        binding.fragmentOperationFromAccountLayout.visibility = View.GONE
        binding.fragmentOperationToAccountLayout.visibility = View.VISIBLE
        binding.fragmentOperationOperationCategoryLayout.visibility = View.VISIBLE
    }

    private fun handleOperationTypeExpenseState() {
        binding.fragmentOperationFromAccountLayout.visibility = View.VISIBLE
        binding.fragmentOperationToAccountLayout.visibility = View.GONE
        binding.fragmentOperationOperationCategoryLayout.visibility = View.VISIBLE
    }

    private fun handleOperationTypeTransferState() {
        binding.fragmentOperationFromAccountLayout.visibility = View.VISIBLE
        binding.fragmentOperationToAccountLayout.visibility = View.VISIBLE
        binding.fragmentOperationOperationCategoryLayout.visibility = View.GONE
    }

    private fun startStateManaging() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                setVisualDataFromViewModel()

                when (state) {
                    OperationViewModel.State.NONE, OperationViewModel.State.INIT -> handleNoneState()
                    OperationViewModel.State.INFO -> handleInfoState()
                    OperationViewModel.State.CREATE -> handleCreateState()
                }
            }
        }
    }

    private fun setVisualDataFromViewModel() {
        viewModel.data.let { data ->
            with(binding) {
                operationTypeAdapter.selectedItem = data.operationType
                accountFromAdapter.selectedItem = data.accountFrom
                accountToAdapter.selectedItem = data.accountTo
                operationCategoryAdapter.selectedItem = data.operationCategory
                currencyAdapter.selectedItem = data.currency

                /*data.amountValue?.let {
                    fragmentOperationOperationSumEditText.text = it.toString().toEditable()
                } ?: run { fragmentOperationOperationSumEditText.text = "".toEditable() }*/
                data.name?.let {
                    fragmentOperationOperationNameEditText.text = it.toEditable()
                }
                data.note?.let {
                    fragmentOperationOperationNoteEditText.text = it.toEditable()
                } ?: run { fragmentOperationOperationNoteEditText.text = "".toEditable() }
                data.dateTime?.let {
                    fragmentOperationOperationDateTimeEditText.text = it.fromIsoToDate()?.toDefaultString()?.toEditable()
                }
            }
        }
    }

    private fun handleNoneState() {
        binding.run {
            /*fragmentOperationFromAccountLayout.setEnabledForAll(false)
            fragmentOperationToAccountLayout.setEnabledForAll(false)
            fragmentOperationOperationCategoryLayout.setEnabledForAll(false)
            fragmentOperationOperationNameLayout.setEnabledForAll(false)
            fragmentOperationOperationSumLayout.setEnabledForAll(false)
            fragmentOperationOperationNoteLayout.setEnabledForAll(false)*/

            fragmentOperationOperationDateTimeLayout.setEnabledForAll(false)
            fragmentOperationOperationDateTimeLayout.visibility = View.GONE

            fragmentOperationPrimaryActionButton.text = ""
            fragmentOperationSecondaryActionButton.text = ""
            fragmentOperationPrimaryActionButton.setOnClickListener(null)
            fragmentOperationSecondaryActionButton.setOnClickListener(null)
        }
    }

    private fun handleInfoState() {
        binding.run {
            /*fragmentOperationFromAccountLayout.setEnabledForAll(false)
            fragmentOperationToAccountLayout.setEnabledForAll(false)
            fragmentOperationOperationCategoryLayout.setEnabledForAll(false)
            fragmentOperationOperationNameLayout.setEnabledForAll(false)
            fragmentOperationOperationSumLayout.setEnabledForAll(false)
            fragmentOperationOperationNoteLayout.setEnabledForAll(false)*/

            fragmentOperationOperationDateTimeLayout.setEnabledForAll(false)
            fragmentOperationOperationDateTimeLayout.visibility = View.VISIBLE

            fragmentOperationPrimaryActionButton.text = "Удалить"
            fragmentOperationSecondaryActionButton.text = "Назад"
            fragmentOperationPrimaryActionButton.setOnClickListener {
                viewModel.data.operationId?.let {
                    // TODO add confirm window
                    viewModel.deleteOperation(it)
                    navController.popBackStack()
                }
            }
            fragmentOperationSecondaryActionButton.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    private fun handleCreateState() {
        binding.run {
            /*fragmentOperationFromAccountLayout.setEnabledForAll(true)
            fragmentOperationToAccountLayout.setEnabledForAll(true)
            fragmentOperationOperationCategoryLayout.setEnabledForAll(true)
            fragmentOperationOperationNameLayout.setEnabledForAll(true)
            fragmentOperationOperationSumLayout.setEnabledForAll(true)
            fragmentOperationOperationNoteLayout.setEnabledForAll(true)*/

            fragmentOperationOperationDateTimeLayout.setEnabledForAll(false)
            fragmentOperationOperationDateTimeLayout.visibility = View.GONE

            fragmentOperationPrimaryActionButton.text = "Создать"
            fragmentOperationSecondaryActionButton.text = "Отменить"
            fragmentOperationPrimaryActionButton.setOnClickListener {
                // TODO check fields?
                viewModel.getOperationFromCurrentData()?.let {
                    viewModel.insertOperation(it)
                    navController.popBackStack()
                } ?: throw IllegalStateException() // TODO handle error(mb toast?)
            }
            fragmentOperationSecondaryActionButton.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    private fun createTextListenersForConverterFields() {
        with(binding) {
            fragmentOperationSumFromValueEditText.doAfterTextChanged {
                if (viewModel.converterState.value == OperationViewModel.ConverterState.WITHOUT_CONVERTER) {
                    viewModel.data.amountValue = it.toString().toDoubleOrNull()
                } else if (viewModel.converterState.value == OperationViewModel.ConverterState.WITH_CONVERTER) {
                    val inputValue = it.toString().toDoubleOrNull()
                    val coeff =
                        fragmentOperationConverterToValueEditText.text.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumToValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.amountValue = resultValue
                }
            }
            fragmentOperationConverterToValueEditText.doAfterTextChanged {
                if (viewModel.converterState.value == OperationViewModel.ConverterState.WITH_CONVERTER) {
                    val inputValue =
                        fragmentOperationSumFromValueEditText.text.toString().toDoubleOrNull()
                    val coeff = it.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumToValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.currencyExchangeRate = inputValue
                }
            }
        }
    }

    /*private fun createTextListenersForConverterFields() {
        with(binding) {
            fragmentOperationSumFromValueEditText.doAfterTextChanged {
                if (
                    viewModel.converterState.value == OperationViewModel.ConverterState.ONLY_ONE_CURRENCY ||
                    viewModel.converterState.value == OperationViewModel.ConverterState.CHANGE_TARGET_CURRENCY
                ) {
                    viewModel.data.amountValue = it.toString().toDoubleOrNull()
                }
            }
            fragmentOperationConverterToValueEditText.doAfterTextChanged {
                if (viewModel.converterState.value == OperationViewModel.ConverterState.TRANSFER_FROM_CURRENCY) {
                    val inputValue =
                        fragmentOperationSumFromValueEditText.text.toString().toDoubleOrNull()
                    val coeff = it.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumToValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.currencyExchangeRate = inputValue
                }
            }

            fragmentOperationSumFromValueEditText.doAfterTextChanged {
                if (viewModel.converterState.value == OperationViewModel.ConverterState.TRANSFER_FROM_CURRENCY) {
                    val inputValue = it.toString().toDoubleOrNull()
                    val coeff =
                        fragmentOperationConverterToValueEditText.text.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumToValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.amountValue = resultValue
                }
            }

            fragmentOperationConverterFromValueEditText.doAfterTextChanged {
                if (
                    viewModel.converterState.value == OperationViewModel.ConverterState.CHANGE_ANOTHER_CURRENCY ||
                    viewModel.converterState.value == OperationViewModel.ConverterState.TRANSFER_TO_CURRENCY
                ) {
                    val inputValue =
                        fragmentOperationSumToValueEditText.text.toString().toDoubleOrNull()
                    val coeff = it.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumFromValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.currencyExchangeRate = inputValue// or reverse?
                }
            }

            fragmentOperationSumToValueEditText.doAfterTextChanged {
                if (
                    viewModel.converterState.value == OperationViewModel.ConverterState.CHANGE_ANOTHER_CURRENCY ||
                    viewModel.converterState.value == OperationViewModel.ConverterState.TRANSFER_TO_CURRENCY
                ) {
                    val inputValue = it.toString().toDoubleOrNull()
                    val coeff =
                        fragmentOperationConverterFromValueEditText.text.toString().toDoubleOrNull()
                    val resultValue = if (inputValue != null && coeff != null)
                        inputValue * coeff
                    else
                        0.0

                    fragmentOperationSumFromValueEditText.text = resultValue.toString().toEditable()

                    viewModel.data.amountValue = inputValue
                }
            }
        }
    }*/

    /*private fun startConverterStateManaging() {
        lifecycleScope.launch {
            viewModel.converterState.collect {
                when (it) {
                    OperationViewModel.ConverterState.NONE,
                    OperationViewModel.ConverterState.ONLY_ONE_CURRENCY,
                    OperationViewModel.ConverterState.CHANGE_TARGET_CURRENCY -> handleConverterDefaultState()

                    OperationViewModel.ConverterState.CHANGE_ANOTHER_CURRENCY,
                    OperationViewModel.ConverterState.TRANSFER_TO_CURRENCY -> handleConverterChangeAnotherCurrencyOrTransferToCurrencyState()

                    OperationViewModel.ConverterState.TRANSFER_FROM_CURRENCY -> handleConverterTransferFromCurrencyState()
                }
            }
        }
    }*/

    private fun setupConvertLayoutEnable() {
        with(binding) {
            fragmentOperationConverterFromLayout.setEnabledForAll(false)
            fragmentOperationConverterToLayout.setEnabledForAll(true)
            fragmentOperationSumFromLayout.setEnabledForAll(true)
            fragmentOperationSumToLayout.setEnabledForAll(false)
        }
    }


    private fun startConverterStateManaging() {
        lifecycleScope.launch {
            viewModel.converterState.collect {
                when (it) {
                    OperationViewModel.ConverterState.NONE,
                    OperationViewModel.ConverterState.WITHOUT_CONVERTER -> handleConverterWithoutState()
                    OperationViewModel.ConverterState.WITH_CONVERTER -> handleConvertWithState()
                }
            }
        }
    }

    private fun handleConverterWithoutState() {
        with(binding) {
            fragmentOperationConverterLayout.visibility = View.GONE
            fragmentOperationSumToLayout.visibility = View.GONE

            fragmentOperationSumFromValueEditText.text =
                viewModel.data.amountValue.toString().toEditable()
        }
    }

    private fun handleConvertWithState() {
        with(binding) {
            fragmentOperationConverterLayout.visibility = View.VISIBLE
            fragmentOperationSumToLayout.visibility = View.VISIBLE

            fragmentOperationConverterFromValueEditText.text =
                "1.0".toEditable()
            fragmentOperationConverterToValueEditText.text = viewModel.data.currencyExchangeRate.let {
                if (it == null)
                    "1.0".toEditable()
                else
                    roundDouble(it).toString().toEditable()
            }
            fragmentOperationSumFromValueEditText.text =
                viewModel.data.amountValue.toString().toEditable()
            fragmentOperationSumToValueEditText.text = "0.0".toEditable()
        }
    }

    private fun handleConverterDefaultState() {
        with(binding) {
            fragmentOperationConverterLayout.visibility = View.GONE
            fragmentOperationSumToLayout.visibility = View.GONE
            fragmentOperationSumFromLayout.setEnabledForAll(true)

            fragmentOperationSumFromValueEditText.text =
                viewModel.data.amountValue.toString().toEditable()
        }
    }

    private fun handleConverterChangeAnotherCurrencyOrTransferToCurrencyState() {
        with(binding) {
            fragmentOperationConverterLayout.visibility = View.VISIBLE
            fragmentOperationSumToLayout.visibility = View.VISIBLE
            fragmentOperationConverterFromLayout.setEnabledForAll(true)
            fragmentOperationConverterToLayout.setEnabledForAll(false)
            fragmentOperationSumFromLayout.setEnabledForAll(false)
            fragmentOperationSumToLayout.setEnabledForAll(true)

            fragmentOperationConverterFromValueEditText.text = viewModel.data.currencyExchangeRate.let {
                if (it == null)
                    "0.0".toEditable()
                else
                    roundDouble(it).toString().toEditable()
            }
            fragmentOperationConverterToValueEditText.text =
                "1.0".toEditable()
            fragmentOperationSumFromValueEditText.text = "0.0".toEditable()
            fragmentOperationSumToValueEditText.text =
                viewModel.data.amountValue.toString().toEditable()
        }
    }

    private fun handleConverterTransferFromCurrencyState() {
        with(binding) {
            fragmentOperationConverterLayout.visibility = View.VISIBLE
            fragmentOperationSumToLayout.visibility = View.VISIBLE
            fragmentOperationConverterFromLayout.setEnabledForAll(true)
            fragmentOperationConverterToLayout.setEnabledForAll(false)
            fragmentOperationSumFromLayout.setEnabledForAll(false)
            fragmentOperationSumToLayout.setEnabledForAll(true)

            fragmentOperationConverterFromValueEditText.text =
                "1.0".toEditable()
            fragmentOperationConverterToValueEditText.text = viewModel.data.currencyExchangeRate.let {
                if (it == null)
                    "0.0".toEditable()
                else
                    roundDouble(it).toString().toEditable()
            }
            fragmentOperationSumFromValueEditText.text =
                viewModel.data.amountValue.toString().toEditable()
            fragmentOperationSumToValueEditText.text = "0.0".toEditable()
        }
    }

    private fun roundDouble(value: Double): Double {
        if (value < 1) {
            return (value * 100).roundToInt() / 100.0
        }
        return value
    }


}