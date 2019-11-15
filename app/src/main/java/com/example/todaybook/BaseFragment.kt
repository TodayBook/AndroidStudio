package com.example.todaybook


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {
    abstract fun title(): String
}