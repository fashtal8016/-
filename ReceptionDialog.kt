package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.DeviceOrderEntity
import com.example.data.model.PartItem
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangeAction
import com.example.util.PartsJsonHelper
import com.example.util.PersianDateHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReceptionDialog(
    orderToEdit: DeviceOrderEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    // Customer Fields
    var customerName by remember { mutableStateOf(orderToEdit?.customerName ?: "") }
    var customerPhone by remember { mutableStateOf(orderToEdit?.customerPhone ?: "") }
    var customerAddress by remember { mutableStateOf(orderToEdit?.customerAddress ?: "") }
    var customerNationalCode by remember { mutableStateOf(orderToEdit?.customerNationalCode ?: "") }

    // Device Fields
    val popularDevices = listOf("پنکه", "مولینکس", "جاروبرقی", "سشوار", "چرخ گوشت", "مخلوط‌کن", "اتو", "آبمیوه‌گیری", "سایر")
    var selectedDeviceType by remember {
        mutableStateOf(
            if (orderToEdit != null && orderToEdit.deviceType in popularDevices) orderToEdit.deviceType
            else if (orderToEdit != null) "سایر"
            else "پنکه"
        )
    }
    var customDeviceType by remember {
        mutableStateOf(if (orderToEdit != null && orderToEdit.deviceType !in popularDevices) orderToEdit.deviceType else "")
    }
    var brandModel by remember { mutableStateOf(orderToEdit?.brandModel ?: "") }
    var deviceColor by remember { mutableStateOf(orderToEdit?.color ?: "") }
    var serialNumber by remember { mutableStateOf(orderToEdit?.serialNumber ?: "") }
    var customerDescription by remember { mutableStateOf(orderToEdit?.customerDescription ?: "") }
    var technicianNote by remember { mutableStateOf(orderToEdit?.technicianNote ?: "") }

    // Status
    var status by remember { mutableStateOf(orderToEdit?.status ?: "PENDING") }
    val statuses = listOf(
        "PENDING" to "در انتظار بررسی",
        "REPAIRING" to "در حال تعمیر",
        "WAITING_FOR_PART" to "منتظر قطعه",
        "READY" to "آماده تحویل",
        "DELIVERED" to "تحویل داده‌شده",
        "RETURNED" to "مرجوعی"
    )

    // Dates
    val receptionDate = remember { orderToEdit?.receptionDate ?: System.currentTimeMillis() }
    var estimatedDaysOffset by remember { mutableStateOf(3) }
    val estimatedDeliveryDate = remember(estimatedDaysOffset) {
        orderToEdit?.estimatedDeliveryDate ?: PersianDateHelper.addDaysToTimestamp(estimatedDaysOffset, receptionDate)
    }

    // Parts List
    val parts = remember {
        mutableStateListOf<PartItem>().apply {
            if (orderToEdit != null) {
                addAll(PartsJsonHelper.fromJson(orderToEdit.partsJson))
            }
        }
    }

    // Financials
    var laborCostStr by remember { mutableStateOf(orderToEdit?.laborCost?.toLong()?.toString() ?: "0") }
    var discountStr by remember { mutableStateOf(orderToEdit?.discount?.toLong()?.toString() ?: "0") }
    var prepaidStr by remember { mutableStateOf(orderToEdit?.prepaidAmount?.toLong()?.toString() ?: "0") }

    val partsTotalCost by remember { derivedStateOf { parts.sumOf { it.totalPrice } } }
    val laborCost by remember { derivedStateOf { laborCostStr.toDoubleOrNull() ?: 0.0 } }
    val discount by remember { derivedStateOf { discountStr.toDoubleOrNull() ?: 0.0 } }
    val prepaidAmount by remember { derivedStateOf { prepaidStr.toDoubleOrNull() ?: 0.0 } }
    val finalTotal by remember { derivedStateOf { (partsTotalCost + laborCost - discount).coerceAtLeast(0.0) } }
    val remainingDebt by remember { derivedStateOf { (finalTotal - prepaidAmount).coerceAtLeast(0.0) } }

    var isError by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Top Bar Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyPrimary)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (orderToEdit == null) "فرم پذیرش جدید دستگاه" else "ویرایش پذیرش (${orderToEdit.trackingNumber})",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "تعمیرگاه لوازم خانگی میرغفاری",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    // Content Scroll
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // SECTION 1: Customer Details
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = OrangeAction)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("اطلاعات مشتری", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = { customerName = it },
                                        label = { Text("نام و نام خانوادگی مشتری *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = isError && customerName.isBlank(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = customerPhone,
                                        onValueChange = { customerPhone = it },
                                        label = { Text("شماره همراه (جهت پیامک و واتساپ) *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        isError = isError && customerPhone.isBlank(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = customerAddress,
                                            onValueChange = { customerAddress = it },
                                            label = { Text("آدرس (اختیاری)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = customerNationalCode,
                                            onValueChange = { customerNationalCode = it },
                                            label = { Text("کد ملی (اختیاری)") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }

                        // SECTION 2: Device Details
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Build, contentDescription = null, tint = OrangeAction)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("مشخصات دستگاه تعمیری", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("نوع دستگاه:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        popularDevices.forEach { device ->
                                            FilterChip(
                                                selected = selectedDeviceType == device,
                                                onClick = { selectedDeviceType = device },
                                                label = { Text(device) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = OrangeAction,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    if (selectedDeviceType == "سایر") {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = customDeviceType,
                                            onValueChange = { customDeviceType = it },
                                            label = { Text("نام نوع دستگاه جدید *") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = brandModel,
                                            onValueChange = { brandModel = it },
                                            label = { Text("برند / مدل (مثلاً پارس خزر)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = deviceColor,
                                            onValueChange = { deviceColor = it },
                                            label = { Text("رنگ دستگاه") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = serialNumber,
                                        onValueChange = { serialNumber = it },
                                        label = { Text("شماره سریال / پلاک (اختیاری)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = customerDescription,
                                        onValueChange = { customerDescription = it },
                                        label = { Text("شرح ایراد به گفته مشتری (مثلاً موتور نمی‌چرخد) *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2,
                                        isError = isError && customerDescription.isBlank()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = technicianNote,
                                        onValueChange = { technicianNote = it },
                                        label = { Text("یادداشت تعمیرکار (لوازم همراه، شکستگی ظاهری...)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 1
                                    )
                                }
                            }
                        }

                        // SECTION 3: Parts Consumed
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("قطعات مصرفی و یدکی", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                                        OutlinedButton(
                                            onClick = {
                                                parts.add(PartItem(name = "", count = 1, unitPrice = 0.0))
                                            },
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("افزودن قطعه", fontSize = 12.sp)
                                        }
                                    }

                                    if (parts.isEmpty()) {
                                        Text(
                                            "قطعه‌ای اضافه نشده است (در صورت عدم مصرف قطعه خالی بگذارید)",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Parts Items
                        itemsIndexed(parts) { index, part ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        OutlinedTextField(
                                            value = part.name,
                                            onValueChange = { newName ->
                                                parts[index] = part.copy(name = newName)
                                            },
                                            label = { Text("نام قطعه (مثلاً بلبرینگ، خازن)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row {
                                            OutlinedTextField(
                                                value = part.count.toString(),
                                                onValueChange = { newCount ->
                                                    parts[index] = part.copy(count = newCount.toIntOrNull() ?: 1)
                                                },
                                                label = { Text("تعداد") },
                                                modifier = Modifier.weight(0.8f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            OutlinedTextField(
                                                value = if (part.unitPrice > 0) part.unitPrice.toLong().toString() else "",
                                                onValueChange = { newPrice ->
                                                    parts[index] = part.copy(unitPrice = newPrice.toDoubleOrNull() ?: 0.0)
                                                },
                                                label = { Text("قیمت واحد (تومان)") },
                                                modifier = Modifier.weight(1.5f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true
                                            )
                                        }
                                    }
                                    IconButton(onClick = { parts.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                                    }
                                }
                            }
                        }

                        // SECTION 4: Financials & Costs
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = OrangeAction)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("محاسبه هزینه‌ها و بیعانه", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(NavyPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("جمع هزینه قطعات:", fontSize = 13.sp)
                                        Text("${PersianDateHelper.formatPrice(partsTotalCost)} تومان", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = laborCostStr,
                                            onValueChange = { laborCostStr = it },
                                            label = { Text("هزینه اجرت تعمیر (تومان)") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = discountStr,
                                            onValueChange = { discountStr = it },
                                            label = { Text("تخفیف (تومان)") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = prepaidStr,
                                        onValueChange = { prepaidStr = it },
                                        label = { Text("پیش‌پرداخت / بیعانه دریافتی (تومان)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Highlight Card for Total and Debt
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("جمع کل فاکتور:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                                Text("${PersianDateHelper.formatPrice(finalTotal)} تومان", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            Divider(
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .width(1.dp),
                                                color = Color.White.copy(alpha = 0.3f)
                                            )
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("مانده بدهی مشتری:", color = OrangeAction, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("${PersianDateHelper.formatPrice(remainingDebt)} تومان", color = OrangeAction, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // SECTION 5: Status and Dates
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = OrangeAction)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("وضعیت و تاریخ‌ها", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("وضعیت جاری دستگاه:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        statuses.forEach { (code, title) ->
                                            FilterChip(
                                                selected = status == code,
                                                onClick = { status = code },
                                                label = { Text(title, fontSize = 12.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = OrangeAction,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("تاریخ پذیرش: ${PersianDateHelper.formatPersianReadable(receptionDate)}", fontSize = 12.sp, color = Color.Gray)
                                        Text("تحویل تخمینی: ${PersianDateHelper.formatPersianReadable(estimatedDeliveryDate)}", fontSize = 12.sp, color = OrangeAction, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val finalDeviceType = if (selectedDeviceType == "سایر") customDeviceType.ifBlank { "سایر" } else selectedDeviceType
                                if (customerName.isBlank() || customerPhone.isBlank() || customerDescription.isBlank()) {
                                    isError = true
                                } else {
                                    onSave(
                                        customerName,
                                        customerPhone,
                                        customerAddress,
                                        customerNationalCode,
                                        finalDeviceType,
                                        brandModel,
                                        deviceColor,
                                        serialNumber,
                                        customerDescription,
                                        technicianNote,
                                        status,
                                        parts.filter { it.name.isNotBlank() },
                                        receptionDate,
                                        estimatedDeliveryDate,
                                        laborCost,
                                        discount,
                                        prepaidAmount
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAction)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (orderToEdit == null) "ثبت و صدور قبض پذیرش" else "ذخیره تغییرات پذیرش",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("انصراف", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
