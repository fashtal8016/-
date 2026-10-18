package com.example.data.model

import java.util.UUID

data class PartItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val count: Int = 1,
    val unitPrice: Double = 0.0
) {
    val totalPrice: Double get() = count * unitPrice
}
