package com.kobeza.sv.currencyexchanger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kobeza.sv.currencyexchanger.R
import com.kobeza.sv.currencyexchanger.presentation.theme.CurrencyExchangerTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainViewModel.UIState,
    action: (MainScreenAction) -> Unit,
) {
    CurrencyExchangerTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopBar(modifier)
            }
        ) { innerPadding ->
            if (state.isLoading) {
                Loading(
                    innerPadding = innerPadding,
                )
            } else {
                Content(
                    innerPadding = innerPadding,
                    state = state,
                    action = action,
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                color = Color.Blue
            )
            .padding(
                bottom = 12.dp,
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = stringResource(R.string.currency_converter),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
) {
    Box(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    state: MainViewModel.UIState,
    action: (MainScreenAction) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(
                top = 16.dp,
                bottom = 16.dp,
            ),
    ) {
        Column {
            BalanceBlock(
                modifier = modifier,
                balances = state.balance,
            )
            ExchangeBlock(
                modifier = modifier,
                state = state,
                action = action,
            )
            Spacer(modifier = modifier.weight(1f))
            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                    ),
                enabled = state.isExchangeButtonActive,
                onClick = {
                    action(MainScreenAction.Exchange)
                }
            ) {
                Text(
                    modifier = modifier
                        .padding(16.dp),
                    text = stringResource(R.string.submit),
                    color = Color.White,
                )
            }
        }
    }

    when (val info = state.dialogInfo) {
        is MainViewModel.DialogInfo.ErrorDialog -> CurrencyConvertedDialog(
            modifier = modifier,
            title = stringResource(R.string.oops),
            description = stringResource(R.string.something_went_wrong),
            buttonText = stringResource(R.string.retry),
            onDismiss = { action(MainScreenAction.Retry) },
        )

        is MainViewModel.DialogInfo.SuccessDialogInfo -> {
            val description = stringResource(
                R.string.success_dialog_description,
                info.amountSold,
                info.amountReceived,
            )
            val commission = if (info.commission.isNotEmpty()) {
                stringResource(
                    R.string.success_dialog_description_commission,
                    info.commission,
                )
            } else ""
            CurrencyConvertedDialog(
                modifier = modifier,
                title = stringResource(R.string.currency_converted),
                description = "$description${commission}",
                buttonText = stringResource(R.string.done),
                onDismiss = { action(MainScreenAction.SuccessDialogShown) },
            )
        }

        else -> {}
    }
}

@Composable
private fun BalanceBlock(
    modifier: Modifier = Modifier,
    balances: List<String>,
) {
    Text(
        modifier = modifier
            .padding(
                start = 16.dp,
            ),
        text = stringResource(R.string.my_balances),
        color = Color.Gray,
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 24.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(36.dp),
    ) {
        items(balances) { item ->
            BalanceItem(
                item = item,
            )
        }
    }
}

@Composable
private fun BalanceItem(
    item: String,
) {
    Text(
        text = item,
        color = Color.Black,
        fontSize = 24.sp,
    )
}

@Composable
private fun ExchangeBlock(
    modifier: Modifier = Modifier,
    state: MainViewModel.UIState,
    action: (MainScreenAction) -> Unit,
) {
    Text(
        modifier = modifier
            .padding(
                start = 16.dp,
            ),
        text = stringResource(R.string.currency_exchange),
        color = Color.Gray,
    )
    ExchangeItem(
        modifier = modifier,
        icon = Icons.Rounded.KeyboardArrowUp,
        iconBackground = Color.Red,
        text = stringResource(R.string.sell),
        textColor = Color.Black,
        selectedCurrency = state.currencyToSell,
        readOnly = false,
        amount = state.amountToSell,
        currencies = state.availableCurrencies,
        currencySelectedAction = {
            action(MainScreenAction.CurrencyToSellSelected(it))
        },
        amountSelectedAction = {
            action(
                MainScreenAction.AmountToSellSelected(it)
            )
        },
    )
    HorizontalDivider(
        modifier = modifier
            .padding(
                start = 56.dp,
                end = 16.dp,
            )
    )
    ExchangeItem(
        modifier = modifier,
        icon = Icons.Rounded.KeyboardArrowDown,
        iconBackground = Color.Green,
        text = stringResource(R.string.buy),
        textColor = Color.Green,
        selectedCurrency = state.currencyToBuy,
        currencies = state.availableCurrencies,
        amount = state.amountToBuy,
        readOnly = true,
        currencySelectedAction = {
            action(MainScreenAction.CurrencyToBuySelected(it))
        },
        amountSelectedAction = {},
    )
}

@Composable
private fun ExchangeItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBackground: Color,
    text: String,
    textColor: Color,
    currencies: Set<String>,
    selectedCurrency: String,
    amount: String,
    readOnly: Boolean,
    currencySelectedAction: (currency: String) -> Unit,
    amountSelectedAction: (amount: String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.operation_type),
            tint = Color.White,
            modifier = modifier
                .size(36.dp)
                .background(
                    color = iconBackground,
                    shape = RoundedCornerShape(36.dp)
                )
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            text = text,
            color = Color.Black,
        )
        BasicTextField(
            readOnly = readOnly,
            value = amount,
            onValueChange = {
                if (isValidNumericInput(it)) {
                    amountSelectedAction(it)
                }
            },
            textStyle = TextStyle(
                fontSize = 24.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        Spacer(modifier = modifier.width(8.dp))
        CurrencyDropdownMenu(
            modifier = modifier,
            selectedCurrency = selectedCurrency,
            currencies = currencies,
            action = currencySelectedAction,
        )
    }
}

fun isValidNumericInput(input: String): Boolean {
    if (input.isEmpty()) return true
    if (input.startsWith("0") && input.length > 1 && input[1] != '.') {
        return false
    }
    val regex = Regex("^\\d*(\\.\\d{0,2})?\$")
    return input.matches(regex)
}

@Composable
private fun CurrencyDropdownMenu(
    modifier: Modifier,
    selectedCurrency: String,
    currencies: Set<String>,
    action: (currency: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable {
                    expanded = expanded.not()
                }
        ) {
            Text(
                text = selectedCurrency,
                color = Color.Black,
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.operation_type),
                modifier = modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        action(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencyConvertedDialog(
    modifier: Modifier,
    title: String,
    description: String,
    buttonText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        },
        title = {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = title, fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = description,
                textAlign = TextAlign.Center,
            )
        }
    )
}

sealed interface MainScreenAction {
    data class CurrencyToSellSelected(
        val currency: String,
    ) : MainScreenAction

    data class CurrencyToBuySelected(
        val currency: String,
    ) : MainScreenAction

    data class AmountToSellSelected(
        val amount: String,
    ) : MainScreenAction

    data object Retry : MainScreenAction

    data object Exchange : MainScreenAction

    data object SuccessDialogShown : MainScreenAction
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CurrencyExchangerTheme {
        MainScreen(
            state = MainViewModel.UIState(),
            action = {},
        )
    }
}