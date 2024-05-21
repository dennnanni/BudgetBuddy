package com.android.budgetbuddy.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun resizeDrawable(context: Context, drawableId: Int, width: Int, height: Int): Drawable? {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    if (drawable != null) {
        val bitmap = drawableToBitmap(drawable)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        return BitmapDrawable(context.resources, resizedBitmap)
    }
    return null
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    var width = drawable.intrinsicWidth
    width = if (width > 0) width else 1
    var height = drawable.intrinsicHeight
    height = if (height > 0) height else 1

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}