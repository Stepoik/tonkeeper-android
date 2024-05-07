package com.tonapps.wallet.data.swap.api

import com.tonapps.wallet.data.swap.entities.AssetListEntity
import com.tonapps.wallet.data.swap.entities.SwapInformationEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StonfiAPI {
    @GET("wallets/{addr_str}/assets")
    suspend fun getAssets(
        @Path("addr_str") walletAddress: String
    ): AssetListEntity

    @POST("swap/simulate")
    suspend fun getSimulateSwap(
        @Query("offer_address") offerAddress: String,
        @Query("ask_address") askAddress: String,
        @Query("units") units: String,
        @Query("slippage_tolerance") tolerance: String
    ): SwapInformationEntity
}