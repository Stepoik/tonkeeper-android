package com.tonapps.wallet.data.swap

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tonapps.extensions.prefs
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapAddresses
import com.tonapps.wallet.data.swap.source.LocalDataSource
import com.tonapps.wallet.data.swap.api.StonfiAPI
import com.tonapps.wallet.data.swap.source.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap

class SwapRepository(
    private val context: Context,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var cachedTokens = ConcurrentHashMap<String, List<StonfiTokenEntity>>()
    private val remoteDataSource = RemoteDataSource(getStonfiAPI())
    private val localDataSource = LocalDataSource(context.prefs(PREFS))

    private val _suggestedTokens = MutableStateFlow<List<String>>(emptyList())
    val suggestedTokens = _suggestedTokens.asStateFlow()

    private val _addressesFlow = MutableStateFlow(SwapAddresses())
    val addressesFlow = _addressesFlow.asStateFlow().filter { it.send != null }

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

    suspend fun getTokens(walletAddress: String): List<StonfiTokenEntity> {
        if (!cachedTokens.contains(walletAddress) &&
            cachedTokens[walletAddress]?.isNotEmpty() == true
        ) {
            return cachedTokens[walletAddress] ?: emptyList()
        }
        return getTokensRemote(walletAddress)
    }

    suspend fun getTokensRemote(walletAddress: String): List<StonfiTokenEntity> {
        val assets = remoteDataSource.loadAssets(walletAddress)
        assets?.let {
            cachedTokens[walletAddress] = it.assetList
        }
        return assets?.assetList ?: emptyList()
    }

    suspend fun swapTokens() {
        val tokenPair = _addressesFlow.value
        if (tokenPair.receive != null) {
            selectSend(tokenPair.receive)
            selectReceive(tokenPair.send)
        }
    }

    suspend fun getTokens(walletAddress: String, query: String): List<StonfiTokenEntity> =
        withContext(Dispatchers.IO) {
            val lowerCaseQuery = query.lowercase()
            return@withContext getTokens(walletAddress).filter {
                it.displayName.lowercase().contains(lowerCaseQuery)
            }
        }

    suspend fun getToken(walletAddress: String, token: String): StonfiTokenEntity? {
        val tokens = getTokens(walletAddress)
        return tokens.find { it.contractAddress == token }
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

    private fun getSuggestedTokens(): List<String> {
        return listOf(TON_ADDRESS)
    }

    companion object {
        const val TON_ADDRESS = "EQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAM9c"
        const val BASE_URL = "https://api.ston.fi/v1/"
        const val PREFS = "swap"

        val DEFAULT_SWAPS = SwapAddresses(
            send = TON_ADDRESS,
            receive = null
        )

        private fun getMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        private fun getRetrofit(): Retrofit {
            val moshi = getMoshi()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        private fun getStonfiAPI(): StonfiAPI {
            val retrofit = getRetrofit()
            return retrofit.create(StonfiAPI::class.java)
        }
    }
}