package com.tonapps.wallet.data.swap.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StonfiTokenEntity(
    @Json(name = "contract_address")
    val contractAddress: String,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "image_url")
    val imageURL: String,
    @Json(name = "decimals")
    val decimals: Int,
    @Json(name = "kind")
    val kind: Kind,
    @Json(name = "deprecated")
    val deprecated: Boolean,
    @Json(name = "community")
    val community: Boolean,
    @Json(name = "blacklisted")
    val blacklisted: Boolean,
    @Json(name = "default_symbol")
    val defaultSymbol: Boolean,
    @Json(name = "balance")
    val balance: String = "0"
) {

    enum class Kind(val kind: String) {
        @Json(name = "Jetton") JETTON("Jetton"),
        @Json(name = "Ton") TON("Ton"),
        @Json(name = "Wton") WTON("Wton")
    }
}
