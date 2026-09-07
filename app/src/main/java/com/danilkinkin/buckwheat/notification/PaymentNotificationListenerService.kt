package com.danilkinkin.buckwheat.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.danilkinkin.buckwheat.capture.CaptureCoordinator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receives Android notifications and forwards them to the capture pipeline.
 *
 * Deliberately free of business logic: it only converts the platform object into a
 * [NotificationSnapshot] and hands it to the [CaptureCoordinator].
 */
@AndroidEntryPoint
class PaymentNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var captureCoordinator: CaptureCoordinator

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn ?: return

        if (notification.packageName == packageName) return

        captureCoordinator.onNotification(NotificationExtractor.extract(notification))
    }

    companion object {
        /**
         * Whether the user granted notification access to this app.
         */
        fun isNotificationAccessGranted(context: Context): Boolean {
            val component = ComponentName(context, PaymentNotificationListenerService::class.java)

            return Settings.Secure
                .getString(context.contentResolver, ENABLED_NOTIFICATION_LISTENERS)
                ?.split(":")
                ?.mapNotNull { ComponentName.unflattenFromString(it) }
                ?.any { it == component }
                ?: false
        }

        /**
         * Opens the system screen where notification access can be granted.
         */
        fun openNotificationAccessSettings(context: Context) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }

        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    }
}
