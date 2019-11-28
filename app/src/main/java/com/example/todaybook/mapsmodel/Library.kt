package com.example.todaybook.mapsmodel

class Library(
    val lbrryNm: String,
    val ctprvnNm: String,
    val signguNm: String,
    val lbrrySe: String,
    val closeDay: String,
    val weekdayOperOpenHhmm: String,
    val weekdayOperColseHhmm: String,
    val satOperOperOpenHhmm: String,
    val satOperCloseHhmm: String,
    val holidayOperOpenHhmm: String,
    val holidayCloseOpenHhmm: String,
    val seatCo: String,
    val bookCo: String,
    val pblictnCo: String,
    val noneBookCo: String,
    val lonCo: String,
    val lonDaycnt: String,
    val rdnmadr: String,
    val operInstitutionNm: String,
    val phoneNumber: String,
    val plotAr: String,
    val buldAr: String,
    val homepageUrl: String,
    val latitude: String,
    val hardness: String,
    val referenceDate: String,
    val insttCode: String,
    val insttNm: String
){
    override fun toString(): String {
        return "Library(lbrryNm='$lbrryNm', ctprvnNm='$ctprvnNm', signguNm='$signguNm', lbrrySe='$lbrrySe', closeDay='$closeDay', weekdayOperOpenHhmm='$weekdayOperOpenHhmm', weekdayOperColseHhmm='$weekdayOperColseHhmm', satOperOperOpenHhmm='$satOperOperOpenHhmm', satOperCloseHhmm='$satOperCloseHhmm', holidayOperOpenHhmm='$holidayOperOpenHhmm', holidayCloseOpenHhmm='$holidayCloseOpenHhmm', seatCo=$seatCo, bookCo=$bookCo, pblictnCo=$pblictnCo, noneBookCo='$noneBookCo', lonCo=$lonCo, lonDaycnt=$lonDaycnt, rdnmadr='$rdnmadr', operInstitutionNm='$operInstitutionNm', phoneNumber='$phoneNumber', plotAr=$plotAr, buldAr=$buldAr, homepageUrl='$homepageUrl', latitude=$latitude, hardness=$hardness, referenceDate='$referenceDate', insttCode=$insttCode, insttNm='$insttNm')"
    }
}
