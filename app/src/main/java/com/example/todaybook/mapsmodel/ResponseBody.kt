package com.example.todaybook.mapsmodel

class ResponseBody(val header:Header,
                   val body:LibraryList){
    override fun toString(): String {
        return "ResponseBody(header=$header, body=$body)"
    }
}