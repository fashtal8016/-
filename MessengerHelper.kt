package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MessengerHelper {

    enum class TargetApp(val appName: String, val packageName: String?, val urlScheme: String?) {
        WHATSAPP("واتساپ", "com.whatsapp", "https://api.whatsapp.com/send?phone="),
        TELEGRAM("تلگرام", "org.telegram.messenger", "https://t.me/share/url?text="),
        BALE("بله", "ir.ble.messenger", null),
        RUBIKA("روبیکا", "ir.resaneh1.iptv", null),
        EITAA("ایتا", "ir.eitaa.messenger", null),
        SMS("پیامک", null, null),
        UNIVERSAL("سایر برنامه‌ها", null, null)
    }

    fun sendInvoice(
        context: Context,
        app: TargetApp,
        phoneNumber: String,
        message: String,
        title: String = "ارسال فاکتور تعمیرگاه"
    ) {
        val cleanPhone = cleanPhoneNumber(phoneNumber)

        try {
            when (app) {
                TargetApp.WHATSAPP -> {
                    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }

                TargetApp.TELEGRAM -> {
                    val url = "https://t.me/share/url?text=${Uri.encode(message)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }

                TargetApp.BALE -> {
                    sendToPackageOrFallback(context, "ir.ble.messenger", message, title)
                }

                TargetApp.RUBIKA -> {
                    sendToPackageOrFallback(context, "ir.resaneh1.iptv", message, title)
                }

                TargetApp.EITAA -> {
                    sendToPackageOrFallback(context, "ir.eitaa.messenger", message, title)
                }

                TargetApp.SMS -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:$phoneNumber")
                        putExtra("sms_body", message)
                    }
                    context.startActivity(intent)
                }

                TargetApp.UNIVERSAL -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, title)
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(Intent.createChooser(intent, title))
                }
            }
        } catch (e: Exception) {
            // Graceful fallback to Universal Share Sheet if specific app intent fails
            try {
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                context.startActivity(Intent.createChooser(fallbackIntent, title))
            } catch (err: Exception) {
                Toast.makeText(context, "خطا در ارسال پیام: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendToPackageOrFallback(context: Context, packageName: String, message: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            `package` = packageName
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val packageManager = context.packageManager
        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Package not installed, fallback to chooser
            Toast.makeText(context, "برنامه مورد نظر نصب نیست، در حال باز کردن منوی اشتراک...", Toast.LENGTH_SHORT).show()
            val chooser = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(chooser, title))
        }
    }

    private fun cleanPhoneNumber(phone: String): String {
        var p = phone.replace(" ", "").replace("-", "").trim()
        if (p.startsWith("0")) {
            p = "98" + p.substring(1)
        } else if (!p.startsWith("98") && p.length == 10) {
            p = "98$p"
        }
        return p
    }
}
