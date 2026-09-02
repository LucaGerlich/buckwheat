package com.danilkinkin.buckwheat.capture.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.LocalWindowInsets
import com.danilkinkin.buckwheat.base.ButtonRow
import com.danilkinkin.buckwheat.base.LocalBottomSheetScrollState
import com.danilkinkin.buckwheat.capture.CaptureViewModel
import com.danilkinkin.buckwheat.editor.toolbar.Header
import com.danilkinkin.buckwheat.editor.toolbar.MonospaceText
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import com.danilkinkin.buckwheat.notification.PaymentNotificationListenerService
import com.danilkinkin.buckwheat.util.prettyDate
import java.util.Date

const val NOTIFICATION_INSPECTOR_SHEET = "notificationInspector"

/**
 * Developer tool that shows the raw payload of every notification the listener service
 * received, and allows recording one of them as a replayable fixture.
 */
@Composable
fun NotificationInspectorScreen(
    captureViewModel: CaptureViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val localBottomSheetScrollState = LocalBottomSheetScrollState.current
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    val notifications by captureViewModel.observedNotifications.observeAsState(emptyList())
    val accessGranted = PaymentNotificationListenerService.isNotificationAccessGranted(context)

    Surface(Modifier.padding(top = localBottomSheetScrollState.topPadding)) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = navigationBarHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Notification inspector",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Header("Notification access")
            Spacer(Modifier.height(16.dp))
            MonospaceText("Status: ${if (accessGranted) "granted" else "not granted"}")
            ButtonRow(
                text = "Open notification access settings",
                iconInset = false,
                onClick = {
                    PaymentNotificationListenerService.openNotificationAccessSettings(context)
                },
            )

            Header("Received notifications (${notifications.size})")

            if (notifications.isEmpty()) {
                Spacer(Modifier.height(16.dp))
                MonospaceText("No notifications received yet.")
                Spacer(Modifier.height(16.dp))
            }

            notifications.forEach { snapshot ->
                NotificationSnapshotRow(
                    snapshot = snapshot,
                    onSaveFixture = {
                        captureViewModel.saveFixture(snapshot, defaultFixtureLabel(snapshot))
                    },
                )
            }

            if (notifications.isNotEmpty()) {
                ButtonRow(
                    text = "Clear list",
                    iconInset = false,
                    onClick = { captureViewModel.clearObservedNotifications() },
                )
            }
        }
    }
}

@Composable
private fun NotificationSnapshotRow(
    snapshot: NotificationSnapshot,
    onSaveFixture: () -> Unit,
) {
    Spacer(Modifier.height(16.dp))
    MonospaceText("Package: ${snapshot.packageName}")
    MonospaceText("Title: ${snapshot.title ?: "—"}")
    MonospaceText("Text: ${snapshot.text ?: "—"}")
    MonospaceText("BigText: ${snapshot.bigText ?: "—"}")
    MonospaceText("SubText: ${snapshot.subText ?: "—"}")
    MonospaceText("InfoText: ${snapshot.infoText ?: "—"}")
    MonospaceText("SummaryText: ${snapshot.summaryText ?: "—"}")
    MonospaceText("TextLines: ${snapshot.textLines.joinToString(" | ").ifEmpty { "—" }}")
    MonospaceText("Channel: ${snapshot.notificationChannel ?: "—"}")
    MonospaceText("Template: ${snapshot.template ?: "—"}")
    MonospaceText("Notification ID: ${snapshot.notificationId}")
    MonospaceText("Notification Key: ${snapshot.notificationKey}")
    MonospaceText(
        "Post Time: ${
            prettyDate(
                date = Date(snapshot.postTime),
                pattern = "dd.MM.yyyy HH:mm:ss",
                simplifyIfToday = false,
            )
        }"
    )
    ButtonRow(
        text = "Save as fixture",
        iconInset = false,
        onClick = onSaveFixture,
    )
}

internal fun defaultFixtureLabel(snapshot: NotificationSnapshot): String =
    listOfNotNull(snapshot.packageName, snapshot.title).joinToString(" · ")
