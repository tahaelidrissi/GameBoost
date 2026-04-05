package com.gameboost.frontend.di

import android.content.Context
import com.gameboost.frontend.data.api.ApiService
import com.gameboost.frontend.data.api.RetrofitClient
import com.gameboost.frontend.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Singleton
    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        return RetrofitClient.getInstance(context).create(ApiService::class.java)
    }
}
