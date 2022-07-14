package org.defichain.portfolio.walletinfo

import kotlinx.serialization.Serializable

@Serializable
data class Block(
    val hash: String,
    val height: Int,
    val time: Int,
    val medianTime: Int,
)
