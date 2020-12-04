package com.antonio.samir.meteoritelandingsspots.features.detail

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteDetailBinding
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragmentDirections.Companion.actionMeteoriteDetailFragmentToMeteoriteListFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.apache.commons.lang3.StringUtils.EMPTY
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class MeteoriteDetailFragment : Fragment(), OnMapReadyCallback {

    private var meteorite: MeteoriteView? = null

    private val viewModel: MeteoriteDetailViewModel by viewModel()

    val args: MeteoriteDetailFragmentArgs by navArgs()

    companion object {

        const val METEORITE_ID = "METEORITE"

        val TAG = MeteoriteDetailFragment::class.java.simpleName

        fun newInstance(meteoriteId: String): MeteoriteDetailFragment {
            val fragment = MeteoriteDetailFragment()
            fragment.arguments = Bundle().apply {
                putString(METEORITE_ID, meteoriteId)
            }
            return fragment
        }

    }

    private var _binding: FragmentMeteoriteDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeteoriteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        //Exit from full meteorite detail screen to list view
        if (isLandscape) {
            findNavController().popBackStack()
        }

        if (!isLandscape) {
            val navDirections = actionMeteoriteDetailFragmentToMeteoriteListFragment(true)
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                findNavController().navigate(navDirections)
            }
        }

        observeMeteorite()

        if (arguments != null) {
            val meteoriteId = arguments?.getString(METEORITE_ID) ?: EMPTY
            viewModel.loadMeteorite(meteoriteId)
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
                            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
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

        setLocationText(meteorite)

        binding.detail?.title?.text = meteorite.name

        binding.detail?.detail?.year?.text = meteorite.yearString

        binding.detail?.detail?.recclass?.text = meteorite.recclass

        binding.detail?.detail?.mass?.text = meteorite.mass

    }

    private fun setLocationText(meteorite: MeteoriteView) {
        val address = meteorite.address
        binding.detail?.locationTxt?.text = address
        binding.detail?.locationTxt?.visibility = if (meteorite.hasAddress) {
            View.VISIBLE
        } else {
            viewModel.requestAddressUpdate(meteorite)
            View.GONE
        }
    }

}

