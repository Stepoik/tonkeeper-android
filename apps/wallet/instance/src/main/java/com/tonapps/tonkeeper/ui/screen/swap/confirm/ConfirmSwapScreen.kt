package com.tonapps.tonkeeper.ui.screen.swap.confirm

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.view.SwapInformationView
import com.tonapps.tonkeeper.ui.screen.swap.confirm.components.ReceiveComponent
import com.tonapps.tonkeeper.ui.screen.swap.confirm.components.SendComponent
import com.tonapps.tonkeeperx.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import uikit.base.BaseFragment
import uikit.extensions.collectFlow
import uikit.navigation.Navigation.Companion.navigation
import uikit.widget.webview.bridge.BridgeWebView

class ConfirmSwapScreen : BaseFragment(R.layout.fragment_swap_confirm), BaseFragment.BottomSheet {
    private val args: ConfirmArgs by lazy { ConfirmArgs(requireArguments()) }

    private val confirmSwapViewModel: ConfirmSwapViewModel by viewModel {
        parametersOf(args.units, args.sendAddress, args.receiveAddress)
    }
    private lateinit var receiveComponent: ReceiveComponent
    private lateinit var sendComponent: SendComponent
    private lateinit var swapInformationView: SwapInformationView
    private lateinit var closeButton: View
    private lateinit var webView: BridgeWebView
    private lateinit var confirmSwapButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveComponent = ReceiveComponent(view, confirmSwapViewModel, this)
        sendComponent = SendComponent(view, confirmSwapViewModel, this)

        swapInformationView = view.findViewById(R.id.swap_information)
        closeButton = view.findViewById(R.id.action_close)
        closeButton.setOnClickListener { finish() }

        webView = view.findViewById(R.id.web_view)
        StonfiBridge(requireContext(), doOnClose = ::finish) {
            webView.jsBridge = this
        }
        webView.loadUrl("https://tonkeeper.ston.fi/swap")

        confirmSwapButton = view.findViewById(R.id.confirm_swap_button)
        confirmSwapButton.setOnClickListener {
            onConfirmButton()
        }
        collectFlow(confirmSwapViewModel.swapInformation, ::setSwapInformation)
    }

    private fun setSwapInformation(swapInformation: SwapInformationVO) {
        swapInformation.swapSimulation?.let {
            swapInformationView.setInformation(it)
        }
    }

    private fun onConfirmButton() {
        lifecycleScope.launch {
            val swapData = confirmSwapViewModel.getSwapData()
            println(swapData)
            webView.executeJS(
                """
                jettonToJetton(
                    "${swapData.wallet}",
                    "${swapData.offerAddress}",
                    "${swapData.askAddress}",
                    "${swapData.units}"
                )
            """.trimIndent()
            )
        }
    }

    companion object {
        fun newInstance(
            units: String,
            sendAddress: String,
            receiveAddress: String
        ): ConfirmSwapScreen {
            val fragment = ConfirmSwapScreen()
            fragment.arguments = ConfirmArgs(
                sendAddress = sendAddress,
                units = units,
                receiveAddress = receiveAddress
            ).toBundle()
            return fragment
        }
    }
}