package com.danilkinkin.buckwheat.capture

import java.math.BigDecimal
import java.util.Date
import java.util.UUID

enum class CaptureTransactionType {
    PAYMENT,
    REFUND,
    UNKNOWN,
}

/**
 * A payment that was recognised either from a bank notification or from the debug
 * payment simulator. It is deliberately not a Buckwheat [com.danilkinkin.buckwheat.data.entities.Transaction]:
 * candidates are not part of Buckwheat's budget model until the user confirms them.
 */
data class TransactionCandidate(
    val id: String = UUID.randomUUID().toString(),
    val amount: BigDecimal,
    val currency: String,
    val merchant: String?,
    val occurredAt: Date,
    val sourcePackage: String?,
    val sourceNotificationKey: String?,
    val transactionType: CaptureTransactionType = CaptureTransactionType.PAYMENT,
    val confidence: Float = 1f,
    val rawNotificationId: Int? = null,
)
