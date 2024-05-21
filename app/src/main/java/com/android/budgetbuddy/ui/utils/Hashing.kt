package com.android.budgetbuddy.ui.utils

import java.security.MessageDigest
import java.util.Base64

fun hashPassword(password: String): String {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val digest = sha256.digest(password.trim().toByteArray())
    return Base64.getEncoder().encodeToString(digest)
}

