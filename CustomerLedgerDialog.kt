package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ShopProfile
import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.MessengerHelper
import com.example.util.PersianDateHelper
import com.example.util.ReceiptHelper

@Composable
fun CustomerLedgerDialog(
    customer: CustomerEntity,
    orders: List<DeviceOrderEntity>,
    profile: ShopProfile,
    onDismiss: () -> Unit,
    onRecordPayment: (orderId: Long, amount: Double) -> Unit
) {
    val context = LocalContext.current
    val customerOrders = orders.filter { it.customerPhone == customer.phoneNumber || it.customerId == customer.id }

    val totalCost = customerOrders.sumOf { it.finalTotalCost }
    val totalPaid = customerOrders.sumOf { it.prepaidAmount }
    val totalDebt = customerOrders.sumOf { it.remainingDebt }

    var showPaymentSheet by remember { mutableStateOf(false) }
    var selectedOrderForPay by remember { mutableStateOf<DeviceOrderEntity?>(null) }
    var paymentAmountStr by remember { mutableStateOf("") }

    val statementText = remember(customer, customerOrders, profile) {
        val ordersSummary = customerOrders.joinToString("\n") {
            "• ${it.deviceType} (${it.trackingNumber}) - تاریخ: ${PersianDateHelper.formatPersian(it.receptionDate)}\n  مبلغ: ${PersianDateHelper.formatPrice(it.finalTotalCost)} | پرداختی: ${PersianDateHelper.formatPrice(it.prepaidAmount)} | مانده: ${PersianDateHelper.formatPrice(it.remainingDebt)}"
        }
        val cardText = if (profile.cardNumber.isNotBlank()) "\n💳 شماره کارت جهت واریز:\n${profile.cardNumber} (${profile.cardHolder})" else ""

        """
صورتحساب مشتری گرامی: ${customer.fullName}
🔧 ${profile.shopName}
تلفن: ${profile.phone}

سوابق دستگاه‌های تحویلی:
$ordersSummary

💰 خلاصه حساب:
• کل مبالغ فاکتورها: ${PersianDateHelper.formatPrice(totalCost)} تومان
• کل پرداختی‌ها: ${PersianDateHelper.formatPrice(totalPaid)} تومان
• مانده بدهی نهایی: ${PersianDateHelper.formatPrice(totalDebt)} تومان
$cardText

📍 آدرس: ${profile.address}
        """.trimIndent()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("دفتر حساب و معین مشتری", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${customer.fullName} - ${customer.phoneNumber}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    // Content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Financial Balance Card
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("وضعیت حساب و مانده بدهی", color = OrangeAction, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("کل مبالغ خدمات:", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                        Text("${PersianDateHelper.formatPrice(totalCost)} تومان", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("کل دریافتی‌های نقدی:", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                        Text("${PersianDateHelper.formatPrice(totalPaid)} تومان", color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color.White.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("مانده بدهی مشتری:", color = if (totalDebt > 0) OrangeAction else Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(
                                            if (totalDebt > 0) "${PersianDateHelper.formatPrice(totalDebt)} تومان" else "تسویه کامل",
                                            color = if (totalDebt > 0) OrangeAction else Color(0xFF81C784),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Order history header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تاریخچه پذیرش‌ها و دستگاه‌ها (${customerOrders.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(onClick = { ReceiptHelper.dialPhone(context, customer.phoneNumber) }) {
                                    Icon(Icons.Default.Call, contentDescription = "تماس", tint = Color(0xFF2E7D32))
                                }
                            }
                        }

                        // Orders List
                        if (customerOrders.isEmpty()) {
                            item {
                                Text("هیچ دستگاهی برای این مشتری ثبت نشده است.", color = Color.Gray, fontSize = 12.sp)
                            }
                        } else {
                            items(customerOrders, key = { it.id }) { ord ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("${ord.deviceType} ${ord.brandModel}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            StatusBadge(status = ord.status)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("قبض: ${ord.trackingNumber}", fontSize = 11.sp, color = Color.Gray)
                                            Text("تاریخ: ${PersianDateHelper.formatPersian(ord.receptionDate)}", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("فاکتور: ${PersianDateHelper.formatPrice(ord.finalTotalCost)} تومان", fontSize = 12.sp)
                                            if (ord.remainingDebt > 0) {
                                                Text("بدهی: ${PersianDateHelper.formatPrice(ord.remainingDebt)} تومان", color = OrangeAction, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            } else {
                                                Text("تسویه شده", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }

                                        if (ord.remainingDebt > 0) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    selectedOrderForPay = ord
                                                    paymentAmountStr = ord.remainingDebt.toLong().toString()
                                                    showPaymentSheet = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("ثبت دریافت وجه / تسویه این قبض", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Share Statement Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                MessengerHelper.sendInvoice(
                                    context = context,
                                    app = MessengerHelper.TargetApp.UNIVERSAL,
                                    phoneNumber = customer.phoneNumber,
                                    message = statementText,
                                    title = "صورتحساب ${customer.fullName}"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAction),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ارسال صورتحساب کامل به مشتری", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Sub-dialog for recording partial or full payment
        if (showPaymentSheet && selectedOrderForPay != null) {
            val ord = selectedOrderForPay!!
            Dialog(onDismissRequest = { showPaymentSheet = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ثبت دریافت وجه از مشتری", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("قبض: ${ord.trackingNumber} (${ord.deviceType})", fontSize = 12.sp, color = Color.Gray)
                        Text("مانده بدهی این قبض: ${PersianDateHelper.formatPrice(ord.remainingDebt)} تومان", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = paymentAmountStr,
                            onValueChange = { paymentAmountStr = it },
                            label = { Text("مبلغ دریافتی جدید (تومان)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val amt = paymentAmountStr.toDoubleOrNull() ?: 0.0
                                    if (amt > 0) {
                                        onRecordPayment(ord.id, amt)
                                        showPaymentSheet = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ثبت دریافت")
                            }

                            OutlinedButton(
                                onClick = { showPaymentSheet = false },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("انصراف")
                            }
                        }
                    }
                }
            }
        }
    }
}
