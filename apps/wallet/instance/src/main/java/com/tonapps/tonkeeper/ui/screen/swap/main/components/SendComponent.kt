package com.tonapps.tonkeeper.ui.screen.swap.main.components

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.tonapps.tonkeeper.ui.screen.swap.choose.ChooseType
import com.tonapps.tonkeeper.ui.screen.swap.choose.SwapChooseScreen
import com.tonapps.tonkeeper.ui.screen.swap.main.SwapViewModel
import com.tonapps.tonkeeper.ui.screen.swap.main.models.TokenVO
import com.tonapps.tonkeeper.ui.screen.swap.view.ChooseButton
import com.tonapps.tonkeeperx.R
import uikit.extensions.collectFlow
import uikit.navigation.Navigation

class SendComponent(
    private val view: View,
    private val viewModel: SwapViewModel,
    private val navigation: Navigation?,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val sendButton: ChooseButton = view.findViewById(R.id.send_choose_button)
    private val sendBalanceText: AppCompatTextView = view.findViewById(R.id.send_balance_text)
    private val sendValueEditText: AppCompatEditText = view.findViewById(R.id.send_value)

    init {
        sendButton.setOnClickListener { navigation?.add(SwapChooseScreen.newInstance(ChooseType.SEND)) }
        lifecycleOwner.collectFlow(viewModel.swaps, ::updateSendInformation)
        sendValueEditText.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                viewModel.updateSendValue("0")
            }
            viewModel.updateSendValue(text.toString())
        }
    }

    private fun updateSendInformation(tokens: Pair<TokenVO?, TokenVO?>) {
        val sendToken = tokens.first
        if (sendToken != null) {
            setSendInformation(sendToken)
            return
        }
        clearSendInformation()
    }

    private fun setSendInformation(token: TokenVO) {
        sendButton.setParameters(token.toChooseButtonParameters())
        sendBalanceText.text = token.balance
    }

    private fun clearSendInformation() {
        sendButton.clear()
        sendBalanceText.text = ""
    }
}