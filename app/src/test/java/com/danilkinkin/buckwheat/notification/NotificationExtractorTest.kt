package com.danilkinkin.buckwheat.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationExtractorTest {

    private fun extract(values: Map<String, Any?>): NotificationSnapshot =
        NotificationExtractor.extract(
            packageName = "de.traderepublic.app",
            notificationId = 42,
            notificationKey = "0|de.traderepublic.app|42|null|10123",
            postTime = 1_700_000_000_000L,
            notificationChannel = "payments",
            reader = MapNotificationFieldReader(values),
        )

    @Test
    fun `maps all payload fields`() {
        val snapshot = extract(
            mapOf(
                NotificationExtractor.EXTRA_TITLE to "Title",
                NotificationExtractor.EXTRA_TEXT to "Text",
                NotificationExtractor.EXTRA_BIG_TEXT to "Big text",
                NotificationExtractor.EXTRA_SUB_TEXT to "Sub",
                NotificationExtractor.EXTRA_INFO_TEXT to "Info",
                NotificationExtractor.EXTRA_SUMMARY_TEXT to "Summary",
                NotificationExtractor.EXTRA_TEMPLATE to "android.app.Notification\$BigTextStyle",
                NotificationExtractor.EXTRA_TEXT_LINES to arrayOf<CharSequence>("Line 1", "Line 2"),
            )
        )

        assertEquals("de.traderepublic.app", snapshot.packageName)
        assertEquals(42, snapshot.notificationId)
        assertEquals("0|de.traderepublic.app|42|null|10123", snapshot.notificationKey)
        assertEquals(1_700_000_000_000L, snapshot.postTime)
        assertEquals("payments", snapshot.notificationChannel)
        assertEquals("Title", snapshot.title)
        assertEquals("Text", snapshot.text)
        assertEquals("Big text", snapshot.bigText)
        assertEquals("Sub", snapshot.subText)
        assertEquals("Info", snapshot.infoText)
        assertEquals("Summary", snapshot.summaryText)
        assertEquals("android.app.Notification\$BigTextStyle", snapshot.template)
        assertEquals(listOf("Line 1", "Line 2"), snapshot.textLines)
    }

    @Test
    fun `missing fields become null and empty list`() {
        val snapshot = extract(emptyMap())

        assertNull(snapshot.title)
        assertNull(snapshot.text)
        assertNull(snapshot.bigText)
        assertNull(snapshot.subText)
        assertNull(snapshot.infoText)
        assertNull(snapshot.summaryText)
        assertTrue(snapshot.textLines.isEmpty())
    }

    @Test
    fun `blank fields are normalised to null`() {
        val snapshot = extract(
            mapOf(
                NotificationExtractor.EXTRA_TITLE to "   ",
                NotificationExtractor.EXTRA_TEXT to "  padded  ",
            )
        )

        assertNull(snapshot.title)
        assertEquals("padded", snapshot.text)
    }

    @Test
    fun `blank text lines are dropped`() {
        val snapshot = extract(
            mapOf(
                NotificationExtractor.EXTRA_TEXT_LINES to
                    arrayOf<CharSequence>(" first ", "   ", "second")
            )
        )

        assertEquals(listOf("first", "second"), snapshot.textLines)
    }

    @Test
    fun `allTextFragments collects every non empty field`() {
        val snapshot = extract(
            mapOf(
                NotificationExtractor.EXTRA_TITLE to "Title",
                NotificationExtractor.EXTRA_TEXT to "Text",
                NotificationExtractor.EXTRA_TEXT_LINES to arrayOf<CharSequence>("Line"),
            )
        )

        assertEquals(listOf("Title", "Text", "Line"), snapshot.allTextFragments)
    }
}
