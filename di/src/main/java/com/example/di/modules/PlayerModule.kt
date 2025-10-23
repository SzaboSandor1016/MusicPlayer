package com.example.di.modules

import com.example.features.player.presentation.viewmodel.ViewModelPlayer
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playerModule = module {

    viewModelOf(::ViewModelPlayer)
}