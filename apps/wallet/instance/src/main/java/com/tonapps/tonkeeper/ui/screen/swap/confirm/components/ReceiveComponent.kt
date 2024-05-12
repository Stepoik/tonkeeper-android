package com.tonapps.tonkeeper.ui.screen.swap.confirm.components

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonView
import com.tonapps.tonkeeper.ui.screen.swap.choose.ChooseType
import com.tonapps.tonkeeper.ui.screen.swap.choose.SwapChooseScreen
import com.tonapps.tonkeeper.ui.screen.swap.main.SwapViewModel
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.models.TokenVO
import com.tonapps.tonkeeper.ui.screen.swap.confirm.ConfirmSwapViewModel
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.UIKitColor
import com.tonapps.uikit.color.resolveColor
import uikit.extensions.collectFlow
import uikit.navigation.Navigation

class ReceiveComponent(
    private val view: View,
    private val viewModel: ConfirmSwapViewModel,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val receiveButton: ChooseButtonView = view.findViewById(R.id.receive_choose_button)
    private val receiveBalanceText: AppCompatTextView = view.findViewById(R.id.receive_balance_text)
    private val receiveValueText: AppCompatTextView = view.findViewById(R.id.receive_value)

    init {
        lifecycleOwner.collectFlow(viewModel.swapInformation, ::updateReceiveInformation)
    }

    private fun updateReceiveInformation(info: SwapInformationVO) {
        val receiveToken = info.swapTokens.second
        if (receiveToken != null) {
            setReceiveInformation(receiveToken)
            return
        }
        clearReceiveInformation()
    }

    private fun setReceiveInformation(token: TokenVO) {
        receiveButton.setParameters(token.toChooseButtonParameters())
        receiveBalanceText.text = token.usdValue

        receiveValueText.text = token.count
    }

    private fun clearReceiveInformation() {
        receiveButton.clear()
    }
}