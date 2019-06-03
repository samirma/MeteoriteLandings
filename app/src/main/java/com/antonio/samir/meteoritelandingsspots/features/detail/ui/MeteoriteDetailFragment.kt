package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.presenter.MeteoriteDetailPresenter
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.meteorite_detail.*
import kotlinx.android.synthetic.main.meteorite_detail_grid.*
import org.apache.commons.lang3.StringUtils

class MeteoriteDetailFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback, MeteoriteDetailView {

    private lateinit var presenter: MeteoriteDetailPresenter

    private var meteoriteId: String? = null

    private var map: GoogleMap? = null
    private var meteoriteLiveData: LiveData<Meteorite>? = null
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

        presenter = MeteoriteDetailPresenter(this, MeteoriteRepositoryFactory.getMeteoriteDao(requireContext()))

        presenter.loadMeteoriteById(meteoriteId)

        return view
    }


    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }

    fun setupMap(meteoriteName: String?, lat: Double, log: Double) {
        map?.clear()

        val latLng = LatLng(lat, log)

        map?.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        map?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }


    override fun initView(meteorite: Meteorite) {
        setLocationText(meteorite.address, view?.findViewById(R.id.location))

        if (!isUiDone) {
            isUiDone = true
            val meteoriteName = meteorite.name
            setText(null, this.title, meteoriteName)

            val yearString = meteorite.yearString
            setText(year_label, this.year, yearString)

            val recclass = meteorite.recclass
            setText(recclass_label, this.recclass, recclass)

            val mass = meteorite.mass
            setText(mass_label, this.mass, mass)

            if (map != null) {
                val lat = java.lang.Double.valueOf(meteorite.reclat)
                val log = java.lang.Double.valueOf(meteorite.reclong)
                setupMap(meteoriteName, lat!!, log!!)
            }

        }
    }

    private fun setLocationText(address: String?, text: TextView?) {
        if (StringUtils.isNotEmpty(address)) {
            setText(null, text, address)
            text!!.visibility = View.VISIBLE
            meteoriteLiveData!!.removeObservers(this)
        } else {
            text!!.visibility = View.GONE
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
