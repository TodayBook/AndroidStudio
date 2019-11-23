package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_book_ranking.*

class BookRanking : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_ranking)
        val vadapter = PagerAdapter(supportFragmentManager)
        pager.adapter = vadapter

        tab.setupWithViewPager(pager)
    }
}
