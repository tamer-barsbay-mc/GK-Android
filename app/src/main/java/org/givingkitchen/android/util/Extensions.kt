package org.givingkitchen.android.util

import android.content.Intent
import android.content.res.Resources
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.util.TypedValue
import android.view.View
import android.widget.TextView

fun Resources.getFloatDimension(@DimenRes dimension: Int): Float {
    val outValue = TypedValue()
    this.getValue(dimension, outValue, true)
    return outValue.float
}

fun Fragment.startShareAction(str: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, str)
        type = "text/plain"
    }
    startActivity(sendIntent)
}

fun View.setPaddingDp(leftDp: Int, topDp: Int, rightDp: Int, bottomDp: Int) {
    this.setPadding(convertToDp(leftDp, resources), convertToDp(topDp, resources), convertToDp(rightDp, resources), convertToDp(bottomDp, resources))
}

fun convertToDp(sizeInDp: Int, resources: Resources): Int {
    val scale = resources.displayMetrics.density
    return (sizeInDp * scale + 0.5f).toInt()
}

/**
 * @return true if the text was set or false if the TextView is now Gone
 */
fun TextView.setTextIfItExists(text: String?): Boolean {
    if (text.isNullOrBlank()) {
        this.visibility = View.GONE
        return false
    } else {
        this.visibility = View.VISIBLE
        this.text = text
        return true
    }
}