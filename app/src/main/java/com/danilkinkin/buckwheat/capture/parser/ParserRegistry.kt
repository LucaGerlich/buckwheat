package com.danilkinkin.buckwheat.capture.parser

import com.danilkinkin.buckwheat.capture.TransactionCandidate
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Picks the first parser that claims support for a notification.
 *
 * The registry is currently empty: the Trade Republic parser is only added once a real
 * notification has been recorded, so that no assumptions about its format are baked in.
 */
@Singleton
class ParserRegistry @Inject constructor(
    private val parsers: Set<@JvmSuppressWildcards BankNotificationParser>,
) {
    fun parse(notification: NotificationSnapshot): TransactionCandidate? =
        parsers.firstOrNull { it.supports(notification) }?.parse(notification)
}
