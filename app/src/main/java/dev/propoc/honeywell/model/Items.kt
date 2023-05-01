package dev.propoc.honeywell.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Items(
    var name: String,
    var image: Bitmap,
    var colorCoding: Int = 0,
): Parcelable
