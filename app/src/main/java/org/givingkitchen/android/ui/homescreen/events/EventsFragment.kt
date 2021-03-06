package org.givingkitchen.android.ui.homescreen.events

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.fragment_events.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.givingkitchen.android.R
import org.givingkitchen.android.ui.homescreen.events.EventsViewModel.Companion.eventsLearnMoreURL
import org.givingkitchen.android.util.Constants.givingKitchenUrl
import org.givingkitchen.android.util.CustomTabs
import java.io.ByteArrayInputStream
import java.io.IOException

class EventsFragment : Fragment() {

    companion object {
        private const val eventsDataURL = "$givingKitchenUrl/events-calendar?format=rss"
    }

    private var adapter = EventsAdapter(mutableListOf(), this)
    private lateinit var model: EventsViewModel

    // todo: don't crash the app if a response is not received within 30 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this).get(EventsViewModel::class.java)
        model.getCurrentEventsList().observe(this, Observer<List<Event>> { liveData ->
            updateEventsList(liveData!!)
        })
        model.isProgressBarVisible().observe(this, Observer<Boolean> { liveData ->
            updateProgressBarVisibility(liveData!!)
        })
        adapter.learnMoreClicks().subscribe { openLearnMoreLink() }
        adapter.eventClicks().subscribe { goToEventDetails(it) }
        GetEventsTask().execute(eventsDataURL)
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_eventsTab.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView_eventsTab.adapter = adapter
    }

    inner class GetEventsTask : AsyncTask<String, Void, String>() {
        private val httpClient = OkHttpClient()

        override fun doInBackground(vararg params: String): String? {
            publishProgress()
            return getData(params[0])
        }

        override fun onProgressUpdate(vararg values: Void?) {
            model.setProgressBarVisibility(true)
        }

        override fun onPostExecute(result: String?) {
            model.setCurrentEventsList(XmlParser().parse(ByteArrayInputStream(result?.toByteArray(Charsets.UTF_8))))
        }

        @Throws(IOException::class)
        fun getData(url: String): String? {
            return httpClient.newCall(Request.Builder().url(url).build()).execute().body()?.string()
        }
    }

    private fun openLearnMoreLink() {
        CustomTabs.openCustomTab(context, eventsLearnMoreURL)
    }

    private fun goToEventDetails(link: String) {
        CustomTabs.openCustomTab(context, link)
    }

    private fun updateEventsList(data: List<Event>) {
        val dataMutableList = data.toMutableList<Any>()
        dataMutableList.add(0, Header())
        adapter.items = dataMutableList
        adapter.notifyDataSetChanged()
        model.setProgressBarVisibility(false)
    }

    private fun updateProgressBarVisibility(visibility: Boolean) {
        when (visibility) {
            true -> {
                progressBar_eventsTab.visibility = View.VISIBLE
            }
            false -> {
                progressBar_eventsTab.visibility = View.GONE
            }
        }
    }
}
