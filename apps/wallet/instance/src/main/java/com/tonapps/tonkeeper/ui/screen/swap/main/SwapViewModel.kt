package com.tonapps.tonkeeper.ui.screen.swap.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonapps.blockchain.Coin
import com.tonapps.network.NetworkMonitor
import com.tonapps.tonkeeper.ui.screen.swap.common.formatters.SwapInformationVoFormatter
import com.tonapps.wallet.data.account.WalletRepository
import com.tonapps.wallet.data.swap.SwapRepository
import com.tonapps.wallet.data.swap.SwapSettingsRepository
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SwapViewModel(
    private val swapRepository: SwapRepository,
    private val swapSettingsRepository: SwapSettingsRepository,
    private val walletRepository: WalletRepository,
    private val networkMonitor: NetworkMonitor,
    private val swapInformationVoMapper: SwapInformationVoFormatter
) : ViewModel() {

    private var previousTokenPair = Pair<StonfiTokenEntity?, StonfiTokenEntity?>(null, null)
    private val _swapInformation = MutableStateFlow<SwapInformationEntity?>(null)

    private val _swaps =
        MutableStateFlow<Pair<StonfiTokenEntity?, StonfiTokenEntity?>>(Pair(null, null))
    val swapInformation = combine(_swaps, _swapInformation) { tokens, swapInfo ->
        swapInformationVoMapper.mapToVo(swapInfo, tokens)
    }

    private val _confirmFlow = MutableSharedFlow<NavigationSettings>()
    val confirmFlow = _confirmFlow.asSharedFlow()

    private val _unitsFlow = MutableStateFlow(DEFAULT_UNITS)
    private val unitsFlow = _unitsFlow.debounce(UNITS_DEBOUNCE)
    private val updateIdFlow = MutableStateFlow(0)

    init {
        combine(
            swapSettingsRepository.addressesFlow,
            walletRepository.activeWalletFlow,
            networkMonitor.isOnlineFlow
        ) { addresses, wallet, isOnline ->
            if (!isOnline) {
                return@combine
            }
            val sendToken = addresses.send?.let {
                swapRepository.getToken(wallet.address, it)
            }
            val receiveToken = addresses.receive?.let {
                swapRepository.getToken(wallet.address, it)
            }
            _swaps.value = Pair(sendToken, receiveToken)
        }.launchIn(viewModelScope)

        combine(
            networkMonitor.isOnlineFlow,
            _swaps,
            unitsFlow,
            updateIdFlow
        ) { isOnline, tokens, units, _ ->
            if (!isOnline) {
                return@combine
            }
            if (tokens != previousTokenPair) {
                _swapInformation.value = null
            }
            val sendToken = tokens.first ?: return@combine
            val receiveToken = tokens.second ?: return@combine
            if (units == "0") {
                _swapInformation.value = null
                return@combine
            }
            val swapInfo = swapRepository.simulateSwap(
                sendToken = sendToken.contractAddress,
                receiveToken = receiveToken.contractAddress,
                units = Coin.bigDecimal(units, sendToken.decimals).toString()
            ) ?: return@combine
            previousTokenPair = tokens
            _swapInformation.value = swapInfo
        }.launchIn(viewModelScope)

        startUpdateLoop()
    }

    private fun startUpdateLoop() {
        viewModelScope.launch {
            while (isActive) {
                delay(UPDATE_DELAY)
                updateIdFlow.update { it + 1 }
            }
        }
    }

    fun updateSendValue(value: String?) {
        if (value.isNullOrEmpty()) {
            _unitsFlow.value = DEFAULT_UNITS
            return
        }
        _unitsFlow.value = value
    }

    fun swap() {
        viewModelScope.launch {
            swapRepository.swapTokens()
        }
    }

    fun confirm() {
        val swapInformation = _swapInformation.value ?: return
        viewModelScope.launch {
            _confirmFlow.emit(NavigationSettings(
                units = _unitsFlow.value,
                sendAddress = swapInformation.offerAddresses,
                receiveAddress = swapInformation.askAddresses
            ))
        }
    }

    private companion object {
        const val UPDATE_DELAY = 10000L
        const val UNITS_DEBOUNCE = 500L
        const val DEFAULT_UNITS = "0"
    }
}