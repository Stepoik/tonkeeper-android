package com.tonapps.tonkeeper.ui.screen.swap.main.models

data class SwapSimulationVO(
    val priceImpact: String,
    val minimumReceived: String,
    val liquidityProviderFee: String,
    val blockchainFee: String,
    val route: String?,
    val provider: String,
    val swapRate: String,
)