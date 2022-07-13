package org.defichain.portfolio

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.defichain.portfolio.settings.DefiAddressHandler
import org.defichain.portfolio.settings.PortfolioSystem
import org.defichain.portfolio.settings.properties.DefiAddress

fun main() = singleWindowApplication(
    title = "DeFi Portfolio",
    state = WindowState(width = 500.dp, height = 800.dp)
) {

    DefiPortfolio()

}

@Composable
fun AddressBox(defiAddresses: Set<DefiAddress>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        defiAddresses.forEach { defiAddress ->
            item {
                Text(text = defiAddress.address + " " + defiAddress.comment)
            }
        }
    }
}

@Composable
fun DefiPortfolio() {
    val injector = Guice.createInjector(DefichainPortfolioModule())
    val portfolioSystem = injector.getInstance<PortfolioSystem>()
    val defiAddressHandler = DefiAddressHandler(portfolioSystem.defiPortfolioHome)
    defiAddressHandler.loadAddresses()

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(

            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.weight(1f)) {
                        AddressBox(defiAddressHandler.addresses)
                    }

                    Button(
                        onClick = {

                        }) {
                        Text("Add Address")
                    }
                    Button(
                        onClick = {

                        }) {
                        Text("Remove Address")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DefiPortfolioPreview() {
    DefiPortfolio()
}