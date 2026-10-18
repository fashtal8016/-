package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.ShopProfile
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.MessengerHelper
import com.example.util.ReceiptHelper

data class MessengerItem(
    val app: MessengerHelper.TargetApp,
    val color: Color,
    val iconDescription: String
)

@Composable
fun SendInvoiceDialog(
    order: DeviceOrderEntity,
    profile: ShopProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedMessageType by remember { mutableStateOf(0) } // 0: Reception, 1: Delivery, 2: Ready notice, 3: Debt reminder

    val messageText = remember(selectedMessageType, order, profile) {
        when (selectedMessageType) {
            0 -> ReceiptHelper.generateReceptionReceiptText(order, profile)
            1 -> ReceiptHelper.generateDeliveryReceiptText(order, profile)
            2 -> ReceiptHelper.generateReadyNotificationText(order, profile)
            3 -> ReceiptHelper.generateDebtReminderText(order, profile)
            else -> ReceiptHelper.generateReceptionReceiptText(order, profile)
        }
    }

    val messengerOptions = listOf(
        MessengerItem(MessengerHelper.TargetApp.BALE, Color(0xFF00897B), "بله"),
        MessengerItem(MessengerHelper.TargetApp.RUBIKA, Color(0xFF7E57C2), "روبیکا"),
        MessengerItem(MessengerHelper.TargetApp.EITAA, Color(0xFFFF6F00), "ایتا"),
        MessengerItem(MessengerHelper.TargetApp.WHATSAPP, Color(0xFF25D366), "واتساپ"),
        MessengerItem(MessengerHelper.TargetApp.TELEGRAM, Color(0xFF0088CC), "تلگرام"),
        MessengerItem(MessengerHelper.TargetApp.SMS, Color(0xFF1976D2), "پیامک عادی"),
        MessengerItem(MessengerHelper.TargetApp.UNIVERSAL, Color(0xFF455A64), "سایر...")
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAction.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = OrangeAction, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("ارسال فاکتور و پیام به مشتری", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("گیرنده: ${order.customerName} (${order.customerPhone})", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "بستن")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("نوع پیام / فاکتور:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    // Message Type Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedMessageType == 0,
                            onClick = { selectedMessageType = 0 },
                            label = { Text("قبض پذیرش", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = selectedMessageType == 1,
                            onClick = { selectedMessageType = 1 },
                            label = { Text("رسید تحویل", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedMessageType == 2,
                            onClick = { selectedMessageType = 2 },
                            label = { Text("اعلام آماده بودن", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = selectedMessageType == 3,
                            onClick = { selectedMessageType = 3 },
                            label = { Text("یادآوری بدهی", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeAction, selectedLabelColor = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("انتخاب پیام‌رسان جهت ارسال:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAction)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Grid of messengers
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(messengerOptions) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        MessengerHelper.sendInvoice(
                                            context = context,
                                            app = item.app,
                                            phoneNumber = order.customerPhone,
                                            message = messageText,
                                            title = "${profile.shopName} - قبض ${order.trackingNumber}"
                                        )
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.12f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(item.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            item.iconDescription.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.iconDescription,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.color,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "متن فاکتور شامل مشخصات دستگاه، ریز هزینه‌ها، شماره کارت بانکی و آدرس تعمیرگاه به طور خودکار آماده شده و در پیام‌رسان قرار می‌گیرد.",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
