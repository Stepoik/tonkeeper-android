package com.tonapps.tonkeeper.ui.screen.swap.main.components

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.tonapps.tonkeeper.ui.screen.swap.choose.ChooseType
import com.tonapps.tonkeeper.ui.screen.swap.choose.SwapChooseScreen
import com.tonapps.tonkeeper.ui.screen.swap.main.SwapViewModel
import com.tonapps.tonkeeper.ui.screen.swap.main.models.TokenVO
import com.tonapps.tonkeeper.ui.screen.swap.view.ChooseButton
import com.tonapps.uikit.color.R as UIKitR
import com.tonapps.tonkeeperx.R
import uikit.extensions.collectFlow
import uikit.navigation.Navigation

class ReceiveComponent(
    private val view: View,
    private val viewModel: SwapViewModel,
    private val navigation: Navigation?,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) {
    private val receiveButton: ChooseButton = view.findViewById(R.id.receive_choose_button)
    private val receiveBalanceText: AppCompatTextView = view.findViewById(R.id.receive_balance_text)
    private val receiveValueText: AppCompatTextView = view.findViewById(R.id.receive_value)

    init {
        receiveButton.setOnClickListener { navigation?.add(SwapChooseScreen.newInstance(ChooseType.RECEIVE)) }

        lifecycleOwner.collectFlow(viewModel.swaps, ::updateReceiveInformation)
    }

    private fun updateReceiveInformation(tokens: Pair<TokenVO?, TokenVO?>) {
        val receiveToken = tokens.second
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
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(UIKitR.attr.textPrimaryColor, typedValue, true)
        receiveValueText.setTextColor(typedValue.data)
    }

    private fun clearReceiveInformation() {
        receiveButton.clear()

        receiveValueText.text = "0"
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(UIKitR.attr.textTertiaryColor, typedValue, true)
        receiveValueText.setTextColor(typedValue.data)

        receiveBalanceText.text = ""
    }
}