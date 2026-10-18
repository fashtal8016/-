package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.DeviceOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {

    // Orders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: DeviceOrderEntity): Long

    @Update
    suspend fun updateOrder(order: DeviceOrderEntity)

    @Delete
    suspend fun deleteOrder(order: DeviceOrderEntity)

    @Query("SELECT * FROM device_orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<DeviceOrderEntity>>

    @Query("SELECT * FROM device_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): DeviceOrderEntity?

    @Query("SELECT * FROM device_orders WHERE customerId = :customerId ORDER BY id DESC")
    fun getOrdersForCustomer(customerId: Long): Flow<List<DeviceOrderEntity>>

    // Customers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): CustomerEntity?
}
