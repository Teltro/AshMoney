package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ashmoney.R
import com.example.ashmoney.holder.AccountListHolder
import com.example.ashmoney.models.ui.AccountUIModel

class AccountListAdapter(
    onClick: ((item: AccountUIModel) -> Unit)? = null
) : ClickedListAdapter<AccountUIModel, AccountListHolder>(onClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_account_item, parent, false)

        return AccountListHolder(itemView)
    }

}