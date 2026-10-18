package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

data class ShopProfile(
    val shopName: String = "تعمیرگاه لوازم خانگی میرغفاری",
    val managerName: String = "میرغفاری",
    val phone: String = "09123456789",
    val landline: String = "021-33445566",
    val address: String = "تهران، خیابان پیروزی، خیابان نبرد، پلاک ۱۱۰",
    val instagram: String = "mirghaffari_repair",
    val cardNumber: String = "6037-9975-1234-5678",
    val cardHolder: String = "به نام میرغفاری",
    val warrantyTerms: String = "کلیه خدمات و قطعات تعویض شده تا ۳۰ روز پس از تاریخ تحویل دارای گارانتی و ضمانت تعمیرگاه می‌باشند."
)

class ShopProfileManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shop_profile_prefs", Context.MODE_PRIVATE)

    fun getProfile(): ShopProfile {
        return ShopProfile(
            shopName = prefs.getString("shop_name", "تعمیرگاه لوازم خانگی میرغفاری") ?: "تعمیرگاه لوازم خانگی میرغفاری",
            managerName = prefs.getString("manager_name", "میرغفاری") ?: "میرغفاری",
            phone = prefs.getString("phone", "09123456789") ?: "09123456789",
            landline = prefs.getString("landline", "021-33445566") ?: "021-33445566",
            address = prefs.getString("address", "تهران، خیابان پیروزی، خیابان نبرد، پلاک ۱۱۰") ?: "تهران، خیابان پیروزی، خیابان نبرد، پلاک ۱۱۰",
            instagram = prefs.getString("instagram", "mirghaffari_repair") ?: "mirghaffari_repair",
            cardNumber = prefs.getString("card_number", "6037-9975-1234-5678") ?: "6037-9975-1234-5678",
            cardHolder = prefs.getString("card_holder", "به نام میرغفاری") ?: "به نام میرغفاری",
            warrantyTerms = prefs.getString("warranty_terms", "کلیه خدمات و قطعات تعویض شده تا ۳۰ روز پس از تاریخ تحویل دارای گارانتی و ضمانت تعمیرگاه می‌باشند.") ?: "کلیه خدمات و قطعات تعویض شده تا ۳۰ روز پس از تاریخ تحویل دارای گارانتی و ضمانت تعمیرگاه می‌باشند."
        )
    }

    fun saveProfile(profile: ShopProfile) {
        prefs.edit()
            .putString("shop_name", profile.shopName.trim())
            .putString("manager_name", profile.managerName.trim())
            .putString("phone", profile.phone.trim())
            .putString("landline", profile.landline.trim())
            .putString("address", profile.address.trim())
            .putString("instagram", profile.instagram.trim().removePrefix("@"))
            .putString("card_number", profile.cardNumber.trim())
            .putString("card_holder", profile.cardHolder.trim())
            .putString("warranty_terms", profile.warrantyTerms.trim())
            .apply()
    }
}
