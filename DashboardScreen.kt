package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangeAction
import com.example.util.PersianDateHelper

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val orders by viewModel.allOrders.collectAsState()
    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val customers by viewModel.allCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val profile by viewModel.shopProfile.collectAsState()

    val showReceptionDialog by viewModel.showReceptionDialog.collectAsState()
    val orderToEdit by viewModel.orderToEdit.collectAsState()
    val selectedOrderForDetail by viewModel.selectedOrderForDetail.collectAsState()
    val receiptOrderToShow by viewModel.receiptOrderToShow.collectAsState()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsState()
    val selectedCustomerForLedger by viewModel.selectedCustomerForLedger.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.setSelectedTab(0) },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = "پذیرش‌ها") },
                        label = { Text("پذیرش‌ها", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = OrangeAction,
                            indicatorColor = OrangeAction,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.setSelectedTab(1) },
                        icon = { Icon(Icons.Default.People, contentDescription = "مشتریان") },
                        label = { Text("مشتریان", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = OrangeAction,
                            indicatorColor = OrangeAction,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.setSelectedTab(2) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = "حسابداری") },
                        label = { Text("حسابداری", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = OrangeAction,
                            indicatorColor = OrangeAction,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.setSelectedTab(3) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "تنظیمات") },
                        label = { Text("تنظیمات", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = OrangeAction,
                            indicatorColor = OrangeAction,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == 0) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openNewReception() },
                        containerColor = OrangeAction,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(28.dp),
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت پذیرش جدید",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header Brand Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(NavyPrimary, NavySecondary)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HomeRepairService,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.shopName,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "مدیریت پذیرش و تعمیرات لوازم خانگی (${profile.managerName})",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                when (selectedTab) {
                    0 -> {
                        // TAB 0: Orders List & Dashboard
                        OrdersTabContent(
                            orders = orders,
                            filteredOrders = filteredOrders,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            selectedStatus = selectedStatusFilter,
                            onStatusSelect = { viewModel.setStatusFilter(it) },
                            onOrderClick = { viewModel.selectOrderForDetail(it) }
                        )
                    }
                    1 -> CustomerListTab(
                        customers = customers,
                        orders = orders,
                        onCustomerClick = { viewModel.selectCustomerForLedger(it) }
                    )
                    2 -> ReportsTab(orders = orders, profile = profile)
                    3 -> SettingsTab(
                        profile = profile,
                        onEditProfileClick = { viewModel.openEditProfile() }
                    )
                }
            }
        }

        // Dialogs
        if (showReceptionDialog) {
            ReceptionDialog(
                orderToEdit = orderToEdit,
                onDismiss = { viewModel.closeReceptionDialog() },
                onSave = { name, phone, address, nationalCode, type, model, color, serial, desc, techNote, status, parts, receptionDate, estDelivery, labor, discount, prepaid ->
                    viewModel.saveOrder(
                        id = orderToEdit?.id ?: 0L,
                        customerName = name,
                        customerPhone = phone,
                        customerAddress = address,
                        customerNationalCode = nationalCode,
                        deviceType = type,
                        brandModel = model,
                        color = color,
                        serialNumber = serial,
                        customerDescription = desc,
                        technicianNote = techNote,
                        status = status,
                        parts = parts,
                        receptionDate = receptionDate,
                        estimatedDeliveryDate = estDelivery,
                        laborCost = labor,
                        discount = discount,
                        prepaidAmount = prepaid
                    )
                }
            )
        }

        selectedOrderForDetail?.let { order ->
            OrderDetailDialog(
                order = order,
                profile = profile,
                onDismiss = { viewModel.selectOrderForDetail(null) },
                onEdit = {
                    viewModel.selectOrderForDetail(null)
                    viewModel.openEditReception(order)
                },
                onDelete = { viewModel.deleteOrder(order) },
                onStatusChange = { newStatus -> viewModel.updateOrderStatus(order, newStatus) },
                onSettlePayment = { viewModel.settlePayment(order) },
                onShowReceipt = { viewModel.showReceipt(order) }
            )
        }

        receiptOrderToShow?.let { order ->
            ReceiptViewDialog(
                order = order,
                profile = profile,
                onDismiss = { viewModel.showReceipt(null) }
            )
        }

        if (showEditProfileDialog) {
            EditShopProfileDialog(
                initialProfile = profile,
                onDismiss = { viewModel.closeEditProfile() },
                onSave = { newProfile -> viewModel.saveShopProfile(newProfile) }
            )
        }

        selectedCustomerForLedger?.let { cust ->
            CustomerLedgerDialog(
                customer = cust,
                orders = orders,
                profile = profile,
                onDismiss = { viewModel.selectCustomerForLedger(null) },
                onRecordPayment = { orderId, amount -> viewModel.recordPayment(orderId, amount) }
            )
        }
    }
}

