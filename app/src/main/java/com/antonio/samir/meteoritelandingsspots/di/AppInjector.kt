import com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.business.AddressService
import com.antonio.samir.meteoritelandingsspots.service.business.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteNasaService
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteDaoFactory
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaRemoteRepository
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaRemoteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaServerEndPoint
import com.antonio.samir.meteoritelandingsspots.util.*
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
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build()
        )
        .build()

val repositoryModule = module {
    single { MeteoriteRepository(get(), get()) as MeteoriteRepositoryInterface }
}

val networkModule = module {
    single { retrofit.create(NasaServerEndPoint::class.java) }
    single { NetworkUtil(get()) as NetworkUtilInterface }
    single { NasaRemoteRepository(get()) as NasaRemoteRepositoryInterface }
}

val databaseModule = module {
    single { MeteoriteDaoFactory.getMeteoriteDao(get()) }
}

val businessModule = module {
    single { GeoLocationUtil(get()) as GeoLocationUtilInterface }
    single { GPSTracker(get()) as GPSTrackerInterface }
    single { AddressService(get(), get(), get()) as AddressServiceInterface }
    single { MeteoriteNasaService(get(), get(), get()) as MeteoriteServiceInterface }
}

val viewModelModule = module {
    viewModel { MeteoriteListViewModel(get(), get()) }
    viewModel { MeteoriteDetailViewModel(get()) }
}


val appModules = listOf(viewModelModule, repositoryModule, networkModule, databaseModule, businessModule)