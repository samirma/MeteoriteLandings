package com.antonio.samir.meteoritelandingsspots.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.meteorite_detail.*
import kotlinx.android.synthetic.main.meteorite_detail_grid.*
import org.apache.commons.lang3.StringUtils

class MeteoriteDetailFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {

    private var meteoriteId: String? = null

    private var mMap: GoogleMap? = null
    private var mMeteoriteLiveData: LiveData<Meteorite>? = null
    private var isUiDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            //Recovering the meteorite to work on it
            meteoriteId = arguments!!.getString(METEORITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_meteorite_detail, container, false)

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        val activity = activity as AppCompatActivity?

        val toolbarView = view.findViewById<Toolbar>(R.id.toolbar)

        if (null != toolbarView) {
            activity!!.setSupportActionBar(toolbarView)
            activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        setMeteorite(meteoriteId)
    }

    override fun onMapReady(map: GoogleMap) {
        this.mMap = map
    }

    fun setupMap(meteoriteName: String, lat: Double, log: Double) {
        mMap?.clear()

        val latLng = LatLng(lat, log)

        mMap?.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }

    fun setMeteorite(meteoriteId: String?) {
        this.meteoriteId = meteoriteId

        val meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(context!!)

        mMeteoriteLiveData = meteoriteId?.let { meteoriteDao.getMeteoriteById(it) }

        val observer = Observer<Meteorite> {t: Meteorite? -> t?.let { this.initView(it) } }

        mMeteoriteLiveData!!.observe(this, observer)

    }

    private fun initView(meteorite: Meteorite) {
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

            if (mMap != null) {
                val lat = java.lang.Double.valueOf(meteorite.reclat)
                val log = java.lang.Double.valueOf(meteorite.reclong)
                setupMap(meteoriteName, lat!!, log!!)
            }

            AnalyticsUtil.logEvent("Detail", String.format("%s detail", meteoriteName))
        }
    }

    fun setLocationText(address: String, text: TextView?) {
        if (StringUtils.isNotEmpty(address)) {
            setText(null, text, address)
            text!!.visibility = View.VISIBLE
            mMeteoriteLiveData!!.removeObservers(this)
        } else {
            text!!.visibility = View.GONE
        }
    }

    private fun setText(textFieldLabel: TextView?, textField: TextView?, text: String?) {
        if (StringUtils.isEmpty(text)) {
            if (textFieldLabel != null) {
                textFieldLabel.visibility = View.GONE
            }
            textField!!.visibility = View.GONE
        } else {
            textField!!.text = text
            textField.contentDescription = text
        }
    }

    companion object {

        val METEORITE = "METEORITE"

        /**
         * Create a MeteoriteDetailFragment to show the meteorite param
         * @param meteorite
         * @return
         */
        fun newInstance(meteorite: String): MeteoriteDetailFragment {
            val fragment = MeteoriteDetailFragment()
            val args = Bundle()
            args.putString(METEORITE, meteorite)
            fragment.arguments = args
            return fragment
        }
    }

}