@Composable
fun OrdersTabContent(
    orders: List<DeviceOrderEntity>,
    filteredOrders: List<DeviceOrderEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedStatus: String,
    onStatusSelect: (String) -> Unit,
    onOrderClick: (DeviceOrderEntity) -> Unit
) {
    val pendingCount = orders.count { it.status == "PENDING" }
    val repairingCount = orders.count { it.status == "REPAIRING" }
    val readyCount = orders.count { it.status == "READY" }
    val deliveredCount = orders.count { it.status == "DELIVERED" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Quick Stat Cards
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    StatCard("کل دستگاه‌ها", orders.size.toString(), NavyPrimary, Icons.Default.ListAlt)
                }
                item {
                    StatCard("در انتظار بررسی", pendingCount.toString(), Color(0xFFE65100), Icons.Default.Build)
                }
                item {
                    StatCard("در حال تعمیر", repairingCount.toString(), Color(0xFF1565C0), Icons.Default.ElectricalServices)
                }
                item {
                    StatCard("آماده تحویل", readyCount.toString(), Color(0xFF2E7D32), Icons.Default.HomeRepairService)
                }
                item {
                    StatCard("تحویل داده‌شده", deliveredCount.toString(), Color(0xFF546E7A), Icons.Default.ListAlt)
                }
            }
        }

        // Search Bar
        item {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("جستجو با نام مشتری، شماره موبایل، نوع دستگاه، شماره قبض...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangeAction) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "پاک کردن")
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAction,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }
        }

        // Filter Chips
        item {
            val filterOptions = listOf(
                "ALL" to "همه",
                "PENDING" to "در انتظار",
                "REPAIRING" to "در حال تعمیر",
                "WAITING_FOR_PART" to "منتظر قطعه",
                "READY" to "آماده تحویل",
                "DELIVERED" to "تحویل شده",
                "DEBTORS" to "دارای بدهی"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filterOptions) { (key, label) ->
                    FilterChip(
                        selected = selectedStatus == key,
                        onClick = { onStatusSelect(key) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangeAction,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Orders List
        if (filteredOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HomeRepairService,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedStatus != "ALL") "موردی با این مشخصات یافت نشد" else "هنوز دستگاهی در سیستم پذیرش نشده است",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "برای شروع، دکمه «ثبت پذیرش جدید» را لمس کنید",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                OrderListItem(order = order, onClick = { onOrderClick(order) })
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: String, color: Color, icon: ImageVector) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.width(115.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        }
    }
}

@Composable
fun OrderListItem(order: DeviceOrderEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Tracking Number + Date + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NavyPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(order.trackingNumber, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        PersianDateHelper.formatPersian(order.receptionDate),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Device & Customer Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.deviceType} ${order.brandModel} ${if (order.color.isNotBlank()) "(${order.color})" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "مشتری: ${order.customerName} - ${order.customerPhone}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Fault preview
            if (order.customerDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ایراد: ${order.customerDescription}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Cost and Debt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "کل: ${PersianDateHelper.formatPrice(order.finalTotalCost)} تومان",
                    fontSize = 12.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                if (order.remainingDebt > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(OrangeAction.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "بدهی: ${PersianDateHelper.formatPrice(order.remainingDebt)} تومان",
                            color = OrangeAction,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "تسویه کامل",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
