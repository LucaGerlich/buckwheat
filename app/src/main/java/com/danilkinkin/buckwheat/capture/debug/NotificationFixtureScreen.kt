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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.LocalWindowInsets
import com.danilkinkin.buckwheat.base.ButtonRow
import com.danilkinkin.buckwheat.base.LocalBottomSheetScrollState
import com.danilkinkin.buckwheat.capture.CaptureViewModel
import com.danilkinkin.buckwheat.data.AppViewModel
import com.danilkinkin.buckwheat.editor.toolbar.Header
import com.danilkinkin.buckwheat.editor.toolbar.MonospaceText
import com.danilkinkin.buckwheat.util.prettyDate

const val NOTIFICATION_FIXTURES_SHEET = "notificationFixtures"

/**
 * Lists recorded notification fixtures and replays them through the regular pipeline.
 */
@Composable
fun NotificationFixtureScreen(
    captureViewModel: CaptureViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val localBottomSheetScrollState = LocalBottomSheetScrollState.current
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    val fixtures by captureViewModel.fixtures.observeAsState(emptyList())

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
                    text = "Notification fixtures",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            if (fixtures.isEmpty()) {
                Spacer(Modifier.height(16.dp))
                MonospaceText("No fixtures recorded yet. Use the notification inspector to save one.")
                Spacer(Modifier.height(16.dp))
            }

            fixtures.forEach { fixture ->
                Header(fixture.label.ifEmpty { fixture.packageName })
                Spacer(Modifier.height(16.dp))
                MonospaceText("Package: ${fixture.packageName}")
                MonospaceText("Title: ${fixture.title ?: "—"}")
                MonospaceText("Text: ${fixture.text ?: "—"}")
                MonospaceText(
                    "Recorded: ${
                        prettyDate(
                            date = fixture.recordedAt,
                            pattern = "dd.MM.yyyy HH:mm:ss",
                            simplifyIfToday = false,
                        )
                    }"
                )
                ButtonRow(
                    text = "Replay through pipeline",
                    iconInset = false,
                    onClick = {
                        val candidate = captureViewModel.replayFixture(fixture)

                        appViewModel.showSnackbar(
                            if (candidate == null) {
                                "Replayed — no parser recognised this notification"
                            } else {
                                "Replayed — candidate recognised"
                            }
                        )
                    },
                )
                ButtonRow(
                    text = "Delete fixture",
                    iconInset = false,
                    onClick = { captureViewModel.deleteFixture(fixture) },
                )
            }
        }
    }
}
