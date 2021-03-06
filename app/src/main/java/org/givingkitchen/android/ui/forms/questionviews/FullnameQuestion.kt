package org.givingkitchen.android.ui.forms.questionviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_question_fullname.view.*
import org.givingkitchen.android.R
import org.givingkitchen.android.util.setTextIfItExists

class FullnameQuestion(title: String?, answer: String? = null, context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): LinearLayout(context, attrs, defStyle), QuestionView {
    // todo: use merge tags in views
    init {
        LayoutInflater.from(context).inflate(R.layout.view_question_fullname, this, true)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL
        title_fullnameQuestion.setTextIfItExists(title)
        if (!answer.isNullOrBlank()) {
            name_fullnameQuestion.setText(answer)
        }
    }

    override fun isAnswered(): Boolean {
        return name_fullnameQuestion.text.isNotBlank()
    }

    override fun placeUnansweredWarning() {
        warning_fullnameQuestion.visibility = View.VISIBLE
    }

    override fun getAnswer(): String? {
        val answer = name_fullnameQuestion.text.toString()

        return if (answer.isBlank()) {
            null
        } else {
            answer
        }
    }
}