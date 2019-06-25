package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel.MeteoriteDetailViewModel
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

    private var meteoriteId: String? = null

    private var map: GoogleMap? = null
    private var isUiDone = false


    companion object {

        const val METEORITE = "METEORITE"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            //Recovering the meteorite to work on it
            meteoriteId = arguments?.getString(METEORITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_meteorite_detail, container, false)

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        observeMeteorite()

        return view
    }

    private fun observeMeteorite() {

        viewModel.getMeteorite().observe(this, Observer {
            setMeteorite(it)
        })

        meteoriteId?.let { viewModel.loadMeteoriteById(it) }
    }


    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }

    private fun setupMap(meteoriteName: String?, lat: Double, log: Double) {
        map?.clear()

        val latLng = LatLng(lat, log)

        map?.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        map?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }


    private fun setMeteorite(meteorite: Meteorite) {
        setLocationText(meteorite.address, view?.findViewById(R.id.location), meteorite)

        if (!isUiDone) {
            isUiDone = true
            val meteoriteName = meteorite.name
            setText(null, this.title, meteoriteName)

            setText(year_label, this.year, meteorite.yearString)

            setText(recclass_label, this.recclass, meteorite.recclass)

            setText(mass_label, this.mass, meteorite.mass)

            val lat = meteorite.reclat?.toDouble()
            val log = meteorite.reclong?.toDouble()
            if (map != null && lat != null && log != null) {
                setupMap(meteoriteName, lat, log)
            }

        }
    }

    private fun setLocationText(address: String?, text: TextView?, meteorite: Meteorite) {

        text?.visibility = if (StringUtils.isNotEmpty(address)) {
            setText(null, text, address + meteorite.distance(41.1579, 8.6291))
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
