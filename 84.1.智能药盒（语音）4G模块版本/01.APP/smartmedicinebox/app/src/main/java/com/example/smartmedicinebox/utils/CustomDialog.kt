package com.example.smartmedicinebox.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.smartmedicinebox.R


/**
 * 等待动画窗口
 */
class CustomDialog(context: Context, str: String?) :
    Dialog(context, R.style.loading_dialog) {
    private val tvLoadingTx: TextView
    private val ivLoading: ImageView

    init {
        setCanceledOnTouchOutside(false)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.loading_dialog)
        tvLoadingTx = findViewById<TextView>(R.id.tv_loading_tx)
        ivLoading = findViewById<ImageView>(R.id.iv_loading)
        tvLoadingTx.text = str
        val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
            context, R.anim.loading_animation
        )
        // 使用ImageView显示动画
        ivLoading.startAnimation(hyperspaceJumpAnimation)
        window!!.attributes.gravity = Gravity.CENTER //居中显示
        window!!.attributes.dimAmount = 0.5f //背景透明度  取值范围 0 ~ 1
    }

}
