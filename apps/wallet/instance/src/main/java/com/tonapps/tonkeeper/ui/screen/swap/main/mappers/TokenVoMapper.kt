package com.tonapps.tonkeeper.ui.screen.swap.main.mappers

import android.content.Context
import com.tonapps.blockchain.Coin
import com.tonapps.tonkeeper.ui.screen.swap.main.models.TokenVO
import com.tonapps.tonkeeperx.R
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.localization.Localization

class TokenVoMapper(
    private val context: Context
) {
    fun mapFromStonfiToken(stonfiTokenEntity: StonfiTokenEntity, count: String): TokenVO {
        val balance = context.resources.getString(Localization.swap_balance, stonfiTokenEntity.balance, stonfiTokenEntity.symbol)
        return TokenVO(
            balance = balance,
            count = Coin.parseJettonBalance(count, stonfiTokenEntity.decimals).toString(),
            iconURL = stonfiTokenEntity.imageURL,
            symbol = stonfiTokenEntity.symbol
        )
    }
}