package com.example.ashmoney.repositories

import com.example.ashmoney.models.Account

interface AccountRepository {
    fun getList() : List<Account>
}