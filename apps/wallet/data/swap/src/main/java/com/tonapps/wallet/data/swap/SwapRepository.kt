package com.tonapps.wallet.data.swap

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tonapps.wallet.data.swap.api.StonfiAPI
import com.tonapps.wallet.data.swap.entities.StonfiTokenEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity
import com.tonapps.wallet.data.swap.source.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap

class SwapRepository(
    private val swapSettingsRepository: SwapSettingsRepository
) {
    private var cachedTokens = ConcurrentHashMap<String, List<StonfiTokenEntity>>()
    private val remoteDataSource = RemoteDataSource(getStonfiAPI())


    suspend fun simulateSwap(
        sendToken: String,
        receiveToken: String,
        units: String
    ): SwapInformationEntity? {
        return remoteDataSource.simulateSwap(
            sendAddress = sendToken,
            receiveAddress = receiveToken,
            units = units,
            slippageTolerance = swapSettingsRepository.getSlippageTolerance()
        )
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
        val tokenPair = swapSettingsRepository.addressesFlow.first()
        if (tokenPair.receive != null) {
            swapSettingsRepository.selectSend(tokenPair.receive)
            swapSettingsRepository.selectReceive(tokenPair.send)
        }
    }

    suspend fun getTokens(walletAddress: String, query: String): List<StonfiTokenEntity> =
        withContext(Dispatchers.IO) {
            val lowerCaseQuery = query.lowercase()
            getTokens(walletAddress).filter {
                it.displayName.lowercase().contains(lowerCaseQuery)
            }
        }

    suspend fun getToken(walletAddress: String, token: String): StonfiTokenEntity? {
        val tokens = getTokens(walletAddress)
        return tokens.find { it.contractAddress == token }
    }

    private companion object {
        const val BASE_URL = "https://api.ston.fi/v1/"

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