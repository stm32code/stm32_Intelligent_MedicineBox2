package com.example.smartmedicinebox.utils

import android.content.Context
import android.widget.Toast

object MToast {
    private var toast: Toast? = null
    fun mToast(context: Context, text: String) {
        toast?.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }
}