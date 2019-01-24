package org.thegivingkitchen.android.thegivingkitchen.ui.forms.questionviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_question_number.view.*
import org.thegivingkitchen.android.thegivingkitchen.R

class NumberQuestion(title: String?, context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): LinearLayout(context, attrs, defStyle) {
    // todo: use merge tags in views
    init {
        LayoutInflater.from(context).inflate(R.layout.view_question_number, this, true)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        title_numberQuestion.text = title
    }
}