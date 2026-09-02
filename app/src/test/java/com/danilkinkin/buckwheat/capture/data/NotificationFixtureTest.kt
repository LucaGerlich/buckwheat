package com.danilkinkin.buckwheat.capture.data

import com.danilkinkin.buckwheat.di.RoomConverters
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class NotificationFixtureTest {

    private val snapshot = NotificationSnapshot(
        packageName = "de.traderepublic.app",
        notificationId = 7,
        notificationKey = "0|de.traderepublic.app|7|null|10123",
        postTime = 1_700_000_000_000L,
        title = "Title",
        text = "Text",
        bigText = "Big",
        subText = "Sub",
        infoText = "Info",
        summaryText = "Summary",
        textLines = listOf("Line 1", "Line 2"),
        notificationChannel = "payments",
        category = "msg",
        template = "BigTextStyle",
        isOngoing = true,
        isGroupSummary = true,
    )

    @Test
    fun `snapshot survives a fixture roundtrip`() {
        val fixture = NotificationFixture.fromSnapshot(snapshot, label = "trade republic")

        assertEquals(snapshot, fixture.toSnapshot())
    }

    @Test
    fun `fixture keeps label and recording time`() {
        val recordedAt = Date(1_700_000_123_000L)

        val fixture = NotificationFixture.fromSnapshot(snapshot, "label", recordedAt)

        assertEquals("label", fixture.label)
        assertEquals(recordedAt, fixture.recordedAt)
    }

    @Test
    fun `text lines survive room serialisation`() {
        val converters = RoomConverters()
        val lines = listOf("plain", "with \\ backslash", "with\nnewline", "")

        val encoded = converters.stringListToString(lines)

        assertEquals(lines, converters.stringToStringList(encoded))
    }

    @Test
    fun `empty text lines are serialised as an empty list`() {
        val converters = RoomConverters()

        assertEquals(emptyList<String>(), converters.stringToStringList(converters.stringListToString(emptyList())))
    }
}
