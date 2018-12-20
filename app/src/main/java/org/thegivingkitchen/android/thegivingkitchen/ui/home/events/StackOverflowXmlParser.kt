package org.thegivingkitchen.android.thegivingkitchen.ui.home.events

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class StackOverflowXmlParser {
    companion object {
        private val namespace: String? = null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<Event> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Event> {
        val entries = mutableListOf<Event>()

        parser.require(XmlPullParser.START_TAG, namespace, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "channel") {
                parser.require(XmlPullParser.START_TAG, namespace, "channel")
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType != XmlPullParser.START_TAG) {
                        continue
                    }

                    if (parser.name == "item") {
                        entries.add(readItem(parser))
                    } else {
                        skip(parser)
                    }
                }
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): Event {
        parser.require(XmlPullParser.START_TAG, namespace, "item")
        var title: String? = null
        var picUrl: String? = null
        var description: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readTitle(parser)
                "media:content" -> picUrl = readLink(parser)
                "description" -> description = readSummary(parser)
                else -> skip(parser)
            }
        }
        return Event(title, picUrl, description)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, "title")
        return title
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, namespace, "media:content")
        val tag = parser.name
        val relType = parser.getAttributeValue(null, "type")
        if (tag == "media:content") {
            if (relType == "image/jpeg") {
                link = parser.getAttributeValue(null, "url")
                parser.nextTag()
            }
        }
        parser.require(XmlPullParser.END_TAG, namespace, "media:content")
        return link
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readSummary(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "description")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, "description")
        return summary
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}