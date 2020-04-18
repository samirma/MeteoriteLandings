import android.location.Geocoder
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteDaoFactory
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaNetworkService
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaServerEndPoint
import com.antonio.samir.meteoritelandingsspots.data.remote.NetworkService
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.AddressService
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.*
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
        .client(OkHttpClient.Builder()
                .addInterceptor(run {
                    val httpLoggingInterceptor = HttpLoggingInterceptor()
                    httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build()
        )
        .build()

val repositoryModule = module {
    single { MeteoriteRepositoryImpl(get(), get()) as MeteoriteLocalRepository }
}

val networkModule = module {
    single { retrofit.create(NasaServerEndPoint::class.java) }
    single { NetworkUtil(get()) as NetworkUtilInterface }
    single { NasaNetworkService(get()) as NetworkService }
}

val databaseModule = module {
    single { MeteoriteDaoFactory.getMeteoriteDao(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
val businessModule = module {
    single { Geocoder(get()) }
    single { GeoLocationUtil(get()) as GeoLocationUtilInterface }
    single { GPSTracker(get()) as GPSTrackerInterface }
    single { AddressService(get(), get()) as AddressServiceInterface }
    single { MeteoriteRepositoryImpl(get(), get()) as com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel { MeteoriteListViewModel(get(), get(), get(), get()) }
    viewModel { MeteoriteDetailViewModel(get(), get()) }
}


val appModules = listOf(viewModelModule, repositoryModule, networkModule, databaseModule, businessModule)