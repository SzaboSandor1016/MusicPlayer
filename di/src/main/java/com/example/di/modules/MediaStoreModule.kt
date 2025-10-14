package com.example.di.modules

import com.example.datasources.mediastore.data.MediaStoreLocalDatasourceImpl
import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaStoreModule = module {

    single<MediaStoreLocalDatasource> {
        MediaStoreLocalDatasourceImpl(get(named("appContext")))
    }
}