package com.kobeza.sv.currencyexchanger.data.di

import com.kobeza.sv.currencyexchanger.BuildConfig
import com.kobeza.sv.currencyexchanger.data.datasource.remote.api.CurrencyExchangeRatesApi
import com.kobeza.sv.currencyexchanger.data.datasource.repository.CurrencyExchangeRatesRepositoryImpl
import com.kobeza.sv.currencyexchanger.domain.repository.CurrencyExchangeRatesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    internal fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val requestTimeout: Long = 30
        val builder = OkHttpClient.Builder()
            .connectTimeout(requestTimeout, TimeUnit.SECONDS)
            .readTimeout(requestTimeout, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyExchangeRatesApi(retrofit: Retrofit): CurrencyExchangeRatesApi =
        retrofit.create(CurrencyExchangeRatesApi::class.java)

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        @Binds
        @Singleton
        fun bindCurrencyExchangeRatesRepository(repository: CurrencyExchangeRatesRepositoryImpl): CurrencyExchangeRatesRepository
    }
}