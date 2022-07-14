package org.defichain.portfolio.walletinfo

@kotlinx.serialization.Serializable
data class Statistic(
    val txCount: Int,
    val txInCount: Int,
    val txOutCount: Int,
)
