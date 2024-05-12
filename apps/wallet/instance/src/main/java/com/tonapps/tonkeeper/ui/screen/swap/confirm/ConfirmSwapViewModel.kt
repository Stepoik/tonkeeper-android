package com.tonapps.tonkeeper.ui.screen.swap.confirm

import androidx.lifecycle.ViewModel
import com.tonapps.blockchain.Coin
import com.tonapps.network.NetworkMonitor
import com.tonapps.tonkeeper.ui.screen.swap.common.formatters.SwapInformationVoFormatter
import com.tonapps.wallet.data.account.WalletRepository
import com.tonapps.wallet.data.swap.SwapRepository
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import uikit.extensions.collectFlow

class ConfirmSwapViewModel(
    private val units: String,
    private val sendAddress: String,
    private val receiveAddress: String,
    private val swapRepository: SwapRepository,
    private val networkMonitor: NetworkMonitor,
    private val walletRepository: WalletRepository,
    private val swapInformationFormatter: SwapInformationVoFormatter
) : ViewModel() {

    private val _swapInformation = MutableStateFlow<SwapInformationEntity?>(null)
    val swapInformation =
        combine(walletRepository.activeWalletFlow, _swapInformation) { wallet, swapInformation ->
            swapInformation ?: return@combine null
            val sendToken =
                swapRepository.getToken(wallet.address, sendAddress) ?: return@combine null
            val receiveToken =
                swapRepository.getToken(wallet.address, receiveAddress) ?: return@combine null
            return@combine swapInformationFormatter.mapToVo(
                swapInformation = swapInformation,
                tokens = Pair(sendToken, receiveToken),
                sendUnits = swapInformation.offerUnits
            )
        }.filterNotNull()

    init {
        var loadJob: Job? = null
        loadJob = collectFlow(networkMonitor.isOnlineFlow) {
            if (!it) {
                return@collectFlow
            }
            val sendToken =
                swapRepository.getToken(
                    walletRepository.activeWalletFlow.first().address,
                    sendAddress
                ) ?: return@collectFlow
            _swapInformation.value = swapRepository.simulateSwap(
                sendToken = sendAddress,
                receiveToken = receiveAddress,
                units = Coin.bigDecimal(units, sendToken.decimals).toString()
            ) ?: return@collectFlow
            loadJob?.cancel()
        }
    }
}