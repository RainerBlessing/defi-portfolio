package org.defichain.portfolio

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun AddressBox(defiAddresses: Set<DefiAddress>) {
    // Each cell of a column must have the same weight.
    val column1Weight = .3f // 30%
    val column2Weight = .7f // 70%

    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Name", weight = column1Weight)
                TableCell(text = "Addresse", weight = column2Weight)
            }
        }
        item(defiAddresses) {
            defiAddresses.forEach { defiAddress ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = defiAddress.comment, weight = column1Weight)
                    TableCell(text = defiAddress.address, weight = column2Weight)
                }
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
                topBar = {
                    TopAppBar(
                        title = { Text("DeFi Addresses") },
                        navigationIcon = {
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(Icons.Filled.Menu, contentDescription = null)
                            }
                        },
                    )
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Box(Modifier.weight(1f)) {
                            AddressBox(defiAddressHandler.addresses)
                        }


                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            onClick = {

                            }) {
                            Text("Add Address")
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Address"
                            )
                        }
                        Button(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            onClick = {

                            }) {
                            Text("Remove Address")
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove Address"
                            )
                        }
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