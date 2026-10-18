package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ShopProfile
import com.example.data.local.ShopProfileManager
import com.example.data.local.dao.RepairDao
import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.DeviceOrderEntity
import com.example.data.model.PartItem
import com.example.util.PartsJsonHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repairDao: RepairDao,
    private val shopProfileManager: ShopProfileManager
) : ViewModel() {

    val allOrders: StateFlow<List<DeviceOrderEntity>> = repairDao.getAllOrders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allCustomers: StateFlow<List<CustomerEntity>> = repairDao.getAllCustomers().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _shopProfile = MutableStateFlow(shopProfileManager.getProfile())
    val shopProfile = _shopProfile.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("ALL")
    val selectedStatusFilter = _selectedStatusFilter.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Orders, 1: Customers, 2: Reports, 3: Settings
    val selectedTab = _selectedTab.asStateFlow()

    // Dialog States
    private val _showReceptionDialog = MutableStateFlow(false)
    val showReceptionDialog = _showReceptionDialog.asStateFlow()

    private val _orderToEdit = MutableStateFlow<DeviceOrderEntity?>(null)
    val orderToEdit = _orderToEdit.asStateFlow()

    private val _selectedOrderForDetail = MutableStateFlow<DeviceOrderEntity?>(null)
    val selectedOrderForDetail = _selectedOrderForDetail.asStateFlow()

    private val _receiptOrderToShow = MutableStateFlow<DeviceOrderEntity?>(null)
    val receiptOrderToShow = _receiptOrderToShow.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog = _showEditProfileDialog.asStateFlow()

    private val _selectedCustomerForLedger = MutableStateFlow<CustomerEntity?>(null)
    val selectedCustomerForLedger = _selectedCustomerForLedger.asStateFlow()

    // Filtered orders
    val filteredOrders = combine(allOrders, searchQuery, selectedStatusFilter) { orders, query, statusFilter ->
        orders.filter { order ->
            val matchesQuery = query.isBlank() ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.customerPhone.contains(query, ignoreCase = true) ||
                    order.deviceType.contains(query, ignoreCase = true) ||
                    order.brandModel.contains(query, ignoreCase = true) ||
                    order.trackingNumber.contains(query, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                "ALL" -> true
                "DEBTORS" -> order.remainingDebt > 0
                else -> order.status == statusFilter
            }

            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun openNewReception() {
        _orderToEdit.value = null
        _showReceptionDialog.value = true
    }

    fun openEditReception(order: DeviceOrderEntity) {
        _orderToEdit.value = order
        _showReceptionDialog.value = true
    }

    fun closeReceptionDialog() {
        _showReceptionDialog.value = false
        _orderToEdit.value = null
    }

    fun selectOrderForDetail(order: DeviceOrderEntity?) {
        _selectedOrderForDetail.value = order
    }

    fun showReceipt(order: DeviceOrderEntity?) {
        _receiptOrderToShow.value = order
    }

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun saveShopProfile(profile: ShopProfile) {
        shopProfileManager.saveProfile(profile)
        _shopProfile.value = profile
        _showEditProfileDialog.value = false
    }

    fun selectCustomerForLedger(customer: CustomerEntity?) {
        _selectedCustomerForLedger.value = customer
    }

    fun saveOrder(
        id: Long = 0,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        customerNationalCode: String,
        deviceType: String,
        brandModel: String,
        color: String,
        serialNumber: String,
        customerDescription: String,
        technicianNote: String,
        status: String,
        parts: List<PartItem>,
        receptionDate: Long,
        estimatedDeliveryDate: Long,
        laborCost: Double,
        discount: Double,
        prepaidAmount: Double
    ) {
        viewModelScope.launch {
            val partsCost = parts.sumOf { it.totalPrice }
            val finalTotal = (partsCost + laborCost - discount).coerceAtLeast(0.0)
            val remainingDebt = (finalTotal - prepaidAmount).coerceAtLeast(0.0)
            val partsJson = PartsJsonHelper.toJson(parts)

            // Find or create customer
            var customer = repairDao.getCustomerByPhone(customerPhone.trim())
            val customerId = if (customer != null) {
                val updatedCustomer = customer.copy(
                    firstName = customerName.split(" ").firstOrNull() ?: customerName,
                    lastName = customerName.split(" ").drop(1).joinToString(" "),
                    address = customerAddress.ifBlank { customer.address },
                    nationalCode = customerNationalCode.ifBlank { customer.nationalCode }
                )
                repairDao.updateCustomer(updatedCustomer)
                customer.id
            } else {
                repairDao.insertCustomer(
                    CustomerEntity(
                        firstName = customerName.split(" ").firstOrNull() ?: customerName,
                        lastName = customerName.split(" ").drop(1).joinToString(" "),
                        phoneNumber = customerPhone.trim(),
                        address = customerAddress,
                        nationalCode = customerNationalCode
                    )
                )
            }

            if (id == 0L) {
                val totalCount = allOrders.value.size
                val trackingNumber = "MG-${1001 + totalCount}"

                val newOrder = DeviceOrderEntity(
                    trackingNumber = trackingNumber,
                    customerId = customerId,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    customerAddress = customerAddress.trim(),
                    customerNationalCode = customerNationalCode.trim(),
                    deviceType = deviceType.trim(),
                    brandModel = brandModel.trim(),
                    color = color.trim(),
                    serialNumber = serialNumber.trim(),
                    customerDescription = customerDescription.trim(),
                    technicianNote = technicianNote.trim(),
                    status = status,
                    partsJson = partsJson,
                    receptionDate = receptionDate,
                    estimatedDeliveryDate = estimatedDeliveryDate,
                    partsCost = partsCost,
                    laborCost = laborCost,
                    discount = discount,
                    prepaidAmount = prepaidAmount,
                    finalTotalCost = finalTotal,
                    remainingDebt = remainingDebt
                )
                val newId = repairDao.insertOrder(newOrder)
                val created = newOrder.copy(id = newId)
                _selectedOrderForDetail.value = created
            } else {
                val existing = _orderToEdit.value
                val updatedOrder = DeviceOrderEntity(
                    id = id,
                    trackingNumber = existing?.trackingNumber ?: "MG-$id",
                    customerId = customerId,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    customerAddress = customerAddress.trim(),
                    customerNationalCode = customerNationalCode.trim(),
                    deviceType = deviceType.trim(),
                    brandModel = brandModel.trim(),
                    color = color.trim(),
                    serialNumber = serialNumber.trim(),
                    customerDescription = customerDescription.trim(),
                    technicianNote = technicianNote.trim(),
                    status = status,
                    partsJson = partsJson,
                    receptionDate = receptionDate,
                    estimatedDeliveryDate = estimatedDeliveryDate,
                    actualDeliveryDate = if (status == "DELIVERED" && existing?.actualDeliveryDate == 0L) System.currentTimeMillis() else existing?.actualDeliveryDate ?: 0L,
                    partsCost = partsCost,
                    laborCost = laborCost,
                    discount = discount,
                    prepaidAmount = prepaidAmount,
                    finalTotalCost = finalTotal,
                    remainingDebt = remainingDebt
                )
                repairDao.updateOrder(updatedOrder)
                _selectedOrderForDetail.value = updatedOrder
            }

            closeReceptionDialog()
        }
    }

    fun updateOrderStatus(order: DeviceOrderEntity, newStatus: String) {
        viewModelScope.launch {
            val updated = order.copy(
                status = newStatus,
                actualDeliveryDate = if (newStatus == "DELIVERED") System.currentTimeMillis() else order.actualDeliveryDate
            )
            repairDao.updateOrder(updated)
            if (_selectedOrderForDetail.value?.id == order.id) {
                _selectedOrderForDetail.value = updated
            }
        }
    }

    fun settlePayment(order: DeviceOrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(
                prepaidAmount = order.finalTotalCost,
                remainingDebt = 0.0
            )
            repairDao.updateOrder(updated)
            if (_selectedOrderForDetail.value?.id == order.id) {
                _selectedOrderForDetail.value = updated
            }
        }
    }

    fun recordPayment(orderId: Long, amount: Double) {
        viewModelScope.launch {
            val order = repairDao.getOrderById(orderId) ?: return@launch
            val newPrepaid = order.prepaidAmount + amount
            val newDebt = (order.finalTotalCost - newPrepaid).coerceAtLeast(0.0)
            val updated = order.copy(
                prepaidAmount = newPrepaid,
                remainingDebt = newDebt
            )
            repairDao.updateOrder(updated)
            if (_selectedOrderForDetail.value?.id == orderId) {
                _selectedOrderForDetail.value = updated
            }
        }
    }

    fun deleteOrder(order: DeviceOrderEntity) {
        viewModelScope.launch {
            repairDao.deleteOrder(order)
            if (_selectedOrderForDetail.value?.id == order.id) {
                _selectedOrderForDetail.value = null
            }
        }
    }

    class Factory(
        private val repairDao: RepairDao,
        private val shopProfileManager: ShopProfileManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repairDao, shopProfileManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
