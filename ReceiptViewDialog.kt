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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ShopProfile
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.MessengerHelper
import com.example.util.PartsJsonHelper
import com.example.util.PersianDateHelper
import com.example.util.ReceiptHelper

@Composable
fun ReceiptViewDialog(
    order: DeviceOrderEntity,
    profile: ShopProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isDeliveryReceipt by remember { mutableStateOf(order.status == "DELIVERED") }
    val parts = PartsJsonHelper.fromJson(order.partsJson)
    var showSendSheet by remember { mutableStateOf(false) }

    val receiptText = remember(isDeliveryReceipt, order, profile) {
        if (isDeliveryReceipt) ReceiptHelper.generateDeliveryReceiptText(order, profile)
        else ReceiptHelper.generateReceptionReceiptText(order, profile)
    }

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

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyPrimary)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isDeliveryReceipt) "رسید تحویل و تسویه حساب" else "قبض پذیرش دستگاه",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    // Toggle Reception / Delivery Receipt
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { isDeliveryReceipt = false },
                            colors = if (!isDeliveryReceipt) ButtonDefaults.buttonColors(containerColor = OrangeAction) else ButtonDefaults.outlinedButtonColors(),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "قبض پذیرش اولیه",
                                color = if (!isDeliveryReceipt) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { isDeliveryReceipt = true },
                            colors = if (isDeliveryReceipt) ButtonDefaults.buttonColors(containerColor = OrangeAction) else ButtonDefaults.outlinedButtonColors(),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "رسید تحویل و تسویه",
                                color = if (isDeliveryReceipt) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Printable Receipt Paper View (styled like paper invoice)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🔧 ${profile.shopName}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NavyPrimary)
                                    Text("مدیریت: ${profile.managerName}", fontSize = 11.sp, color = Color.Gray)
                                    Text("تلفن: ${profile.phone} - ${profile.landline}", fontSize = 11.sp, color = Color.Gray)
                                    if (profile.instagram.isNotBlank()) {
                                        Text("اینستاگرام: @${profile.instagram.removePrefix("@")}", fontSize = 11.sp, color = Color(0xFFE1306C), fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(profile.address, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)

                                    Divider(modifier = Modifier.padding(vertical = 10.dp), thickness = 1.dp, color = Color.LightGray)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("شماره قبض: ${order.trackingNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("تاریخ: ${PersianDateHelper.formatPersian(order.receptionDate)}", fontSize = 12.sp)
                                    }
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F0), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("اطلاعات مشتری:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                    Text("نام: ${order.customerName}", fontSize = 12.sp)
                                    Text("شماره همراه: ${order.customerPhone}", fontSize = 12.sp)
                                    if (order.customerAddress.isNotBlank()) {
                                        Text("آدرس: ${order.customerAddress}", fontSize = 11.sp)
                                    }
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F0), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("اطلاعات دستگاه تعمیری:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                    Text("دستگاه: ${order.deviceType} (${order.brandModel})", fontSize = 12.sp)
                                    Text("رنگ / سریال: ${order.color} / ${order.serialNumber.ifBlank { "—" }}", fontSize = 12.sp)
                                    Text("شرح خرابی: ${order.customerDescription}", fontSize = 12.sp)
                                }
                            }

                            if (parts.isNotEmpty()) {
                                item {
                                    Text("قطعات مصرفی و یدکی:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                    parts.forEach { part ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("• ${part.name} (×${part.count})", fontSize = 11.sp)
                                            Text("${PersianDateHelper.formatPrice(part.totalPrice)} تومان", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            item {
                                Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("جمع کل هزینه فاکتور:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${PersianDateHelper.formatPrice(order.finalTotalCost)} تومان", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("پیش‌پرداخت / بیعانه:", fontSize = 12.sp)
                                    Text("${PersianDateHelper.formatPrice(order.prepaidAmount)} تومان", fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("مانده حساب مشتری:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangeAction)
                                    Text("${PersianDateHelper.formatPrice(order.remainingDebt)} تومان", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangeAction)
                                }
                            }

                            if (profile.cardNumber.isNotBlank()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(NavyPrimary.copy(alpha = 0.06f), RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text("شماره کارت جهت واریز:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        Text(profile.cardNumber, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                        Text("به نام: ${profile.cardHolder}", fontSize = 10.sp, color = Color.DarkGray)
                                    }
                                }
                            }

                            item {
                                Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color.LightGray)
                                Text("شرایط گارانتی و ضمانت خدمات:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyPrimary)
                                Text(profile.warrantyTerms, fontSize = 10.sp, color = Color.DarkGray)
                                Text("ارائه این برگه برای تحویل گرفتن دستگاه الزامی است.", fontSize = 10.sp, color = Color.DarkGray)
                            }
                        }
                    }

                    // Share & Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showSendSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAction),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ارسال به بله/ایتا/روبیکا/واتساپ", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                MessengerHelper.sendInvoice(
                                    context = context,
                                    app = MessengerHelper.TargetApp.UNIVERSAL,
                                    phoneNumber = order.customerPhone,
                                    message = receiptText,
                                    title = "رسید ${profile.shopName}"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اشتراک/چاپ", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        if (showSendSheet) {
            SendInvoiceDialog(
                order = order,
                profile = profile,
                onDismiss = { showSendSheet = false }
            )
        }
    }
}
