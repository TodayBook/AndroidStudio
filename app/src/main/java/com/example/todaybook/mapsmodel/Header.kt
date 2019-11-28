package com.example.todaybook.mapsmodel

class Header(val resultCode:Int,
             val resultMsg:String,
             val type:String){
    override fun toString(): String {
        return "Header(resultCode=$resultCode, resultMsg='$resultMsg', type='$type')"
    }
}