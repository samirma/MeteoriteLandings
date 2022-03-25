package com.antonio.samir.meteoritelandingsspots.features.detail

import MeteoriteDetail
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.*
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.showActionBar
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.stateViewModel

@ExperimentalCoroutinesApi
@FlowPreview
class MeteoriteDetailFragment : Fragment(), OnMapReadyCallback {

    private var meteorite: MeteoriteView? = null

    private val viewModel: MeteoriteDetailViewModel by stateViewModel()

    private val args: MeteoriteDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentMeteoriteDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMeteoriteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Exit from full meteorite detail screen to list view
        if (isLandscape()) {
            findNavController().navigateUp()
        }

        observeMeteorite()

        if (arguments != null) {
            viewModel.loadMeteorite(args.meteoriteId)
        }
    }


    private fun observeMeteorite() {

        viewModel.getMeteorite().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Success -> {
                    result.data.let { meteorite ->
                        setMeteorite(meteorite)
                        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                            .getMapAsync(this)
                    }
                    showContent()
                }
                is Error -> showError()
                is InProgress -> showProgressLoader()
            }
        }

    }

    private fun showError() {
        error(getString(R.string.general_error))
    }

    private fun error(messageString: String) {
        binding.progressLoader.visibility = INVISIBLE
        binding.content.visibility = INVISIBLE
        binding.messageTV.visibility = VISIBLE
        binding.messageTV.text = messageString
    }

    private fun showProgressLoader() {
        binding.messageTV.visibility = INVISIBLE
        binding.progressLoader.visibility = VISIBLE
        binding.content.visibility = INVISIBLE
    }

    private fun showContent() {
        binding.messageTV.visibility = INVISIBLE
        binding.progressLoader.visibility = INVISIBLE
        binding.content.visibility = VISIBLE
    }

    fun setCurrentMeteorite(meteoriteId: String) {
        viewModel.loadMeteorite(meteoriteId)
    }

    override fun onMapReady(map: GoogleMap) {

        meteorite?.let {
            val lat = it.reclat
            val log = it.reclong

            Log.d(TAG, "Show meteorite: ${it.id} $lat $log")

            setupMap(it.name, lat, log, map)
        }
    }

    private fun setupMap(meteoriteName: String?, lat: Double, log: Double, map: GoogleMap) {
        map.clear()

        val latLng = LatLng(lat, log)

        map.addMarker(MarkerOptions().position(latLng).title(meteoriteName))
        Log.d(TAG, "Marker added: $lat $log")

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(5f), 2000, null)
    }


    private fun setMeteorite(meteorite: MeteoriteView) {

        this.meteorite = meteorite

        if (!isLandscape()) {
            showActionBar(meteorite.name)
        }

        binding.meteoriteDetail.setContent {
            MeteoriteDetail(itemView = meteorite)
        }

    }

    companion object {

        val TAG: String = MeteoriteDetailFragment::class.java.simpleName

        fun newInstance(meteoriteId: String): MeteoriteDetailFragment {
            return MeteoriteDetailFragment().apply {
                arguments = MeteoriteDetailFragmentArgs(meteoriteId).toBundle()
            }
        }

    }

}

