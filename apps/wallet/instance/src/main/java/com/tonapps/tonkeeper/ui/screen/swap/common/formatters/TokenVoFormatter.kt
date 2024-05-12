package com.tonapps.tonkeeper.ui.screen.swap.common.formatters

import android.content.Context
import com.tonapps.blockchain.Coin
import com.tonapps.tonkeeper.ui.screen.swap.common.models.TokenVO
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.localization.Localization
import java.math.BigDecimal

class TokenVoFormatter(
    private val context: Context
) {
    fun mapFromStonfiToken(stonfiTokenEntity: StonfiTokenEntity, count: String): TokenVO {
        val tokenBalance =
            Coin.parseJettonBalance(stonfiTokenEntity.balance, stonfiTokenEntity.decimals)
        val balance = context.resources.getString(
            Localization.swap_balance,
            tokenBalance.toString(),
            stonfiTokenEntity.symbol
        )
        val jettonCount = Coin.parseJettonBalance(count, stonfiTokenEntity.decimals)
        val jettonUsdValue = stonfiTokenEntity.dexUsdPrice?.let {
            val usd = jettonCount.toBigDecimal().multiply(BigDecimal(it))
            "$ $usd"
        } ?: "$ 0"
        return TokenVO(
            balance = balance,
            count = jettonCount.toString(),
            iconURL = stonfiTokenEntity.imageURL,
            symbol = stonfiTokenEntity.symbol,
            usdValue = jettonUsdValue
        )
    }
}