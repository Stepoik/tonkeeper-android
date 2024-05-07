package com.tonapps.wallet.data.swap.source

import com.tonapps.wallet.data.swap.api.StonfiAPI
import com.tonapps.wallet.data.swap.entities.AssetListEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity

class RemoteDataSource(
    private val api: StonfiAPI
) {
    suspend fun loadAssets(walletAddress: String): AssetListEntity? {
        return try {
            api.getAssets(walletAddress)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun simulateSwap(
        sendAddress: String,
        receiveAddress: String,
        units: String,
        slippageTolerance: String
    ): SwapInformationEntity? {
        return try {
            api.getSimulateSwap(
                offerAddress = sendAddress,
                askAddress = receiveAddress,
                units = units,
                tolerance = slippageTolerance
            )
        } catch (e: Exception) {
            null
        }
    }
}