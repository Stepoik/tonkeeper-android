package com.tonapps.wallet.data.swap

import android.content.Context
import com.tonapps.extensions.prefs
import com.tonapps.wallet.data.swap.entities.SwapAddresses
import com.tonapps.wallet.data.swap.source.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SwapSettingsRepository(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _addressesFlow = MutableStateFlow(SwapAddresses())
    val addressesFlow = _addressesFlow.asStateFlow().filter { it.send != null }

    private val localDataSource = LocalDataSource(context.prefs(PREFS))

    private val _suggestedTokens = MutableStateFlow<List<String>>(emptyList())
    val suggestedTokens = _suggestedTokens.asStateFlow()

    init {
        scope.launch {
            val localAddresses = localDataSource.getAddresses()
            _suggestedTokens.value = getSuggestedTokens()
            if (localAddresses.isEmpty) {
                val defaultSwap = DEFAULT_SWAPS
                selectSend(defaultSwap.send)
                selectReceive(defaultSwap.receive)
            } else {
                _addressesFlow.update {
                    it.copy(send = localAddresses.send, receive = localAddresses.receive)
                }
            }
        }
    }

    suspend fun setSlippageTolerance(value: String) {
        localDataSource.setSlippageTolerance(value)
    }

    suspend fun getSlippageTolerance(): String {
        var slippageTolerance = localDataSource.getSlippageTolerance()
        if (slippageTolerance == null) {
            setSlippageTolerance(DEFAULT_TOLERANCE)
            slippageTolerance = DEFAULT_TOLERANCE
        }
        return slippageTolerance
    }

    private fun getSuggestedTokens(): List<String> {
        return listOf(TON_ADDRESS)
    }

    suspend fun selectSend(address: String?) {
        if (address == _addressesFlow.value.receive) {
            selectReceive(null)
        }
        localDataSource.saveSendAddress(address)
        _addressesFlow.update {
            it.copy(send = address)
        }
    }

    suspend fun selectReceive(address: String?) {
        var selectedAddress = address
        if (address == _addressesFlow.value.send) {
            selectedAddress = null
        }
        localDataSource.saveReceiveAddress(selectedAddress)
        _addressesFlow.update {
            it.copy(receive = selectedAddress)
        }
    }

    private companion object {
        const val TON_ADDRESS = "EQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAM9c"
        const val PREFS = "swap"
        const val DEFAULT_TOLERANCE = "1"

        val DEFAULT_SWAPS = SwapAddresses(
            send = TON_ADDRESS,
            receive = null
        )
    }
}