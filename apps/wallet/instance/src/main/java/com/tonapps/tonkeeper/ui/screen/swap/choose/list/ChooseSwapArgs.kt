package com.tonapps.tonkeeper.ui.screen.swap.choose.list

import android.os.Bundle
import uikit.base.BaseArgs

data class ChooseSwapArgs(
    val chooseType: Int
): BaseArgs() {
    private companion object {
        const val CHOOSE_TYPE = "choose_type"
    }

    constructor(bundle: Bundle) : this(
        chooseType = bundle.getInt(CHOOSE_TYPE)
    )

    override fun toBundle() = Bundle().apply {
        putInt(CHOOSE_TYPE, chooseType)
    }
}
