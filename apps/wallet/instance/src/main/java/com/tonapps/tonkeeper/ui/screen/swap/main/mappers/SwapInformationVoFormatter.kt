package com.tonapps.tonkeeper.ui.screen.swap.main.mappers

import com.tonapps.blockchain.Coin
import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapSimulationVO
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity

class SwapInformationVoFormatter(
    private val tokenVoMapper: TokenVoFormatter
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
        val firstToken = tokens.first
        val secondToken = tokens.second
        val swapSimulationVo = if (firstToken != null && secondToken != null) {
            swapInformationEntity?.let {
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