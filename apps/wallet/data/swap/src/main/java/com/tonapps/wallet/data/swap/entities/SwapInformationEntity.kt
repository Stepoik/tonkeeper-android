package com.tonapps.wallet.data.swap.entities

import com.squareup.moshi.Json

data class SwapInformationEntity(
    @Json(name = "offer_address")
    val offerAddresses: String,
    @Json(name = "ask_address")
    val askAddresses: String,
    @Json(name = "route_address")
    val routeAddress: String?,
    @Json(name = "pool_address")
    val poolAddress: String,
    @Json(name = "offer_units")
    val offerUnits: String,
    @Json(name = "ask_units")
    val askUnits: String,
    @Json(name = "slippage_tolerance")
    val slippageTolerance: String,
    @Json(name = "min_ask_units")
    val minAskUnits: String,
    @Json(name = "swap_rate")
    val swapRate: String,
    @Json(name = "price_impact")
    val priceImpact: String,
    @Json(name = "fee_address")
    val feeAddress: String,
    @Json(name = "fee_units")
    val feeUnits: String,
    @Json(name = "fee_percent")
    val feePercent: String
)