package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteMigrations.MIGRATION_1_2
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteMigrations.MIGRATION_2_3
import com.antonio.samir.meteoritelandingsspots.data.local.database.AppDataBase
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaNetworkService
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaServerEndPoint
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.detail.mapper.MeteoriteMapper
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import com.antonio.samir.meteoritelandingsspots.service.address.AddressService
import com.antonio.samir.meteoritelandingsspots.service.address.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.monetization.MonetizationImpl
import com.antonio.samir.meteoritelandingsspots.service.monetization.MonetizationInterface
import com.antonio.samir.meteoritelandingsspots.util.*
import io.nodle.sdk.android.Nodle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val localRepositoryModule = module {
    single<MeteoriteLocalRepository> { MeteoriteLocalRepositoryImpl(get()) }
}

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(run<HttpLoggingInterceptor> {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                }
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(NasaServerEndPoint.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build().create(NasaServerEndPoint::class.java)
    }
    single<NetworkUtilInterface> { NetworkUtil(get()) }
    single<MeteoriteRemoteRepository> { NasaNetworkService(get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDataBase::class.java, "meteorites"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build().meteoriteDao()
    }
}

val mappersModule = module {
    factory { MeteoriteMapper() }
    factory { MeteoriteViewMapper(context = get()) }
}

@FlowPreview
val useCaseModule = module {
    factory { GetMeteoriteById(get(), get()) }
    factory { GetMeteorites(get(), get(), get()) }
    factory { FetchMeteoriteList(get()) }
    factory { StartAddressRecover(get()) }
    factory { StatusAddressRecover(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
val businessModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { Geocoder(get()) }
    single<GeoLocationUtilInterface> { GeoLocationUtil(get()) }
    single<GPSTrackerInterface> { GPSTracker(context = get()) }
    single<AddressServiceInterface> { AddressService(get(), get()) }
    single<MeteoriteRepository> { MeteoriteRepositoryImpl(get(), get(), get()) }
    single<MonetizationInterface> {
        val context = get<Context>()
        val nodleKey = context.getString(R.string.nodle_key)
        MonetizationImpl(
            context = context,
            nodleKey = nodleKey
        ).apply {
            init()
            setNodle(Nodle.Nodle())
        }
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel {
        MeteoriteDetailViewModel(
            gpsTracker = get(),
            getMeteoriteById = get()
        )
    }
    viewModel {
        MeteoriteListViewModel(
            stateHandle = get(),
            startAddressRecover = get(),
            statusAddressRecover = get(),
            fetchMeteoriteList = get(),
            gpsTracker = get(),
            dispatchers = get(),
            getMeteorites = get()
        )
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
val appModules =
    listOf(
        viewModelModule,
        useCaseModule,
        mappersModule,
        localRepositoryModule,
        networkModule,
        databaseModule,
        businessModule
    )