package com.tonapps.tonkeeper.ui.screen.swap.choose.models

import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonParameters

data class SuggestedItemVO(
    val iconURL: String,
    val symbol: String,
    val token: String
) {
    fun toChooseButtonParameters(): ChooseButtonParameters {
        return ChooseButtonParameters(
            iconURL = iconURL,
            text = symbol
        )
    }
}
