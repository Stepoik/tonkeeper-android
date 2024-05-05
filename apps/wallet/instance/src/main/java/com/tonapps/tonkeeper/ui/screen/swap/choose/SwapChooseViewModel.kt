package com.tonapps.tonkeeper.ui.screen.swap.choose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.ChooseItem
import com.tonapps.tonkeeper.ui.screen.swap.choose.models.SuggestedItemVO
import com.tonapps.wallet.data.account.WalletRepository
import com.tonapps.wallet.data.swap.SwapRepository
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SwapChooseViewModel(
    private val chooseType: Int,
    private val swapRepository: SwapRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _tokens = MutableStateFlow<List<StonfiTokenEntity>?>(null)
    val tokens = _tokens.asStateFlow().filterNotNull().map {
        it.map(ChooseItem.Token::fromStonfiToken)
    }

    private val _close = MutableStateFlow(false)
    val close = _close.asStateFlow()

    val suggestedTokens: Flow<List<SuggestedItemVO>> = combine(
        swapRepository.suggestedTokens,
        walletRepository.activeWalletFlow
    ) { tokens, wallet ->
        val result = mutableListOf<SuggestedItemVO>()
        for (token in tokens) {
            swapRepository.getToken(walletAddress = wallet.address, token)?.let { params ->
                result.add(params.toSuggestedItemVO())
            }
        }
        result
    }

    private val searchQuery = MutableStateFlow("")

    init {
        combine(
            searchQuery,
            walletRepository.activeWalletFlow
        ) { query, wallet ->
            val tokens = swapRepository.getTokens(walletAddress = wallet.address, query = query)
            _tokens.update { tokens }
        }.launchIn(viewModelScope)
    }

    fun onSelect(contractAddress: String) {
        viewModelScope.launch {
            if (chooseType == ChooseType.SEND) {
                swapRepository.selectSend(contractAddress)
            } else {
                swapRepository.selectReceive(contractAddress)
            }
            _close.value = true
        }
    }

    fun search(query: String) {
        searchQuery.update { query }
    }
}

private fun StonfiTokenEntity.toSuggestedItemVO(): SuggestedItemVO {
    return SuggestedItemVO(
        iconURL = imageURL,
        symbol = symbol,
        token = contractAddress
    )
}