package org.defichain.portfolio.walletinfo

@kotlinx.serialization.Serializable
data class Script(
    val type: String,
    val hex: String,
)
