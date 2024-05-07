package com.tonapps.tonkeeper.ui.screen.swap.main.mappers

import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapSimulationVO
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity

class SwapInformationVoMapper(
    private val tokenVoMapper: TokenVoMapper
) {
    fun mapToVo(
        swapInformationEntity: SwapInformationEntity?,
        tokens: Pair<StonfiTokenEntity?, StonfiTokenEntity?>
    ): SwapInformationVO {
        val sendToken = tokens.first?.let { tokenVoMapper.mapFromStonfiToken(it, "") }
        val receiveToken = tokens.second?.let {
            tokenVoMapper.mapFromStonfiToken(
                stonfiTokenEntity = it,
                count = swapInformationEntity?.askUnits ?: "0"
            )
        }
        val sendSymbol = sendToken?.symbol ?: ""
        val receiveSymbol = receiveToken?.symbol ?: ""
        return SwapInformationVO(
            swapTokens = Pair(sendToken, receiveToken),
            swapSimulation = swapInformationEntity?.let {
                mapSwapSimulationVo(
                    swapInformationEntity = it,
                    sendSymbol = sendSymbol,
                    receiveSymbol = receiveSymbol
                )
            }
        )
    }

    private fun mapSwapSimulationVo(
        swapInformationEntity: SwapInformationEntity,
        sendSymbol: String,
        receiveSymbol: String
    ): SwapSimulationVO {
        val swapRate = "1 $sendSymbol ≈ ${swapInformationEntity.swapRate} $receiveSymbol"
        return SwapSimulationVO(
            priceImpact = "${swapInformationEntity.priceImpact}%",
            minimumReceived = "${swapInformationEntity.minAskUnits} $receiveSymbol",
            liquidityProviderFee = "${swapInformationEntity.feeUnits} $receiveSymbol",
            blockchainFee = "${swapInformationEntity.feePercent} $sendSymbol",
            route = swapInformationEntity.routeAddress,
            provider = "STON.fi",
            swapRate = swapRate
        )
    }
}