package com.danilkinkin.buckwheat.capture.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import java.util.Date

/**
 * A locally recorded notification that can be replayed through the capture pipeline.
 *
 * Fixtures are a developer tool and never leave the device.
 */
@Entity(tableName = "notification_fixtures")
data class NotificationFixture(
    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "recorded_at")
    val recordedAt: Date,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "notification_id")
    val notificationId: Int,

    @ColumnInfo(name = "notification_key")
    val notificationKey: String,

    @ColumnInfo(name = "post_time")
    val postTime: Long,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "text")
    val text: String? = null,

    @ColumnInfo(name = "big_text")
    val bigText: String? = null,

    @ColumnInfo(name = "sub_text")
    val subText: String? = null,

    @ColumnInfo(name = "info_text")
    val infoText: String? = null,

    @ColumnInfo(name = "summary_text")
    val summaryText: String? = null,

    @ColumnInfo(name = "text_lines")
    val textLines: List<String> = emptyList(),

    @ColumnInfo(name = "notification_channel")
    val notificationChannel: String? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "template")
    val template: String? = null,

    @ColumnInfo(name = "is_ongoing")
    val isOngoing: Boolean = false,

    @ColumnInfo(name = "is_group_summary")
    val isGroupSummary: Boolean = false,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    fun toSnapshot(): NotificationSnapshot = NotificationSnapshot(
        packageName = packageName,
        notificationId = notificationId,
        notificationKey = notificationKey,
        postTime = postTime,
        title = title,
        text = text,
        bigText = bigText,
        subText = subText,
        infoText = infoText,
        summaryText = summaryText,
        textLines = textLines,
        notificationChannel = notificationChannel,
        category = category,
        template = template,
        isOngoing = isOngoing,
        isGroupSummary = isGroupSummary,
    )

    companion object {
        fun fromSnapshot(
            snapshot: NotificationSnapshot,
            label: String,
            recordedAt: Date = Date(),
        ): NotificationFixture = NotificationFixture(
            label = label,
            recordedAt = recordedAt,
            packageName = snapshot.packageName,
            notificationId = snapshot.notificationId,
            notificationKey = snapshot.notificationKey,
            postTime = snapshot.postTime,
            title = snapshot.title,
            text = snapshot.text,
            bigText = snapshot.bigText,
            subText = snapshot.subText,
            infoText = snapshot.infoText,
            summaryText = snapshot.summaryText,
            textLines = snapshot.textLines,
            notificationChannel = snapshot.notificationChannel,
            category = snapshot.category,
            template = snapshot.template,
            isOngoing = snapshot.isOngoing,
            isGroupSummary = snapshot.isGroupSummary,
        )
    }
}
