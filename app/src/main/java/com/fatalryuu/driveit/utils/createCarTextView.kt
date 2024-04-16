package com.fatalryuu.driveit.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.fatalryuu.driveit.R

fun Context.createCarTextView(name: String): TextView {
    val textView = TextView(this)
    textView.text = name
    textView.setTextColor(Color.WHITE)
    textView.gravity = Gravity.CENTER
    textView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        450
    ).apply {
        bottomMargin = resources.getDimensionPixelSize(R.dimen.car_margin_bottom)
    }
    textView.textSize = 24f
    textView.typeface = Typeface.DEFAULT_BOLD

    val drawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(Color.parseColor("#FF4081"))
        cornerRadius = resources.getDimension(R.dimen.car_corner_radius)
    }
    textView.background = drawable

    return textView
}
