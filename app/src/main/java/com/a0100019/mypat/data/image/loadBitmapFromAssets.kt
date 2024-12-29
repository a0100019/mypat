package com.a0100019.mypat.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun loadBitmapFromAssets(context: Context, filePath: String): Bitmap {
    val inputStream = context.assets.open(filePath)
    return BitmapFactory.decodeStream(inputStream)
}
