package com.danilkinkin.buckwheat.notification

import android.os.Bundle
import android.service.notification.StatusBarNotification

/**
 * Reads the raw key/value payload of a notification.
 *
 * The indirection keeps [NotificationExtractor] testable without an Android runtime.
 */
interface NotificationFieldReader {
    fun charSequence(key: String): CharSequence?
    fun charSequenceArray(key: String): Array<CharSequence>?
}

class MapNotificationFieldReader(
    private val values: Map<String, Any?>,
) : NotificationFieldReader {
    override fun charSequence(key: String): CharSequence? = values[key] as? CharSequence

    @Suppress("UNCHECKED_CAST")
    override fun charSequenceArray(key: String): Array<CharSequence>? =
        values[key] as? Array<CharSequence>
}

class BundleNotificationFieldReader(
    private val bundle: Bundle,
) : NotificationFieldReader {
    override fun charSequence(key: String): CharSequence? = bundle.getCharSequence(key)

    override fun charSequenceArray(key: String): Array<CharSequence>? =
        bundle.getCharSequenceArray(key)
}

/**
 * Converts an Android notification into a neutral [NotificationSnapshot].
 *
 * Contains no business logic beyond field mapping and whitespace normalisation.
 */
object NotificationExtractor {
    const val EXTRA_TITLE = "android.title"
    const val EXTRA_TEXT = "android.text"
    const val EXTRA_BIG_TEXT = "android.bigText"
    const val EXTRA_SUB_TEXT = "android.subText"
    const val EXTRA_INFO_TEXT = "android.infoText"
    const val EXTRA_SUMMARY_TEXT = "android.summaryText"
    const val EXTRA_TEXT_LINES = "android.textLines"
    const val EXTRA_TEMPLATE = "android.template"

    fun extract(statusBarNotification: StatusBarNotification): NotificationSnapshot {
        val notification = statusBarNotification.notification

        return extract(
            packageName = statusBarNotification.packageName,
            notificationId = statusBarNotification.id,
            notificationKey = statusBarNotification.key,
            postTime = statusBarNotification.postTime,
            notificationChannel = notification.channelId,
            category = notification.category,
            isOngoing = statusBarNotification.isOngoing,
            isGroupSummary = statusBarNotification.notification.flags and
                android.app.Notification.FLAG_GROUP_SUMMARY != 0,
            reader = BundleNotificationFieldReader(notification.extras ?: Bundle()),
        )
    }

    fun extract(
        packageName: String,
        notificationId: Int,
        notificationKey: String,
        postTime: Long,
        notificationChannel: String? = null,
        category: String? = null,
        isOngoing: Boolean = false,
        isGroupSummary: Boolean = false,
        reader: NotificationFieldReader,
    ): NotificationSnapshot = NotificationSnapshot(
        packageName = packageName,
        notificationId = notificationId,
        notificationKey = notificationKey,
        postTime = postTime,
        title = reader.string(EXTRA_TITLE),
        text = reader.string(EXTRA_TEXT),
        bigText = reader.string(EXTRA_BIG_TEXT),
        subText = reader.string(EXTRA_SUB_TEXT),
        infoText = reader.string(EXTRA_INFO_TEXT),
        summaryText = reader.string(EXTRA_SUMMARY_TEXT),
        textLines = reader.charSequenceArray(EXTRA_TEXT_LINES)
            ?.mapNotNull { it.toString().trim().ifEmpty { null } }
            ?: emptyList(),
        notificationChannel = notificationChannel,
        category = category,
        template = reader.string(EXTRA_TEMPLATE),
        isOngoing = isOngoing,
        isGroupSummary = isGroupSummary,
    )

    private fun NotificationFieldReader.string(key: String): String? =
        charSequence(key)?.toString()?.trim()?.ifEmpty { null }
}
