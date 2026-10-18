package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_orders")
data class DeviceOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingNumber: String = "",
    val customerId: Long = 0,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val customerNationalCode: String = "",
    val deviceType: String = "پنکه",
    val brandModel: String = "",
    val color: String = "",
    val serialNumber: String = "",
    val customerDescription: String = "",
    val technicianNote: String = "",
    val status: String = "PENDING", // PENDING, REPAIRING, WAITING_FOR_PART, READY, DELIVERED, RETURNED
    val partsJson: String = "[]",
    val receptionDate: Long = System.currentTimeMillis(),
    val estimatedDeliveryDate: Long = 0,
    val actualDeliveryDate: Long = 0,
    val partsCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val discount: Double = 0.0,
    val prepaidAmount: Double = 0.0,
    val finalTotalCost: Double = 0.0,
    val remainingDebt: Double = 0.0
)
