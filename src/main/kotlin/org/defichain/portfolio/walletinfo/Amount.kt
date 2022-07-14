package org.defichain.portfolio.walletinfo

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val txIn: String,
    val txOut: String,
    val unspent: String,
)
