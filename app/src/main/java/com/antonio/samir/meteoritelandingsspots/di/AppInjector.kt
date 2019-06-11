import com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.service.local.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteNasaService
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteDaoFactory
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServerEndPoint
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(NasaServerEndPoint.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

val repositoryModule = module {
    single { MeteoriteRepository(get(), get()) as MeteoriteRepositoryInterface }
}

val networkModule = module {
    single { retrofit.create(NasaServerEndPoint::class.java) }
    single { NetworkUtil(get()) as NetworkUtilInterface }
    single { NasaService(get()) as NasaServiceInterface }
}

val databaseModule = module {
    single { MeteoriteDaoFactory.getMeteoriteDao(get()) }
}

val businessModule = module {
    single { GeoLocationUtil(get()) as GeoLocationUtilInterface }
    single { GPSTracker(get()) as GPSTrackerInterface }
    single { AddressService(get(), get(), get()) as AddressServiceInterface }
    single { MeteoriteNasaService(get(), get(), get()) as MeteoriteService }
}

val viewModelModule = module {
    viewModel { MeteoriteListViewModel(get(), get(), get()) }
    viewModel { MeteoriteDetailViewModel(get()) }
}


val appModules = listOf(viewModelModule, repositoryModule, networkModule, databaseModule, businessModule)