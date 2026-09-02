package com.danilkinkin.buckwheat.notification

/**
 * Neutral, Android-independent representation of a posted notification.
 *
 * Keeping this free of Android types allows parsers and the capture pipeline to be
 * covered by plain JVM unit tests.
 */
data class NotificationSnapshot(
    val packageName: String,
    val notificationId: Int,
    val notificationKey: String,
    val postTime: Long,
    val title: String? = null,
    val text: String? = null,
    val bigText: String? = null,
    val subText: String? = null,
    val infoText: String? = null,
    val summaryText: String? = null,
    val textLines: List<String> = emptyList(),
    val notificationChannel: String? = null,
    val category: String? = null,
    val template: String? = null,
    val isOngoing: Boolean = false,
    val isGroupSummary: Boolean = false,
) {
    /**
     * All human readable payload fields, in a stable order. Parsers usually want to
     * search across every field instead of guessing which one a bank app uses.
     */
    val allTextFragments: List<String>
        get() = (listOf(title, text, bigText, subText, infoText, summaryText) + textLines)
            .mapNotNull { it?.trim() }
            .filter { it.isNotEmpty() }
}
