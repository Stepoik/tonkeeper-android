package com.tonapps.wallet.data.swap.source

import com.tonapps.wallet.data.swap.api.StonfiAPI
import com.tonapps.wallet.data.swap.entities.AssetListEntity

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
}