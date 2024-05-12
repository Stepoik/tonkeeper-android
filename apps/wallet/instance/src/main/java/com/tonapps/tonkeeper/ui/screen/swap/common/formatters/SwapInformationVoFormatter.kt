package com.tonapps.tonkeeper.ui.screen.swap.common.formatters

import com.tonapps.blockchain.Coin
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapSimulationVO
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity

class SwapInformationVoFormatter(
    private val tokenVoFormatter: TokenVoFormatter
) {
    fun mapToVo(
        swapInformation: SwapInformationEntity?,
        tokens: Pair<StonfiTokenEntity?, StonfiTokenEntity?>,
        sendUnits: String = ""
    ): SwapInformationVO {
        val sendToken = tokens.first?.let { tokenVoFormatter.mapFromStonfiToken(it, sendUnits) }
        val receiveToken = tokens.second?.let {
            tokenVoFormatter.mapFromStonfiToken(
                stonfiTokenEntity = it,
                count = swapInformation?.askUnits ?: "0"
            )
        }
        val firstToken = tokens.first
        val secondToken = tokens.second
        val swapSimulationVo = if (firstToken != null && secondToken != null) {
            swapInformation?.let {
                mapSwapSimulationVo(
                    swapInformationEntity = it,
                    tokens = Pair(firstToken, secondToken)
                )
            }
        } else {
            null
        }
        return SwapInformationVO(
            swapTokens = Pair(sendToken, receiveToken),
            swapSimulation = swapSimulationVo
        )
    }

    private fun mapSwapSimulationVo(
        swapInformationEntity: SwapInformationEntity,
        tokens: Pair<StonfiTokenEntity, StonfiTokenEntity>
    ): SwapSimulationVO {
        val priceImpact =
            "%.2f".format(swapInformationEntity.priceImpact.toFloat() * 100)
                .replace(',', '.')
        val sendSymbol = tokens.first.symbol
        val receiveSymbol = tokens.second.symbol
        val swapRate = "1 $sendSymbol ≈ ${swapInformationEntity.swapRate} $receiveSymbol"
        val providerFee =
            Coin.parseJettonBalance(swapInformationEntity.feeUnits, tokens.second.decimals)
        val blockchainFee =
            Coin.parseJettonBalance(swapInformationEntity.feePercent, tokens.first.decimals)
        return SwapSimulationVO(
            priceImpact = "$priceImpact%",
            minimumReceived = "${swapInformationEntity.minAskUnits} $receiveSymbol",
            liquidityProviderFee = "$providerFee $receiveSymbol",
            blockchainFee = "$blockchainFee $sendSymbol",
            route = swapInformationEntity.routeAddress,
            provider = "STON.fi",
            swapRate = swapRate
        )
    }
}