package com.example.ashmoney.utils

enum class OperationType(val id: Int) {
    INCOME(1),
    EXPENSE(2),
    TRANSFER(3)
    ;

    companion object {
        fun fromId(id: Int) = values().firstOrNull { it.id == id}
    }
}