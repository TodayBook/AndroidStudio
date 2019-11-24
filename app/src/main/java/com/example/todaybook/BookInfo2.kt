package com.example.todaybook

import android.os.Parcel
import android.os.Parcelable

class BookInfo2(var imageurl: String, var title: String, var author: String, var pub: String, var contents:String) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageurl)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(pub)
        parcel.writeString(contents)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookInfo2> {
        override fun createFromParcel(parcel: Parcel): BookInfo2 {
            return BookInfo2(parcel)
        }

        override fun newArray(size: Int): Array<BookInfo2?> {
            return arrayOfNulls(size)
        }
    }
}
