package com.example.todaybook.mapsmodel

class LibraryList (val items:List<Library>,
                   val totalCount:Int,
                   val pageNo:Int,
                   val numOfRows:Int){
    override fun toString(): String {
        return "LibraryList(items=$items, totalCount=$totalCount, pageNo=$pageNo, numOfRows=$numOfRows)"
    }
}