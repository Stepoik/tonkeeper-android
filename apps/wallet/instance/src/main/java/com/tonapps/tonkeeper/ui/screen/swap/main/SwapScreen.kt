package com.tonapps.tonkeeper.ui.screen.swap.main

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import com.tonapps.tonkeeper.sign.SignRequestEntity
import com.tonapps.tonkeeper.ui.screen.root.RootViewModel
import com.tonapps.tonkeeper.ui.screen.swap.main.components.ReceiveComponent
import com.tonapps.tonkeeper.ui.screen.swap.main.components.SendComponent
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.view.SwapInformationView
import com.tonapps.tonkeeper.ui.screen.swap.confirm.ConfirmSwapScreen
import com.tonapps.tonkeeperx.BuildConfig
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.UIKitColor
import com.tonapps.uikit.color.resolveColor
import com.tonapps.wallet.localization.Localization
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import uikit.base.BaseFragment
import uikit.extensions.applyNavBottomPadding
import uikit.extensions.collectFlow
import uikit.extensions.getDimensionPixelSize
import uikit.navigation.Navigation.Companion.navigation
import uikit.widget.webview.bridge.BridgeWebView

class SwapScreen : BaseFragment(R.layout.fragment_swap), BaseFragment.BottomSheet {

    private val args: SwapArgs by lazy { SwapArgs(requireArguments()) }

    private val rootViewModel: RootViewModel by activityViewModel()

    private val swapViewModel: SwapViewModel by viewModel()

    private lateinit var webView: BridgeWebView
    private lateinit var swapButton: View
    private lateinit var closeButton: View
    private lateinit var receiveComponent: ReceiveComponent
    private lateinit var sendComponent: SendComponent
    private lateinit var swapInformationView: SwapInformationView
    private lateinit var submitButton: View
    private lateinit var submitButtonText: AppCompatTextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendComponent = SendComponent(view, swapViewModel, navigation, this)
        receiveComponent = ReceiveComponent(view, swapViewModel, navigation, this, requireContext())

        submitButton = view.findViewById(R.id.submit_button)
        submitButtonText = view.findViewById(R.id.button_text)

        swapInformationView = view.findViewById(R.id.swap_information)

        webView = view.findViewById(R.id.web)
        webView.clipToPadding = false
        webView.applyNavBottomPadding(requireContext().getDimensionPixelSize(uikit.R.dimen.offsetMedium))
        webView.loadUrl(getUri().toString())
        webView.jsBridge = StonfiBridge2(
            address = args.address,
            close = ::finish,
            sendTransaction = ::sing
        )

        swapButton = view.findViewById(R.id.action_swap)
        swapButton.setOnClickListener { swapViewModel.swap() }

        closeButton = view.findViewById(R.id.action_close)
        closeButton.setOnClickListener { finish() }

        collectFlow(swapViewModel.swapInformation, ::setSwapInformation)
        collectFlow(swapViewModel.confirmFlow, ::onConfirm)
    }

    private fun onConfirm(navigationSettings: NavigationSettings) {
        navigation?.add(
            ConfirmSwapScreen.newInstance(
                units = navigationSettings.units,
                sendAddress = navigationSettings.sendAddress,
                receiveAddress = navigationSettings.receiveAddress
            )
        )
    }

    private fun setSwapInformation(swapInformation: SwapInformationVO?) {
        updateButtonState(swapInformation)
        if (swapInformation?.swapSimulation == null) {
            swapInformationView.visibility = View.GONE
            return
        }
        swapInformationView.setInformation(swapInformation.swapSimulation)
        swapInformationView.visibility = View.VISIBLE
    }

    private fun updateButtonState(swapInformation: SwapInformationVO?) {
        if (swapInformation == null || swapInformation.swapTokens.second == null) {
            submitButtonText.text = "Choose token"
            submitButton.setBackgroundResource(R.drawable.bg_swap_item)
            submitButton.setOnClickListener(null)
            return
        }
        if (swapInformation.swapSimulation == null) {
            submitButtonText.text = "Enter amount"
            submitButton.setBackgroundResource(R.drawable.bg_swap_item)
            submitButton.setOnClickListener(null)
            return
        }
        submitButtonText.text = "Continue"
        submitButton.setBackgroundResource(R.drawable.bg_button_primary)
        submitButton.setOnClickListener { swapViewModel.confirm() }
    }

    private fun getUri(): Uri {
        val builder = args.uri.buildUpon()
        builder.appendQueryParameter("clientVersion", BuildConfig.VERSION_NAME)
        builder.appendQueryParameter("ft", args.fromToken)
        args.toToken?.let {
            builder.appendQueryParameter("tt", it)
        }
        return builder.build()
    }

    private suspend fun sing(
        request: SignRequestEntity
    ): String {
        return rootViewModel.requestSign(requireContext(), request)
    }

    override fun onDestroyView() {
        webView.destroy()
        super.onDestroyView()
    }

    companion object {

        fun newInstance(
            uri: Uri,
            address: String,
            fromToken: String,
            toToken: String? = null
        ): SwapScreen {
            val fragment = SwapScreen()
            fragment.arguments = SwapArgs(uri, address, fromToken, toToken).toBundle()
            return fragment
        }
    }
}