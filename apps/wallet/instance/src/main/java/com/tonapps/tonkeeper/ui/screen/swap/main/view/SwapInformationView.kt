package com.tonapps.tonkeeper.ui.screen.swap.main.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.tonapps.tonkeeper.ui.screen.swap.main.models.SwapSimulationVO
import com.tonapps.tonkeeperx.R
import uikit.widget.RowLayout

class SwapInformationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RowLayout(context, attrs, defStyle) {

    private var priceImpactValue: AppCompatTextView
    private var minimumReceivedValue: AppCompatTextView
    private var providerFeeValue: AppCompatTextView
    private var blockchainFeeValue: AppCompatTextView
    private var routeValue: AppCompatTextView
    private var providerValue: AppCompatTextView
    private var routeText: AppCompatTextView
    private var swapRateText: AppCompatTextView

    init {
        inflate(context, R.layout.view_swap_information, this)
        priceImpactValue = findViewById(R.id.price_impact_text_value)
        minimumReceivedValue = findViewById(R.id.minimum_received_text_value)
        providerFeeValue = findViewById(R.id.provider_fee_text_value)
        blockchainFeeValue = findViewById(R.id.blockchain_fee_text_value)
        routeValue = findViewById(R.id.route_text_value)
        providerValue = findViewById(R.id.provider_text_value)

        swapRateText = findViewById(R.id.swap_rate_text)
        routeText = findViewById(R.id.route_text)
    }

    fun setInformation(swapSimulationVO: SwapSimulationVO) {
        priceImpactValue.text = swapSimulationVO.priceImpact
        minimumReceivedValue.text = swapSimulationVO.minimumReceived
        providerFeeValue.text = swapSimulationVO.liquidityProviderFee
        blockchainFeeValue.text = swapSimulationVO.blockchainFee
        providerValue.text = swapSimulationVO.provider
        swapRateText.text = swapSimulationVO.swapRate

        if (swapSimulationVO.route != null) {
            routeValue.text = swapSimulationVO.route
            routeValue.visibility = VISIBLE
            routeText.visibility = VISIBLE
        } else {
            routeValue.visibility = GONE
            routeText.visibility = GONE
        }
    }
}