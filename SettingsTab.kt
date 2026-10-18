package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.data.local.ShopProfile
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAction

@Composable
fun SettingsTab(
    profile: ShopProfile,
    onEditProfileClick: () -> Unit
) {
    val context = LocalContext.current

    val digitalCardText = """
🔧 ${profile.shopName}
مدیریت: ${profile.managerName}
متخصص در تعمیر انواع لوازم خانگی و برقی (پنکه، مولینکس، جاروبرقی، سشوار و ...)

☎️ تلفن همراه: ${profile.phone}
📞 تلفن ثابت: ${profile.landline}
📍 آدرس: ${profile.address}
📷 اینستاگرام: https://instagram.com/${profile.instagram.removePrefix("@")}
💳 شماره کارت: ${profile.cardNumber} (${profile.cardHolder})

گارانتی خدمات: ${profile.warrantyTerms}
    """.trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Shop Profile Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = OrangeAction, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اطلاعات و پروفایل تعمیرگاه", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                        }

                        Button(
                            onClick = onEditProfileClick,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAction),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ویرایش", fontSize = 12.sp, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow("نام تعمیرگاه:", profile.shopName)
                    DetailRow("مدیر / تعمیرکار:", profile.managerName)
                    DetailRow("شماره همراه:", profile.phone)
                    DetailRow("تلفن ثابت:", profile.landline)
                    DetailRow("آدرس مغازه:", profile.address)

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Instagram link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val uri = Uri.parse("https://instagram.com/${profile.instagram.removePrefix("@")}")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطا در باز کردن اینستاگرام", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("صفحه اینستاگرام:", color = Color.Gray, fontSize = 13.sp)
                        }
                        Text("@${profile.instagram.removePrefix("@")}", color = Color(0xFFE1306C), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Bank card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("شماره کارت درج در فاکتور:", color = Color.Gray, fontSize = 13.sp)
                        }
                        Text(profile.cardNumber, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("شرایط گارانتی و ضمانت فاکتور:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(profile.warrantyTerms, fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Share digital card
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "کارت ویزیت ${profile.shopName}")
                                putExtra(Intent.EXTRA_TEXT, digitalCardText)
                            }
                            context.startActivity(Intent.createChooser(intent, "ارسال کارت ویزیت مغازه"))
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("اشتراک‌گذاری کارت ویزیت و مشخصات مغازه")
                    }
                }
            }
        }

        // Offline Database Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, contentDescription = null, tint = OrangeAction)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("امنیت داده‌ها و دیتابیس آفلاین", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "تمامی سوابق پذیرش‌ها، حسابداری مشتریان و گزارش‌ها به صورت ۱۰۰٪ آفلاین بر روی حافظه دستگاه ذخیره می‌شوند و بدون اینترنت با حداکثر سرعت کار می‌کنند.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // About App
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = OrangeAction)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("درباره اپلیکیشن", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangeAction)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("سامانه جامع مدیریت تعمیرگاه لوازم خانگی", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("پشتیبانی از ارسال مستقیم به بله، روبیکا، ایتا، واتساپ، تلگرام و پیامک", fontSize = 11.sp, color = Color.Gray)
                    Text("حسابداری معین مشتریان، گزارش‌های سود و زیان و تراز مالی ماهانه", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}
