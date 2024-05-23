package com.android.budgetbuddy.ui.utils

import java.text.NumberFormat
import java.util.Locale


fun Double.toLocaleString(locale: Locale = Locale.getDefault()): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    return numberFormat.format(this)
}