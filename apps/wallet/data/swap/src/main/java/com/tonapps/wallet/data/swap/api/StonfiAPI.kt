package com.tonapps.wallet.data.swap.api

import com.tonapps.wallet.data.swap.entities.AssetListEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface StonfiAPI {
    @GET("wallets/{addr_str}/assets")
    suspend fun getAssets(
        @Path("addr_str") walletAddress: String
    ): AssetListEntity
}