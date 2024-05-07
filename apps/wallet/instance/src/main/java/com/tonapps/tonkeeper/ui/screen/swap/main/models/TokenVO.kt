package com.tonapps.tonkeeper.ui.screen.swap.main.models

import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonParameters

data class TokenVO(
    val balance: String,
    val count: String,
    val iconURL: String,
    val symbol: String
) {
    fun toChooseButtonParameters(): ChooseButtonParameters {
        return ChooseButtonParameters(
            iconURL = iconURL,
            text = symbol
        )
    }
}
