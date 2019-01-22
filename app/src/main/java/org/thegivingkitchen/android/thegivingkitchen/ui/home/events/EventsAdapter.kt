package org.thegivingkitchen.android.thegivingkitchen.ui.home.events

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import org.thegivingkitchen.android.thegivingkitchen.R
import org.thegivingkitchen.android.thegivingkitchen.util.RecyclerViewAdapterWithHeaders
import org.thegivingkitchen.android.thegivingkitchen.util.setTextIfItExists

class EventsAdapter(items: List<Event>, val fragment: Fragment) : RecyclerViewAdapterWithHeaders<RecyclerView.ViewHolder>(items) {
    companion object {
        const val VIEW_TYPE_EVENT = 0
        const val VIEW_TYPE_HEADER = 1
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position] is Event) {
            return VIEW_TYPE_EVENT
        }
        return VIEW_TYPE_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {

        }
        return EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_event, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) {
            holder.bind(items[position] as Event, fragment)
        }
    }
}

class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(event: Event, fragment: Fragment) {
        view.findViewById<TextView>(R.id.title_EventsRecycler).setTextIfItExists(event.title)
        view.findViewById<TextView>(R.id.description_EventsRecycler).setTextIfItExists(event.subtitle?.replace("\n", ""))
        setPicture(event.picUrl, R.id.image_EventsRecycler, fragment)
    }

    private fun setPicture(url: String?, @IdRes id: Int, fragment: Fragment) {
        // url must be https
        var httpsUrl = ""
        if (url != null) {
            if (url.startsWith("https")) {
                httpsUrl = url
            } else {
                httpsUrl = url.substring(0, 4) + "s" + url.substring(4)
            }
        }

        Glide.with(fragment)
                .load(httpsUrl)
                .into(view.findViewById(id))
    }
}
