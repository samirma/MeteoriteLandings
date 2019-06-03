package com.antonio.samir.meteoritelandingsspots.features.list.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailFragment.Companion.METEORITE
import com.antonio.samir.meteoritelandingsspots.features.list.presenter.MeteoriteListPresenter
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorFactory
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorView
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import kotlinx.android.synthetic.main.fragment_meteorite_list.*
import org.apache.commons.lang3.StringUtils

class MeteoriteListFragment : androidx.fragment.app.Fragment(),
        MeteoriteListView,
        MeteoriteSelectorView,
        GPSTracker.GPSTrackerDelegate {

    private var presenter: MeteoriteListPresenter? = null
    private var sglm: GridLayoutManager? = null
    private var meteoriteAdapter: MeteoriteAdapter? = null
    private var selectedMeteorite: String? = null

    private var progressDialog: ProgressDialog? = null
    private var mSavedInstanceState: Bundle? = null
    private var isLandscape: Boolean = false
    private var listViewModel: MeteoriteListViewModel? = null

    val TAG = MeteoriteListMainActivity::class.java.simpleName

    val LOCATION_REQUEST_CODE = 11111

    companion object {
        val ITEM_SELECTED = "ITEM_SELECTED"
        val SCROLL_POSITION = "SCROLL_POSITION"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meteorite_list, container, false)
    }


    override val gpsDelegate: GPSTracker.GPSTrackerDelegate
        get() = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        listViewModel = ViewModelProviders.of(this).get(MeteoriteListViewModel::class.java)

        presenter = listViewModel?.getPresenter()

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(isLandscape, this)

        meteoriteAdapter = MeteoriteAdapter(requireContext(), meteoriteSelector, presenter!!).apply {
            setHasStableIds(true)
            meteoriteRV?.adapter = meteoriteAdapter
        }


        setupGridLayout()

        val selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState)

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite)
        }

        this.mSavedInstanceState = savedInstanceState

        presenter?.attachView(this)

        presenter!!.recoveryAddress!!.observe(this, Observer { status ->
            if (status == null || status === AddressService.Status.DONE) {
                this.hideAddressLoading()
            } else if (status === AddressService.Status.LOADING) {
                this.showAddressLoading()
            }
        })

        getMeteorites()

    }

    private fun showAddressLoading() {
        statusTV?.visibility = View.VISIBLE
    }

    private fun hideAddressLoading() {
        statusTV?.visibility = View.GONE
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        if (selectedMeteorite != null) {
            savedInstanceState.putString(ITEM_SELECTED, selectedMeteorite)
        }

        val lastFirstVisiblePosition = sglm!!.findFirstCompletelyVisibleItemPosition()
        savedInstanceState.putInt(SCROLL_POSITION, lastFirstVisiblePosition)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun getMeteorites() {

        val meteorites = listViewModel?.getMeteorites()

        meteorites?.observe(this, Observer { meteorites1 ->
            if (meteorites1 != null && !meteorites1.isEmpty()) {

                meteoriteAdapter?.apply {
                    setData(meteorites1)
                    notifyDataSetChanged()
                }

                if (mSavedInstanceState != null) {
                    val anInt = mSavedInstanceState!!.getInt(SCROLL_POSITION, -1)
                    if (anInt > 0) {
                        sglm!!.scrollToPosition(anInt)
                    }
                }
            }
        })

    }

    override fun unableToFetch() {
        error(getString(R.string.no_network))
    }

    fun error(messageString: String) {
        meteoriteRV.visibility = View.GONE
        messageTV.visibility = View.VISIBLE
        messageTV.text = messageString
        meteoriteLoadingStopped()
    }

    override fun hideList() {
        meteoriteRV.visibility = View.GONE
    }

//    /*
//    MeteoriteSelectorView
//     */
//    override fun selectLandscape(meteorite: String?) {
//
//        var fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        if (selectedMeteorite == null) {
//            mFrameLayout!!.visibility = View.VISIBLE
//            sglm = GridLayoutManager(this, 1)
//            meteoriteRV.layoutManager = sglm
//        }
//        fragmentTransaction = fragmentTransaction.setCustomAnimations(
//                R.anim.fragment_slide_left_enter,
//                R.anim.fragment_slide_left_exit)
//
//        val mMeteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite!!)
//        fragmentTransaction.replace(R.id.fragment, mMeteoriteDetailFragment)
//        fragmentTransaction.commit()
//
//        selectedMeteorite = meteorite
//
//        meteoriteAdapter!!.setSelectedMeteorite(selectedMeteorite!!)
//
//    }

    override fun selectPortrait(meteorite: String?) {
        val bundle = Bundle().apply {
            putString(METEORITE, meteorite)
        }
        findNavController().navigate(R.id.action_meteoriteListFragment_to_meteoriteDetailFragment, bundle)

    }

//    override fun onBackPressed() {
//        if (selectedMeteorite != null && isLandscape) {
//            selectedMeteorite = null
//            meteoriteAdapter!!.setSelectedMeteorite(null!!)
//            setupGridLayout()
//            mFrameLayout!!.visibility = View.GONE
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        sglm = GridLayoutManager(requireContext(), columnCount)

        meteoriteRV.layoutManager = sglm
    }

    private fun getPreviousSelectedMeteorite(savedInstanceState: Bundle?): String? {

        var meteorite: String? = null

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED)
        }

        val intent = Intent()
        val extras = intent.extras
        val isRedeliver = savedInstanceState != null || intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0
        if (meteorite == null && extras != null && !isRedeliver) {
            meteorite = extras.getString(ITEM_SELECTED)
        }

        Log.i(TAG, "isRedeliver: $isRedeliver")

        return meteorite
    }

    override fun meteoriteLoadingStarted() {
        try {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(requireContext(), "", getString(R.string.load), true)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    override fun meteoriteLoadingStopped() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog?.dismiss()
                progressDialog = null
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            for (grantResult in grantResults) {
                val isPermitted = grantResult == PackageManager.PERMISSION_GRANTED
                if (isPermitted && presenter != null) {
                    presenter?.updateLocation()
                }
            }
        }
    }

}