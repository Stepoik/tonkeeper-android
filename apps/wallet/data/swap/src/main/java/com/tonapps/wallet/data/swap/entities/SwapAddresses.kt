package com.tonapps.wallet.data.swap.entities

data class SwapAddresses(
    val send: String? = null,
    val receive: String? = null
) {
    val isEmpty get() = run { send == null && receive == null }
}
