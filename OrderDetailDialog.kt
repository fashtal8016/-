package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ShopProfile
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.PartsJsonHelper
import com.example.util.PersianDateHelper
import com.example.util.ReceiptHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderDetailDialog(
    order: DeviceOrderEntity,
    profile: ShopProfile,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit,
    onSettlePayment: () -> Unit,
    onShowReceipt: () -> Unit
) {
    val context = LocalContext.current
    val parts = PartsJsonHelper.fromJson(order.partsJson)
    var showSendInvoiceDialog by remember { mutableStateOf(false) }

    val statuses = listOf(
        "PENDING" to "در انتظار بررسی",
        "REPAIRING" to "در حال تعمیر",
        "WAITING_FOR_PART" to "منتظر قطعه",
        "READY" to "آماده تحویل",
        "DELIVERED" to "تحویل داده‌شده",
        "RETURNED" to "مرجوعی"
    )

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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "قبض: ${order.trackingNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                StatusBadge(status = order.status)
                            }
                            Text(
                                text = "ثبت شده در ${PersianDateHelper.formatPersianReadable(order.receptionDate)}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    // Content Scroll
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Quick Actions & Messengers
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(order.customerPhone, color = Color.Gray, fontSize = 13.sp)
                                        }

                                        IconButton(
                                            onClick = { ReceiptHelper.dialPhone(context, order.customerPhone) },
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2E7D32))
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = "تماس", tint = Color.White, modifier = Modifier.size(22.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Button to send invoice to all messengers
                                    Button(
                                        onClick = { showSendInvoiceDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAction),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ارسال فاکتور (بله، روبیکا، ایتا، واتساپ، پیامک)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Device Details
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("مشخصات دستگاه", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    DetailRow("نوع دستگاه:", order.deviceType)
                                    DetailRow("برند و مدل:", order.brandModel.ifBlank { "—" })
                                    DetailRow("رنگ:", order.color.ifBlank { "—" })
                                    DetailRow("شماره سریال:", order.serialNumber.ifBlank { "—" })

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text("شرح ایراد:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(order.customerDescription.ifBlank { "بدون شرح ایراد" }, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

                                    if (order.technicianNote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("یادداشت تعمیرکار:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = OrangeAction)
                                        Text(order.technicianNote, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        // Parts List
                        if (parts.isNotEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("قطعات مصرفی (${parts.size} قلم)", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        parts.forEach { part ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("• ${part.name} (×${part.count})", fontSize = 13.sp)
                                                Text("${PersianDateHelper.formatPrice(part.totalPrice)} تومان", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Financial Breakdown
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("حسابداری و مبالغ", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    DetailRow("هزینه قطعات:", "${PersianDateHelper.formatPrice(order.partsCost)} تومان")
                                    DetailRow("هزینه اجرت تعمیر:", "${PersianDateHelper.formatPrice(order.laborCost)} تومان")
                                    DetailRow("تخفیف:", "${PersianDateHelper.formatPrice(order.discount)} تومان")
                                    DetailRow("پیش‌پرداخت/بیعانه:", "${PersianDateHelper.formatPrice(order.prepaidAmount)} تومان")

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("جمع کل فاکتور:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${PersianDateHelper.formatPrice(order.finalTotalCost)} تومان", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("مانده بدهی مشتری:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (order.remainingDebt > 0) OrangeAction else Color(0xFF2E7D32))
                                        Text(
                                            if (order.remainingDebt > 0) "${PersianDateHelper.formatPrice(order.remainingDebt)} تومان" else "تسویه کامل",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (order.remainingDebt > 0) OrangeAction else Color(0xFF2E7D32)
                                        )
                                    }

                                    if (order.remainingDebt > 0) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = onSettlePayment,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(20.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Payment, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تسویه مانده بدهی (${PersianDateHelper.formatPrice(order.remainingDebt)} تومان)")
                                        }
                                    }
                                }
                            }
                        }

                        // Quick Status Changer
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("تغییر وضعیت دستگاه:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        statuses.forEach { (code, title) ->
                                            FilterChip(
                                                selected = order.status == code,
                                                onClick = { onStatusChange(code) },
                                                label = { Text(title, fontSize = 12.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = OrangeAction,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Buttons (Print Receipt, Edit, Delete)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onShowReceipt,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("پیش‌نمایش فاکتور", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ویرایش", fontSize = 13.sp)
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                        }
                    }
                }
            }
        }

        if (showSendInvoiceDialog) {
            SendInvoiceDialog(
                order = order,
                profile = profile,
                onDismiss = { showSendInvoiceDialog = false }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, title) = when (status) {
        "PENDING" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "در انتظار بررسی")
        "REPAIRING" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "در حال تعمیر")
        "WAITING_FOR_PART" -> Triple(Color(0xFFF3E5F5), Color(0xFF7B1FA2), "منتظر قطعه")
        "READY" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "آماده تحویل")
        "DELIVERED" -> Triple(Color(0xFFECEFF1), Color(0xFF455A64), "تحویل داده‌شده")
        "RETURNED" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "مرجوعی")
        else -> Triple(Color.LightGray, Color.DarkGray, status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
