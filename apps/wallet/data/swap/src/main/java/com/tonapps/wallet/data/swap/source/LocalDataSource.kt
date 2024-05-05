package com.tonapps.wallet.data.swap.source

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tonapps.extensions.string
import com.tonapps.wallet.data.swap.entities.SwapAddresses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val preferences: SharedPreferences
) {
    suspend fun saveSendAddress(address: String?) = withContext(Dispatchers.IO) {
        preferences.string(Scheme.SEND_ADDRESS, address)
    }

    suspend fun saveReceiveAddress(address: String?) = withContext(Dispatchers.IO) {
        preferences.string(Scheme.RECEIVE_ADDRESS, address)
    }

    suspend fun getAddresses(): SwapAddresses = withContext(Dispatchers.IO) {
        val receiveAddress = preferences.string(Scheme.RECEIVE_ADDRESS)
        val sendAddress = preferences.string(Scheme.SEND_ADDRESS)
        return@withContext SwapAddresses(send = sendAddress, receive = receiveAddress)
    }

    object Scheme {
        const val SEND_ADDRESS = "send_address"
        const val RECEIVE_ADDRESS = "receive_address"
    }
}