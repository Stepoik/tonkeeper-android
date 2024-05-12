package com.tonapps.tonkeeper.ui.screen.swap.confirm

import android.os.Bundle

data class ConfirmArgs(
    val sendAddress: String,
    val receiveAddress: String,
    val units: String
) {
    private companion object {
        const val SEND_ADDRESS = "send_address"
        const val RECEIVE_ADDRESS = "receive_address"
        const val UNITS = "units"
    }

    constructor(bundle: Bundle): this(
        sendAddress = bundle.getString(SEND_ADDRESS)!!,
        receiveAddress = bundle.getString(RECEIVE_ADDRESS)!!,
        units = bundle.getString(UNITS)!!
    )

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(SEND_ADDRESS, sendAddress)
        bundle.putString(RECEIVE_ADDRESS, receiveAddress)
        bundle.putString(UNITS, units)
        return bundle
    }
}
