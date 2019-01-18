package ru.zuma.unicornclub

import android.os.Parcel
import android.os.Parcelable

data class UnicornImage (
    var month: Int = 0,
    var dayOfMonth: Int = 0,
    var isKnown: Boolean = false
) : Parcelable {

    protected constructor(`in`: Parcel) : this() {
        dayOfMonth = `in`.readInt()
        month = `in`.readInt()
        isKnown = `in`.readInt() == 1
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(dayOfMonth)
        parcel.writeInt(month)
        parcel.writeInt(isKnown.toInt())
    }

    fun Boolean.toInt() = if (this) 1 else 0

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<UnicornImage> = object : Parcelable.Creator<UnicornImage> {
            override fun createFromParcel(`in`: Parcel): UnicornImage {
                return UnicornImage(`in`)
            }

            override fun newArray(size: Int): Array<UnicornImage?> {
                return arrayOfNulls(size)
            }
        }
    }
}