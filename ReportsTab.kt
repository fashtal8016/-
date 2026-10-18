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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ShopProfile
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.MessengerHelper
import com.example.util.PersianDateHelper
import com.example.util.ReceiptHelper

@Composable
fun ReportsTab(
    orders: List<DeviceOrderEntity>,
    profile: ShopProfile
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("ALL") } // ALL, THIS_MONTH, LAST_MONTH, 3_MONTHS

    val currentTime = System.currentTimeMillis()
    val oneMonthMillis = 30L * 24 * 60 * 60 * 1000
    val threeMonthsMillis = 90L * 24 * 60 * 60 * 1000

    val filteredOrders = remember(orders, selectedPeriod) {
        when (selectedPeriod) {
            "THIS_MONTH" -> orders.filter { currentTime - it.receptionDate <= oneMonthMillis }
            "LAST_MONTH" -> orders.filter {
                val diff = currentTime - it.receptionDate
                diff in oneMonthMillis..(2 * oneMonthMillis)
            }
            "3_MONTHS" -> orders.filter { currentTime - it.receptionDate <= threeMonthsMillis }
            else -> orders
        }
    }

    val totalOrders = filteredOrders.size
    val totalRevenue = filteredOrders.sumOf { it.finalTotalCost }
    val totalCollected = filteredOrders.sumOf { it.prepaidAmount }
    val totalRemainingDebt = filteredOrders.sumOf { it.remainingDebt }
    val totalPartsCost = filteredOrders.sumOf { it.partsCost }
    val totalLaborCost = filteredOrders.sumOf { it.laborCost }
    val totalDiscount = filteredOrders.sumOf { it.discount }
    val netProfit = (totalLaborCost - totalDiscount).coerceAtLeast(0.0)

    val deliveredCount = filteredOrders.count { it.status == "DELIVERED" }
    val inProgressCount = filteredOrders.count { it.status in listOf("PENDING", "REPAIRING", "WAITING_FOR_PART", "READY") }

    val debtorsList = remember(filteredOrders) {
        filteredOrders.filter { it.remainingDebt > 0 }.sortedByDescending { it.remainingDebt }.take(10)
    }

    val deviceBreakdown = remember(filteredOrders) {
        filteredOrders.groupingBy { it.deviceType }.eachCount().toList().sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Period Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangeAction, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("بازه زمانی گزارش و حسابداری:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedPeriod == "ALL",
                                onClick = { selectedPeriod = "ALL" },
                                label = { Text("کل دوره", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedPeriod == "THIS_MONTH",
                                onClick = { selectedPeriod = "THIS_MONTH" },
                                label = { Text("این ماه (۳۰ روز اخیر)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedPeriod == "LAST_MONTH",
                                onClick = { selectedPeriod = "LAST_MONTH" },
                                label = { Text("ماه گذشته", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedPeriod == "3_MONTHS",
                                onClick = { selectedPeriod = "3_MONTHS" },
                                label = { Text("۳ ماه گذشته", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = OrangeAction)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تراز مالی و سود تعمیرگاه", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("کل فاکتورهای صادره:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("${PersianDateHelper.formatPrice(totalRevenue)} تومان", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("دریافت نقدی / بیعانه:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("${PersianDateHelper.formatPrice(totalCollected)} تومان", fontWeight = FontWeight.Bold, color = Color(0xFF81C784), fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("سود خالص اجرت:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("${PersianDateHelper.formatPrice(netProfit)} تومان", color = OrangeAction, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("طلب‌های معوقه (بدهکاران):", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("${PersianDateHelper.formatPrice(totalRemainingDebt)} تومان", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Accounting breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ریز اقلام درآمد و هزینه", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("کل اجرت خدمات تعمیرگاه:", "${PersianDateHelper.formatPrice(totalLaborCost)} تومان")
                    DetailRow("کل بهای قطعات مصرفی:", "${PersianDateHelper.formatPrice(totalPartsCost)} تومان")
                    DetailRow("کل تخفیفات داده شده:", "${PersianDateHelper.formatPrice(totalDiscount)} تومان")
                    DetailRow("تعداد کل دستگاه‌های پذیرش شده:", "$totalOrders عدد")
                    DetailRow("تعداد دستگاه‌های تحویل داده‌شده:", "$deliveredCount عدد")
                    DetailRow("دستگاه‌های در حال اقدام / آماده:", "$inProgressCount عدد")
                }
            }
        }

        // Debtors section with 1-tap reminders
        if (debtorsList.isNotEmpty()) {
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
                            Text("لیست بدهکاران (پیگیری طلب‌ها)", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 14.sp)
                            Text("${debtorsList.size} نفر", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        debtorsList.forEach { debtor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${debtor.customerName} (${debtor.deviceType})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("بدهی: ${PersianDateHelper.formatPrice(debtor.remainingDebt)} تومان", color = OrangeAction, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { ReceiptHelper.dialPhone(context, debtor.customerPhone) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "تماس", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val reminder = ReceiptHelper.generateDebtReminderText(debtor, profile)
                                            MessengerHelper.sendInvoice(
                                                context = context,
                                                app = MessengerHelper.TargetApp.UNIVERSAL,
                                                phoneNumber = debtor.customerPhone,
                                                message = reminder,
                                                title = "یادآوری بدهی ${debtor.customerName}"
                                            )
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "ارسال یادآوری", tint = OrangeAction, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }

        // Top Devices Repaired
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("آمار تعمیرات به تفکیک نوع دستگاه", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (deviceBreakdown.isEmpty()) {
                        Text("اطلاعاتی در این بازه زمانی یافت نشد.", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        deviceBreakdown.forEach { (device, count) ->
                            val percent = if (totalOrders > 0) (count * 100 / totalOrders) else 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• $device", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("$count دستگاه ($percent٪)", fontSize = 12.sp, color = NavyPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
