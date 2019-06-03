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
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailActivity
import com.antonio.samir.meteoritelandingsspots.features.list.presenter.MeteoriteListPresenter
import com.antonio.samir.meteoritelandingsspots.features.list.presenter.MeteoriteListView
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorFactory
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorView
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import kotlinx.android.synthetic.main.fragment_meteorite_list.*
import org.apache.commons.lang3.StringUtils

class MeteoriteListFragment : Fragment(), MeteoriteListView, MeteoriteSelectorView, GPSTracker.GPSTrackerDelegate {

    private var mPresenter: MeteoriteListPresenter? = null
    private var mSglm: GridLayoutManager? = null
    private var mMeteoriteAdapter: MeteoriteAdapter? = null
    private var mSelectedMeteorite: String? = null

    private var mProgressDialog: ProgressDialog? = null
    private var mFrameLayout: FrameLayout? = null
    private var mSavedInstanceState: Bundle? = null
    private var mIsLandscape: Boolean = false
    private var mMeteoriteViewModel: MeteoriteViewModel? = null

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

        mMeteoriteViewModel = ViewModelProviders.of(this).get(MeteoriteViewModel::class.java)

        mPresenter = mMeteoriteViewModel!!.getPresenter()

        mIsLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(mIsLandscape, this)

        mMeteoriteAdapter = MeteoriteAdapter(requireContext(), meteoriteSelector, mPresenter!!)
        mMeteoriteAdapter!!.setHasStableIds(true)
        meteoriteRV?.adapter = mMeteoriteAdapter


        setupGridLayout()

        val selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState)

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite)
        }

        this.mSavedInstanceState = savedInstanceState

        mPresenter?.attachView(this)

        mPresenter!!.recoveryAddress!!.observe(this, Observer { status ->
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

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {

        if (mSelectedMeteorite != null) {
            savedInstanceState.putString(ITEM_SELECTED, mSelectedMeteorite)
        }

        val lastFirstVisiblePosition = mSglm!!.findFirstCompletelyVisibleItemPosition()
        savedInstanceState.putInt(SCROLL_POSITION, lastFirstVisiblePosition)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    fun getMeteorites() {

        val meteorites = mMeteoriteViewModel!!.getMeteorites()

        meteorites!!.observe(this, Observer { meteorites1 ->
            if (meteorites1 != null && !meteorites1.isEmpty()) {
                mMeteoriteAdapter!!.setData(meteorites1)
                mMeteoriteAdapter!!.notifyDataSetChanged()

                if (mSavedInstanceState != null) {
                    val anInt = mSavedInstanceState!!.getInt(SCROLL_POSITION, -1)
                    if (anInt > 0) {
                        mSglm!!.scrollToPosition(anInt)
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
//        if (mSelectedMeteorite == null) {
//            mFrameLayout!!.visibility = View.VISIBLE
//            mSglm = GridLayoutManager(this, 1)
//            meteoriteRV.layoutManager = mSglm
//        }
//        fragmentTransaction = fragmentTransaction.setCustomAnimations(
//                R.anim.fragment_slide_left_enter,
//                R.anim.fragment_slide_left_exit)
//
//        val mMeteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite!!)
//        fragmentTransaction.replace(R.id.fragment, mMeteoriteDetailFragment)
//        fragmentTransaction.commit()
//
//        mSelectedMeteorite = meteorite
//
//        mMeteoriteAdapter!!.setSelectedMeteorite(mSelectedMeteorite!!)
//
//    }

    override fun selectPortrait(meteorite: String?) {

        val intent = Intent(requireContext(), MeteoriteDetailActivity::class.java)
        intent.putExtra(ITEM_SELECTED, meteorite)

        val viewHolderMeteorite = mMeteoriteAdapter!!.vieHolderMeteorite
        if (viewHolderMeteorite != null) {

            val container = Pair.create<View, String>(viewHolderMeteorite.mCardview, viewHolderMeteorite.mCardview.transitionName)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), container)

            startActivity(intent, options.toBundle())

        } else {
            startActivity(intent)
        }

    }

//    override fun onBackPressed() {
//        if (mSelectedMeteorite != null && mIsLandscape) {
//            mSelectedMeteorite = null
//            mMeteoriteAdapter!!.setSelectedMeteorite(null!!)
//            setupGridLayout()
//            mFrameLayout!!.visibility = View.GONE
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        mSglm = GridLayoutManager(requireContext(), columnCount)
        meteoriteRV.layoutManager = mSglm
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
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(requireContext(), "", getString(R.string.load), true)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    override fun meteoriteLoadingStopped() {
        try {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
                mProgressDialog = null
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
                if (isPermitted) {
                    mPresenter!!.updateLocation()
                }
            }
        }
    }

}