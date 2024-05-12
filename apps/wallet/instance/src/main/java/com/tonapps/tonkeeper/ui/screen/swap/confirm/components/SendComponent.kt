package com.tonapps.tonkeeper.ui.screen.swap.confirm.components

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonView
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.models.TokenVO
import com.tonapps.tonkeeper.ui.screen.swap.confirm.ConfirmSwapViewModel
import com.tonapps.tonkeeperx.R
import uikit.extensions.collectFlow

class SendComponent(
    private val view: View,
    private val viewModel: ConfirmSwapViewModel,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val sendButton: ChooseButtonView = view.findViewById(R.id.send_choose_button)
    private val sendBalanceText: AppCompatTextView = view.findViewById(R.id.send_balance_text)
    private val sendValueText: AppCompatTextView = view.findViewById(R.id.send_value)

    init {
        lifecycleOwner.collectFlow(viewModel.swapInformation, ::updateSendInformation)
    }

    private fun updateSendInformation(info: SwapInformationVO) {
        val sendToken = info.swapTokens.first
        if (sendToken != null) {
            setSendInformation(sendToken)
            return
        }
        clearSendInformation()
    }

    private fun setSendInformation(token: TokenVO) {
        sendButton.setParameters(token.toChooseButtonParameters())
        sendBalanceText.text = token.usdValue
        sendValueText.text = token.count
    }

    private fun clearSendInformation() {
        sendButton.clear()
        sendBalanceText.text = ""
    }
}