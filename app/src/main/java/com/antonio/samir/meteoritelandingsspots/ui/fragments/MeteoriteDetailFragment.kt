package com.antonio.samir.meteoritelandingsspots.ui.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
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
import org.apache.commons.lang3.StringUtils

class MeteoriteDetailFragment : Fragment(), OnMapReadyCallback {

    @BindView(R.id.title)
    internal var title: TextView? = null
    @BindView(R.id.location)
    internal var location: TextView? = null
    @BindView(R.id.year)
    internal var year: TextView? = null
    @BindView(R.id.mass)
    internal var mass: TextView? = null
    @BindView(R.id.recclass)
    internal var recclass: TextView? = null

    @BindView(R.id.year_label)
    internal var year_label: TextView? = null
    @BindView(R.id.mass_label)
    internal var mass_label: TextView? = null
    @BindView(R.id.recclass_label)
    internal var recclass_label: TextView? = null

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

        //        ButterKnife.bind(this, view);

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
        mMap!!.clear()

        val latLng = LatLng(lat, log)

        mMap!!.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }

    fun setMeteorite(meteoriteId: String?) {
        this.meteoriteId = meteoriteId

        val meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(context)

        mMeteoriteLiveData = meteoriteDao.getMeteoriteById(meteoriteId)

        val observer = Observer<Meteorite> {t: Meteorite? -> t?.let { this.initView(it) } }

        mMeteoriteLiveData!!.observe(this, observer)

    }

    private fun initView(meteorite: Meteorite) {
        setLocationText(meteorite.address, this.location)

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
