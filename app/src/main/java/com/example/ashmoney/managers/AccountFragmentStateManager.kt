/*
package com.example.ashmoney.managers

import com.example.ashmoney.adapters.CurrencyAdapter
import com.example.ashmoney.databinding.FragmentAccountBinding
import com.example.ashmoney.fragments.AccountFragment
import com.example.ashmoney.models.Account
import com.example.ashmoney.utils.toEditable
import com.example.ashmoney.viewmodels.AccountViewModel

class AccountFragmentStateManager(
    private val binding: FragmentAccountBinding,
    private val viewModel: AccountViewModel
) {

    var state: AccountFragment.State?
        get() = viewModel.state
        set(value) {
            value?.let {
                handleNewState(it)
            } ?: handleNewState(AccountFragment.State.INFO)
        }

    private fun handleNewState(state: AccountFragment.State) {
        if (viewModel.state == state)
            return

        viewModel.state = state

        when (state) {
            AccountFragment.State.INFO -> setInfoState()
            AccountFragment.State.CREATE -> setCreateState()
            AccountFragment.State.UPDATE -> setUpdateState()
        }

    }

    private fun setInfoState() {
        binding.run {
            viewModel.account?.let { account ->
                fragmentAccountNameTextView.text = account.name.toEditable()
                fragmentAccountStartAmountTextView.text =
                    account.amountValue.toString().toEditable()
                fragmentAccountNoteTextView.text = "".toEditable()

                fragmentAccountNoteTextView.isEnabled = false
                fragmentAccountNameTextView.isEnabled = false
                fragmentAccountStartAmountTextView.isEnabled = false
                (fragmentAccountCurrencyRecyclerView.adapter as? CurrencyAdapter)?.isEnabled = false

                fragmentAccountPrimaryActionButton.text = "Изменить"
                fragmentAccountSecondaryActionButton.text = "Удалить"
            } ?: throw IllegalStateException()
        }
    }

    private fun setCreateState() {
        binding.run {
            fragmentAccountNameTextView.text = "".toEditable()
            fragmentAccountNoteTextView.text = "".toEditable()
            fragmentAccountStartAmountTextView.text = 0.0.toString().toEditable()

            fragmentAccountNameTextView.isEnabled = true
            fragmentAccountNoteTextView.isEnabled = true
            fragmentAccountStartAmountTextView.isEnabled = true
            (fragmentAccountCurrencyRecyclerView.adapter as? CurrencyAdapter)?.isEnabled = true

            fragmentAccountPrimaryActionButton.text = "Создать"
            fragmentAccountSecondaryActionButton.text = "Отмена"
        }
    }

    private fun setUpdateState() {
        binding.run {
            viewModel.account?.let {
                fragmentAccountNameTextView.text = viewModel.accountName.toEditable()
                fragmentAccountStartAmountTextView.text =
                    viewModel.amountValue.toString().toEditable()
                fragmentAccountNoteTextView.text = "".toEditable()

                fragmentAccountNameTextView.isEnabled = true
                fragmentAccountNoteTextView.isEnabled = true
                fragmentAccountStartAmountTextView.isEnabled = true
                (fragmentAccountCurrencyRecyclerView.adapter as? CurrencyAdapter)?.isEnabled = true

                fragmentAccountPrimaryActionButton.text = "Применить"
                fragmentAccountSecondaryActionButton.text = "Отмена"
            } ?: throw IllegalStateException()
        }
    }


}*/
