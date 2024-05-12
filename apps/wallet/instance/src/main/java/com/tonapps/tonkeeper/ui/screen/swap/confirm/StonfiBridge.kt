package com.tonapps.tonkeeper.ui.screen.swap.confirm

import android.content.Context
import com.tonapps.tonkeeperx.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import uikit.widget.webview.bridge.JsBridge

class StonfiBridge(
    private val context: Context,
    private val doOnClose: () -> Unit,
    private val doOnLoaded: JsBridge.() -> Unit
) : JsBridge("tonkeeperStonfi") {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var injectCode = ""

    init {
        scope.launch {
            val inputStream = context.resources.openRawResource(R.raw.stonfi_sdk)
            val stonfiSDK = inputStream.bufferedReader().use { it.readText() }
            injectCode = super.jsInjection() + "\n" + stonfiSDK + "\n" + jsCode()
            withContext(Dispatchers.Main) {
                doOnLoaded()
            }
        }
    }

    override val availableFunctions: Array<String>
        get() = arrayOf("close")

    override suspend fun invokeFunction(name: String, args: JSONArray): Any? {
        if (name == "close") {
            doOnClose()
        }
        return null
    }

    override fun jsInjection(): String {
        return injectCode
    }

    fun jsCode(): String {
        return """
//
            const router = new window.DEX.v1.Router({
              tonApiClient: new window.TonWeb.HttpProvider('https://toncenter.com/api/v2/jsonRPC', {apiKey: '092cf20191c4edf38f0d3bb9a9e930a8e6755d7028d5e81cb3cc1d1b95915613'}),
            });
//            
//            async function jettonToTon(walletAddress, jettonAddress, offerAmount) {
//                const txParams = await router.buildSwapJettonToTonTxParams({
//                  userWalletAddress: walletAddress,
//                  offerJettonAddress: jettonAddress, // GEMSTON
//                  offerAmount: new TonWeb.utils.BN(offerAmount),
//                  proxyTonAddress: pTON.v1.address,
//                  minAskAmount: new TonWeb.utils.BN("1"),
//                  queryId: 12345,
//                });
//                window.tonkeeprStonfi.close()
//            }
            
async function jettonToJetton(walletAddress, offerJettonAddress, askJettonAddress, offerAmount) {
                const txParams = await router.buildSwapJettonToJettonTxParams({
                  userWalletAddress: walletAddress,
                  offerJettonAddress: offerJettonAddress,
                  offerAmount: new TonWeb.utils.BN(offerAmount),
                  askJettonAddress: askJettonAddress,
                  minAskAmount: new TonWeb.utils.BN("1"),
                  queryId: 12345,
                });
                console.log(txParams)
                window.tonkeeperStonfi.close()
}
            
            async function tonToJetton(walletAddress, jettonAddress, offerAmount) {
                const txParams = await router.buildSwapTonToJettonTxParams({
                  userWalletAddress: walletAddress, // ! replace with your address
                  proxyTonAddress: pTON.v1.address,
                  offerAmount: new TonWeb.utils.BN(offerAmount),
                  askJettonAddress: jettonAddress,
                  minAskAmount: new TonWeb.utils.BN("1"),
                  queryId: 12345,
                });
                return txParams;
            }
        """.trimIndent()
    }
}