package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.getDistanceFrom
import com.antonio.samir.meteoritelandingsspots.features.yearString
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.meteorite_detail.*
import kotlinx.android.synthetic.main.meteorite_detail_grid.*
import org.apache.commons.lang3.StringUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MeteoriteDetailFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {

    val viewModel: MeteoriteDetailViewModel by viewModel()

    private var meteoriteId: Meteorite? = null

    val TAG = MeteoriteDetailFragment::class.java.simpleName

    companion object {

        const val METEORITE = "METEORITE"
        const val OPENED_INSIDE_NAVIGATOR = "OPENED_INSIDE_NAVIGATOR"

        fun newInstance(meteorite: Meteorite): MeteoriteDetailFragment {
            val fragment = MeteoriteDetailFragment()
            val args = Bundle()
            args.putParcelable(METEORITE, meteorite)
            args.putBoolean(OPENED_INSIDE_NAVIGATOR, false)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            //Recovering the meteorite to work on it
            meteoriteId = arguments?.getParcelable(METEORITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_meteorite_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape && arguments?.getBoolean(OPENED_INSIDE_NAVIGATOR) == false) {
            findNavController().popBackStack()
        }

        observeMeteorite()
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

    }

    private fun observeMeteorite() {

        viewModel.getMeteorite().observe(viewLifecycleOwner, Observer {
            setMeteorite(it)
        })

        meteoriteId?.let { setCurrentMeteorite(it) }
    }

    fun setCurrentMeteorite(meteorite: Meteorite) {
        viewModel.loadMeteorite(meteorite)
    }

    override fun onMapReady(map: GoogleMap) {

        viewModel.getMeteorite().observe(viewLifecycleOwner, Observer { meteorite ->
            val lat = meteorite.reclat?.toDouble()
            val log = meteorite.reclong?.toDouble()

            Log.d(TAG, "Show meteorite: ${meteorite.id} $lat $log")

            if (lat != null && log != null) {
                setupMap(meteorite.name, lat, log, map)
            }
        })

    }

    private fun setupMap(meteoriteName: String?, lat: Double, log: Double, map: GoogleMap) {
        map.clear()

        val latLng = LatLng(lat, log)

        map.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))
        Log.d(TAG, "Marker added: $lat $log")

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }


    private fun setMeteorite(meteorite: Meteorite) {

        setLocationText(meteorite.address, locationTxt, meteorite)

        val meteoriteName = meteorite.name
        setText(null, this.title, meteoriteName)

        setText(year_label, this.year, meteorite.yearString)

        setText(recclass_label, this.recclass, meteorite.recclass)

        setText(mass_label, this.mass, meteorite.mass)

    }

    private fun setLocationText(address: String?, text: TextView?, meteorite: Meteorite) {

        text?.visibility = if (StringUtils.isNotEmpty(address)) {
            val location = viewModel.getLocation()
            val finalAddress = address + if (location != null) {
                " - ${meteorite.getDistanceFrom(location.latitude, location.longitude)}"
            } else {
                ""
            }
            setText(null, text, finalAddress)
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setText(textFieldLabel: TextView?, textField: TextView?, text: String?) {
        if (StringUtils.isEmpty(text)) {
            textFieldLabel?.visibility = View.GONE
            textField?.visibility = View.GONE
        } else {
            textField?.text = text
            textField?.contentDescription = text
        }
    }

}

