package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import android.location.Geocoder
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteDaoFactory
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaNetworkService
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaServerEndPoint
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.AddressService
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtil
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import com.antonio.samir.meteoritelandingsspots.util.MarketingImpl
import com.antonio.samir.meteoritelandingsspots.util.MarketingInterface
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtilInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(NasaServerEndPoint.URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        OkHttpClient.Builder()
            .addInterceptor(run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    )
    .build()

val localRepositoryModule = module {
    single<MeteoriteLocalRepository> { MeteoriteLocalRepositoryImpl(get(), get()) }
}

val networkModule = module {
    single { retrofit.create(NasaServerEndPoint::class.java) }
    single<NetworkUtilInterface> { NetworkUtil(get()) }
    single<MeteoriteRemoteRepository> { NasaNetworkService(get()) }
}

val databaseModule = module {
    single { MeteoriteDaoFactory.getMeteoriteDao(context = get()) }
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
    single<MarketingInterface> {
        val context = get<Context>()
        MarketingImpl(
            context = context,
            nodleKey = ""
        ).apply {
            init()
        }
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel {
        MeteoriteDetailViewModel(get(), get())
    }
    viewModel {
        MeteoriteListViewModel(
            stateHandle = get(),
            meteoriteRepository = get(),
            gpsTracker = get(),
            addressService = get(),
            dispatchers = get()
        )
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
val appModules =
    listOf(viewModelModule, localRepositoryModule, networkModule, databaseModule, businessModule)