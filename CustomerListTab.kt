package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.DeviceOrderEntity
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction
import com.example.util.PersianDateHelper
import com.example.util.ReceiptHelper

@Composable
fun CustomerListTab(
    customers: List<CustomerEntity>,
    orders: List<DeviceOrderEntity>,
    onCustomerClick: (CustomerEntity) -> Unit
) {
    val context = LocalContext.current

    if (customers.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("هنوز مشتری در سیستم ثبت نشده است", color = Color.Gray, fontSize = 14.sp)
                Text("با ثبت اولین پذیرش، مشتریان به همراه دفتر حساب اختصاصی در این لیست قرار می‌گیرند", color = Color.Gray, fontSize = 12.sp)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(customers, key = { it.id }) { customer ->
                val customerOrders = orders.filter { it.customerPhone == customer.phoneNumber || it.customerId == customer.id }
                val totalDebt = customerOrders.sumOf { it.remainingDebt }
                val totalSpent = customerOrders.sumOf { it.finalTotalCost }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCustomerClick(customer) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(NavyPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        customer.fullName.ifBlank { "مشتری کد ${customer.id}" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(customer.phoneNumber, color = Color.Gray, fontSize = 12.sp)
                                }
                            }

                            IconButton(
                                onClick = { ReceiptHelper.dialPhone(context, customer.phoneNumber) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32).copy(alpha = 0.12f))
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "تماس", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تعداد دستگاه‌ها: ${customerOrders.size}", fontSize = 11.sp, color = NavyPrimary)
                            Text("گردش کل: ${PersianDateHelper.formatPrice(totalSpent)} تومان", fontSize = 11.sp, color = Color.DarkGray)
                            if (totalDebt > 0) {
                                Text("بدهی: ${PersianDateHelper.formatPrice(totalDebt)} تومان", fontSize = 11.sp, color = OrangeAction, fontWeight = FontWeight.Bold)
                            } else {
                                Text("تسویه کامل", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onCustomerClick(customer) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp), tint = OrangeAction)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مشاهده دفتر حساب و صورتحساب", fontSize = 11.sp, color = OrangeAction, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
