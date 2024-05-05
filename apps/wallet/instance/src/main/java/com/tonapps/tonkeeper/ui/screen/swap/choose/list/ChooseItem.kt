package com.tonapps.tonkeeper.ui.screen.swap.choose.list

import com.tonapps.uikit.list.BaseListItem
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity

sealed class ChooseItem(
    type: Int
): BaseListItem(type) {
    companion object {
        const val SKELETON = 0
        const val TOKEN = 1
    }

    class Skeleton: ChooseItem(SKELETON)

    data class Token(
        val contractAddress: String,
        val iconURL: String,
        val symbol: String,
        val displayName: String,
        val balance: String
    ): ChooseItem(TOKEN) {
        companion object {
            fun fromStonfiToken(tokenEntity: StonfiTokenEntity): Token {
                val stringPattern = "%.${tokenEntity.decimals}f"
                val balanceString = stringPattern.format(tokenEntity.balance.toFloat())
                return Token(
                    contractAddress = tokenEntity.contractAddress,
                    displayName = tokenEntity.displayName,
                    iconURL = tokenEntity.imageURL,
                    symbol = tokenEntity.symbol,
                    balance = balanceString
                )
            }
        }
    }
}