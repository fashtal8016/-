package com.example.util

import java.util.Calendar
import java.util.Date
import java.util.Locale

object PersianDateHelper {

    data class PersianDate(val year: Int, val month: Int, val day: Int) {
        fun format(separator: String = "/"): String {
            val m = if (month < 10) "0$month" else "$month"
            val d = if (day < 10) "0$day" else "$day"
            return "$year$separator$m$separator$d"
        }

        fun toReadableString(): String {
            val monthNames = arrayOf(
                "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
                "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
            )
            val monthName = if (month in 1..12) monthNames[month - 1] else ""
            return "$day $monthName $year"
        }
    }

    /**
     * Converts Gregorian timestamp (millis) to Persian Jalali date.
     */
    fun getPersianDate(timestamp: Long = System.currentTimeMillis()): PersianDate {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)

        return gregorianToJalali(gy, gm, gd)
    }

    fun formatPersian(timestamp: Long = System.currentTimeMillis()): String {
        return getPersianDate(timestamp).format()
    }

    fun formatPersianReadable(timestamp: Long = System.currentTimeMillis()): String {
        return getPersianDate(timestamp).toReadableString()
    }

    fun addDaysToTimestamp(days: Int, fromTimestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = fromTimestamp }
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.timeInMillis
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): PersianDate {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var gDayNo = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]

        var jy = -1595 + (33 * (gDayNo / 12053))
        gDayNo %= 12053

        jy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo > 365) {
            jy += ((gDayNo - 1) / 365)
            gDayNo = (gDayNo - 1) % 365
        }

        val jm: Int
        val jd: Int
        if (gDayNo < 186) {
            jm = 1 + (gDayNo / 31)
            jd = 1 + (gDayNo % 31)
        } else {
            jm = 7 + ((gDayNo - 186) / 30)
            jd = 1 + ((gDayNo - 186) % 30)
        }

        return PersianDate(jy, jm, jd)
    }

    fun formatPrice(amount: Double): String {
        val longVal = amount.toLong()
        return String.format(Locale.US, "%,d", longVal)
    }
}
