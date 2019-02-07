package org.thegivingkitchen.android.thegivingkitchen.ui.forms.questionviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_question_number.view.*
import org.thegivingkitchen.android.thegivingkitchen.R
import org.thegivingkitchen.android.thegivingkitchen.util.setTextIfItExists

class NumberQuestion(title: String?, context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): LinearLayout(context, attrs, defStyle), QuestionView {
    // todo: use merge tags in views
    init {
        LayoutInflater.from(context).inflate(R.layout.view_question_number, this, true)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        title_numberQuestion.setTextIfItExists(title)
    }

    override fun isAnswered(): Boolean {
        return number_numberQuestion.text.isNotBlank()
    }
}