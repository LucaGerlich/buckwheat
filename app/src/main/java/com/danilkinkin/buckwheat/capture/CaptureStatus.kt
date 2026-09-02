package com.danilkinkin.buckwheat.capture

/**
 * Lifecycle of an automatically captured transaction.
 *
 * The full set is declared up front because it describes the intended workflow, even
 * though only the early states are produced by the current milestone.
 */
enum class CaptureStatus {
    DETECTED,
    WAITING_FOR_CONFIRMATION,
    CONFIRMED,
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    FAILED,
    IGNORED,
}
