package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val nationalCode: String = "",
    val totalOrdersCount: Int = 0,
    val totalSpent: Double = 0.0,
    val totalDebt: Double = 0.0
) {
    val fullName: String get() = "$firstName $lastName".trim()
}
