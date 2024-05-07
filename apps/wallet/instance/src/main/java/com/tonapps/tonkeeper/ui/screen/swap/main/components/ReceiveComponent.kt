package com.tonapps.tonkeeper.ui.screen.swap.main.components

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonView
import com.tonapps.tonkeeper.ui.screen.swap.choose.ChooseType
import com.tonapps.tonkeeper.ui.screen.swap.choose.SwapChooseScreen
import com.tonapps.tonkeeper.ui.screen.swap.main.SwapViewModel
import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.main.models.TokenVO
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.UIKitColor
import com.tonapps.uikit.color.resolveColor
import uikit.extensions.collectFlow
import uikit.navigation.Navigation

class ReceiveComponent(
    private val view: View,
    private val viewModel: SwapViewModel,
    private val navigation: Navigation?,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) {
    private val receiveButton: ChooseButtonView = view.findViewById(R.id.receive_choose_button)
    private val receiveBalanceText: AppCompatTextView = view.findViewById(R.id.receive_balance_text)
    private val receiveValueText: AppCompatTextView = view.findViewById(R.id.receive_value)

    init {
        receiveButton.setOnClickListener { navigation?.add(SwapChooseScreen.newInstance(ChooseType.RECEIVE)) }

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
        receiveBalanceText.text = token.balance

        receiveValueText.text = token.count
        receiveValueText.setTextColor(context.resolveColor(UIKitColor.textPrimaryColor))
    }

    private fun clearReceiveInformation() {
        receiveButton.clear()

        receiveValueText.text = "0"
        receiveValueText.setTextColor(context.resolveColor(UIKitColor.textTertiaryColor))

        receiveBalanceText.text = ""
    }
}