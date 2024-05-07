package com.tonapps.wallet.data.swap

import org.koin.dsl.module

val swapModule = module {
    single { SwapSettingsRepository(get()) }
    single { SwapRepository(get()) }
}