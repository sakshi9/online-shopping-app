package com.example.onlineshopping.ui.util

import android.annotation.SuppressLint

class Conversion {

    companion object{
        @SuppressLint("DefaultLocale")
        fun formatValue(value : Double): String =
            "₹${String.format("%.2f", value)}"
    }
}