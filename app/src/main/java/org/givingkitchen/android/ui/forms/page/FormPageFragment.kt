package org.givingkitchen.android.ui.forms.page

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_form_question.*
import org.givingkitchen.android.R
import org.givingkitchen.android.ui.forms.Page
import org.givingkitchen.android.ui.forms.Question
import org.givingkitchen.android.ui.forms.questionviews.*
import org.givingkitchen.android.ui.forms.questionviews.checkboxquestion.CheckboxQuestion
import org.givingkitchen.android.ui.forms.questionviews.datequestion.DatePickerFragment
import org.givingkitchen.android.ui.forms.questionviews.datequestion.DateQuestion
import org.givingkitchen.android.ui.forms.questionviews.moneyquestion.MoneyQuestion
import org.givingkitchen.android.ui.forms.questionviews.radioquestion.RadioQuestion
import org.givingkitchen.android.ui.forms.questionviews.timequestion.TimePickerFragment
import org.givingkitchen.android.ui.forms.questionviews.timequestion.TimeQuestion
import org.givingkitchen.android.util.setTextIfItExists

class FormPageFragment : Fragment() {

    companion object {
        const val pageArg = "page"

        fun newInstance(page: Page): FormPageFragment {
            val formPageFragment = FormPageFragment()
            val args = Bundle()
            args.putParcelable(pageArg, page)
            formPageFragment.arguments = args
            return formPageFragment
        }
    }

    private data class QuestionWithView(val question: Question, val questionView: QuestionView)

    private lateinit var page: Page
    private val questionsWithViews: ArrayList<QuestionWithView> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            page = arguments!!.getParcelable(pageArg)!!
        }
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_form_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageTitle_formQuestion.setTextIfItExists(page.pageInformation)

        val questions = page.questions
        if (questions != null) {
            for (question in questions) {
                val questionView = getQuestionView(question)
                if (questionView != null && questionView is QuestionView) {
                    container_formQuestion.addView(questionView)
                    questionsWithViews.add(QuestionWithView(question, questionView as QuestionView))
                } else {
                    // todo: log unexpected question type crash
                }
            }
        }
    }

    fun getQuestionsAndAnswers(): List<Pair<String, String>> {
        val questionsAndAnswers = arrayListOf<Pair<String, String>>()

        for (questionView in questionsWithViews) {
            val questionTitle = questionView.question.Title
            val questionAnswer = questionView.questionView.getAnswer()

            if (questionTitle != null && questionAnswer != null) {
                questionsAndAnswers.add(Pair(questionTitle, questionAnswer))
            }
        }

        return questionsAndAnswers
    }

    fun areAllQuestionsAnswered(): Boolean {
        for (questionView in questionsWithViews) {
            if (questionView.question.IsRequired == "1" && !questionView.questionView.isAnswered()) {
                return false
            }
        }
        return true
    }

    fun placeQuestionUnansweredWarnings() {
        for (questionView in questionsWithViews) {
            if (questionView.question.IsRequired == "1" && !questionView.questionView.isAnswered()) {
                questionView.questionView.placeUnansweredWarning()
            }
        }
    }

    private fun getQuestionView(question: Question): View? {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val savedAnswer = sharedPref?.getString(question.Title, null)

        return when (question.Type) {
            QuestionType.shortname -> {
                ShortnameQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.fullname -> {
                FullnameQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.text -> {
                TextQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.phone -> {
                PhoneQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.email -> {
                EmailQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.address -> {
                AddressQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.date -> {
                val dateQuestion = DateQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
                dateQuestion.setOnClickListener {
                    DatePickerFragment().newInstance(dateQuestion, dateQuestion.dateYear, dateQuestion.dateMonth, dateQuestion.dateDay).show(fragmentManager, "Date")
                }
                dateQuestion
            }
            QuestionType.time -> {
                val timeQuestion = TimeQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
                timeQuestion.setOnClickListener {
                    TimePickerFragment().newInstance(timeQuestion, timeQuestion.timeHour, timeQuestion.timeMinute).show(fragmentManager, "Time")
                }
                timeQuestion
            }
            QuestionType.number -> {
                NumberQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.money -> {
                MoneyQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.checkbox -> {
                CheckboxQuestion(formatTitle(question.Title, question.IsRequired), question.SubFields?.map { it.Label }, question.HasOtherField, savedAnswer, context!!)
            }
            QuestionType.textarea -> {
                TextareaQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.url -> {
                UrlQuestion(formatTitle(question.Title, question.IsRequired), savedAnswer, context!!)
            }
            QuestionType.radio -> {
                RadioQuestion(formatTitle(question.Title, question.IsRequired), question.Choices?.map { it.Label }, question.HasOtherField, savedAnswer, context!!)
            }
            QuestionType.select -> {
                RadioQuestion(formatTitle(question.Title, question.IsRequired), question.Choices?.map { it.Label }, question.HasOtherField, savedAnswer, context!!)
            }
            else -> {
                null
            }
        }
    }

    private fun formatTitle(title: String?, isRequired: String?): String? {
        return if (title != null && isRequired != null && isRequired == "1") "$title*" else title
    }
}
