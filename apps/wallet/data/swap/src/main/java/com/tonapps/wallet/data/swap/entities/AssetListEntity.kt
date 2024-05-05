package com.tonapps.wallet.data.swap.entities

import com.squareup.moshi.Json

data class AssetListEntity(
    @Json(name = "asset_list")
    val assetList: List<StonfiTokenEntity>
)
