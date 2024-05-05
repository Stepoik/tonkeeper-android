package com.tonapps.tonkeeper.ui.screen.swap.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonapps.tonkeeper.ui.screen.swap.main.mappers.TokenVoMapper
import com.tonapps.tonkeeper.ui.screen.swap.main.models.TokenVO
import com.tonapps.wallet.data.account.WalletRepository
import com.tonapps.wallet.data.swap.SwapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class SwapViewModel(
    private val swapRepository: SwapRepository,
    private val walletRepository: WalletRepository,
    private val tokenVoMapper: TokenVoMapper
) : ViewModel() {

    private val _swaps = MutableStateFlow<Pair<TokenVO?, TokenVO?>>(Pair(null, null))
    val swaps = _swaps.asStateFlow()

    init {
        combine(swapRepository.addressesFlow, walletRepository.activeWalletFlow) { addresses,
                                                                                   wallet ->
            val sendToken = addresses.send?.let {
                swapRepository.getToken(wallet.address, it)?.let { token ->
                    tokenVoMapper.mapFromStonfiToken(token)
                }
            }
            val receiveToken = addresses.receive?.let {
                swapRepository.getToken(wallet.address, it)?.let { token ->
                    tokenVoMapper.mapFromStonfiToken(token)
                }
            }
            _swaps.value = Pair(sendToken, receiveToken)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    fun updateSendValue(value: String) {

    }

    fun swap() {
        viewModelScope.launch {
            swapRepository.swapTokens()
        }
    }
}