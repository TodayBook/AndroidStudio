package com.example.todaybook

import android.os.Parcel
import android.os.Parcelable


class BookInfo(var imageurl: String, var title: String, var author: String, var pub: String) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
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
        val CREATOR: Parcelable.Creator<BookInfo> = object : Parcelable.Creator<BookInfo> {
            override fun createFromParcel(source: Parcel): BookInfo = BookInfo(source)
            override fun newArray(size: Int): Array<BookInfo?> = arrayOfNulls(size)
        }
    }
}
