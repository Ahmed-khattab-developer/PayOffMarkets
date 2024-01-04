package com.khattab.payoff.di.module

import android.content.Context
import com.khattab.payoff.repository.ConnectivityRepository
import com.khattab.payoff.repository.MainRepository
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import dagger.Provides

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context) = appContext

    @Provides
    fun provideMainRepository() = MainRepository()

    @Provides
    fun provideConnectivityRepository(@ApplicationContext context: Context) =
        ConnectivityRepository(context)

}