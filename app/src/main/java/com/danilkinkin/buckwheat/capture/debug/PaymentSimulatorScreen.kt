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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilkinkin.buckwheat.LocalWindowInsets
import com.danilkinkin.buckwheat.base.ButtonRow
import com.danilkinkin.buckwheat.base.LocalBottomSheetScrollState
import com.danilkinkin.buckwheat.capture.CaptureViewModel
import com.danilkinkin.buckwheat.data.AppViewModel
import com.danilkinkin.buckwheat.editor.toolbar.MonospaceText
import java.math.BigDecimal

const val PAYMENT_SIMULATOR_SHEET = "paymentSimulator"

/**
 * Injects a manually entered payment into the capture pipeline, so the later capture UI
 * can be developed independently of any bank parser.
 */
@Composable
fun PaymentSimulatorScreen(
    captureViewModel: CaptureViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val localBottomSheetScrollState = LocalBottomSheetScrollState.current
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    var merchant by remember { mutableStateOf("ALDI SÜD") }
    var amount by remember { mutableStateOf("24.89") }
    var currency by remember { mutableStateOf("EUR") }

    val parsedAmount = amount.trim().toBigDecimalOrNull()

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
                    text = "Payment simulator",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                isError = parsedAmount == null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Spacer(Modifier.height(8.dp))
            MonospaceText("Date: now")
            Spacer(Modifier.height(8.dp))

            ButtonRow(
                text = "Simulate payment",
                iconInset = false,
                onClick = {
                    if (parsedAmount == null) {
                        appViewModel.showSnackbar("Amount is not a valid number")
                        return@ButtonRow
                    }

                    captureViewModel.simulatePayment(
                        merchant = merchant.trim(),
                        amount = parsedAmount,
                        currency = currency.trim().uppercase(),
                    )

                    appViewModel.showSnackbar("Candidate sent through capture pipeline")
                },
            )
        }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(replace(',', '.'))
} catch (e: NumberFormatException) {
    null
}
