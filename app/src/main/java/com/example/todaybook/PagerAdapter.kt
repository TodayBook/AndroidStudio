package com.example.todaybook

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import follower
import following

class PagerAdapter : FragmentPagerAdapter {

    private val list: ArrayList<BaseFragment> = ArrayList();

    constructor(fragmentManager: FragmentManager) : super(fragmentManager) {
        list.add(following())
        list.add(follower())
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].title()
    }

    override fun getItem(position: Int): Fragment {
        return list.get(position)
    }

    override fun getCount(): Int {
        return list.size
    }
}