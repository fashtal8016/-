package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.local.ShopProfile
import com.example.data.local.entity.DeviceOrderEntity

object ReceiptHelper {

    fun getStatusTitle(status: String): String {
        return when (status) {
            "PENDING" -> "در انتظار بررسی"
            "REPAIRING" -> "در حال تعمیر"
            "WAITING_FOR_PART" -> "منتظر قطعه"
            "READY" -> "آماده تحویل"
            "DELIVERED" -> "تحویل داده‌شده"
            "RETURNED" -> "مرجوعی"
            else -> status
        }
    }

    fun generateReceptionReceiptText(order: DeviceOrderEntity, profile: ShopProfile): String {
        val dateStr = PersianDateHelper.formatPersian(order.receptionDate)
        val estDateStr = if (order.estimatedDeliveryDate > 0) PersianDateHelper.formatPersian(order.estimatedDeliveryDate) else "نامشخص"
        val parts = PartsJsonHelper.fromJson(order.partsJson)
        val partsText = if (parts.isEmpty()) "—" else parts.joinToString("\n") { "• ${it.name} (${it.count} عدد) : ${PersianDateHelper.formatPrice(it.totalPrice)} تومان" }

        val instaText = if (profile.instagram.isNotBlank()) "\n📷 اینستاگرام: instagram.com/${profile.instagram.removePrefix("@")}" else ""
        val cardText = if (profile.cardNumber.isNotBlank()) "\n💳 شماره کارت جهت بیعانه:\n${profile.cardNumber} (${profile.cardHolder})" else ""

        return """
════════════════════════════
🔧 ${profile.shopName}
════════════════════════════
📋 قبض پذیرش دستگاه: ${order.trackingNumber}
📅 تاریخ پذیرش: $dateStr
⏳ تاریخ تخمینی تحویل: $estDateStr

👤 مشتری گرامی: ${order.customerName}
📞 شماره تماس: ${order.customerPhone}

📱 اطلاعات دستگاه:
• نوع دستگاه: ${order.deviceType}
• مدل/برند: ${order.brandModel.ifBlank { "—" }}
• رنگ: ${order.color.ifBlank { "—" }}
• شماره سریال: ${order.serialNumber.ifBlank { "—" }}

🔍 شرح ایراد به گفته مشتری:
${order.customerDescription.ifBlank { "بدون شرح ایراد" }}

🔩 قطعات مصرفی:
$partsText

💰 امور مالی و پیش‌پرداخت:
• هزینه قطعات: ${PersianDateHelper.formatPrice(order.partsCost)} تومان
• اجرت تعمیر: ${PersianDateHelper.formatPrice(order.laborCost)} تومان
• تخفیف: ${PersianDateHelper.formatPrice(order.discount)} تومان
• جمع کل فاکتور: ${PersianDateHelper.formatPrice(order.finalTotalCost)} تومان
• بیعانه دریافتی: ${PersianDateHelper.formatPrice(order.prepaidAmount)} تومان
• مانده بدهی: ${PersianDateHelper.formatPrice(order.remainingDebt)} تومان
$cardText

📌 وضعیت فعلی: ${getStatusTitle(order.status)}

📜 شرایط ضمانت:
${profile.warrantyTerms}

📍 آدرس: ${profile.address}
☎️ تلفن: ${profile.phone} - ${profile.landline}$instaText
════════════════════════════
        """.trimIndent()
    }

    fun generateDeliveryReceiptText(order: DeviceOrderEntity, profile: ShopProfile): String {
        val dateStr = PersianDateHelper.formatPersian(order.receptionDate)
        val deliveryDateStr = PersianDateHelper.formatPersian(System.currentTimeMillis())

        return """
════════════════════════════
✅ ${profile.shopName}
════════════════════════════
🧾 رسید تحویل دستگاه (تسویه نهایی)
📋 شماره قبض: ${order.trackingNumber}
📅 تاریخ پذیرش: $dateStr
📦 تاریخ تحویل: $deliveryDateStr

👤 مشتری: ${order.customerName}
📞 تماس: ${order.customerPhone}
📱 دستگاه: ${order.deviceType} - ${order.brandModel}

💰 وضعیت تسویه حساب:
• جمع کل هزینه خدمات: ${PersianDateHelper.formatPrice(order.finalTotalCost)} تومان
• بیعانه قبلی: ${PersianDateHelper.formatPrice(order.prepaidAmount)} تومان
• مبلغ تسویه شده در تحویل: ${PersianDateHelper.formatPrice(order.remainingDebt)} تومان
• مانده حساب: صفر (تسویه کامل شد)

🛡️ گارانتی و ضمانت تعمیرگاه فعال شد.
${profile.warrantyTerms}

با تشکر از اعتماد شما به ${profile.shopName}
☎️ تلفن: ${profile.phone}
📍 آدرس: ${profile.address}
════════════════════════════
        """.trimIndent()
    }

    fun generateDebtReminderText(order: DeviceOrderEntity, profile: ShopProfile): String {
        val cardText = if (profile.cardNumber.isNotBlank()) "\n💳 شماره کارت جهت واریز:\n${profile.cardNumber} (${profile.cardHolder})" else ""

        return """
سلام ${order.customerName} عزیز،
احتراماً به اطلاع می‌رساند پیرو خدمات تعمیر دستگاه ${order.deviceType} (شماره قبض: ${order.trackingNumber}) در «${profile.shopName}»، مانده بدهی حساب شما مبلغ ${PersianDateHelper.formatPrice(order.remainingDebt)} تومان می‌باشد.
$cardText

خواهشمند است پس از واریز، مراتب را با ارسال فیش به این شماره اطلاع دهید.
با تشکر، ${profile.managerName}
تلفن: ${profile.phone}
        """.trimIndent()
    }

    fun generateReadyNotificationText(order: DeviceOrderEntity, profile: ShopProfile): String {
        return """
سلام ${order.customerName} عزیز،
دستگاه شما (${order.deviceType} ${order.brandModel}) در «${profile.shopName}» با موفقیت تعمیر شده و آماده تحویل می‌باشد.

📋 شماره قبض: ${order.trackingNumber}
💰 مانده حساب قابل پرداخت در تحویل: ${PersianDateHelper.formatPrice(order.remainingDebt)} تومان

📍 آدرس جهت دریافت دستگاه:
${profile.address}
☎️ تلفن هماهنگی: ${profile.phone}
ساعات کاری: ۹ صبح الی ۲۱
        """.trimIndent()
    }

    fun dialPhone(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در برقراری تماس", Toast.LENGTH_SHORT).show()
        }
    }
}
