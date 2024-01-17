/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.tonapi.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param address 
 * @param isWallet 
 * @param hasMethodPubkey 
 * @param hasMethodSeqno 
 * @param names 
 */


data class WalletDNS (

    @Json(name = "address")
    val address: kotlin.String,

    @Json(name = "is_wallet")
    val isWallet: kotlin.Boolean,

    @Json(name = "has_method_pubkey")
    val hasMethodPubkey: kotlin.Boolean,

    @Json(name = "has_method_seqno")
    val hasMethodSeqno: kotlin.Boolean,

    @Json(name = "names")
    val names: kotlin.collections.List<kotlin.String>

)
