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
import com.example.ashmoney.viewmodels.AccountViewModel
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private companion object {
        const val ACCOUNT_ID_KEY = "accountId"
    }

    private val viewModel: AccountViewModel by viewModels()

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
        startStateManaging()
    }

    private fun setupIconList() {
        iconAdapter = IconRadioAdapter {
            viewModel.data.icon = it
        }
        setupDefaultHorizontalList(binding.fragmentAccountIconRecyclerView, iconAdapter)
        lifecycleScope.launch {
            viewModel.iconList.collect(iconAdapter::submitList)
        }
        //viewModel.iconList.observe(viewLifecycleOwner, iconAdapter::submitList)
    }

    private fun setupIconColorList() {
        iconColorAdapter = IconColorRadioAdapter {
            viewModel.data.iconColor = it
            iconAdapter.iconColor = it
        }
        setupDefaultHorizontalList(binding.fragmentAccountIconColorRecyclerView, iconColorAdapter)
        lifecycleScope.launch {
            viewModel.iconColorList.collect(iconColorAdapter::submitList)
        }
        //viewModel.iconColorList.observe(viewLifecycleOwner, iconColorAdapter::submitList)
    }

    private fun setupCurrencyList() {
        currencyAdapter = CurrencyRadioAdapter {
            viewModel.data.currency = it
        }
        setupDefaultHorizontalList(binding.fragmentAccountCurrencyRecyclerView, currencyAdapter)

        lifecycleScope.launch {
            viewModel.currencyList.collect(currencyAdapter::submitList)
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
            viewModel.data.accountName = it.toString()
        }
        binding.fragmentAccountStartAmountTextView.doAfterTextChanged {
            viewModel.data.run {
                if (it != null && it.isNotEmpty())
                    amountValue = it.toString().toDouble()
                else
                    amountValue = 0.0;
            }
        }
        binding.fragmentAccountNoteTextView.doAfterTextChanged {
            viewModel.data.note = it.toString()
        }
    }

    private fun startStateManaging() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state != AccountViewModel.State.INIT && state != AccountViewModel.State.NONE) {
                    setVisualDataFromViewModel()
                }

                when (state) {
                    AccountViewModel.State.NONE, AccountViewModel.State.INIT, AccountViewModel.State.ERROR -> handleNoneState()
                    AccountViewModel.State.INFO -> handleInfoState()
                    AccountViewModel.State.CREATE -> handleCreateState()
                    AccountViewModel.State.UPDATE -> handleUpdateState()
                }
            }
        }
    }

    private fun setVisualDataFromViewModel() {
        viewModel.data.let { data ->
            with(binding) {
                data.accountName?.let { fragmentAccountNameTextView.setText(it) }
                data.amountValue?.let { fragmentAccountStartAmountTextView.setText(it.toString()) }
                data.note?.let { fragmentAccountNoteTextView.setText(it) }
                data.currency?.let {
                    if (currencyAdapter.selectedItem != it) currencyAdapter.selectedItem = it
                }
                data.icon?.let {
                    if (iconAdapter.selectedItem != it) iconAdapter.selectedItem = it
                }
                data.iconColor?.let {
                    if (iconColorAdapter.selectedItem != it) iconColorAdapter.selectedItem = it
                }
            }
        }
    }

    private fun handleNoneState() {
        binding.run {
            fragmentAccountPrimaryActionButton.text = ""
            fragmentAccountSecondaryActionButton.text = ""
            fragmentAccountPrimaryActionButton.setOnClickListener(null)
            fragmentAccountSecondaryActionButton.setOnClickListener(null)
        }
    }

    private fun handleInfoState() {
        binding.run {
            fragmentAccountPrimaryActionButton.text = "Изменить"
            fragmentAccountSecondaryActionButton.text = "Удалить"
            fragmentAccountPrimaryActionButton.setOnClickListener {
                viewModel.state.value = AccountViewModel.State.UPDATE
            }
            fragmentAccountSecondaryActionButton.setOnClickListener {
                viewModel.data.accountId?.let {
                    // TODO add confirm window
                    //viewModel.deleteAccount(it)
                    viewModel.deleteAccount2(it)
                    navController.popBackStack()
                }
            }
        }
    }

    private fun handleCreateState() {
        binding.run {
            fragmentAccountPrimaryActionButton.text = "Создать"
            fragmentAccountSecondaryActionButton.text = "Отменить"
            fragmentAccountPrimaryActionButton.setOnClickListener {
                // TODO check fields?
                //val account = viewModel.getAccountFromCurrentDataForUpdate()
                val account = viewModel.getAccountFromCurrentData()
                account?.let {
                    viewModel.insertAccount(it)
                    navController.popBackStack()
                } ?: throw IllegalStateException() // TODO handle error(mb toast?)
            }
            fragmentAccountSecondaryActionButton.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    private fun handleUpdateState() {
        binding.run {
            fragmentAccountPrimaryActionButton.text = "Применить"
            fragmentAccountSecondaryActionButton.text = "Отменить"
            fragmentAccountPrimaryActionButton.setOnClickListener {
                // TODO check fields?
                //val account = viewModel.getAccountFromCurrentDataForCreate()
                val account = viewModel.getAccountFromCurrentData()
                account?.let {
                    viewModel.updateAccount(it)
                    viewModel.state.value = AccountViewModel.State.INFO
                } ?: throw IllegalStateException() // TODO handle error(mb toast?)
            }
            fragmentAccountSecondaryActionButton.setOnClickListener {
                viewModel.state.value = AccountViewModel.State.INFO
            }
        }
    }

}
