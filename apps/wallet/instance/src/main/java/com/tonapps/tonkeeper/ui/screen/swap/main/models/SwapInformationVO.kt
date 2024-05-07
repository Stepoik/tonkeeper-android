package com.tonapps.tonkeeper.ui.screen.swap.main.models

data class SwapInformationVO(
    val swapSimulation: SwapSimulationVO?,
    val swapTokens: Pair<TokenVO?, TokenVO?>
)
