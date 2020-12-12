package com.antonio.samir.meteoritelandingsspots.features.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteDetailBinding
import com.antonio.samir.meteoritelandingsspots.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.ui.extension.showActionBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class MeteoriteDetailFragment : Fragment(), OnMapReadyCallback {

    private var meteorite: MeteoriteView? = null

    private val viewModel: MeteoriteDetailViewModel by viewModel()

    val args: MeteoriteDetailFragmentArgs by navArgs()

    private var _binding: FragmentMeteoriteDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeteoriteDetailBinding.inflate(inflater, container, false)
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

        viewModel.meteorite.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Success -> {
                    result.data.let { meteorite ->
                        if (meteorite == this.meteorite) {
                            if (meteorite.address != this.meteorite?.address) {
                                this.meteorite = meteorite
                                setLocationText(meteorite)
                            }
                        } else {
                            setMeteorite(meteorite)
                            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                                    .getMapAsync(this)
                        }
                    }

                }
            }
        })

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

        setLocationText(meteorite)

        if (!isLandscape()) {
            showActionBar(meteorite.name)
        }

        binding.year.text = meteorite.yearString

        binding.recclass.text = meteorite.recclass

        binding.mass.text = meteorite.mass

    }

    private fun setLocationText(meteorite: MeteoriteView) {
        val address = meteorite.address
        binding.locationTxt.text = address
        binding.locationTxt.visibility = if (meteorite.hasAddress) {
            View.VISIBLE
        } else {
            viewModel.requestAddressUpdate(meteorite)
            View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        val TAG = MeteoriteDetailFragment::class.java.simpleName

        fun newInstance(meteoriteId: String): MeteoriteDetailFragment {
            return MeteoriteDetailFragment().apply {
                arguments = MeteoriteDetailFragmentArgs(meteoriteId).toBundle()
            }
        }

    }

}

