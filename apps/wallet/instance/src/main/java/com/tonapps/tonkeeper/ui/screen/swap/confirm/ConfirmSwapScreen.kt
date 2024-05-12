package com.tonapps.tonkeeper.ui.screen.swap.confirm

import android.os.Bundle
import android.view.View
import com.tonapps.tonkeeper.ui.screen.swap.common.models.SwapInformationVO
import com.tonapps.tonkeeper.ui.screen.swap.common.view.SwapInformationView
import com.tonapps.tonkeeper.ui.screen.swap.confirm.components.ReceiveComponent
import com.tonapps.tonkeeper.ui.screen.swap.confirm.components.SendComponent
import com.tonapps.tonkeeperx.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import uikit.base.BaseFragment
import uikit.extensions.collectFlow

class ConfirmSwapScreen : BaseFragment(R.layout.fragment_swap_confirm), BaseFragment.BottomSheet {
    private val args: ConfirmArgs by lazy { ConfirmArgs(requireArguments()) }

    private val confirmSwapViewModel: ConfirmSwapViewModel by viewModel {
        parametersOf(args.units, args.sendAddress, args.receiveAddress)
    }
    private lateinit var receiveComponent: ReceiveComponent
    private lateinit var sendComponent: SendComponent
    private lateinit var swapInformationView: SwapInformationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveComponent = ReceiveComponent(view, confirmSwapViewModel, this)
        sendComponent = SendComponent(view, confirmSwapViewModel, this)

        swapInformationView = view.findViewById(R.id.swap_information)

        collectFlow(confirmSwapViewModel.swapInformation, ::setSwapInformation)
    }

    private fun setSwapInformation(swapInformation: SwapInformationVO) {
        swapInformation.swapSimulation?.let {
            swapInformationView.setInformation(it)
        }
    }

    companion object {
        fun newInstance(units: String, sendAddress: String, receiveAddress: String): ConfirmSwapScreen {
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