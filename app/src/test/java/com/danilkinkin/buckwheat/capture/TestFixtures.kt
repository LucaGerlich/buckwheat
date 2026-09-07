package com.danilkinkin.buckwheat.capture

import com.danilkinkin.buckwheat.capture.parser.BankNotificationParser
import com.danilkinkin.buckwheat.capture.parser.ParserRegistry
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import java.math.BigDecimal
import java.util.Date

internal fun snapshot(
    packageName: String = "de.traderepublic.app",
    notificationKey: String = "key-1",
    notificationId: Int = 1,
    title: String? = null,
    text: String? = null,
    textLines: List<String> = emptyList(),
) = NotificationSnapshot(
    packageName = packageName,
    notificationId = notificationId,
    notificationKey = notificationKey,
    postTime = 1_700_000_000_000L,
    title = title,
    text = text,
    textLines = textLines,
)

internal fun candidate(
    merchant: String = "ALDI SÜD",
    amount: BigDecimal = BigDecimal("24.89"),
    sourceNotificationKey: String? = null,
) = TransactionCandidate(
    amount = amount,
    currency = "EUR",
    merchant = merchant,
    occurredAt = Date(1_700_000_000_000L),
    sourcePackage = "de.traderepublic.app",
    sourceNotificationKey = sourceNotificationKey,
)

internal class FakeParser(
    private val supportedPackage: String,
    private val result: TransactionCandidate? = candidate(),
) : BankNotificationParser {
    var parseCalls = 0
        private set

    override fun supports(notification: NotificationSnapshot): Boolean =
        notification.packageName == supportedPackage

    override fun parse(notification: NotificationSnapshot): TransactionCandidate? {
        parseCalls++
        return result
    }
}

internal fun registryOf(vararg parsers: BankNotificationParser) =
    ParserRegistry(parsers.toSet())
