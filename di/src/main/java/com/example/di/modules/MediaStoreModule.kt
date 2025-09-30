package com.example.di.modules

import com.example.datasources.mediastore.data.SongsLocalDatasourceImpl
import com.example.datasources.mediastore.domain.SongsLocalDatasource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaStoreModule = module {

    single<SongsLocalDatasource> {
        SongsLocalDatasourceImpl(get(named("appContext")))
    }
}