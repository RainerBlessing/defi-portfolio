package org.defichain.portfolio.walletinfo

import kotlinx.serialization.Serializable

@Serializable
data class Aggregation(
    val id: String,
    val hid: String,
    val block: Block,
    var script: Script,
    var statistic: Statistic,
    var amount: Amount,
)
