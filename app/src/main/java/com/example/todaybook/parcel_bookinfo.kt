package com.example.todaybook

import android.os.Parcel
import android.os.Parcelable

class parcel_bookinfo(imageurl:String?,title:String?,author:String?,pub:String?): BookInfo(imageurl,title,author,pub),Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
        )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(imageurl)
        writeString(title)
        writeString(author)
        writeString(pub)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<parcel_bookinfo> = object : Parcelable.Creator<parcel_bookinfo> {
            override fun createFromParcel(source: Parcel): parcel_bookinfo = parcel_bookinfo(source)
            override fun newArray(size: Int): Array<parcel_bookinfo?> = arrayOfNulls(size)
        }
    }
}