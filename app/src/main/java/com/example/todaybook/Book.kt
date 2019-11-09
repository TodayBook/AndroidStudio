package com.example.todaybook

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Array

/*class Book(val title:String, val authors:String, val publisher:String, val thumbnail:String)*/

data class FocusArea (

    @SerializedName("documents")
    val documentsValue: List<book>
    )

/*data class book(val title:String, val authors:String, val publisher:String, val thumbnail:String)*/



data class book (


    @SerializedName("title")
    var title: String?,

    @SerializedName("authors")
    var authors:ArrayList<String> = ArrayList(),

    @SerializedName("publisher")
    var publisher: String?,

    @SerializedName("thumbnail")
    var thumbnail: String?

)



