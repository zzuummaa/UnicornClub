package ru.zuma.unicornclub

import android.os.Parcel
import android.os.Parcelable

class UnicornImage : Parcelable {

    var month: Int = 0
    var dayOfMonth: Int = 0
    var isKnown: Boolean = false



    constructor(month: Int, dayOfMonth: Int, isKnown: Boolean) {
        this.month = month
        this.dayOfMonth = dayOfMonth
        this.isKnown = isKnown
    }

    protected constructor(`in`: Parcel) {
        dayOfMonth = `in`.readInt()
        month = `in`.readInt()
        isKnown = `in`.readInt() == 1
    }

    constructor()

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