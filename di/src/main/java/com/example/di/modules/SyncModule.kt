package com.example.di.modules

import com.example.sync.domain.usecases.SyncRoomWithMediaStoreUseCase
import org.koin.dsl.module

val syncModule = module {


    factory {
        SyncRoomWithMediaStoreUseCase(
        get(),
        get(),
            get(),
            get(),
            get()
        )
    }
}