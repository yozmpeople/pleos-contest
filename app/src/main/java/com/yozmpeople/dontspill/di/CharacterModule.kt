package com.yozmpeople.dontspill.di

import com.yozmpeople.dontspill.ui.character.CharacterRepository
import com.yozmpeople.dontspill.ui.character.DefaultCharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CharacterModule {

    @Binds
    abstract fun bindCharacterRepository(impl: DefaultCharacterRepository): CharacterRepository
}
