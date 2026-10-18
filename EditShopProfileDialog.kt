package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction

@Composable
fun EditShopProfileDialog(
    initialProfile: ShopProfile,
    onDismiss: () -> Unit,
    onSave: (ShopProfile) -> Unit
) {
    val context = LocalContext.current

    var shopName by remember { mutableStateOf(initialProfile.shopName) }
    var managerName by remember { mutableStateOf(initialProfile.managerName) }
    var phone by remember { mutableStateOf(initialProfile.phone) }
    var landline by remember { mutableStateOf(initialProfile.landline) }
    var address by remember { mutableStateOf(initialProfile.address) }
    var instagram by remember { mutableStateOf(initialProfile.instagram) }
    var cardNumber by remember { mutableStateOf(initialProfile.cardNumber) }
    var cardHolder by remember { mutableStateOf(initialProfile.cardHolder) }
    var warrantyTerms by remember { mutableStateOf(initialProfile.warrantyTerms) }

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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("ویرایش اطلاعات و مشخصات مغازه", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    // Fields Scroll
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("اطلاعات عمومی مغازه", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = shopName,
                                        onValueChange = { shopName = it },
                                        label = { Text("نام تعمیرگاه / مغازه *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = managerName,
                                        onValueChange = { managerName = it },
                                        label = { Text("نام مدیر / تعمیرکار مسئول") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = phone,
                                            onValueChange = { phone = it },
                                            label = { Text("شماره همراه مغازه *") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = landline,
                                            onValueChange = { landline = it },
                                            label = { Text("تلفن ثابت مغازه") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = address,
                                        onValueChange = { address = it },
                                        label = { Text("آدرس پستی مغازه") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = instagram,
                                        onValueChange = { instagram = it },
                                        label = { Text("آیدی اینستاگرام (مثلاً mirghaffari_repair)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("اطلاعات بانکی جهت درج روی فاکتور", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = cardNumber,
                                        onValueChange = { cardNumber = it },
                                        label = { Text("شماره کارت بانکی (جهت واریز بیعانه/تسویه)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = cardHolder,
                                        onValueChange = { cardHolder = it },
                                        label = { Text("نام صاحب حساب / کارت") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("متن شرایط ضمانت و گارانتی رسید", fontWeight = FontWeight.Bold, color = OrangeAction, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = warrantyTerms,
                                        onValueChange = { warrantyTerms = it },
                                        label = { Text("متن ضمانت‌نامه در انتهای فاکتور") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Save Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (shopName.isBlank() || phone.isBlank()) {
                                    Toast.makeText(context, "نام مغازه و تلفن همراه الزامی است.", Toast.LENGTH_SHORT).show()
                                } else {
                                    onSave(
                                        ShopProfile(
                                            shopName = shopName,
                                            managerName = managerName,
                                            phone = phone,
                                            landline = landline,
                                            address = address,
                                            instagram = instagram,
                                            cardNumber = cardNumber,
                                            cardHolder = cardHolder,
                                            warrantyTerms = warrantyTerms
                                        )
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAction),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ذخیره مشخصات تعمیرگاه", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("انصراف")
                        }
                    }
                }
            }
        }
    }
}
