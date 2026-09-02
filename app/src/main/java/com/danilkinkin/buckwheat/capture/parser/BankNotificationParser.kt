package com.danilkinkin.buckwheat.capture.parser

import com.danilkinkin.buckwheat.capture.TransactionCandidate
import com.danilkinkin.buckwheat.notification.NotificationSnapshot

/**
 * Bank specific translation of a notification into a [TransactionCandidate].
 *
 * Implementations must stay free of Android dependencies so they can be unit tested
 * against recorded fixtures.
 */
interface BankNotificationParser {
    fun supports(notification: NotificationSnapshot): Boolean

    fun parse(notification: NotificationSnapshot): TransactionCandidate?
}
