package com.antonio.samir.meteoritelandingsspots.ui.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListView
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorFactory
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorView
import com.antonio.samir.meteoritelandingsspots.ui.viewmodel.MeteoriteViewModel
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import org.apache.commons.lang3.StringUtils

class MeteoriteListMainActivity : AppCompatActivity(), MeteoriteListView, MeteoriteSelectorView, GPSTracker.GPSTrackerDelegate {

    lateinit var mRecyclerView: RecyclerView

    lateinit var mMessage: TextView

    lateinit var mStatus: TextView

    internal var mToolbar: Toolbar? = null

    private var mPresenter: MeteoriteListPresenter? = null
    private var mSglm: GridLayoutManager? = null
    private var mMeteoriteAdapter: MeteoriteAdapter? = null
    private var mSelectedMeteorite: String? = null

    private var mProgressDialog: ProgressDialog? = null
    private var mFrameLayout: FrameLayout? = null
    private var mSavedInstanceState: Bundle? = null
    private var mIsLandscape: Boolean = false
    private var mMeteoriteViewModel: MeteoriteViewModel? = null

    /*
    MeteoriteListView
     */
    override val context: Context
        get() = this

    override val gpsDelegate: GPSTracker.GPSTrackerDelegate
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_list)

        mRecyclerView = findViewById(R.id.recycler_view)

        mMessage = findViewById(R.id.message)

        mStatus = findViewById(R.id.status)

        mToolbar = findViewById(R.id.toolbar)

        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
        }

        mMeteoriteViewModel = ViewModelProviders.of(this).get(MeteoriteViewModel::class.java)

        mPresenter = mMeteoriteViewModel!!.getPresenter()

        mIsLandscape = resources.configuration.orientation == ORIENTATION_LANDSCAPE

        val meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(mIsLandscape, this)

        mMeteoriteAdapter = MeteoriteAdapter(this, meteoriteSelector, mPresenter!!)
        mMeteoriteAdapter!!.setHasStableIds(true)
        mRecyclerView.adapter = mMeteoriteAdapter

        mFrameLayout = findViewById(R.id.fragment)

        setupGridLayout()

        val selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState)

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite)
        }

        this.mSavedInstanceState = savedInstanceState

        mPresenter!!.attachView(this)

        mPresenter!!.recoveryAddress!!.observe(this, Observer { status ->
            if (status == null || status === AddressService.Status.DONE) {
                this@MeteoriteListMainActivity.hideAddressLoading()
            } else if (status === AddressService.Status.LOADING) {
                this@MeteoriteListMainActivity.showAddressLoading()
            }
        })

        getMeteorites()


    }

    private fun showAddressLoading() {
        mStatus.visibility = View.VISIBLE
    }

    private fun hideAddressLoading() {
        mStatus.visibility = View.GONE
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
        mRecyclerView.visibility = View.GONE
        mMessage.visibility = View.VISIBLE
        mMessage.text = messageString
        meteoriteLoadingStopped()
    }

    override fun hideList() {
        mRecyclerView.visibility = View.GONE
    }

    /*
    MeteoriteSelectorView
     */
    override fun selectLandscape(meteorite: String) {

        var fragmentTransaction = supportFragmentManager.beginTransaction()

        if (mSelectedMeteorite == null) {
            mFrameLayout!!.visibility = View.VISIBLE
            mSglm = GridLayoutManager(this, 1)
            mRecyclerView.layoutManager = mSglm
        }
        fragmentTransaction = fragmentTransaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit)

        val mMeteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite)
        fragmentTransaction.replace(R.id.fragment, mMeteoriteDetailFragment)
        fragmentTransaction.commit()

        mSelectedMeteorite = meteorite

        mMeteoriteAdapter!!.setSelectedMeteorite(mSelectedMeteorite!!)

    }

    override fun selectPortrait(meteorite: String) {

        val intent = Intent(this, MeteoriteDetailActivity::class.java)
        intent.putExtra(ITEM_SELECTED, meteorite)

        val viewHolderMeteorite = mMeteoriteAdapter!!.vieHolderMeteorite
        if (viewHolderMeteorite != null) {

            val container = Pair.create<View, String>(viewHolderMeteorite.mCardview, viewHolderMeteorite.mCardview.transitionName)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, container)

            startActivity(intent, options.toBundle())

        } else {
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (mSelectedMeteorite != null && mIsLandscape) {
            mSelectedMeteorite = null
            mMeteoriteAdapter!!.setSelectedMeteorite(null!!)
            setupGridLayout()
            mFrameLayout!!.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        mSglm = GridLayoutManager(this, columnCount)
        mRecyclerView.layoutManager = mSglm
    }

    private fun getPreviousSelectedMeteorite(savedInstanceState: Bundle?): String? {

        var meteorite: String? = null

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED)
        }

        val intent = intent
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
                mProgressDialog = ProgressDialog.show(this, "", getString(R.string.load), true)
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
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
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

    companion object {

        val ITEM_SELECTED = "ITEM_SELECTED"
        val SCROLL_POSITION = "SCROLL_POSITION"
        val TAG = MeteoriteListMainActivity::class.java.simpleName
        val LOCATION_REQUEST_CODE = 11111
    }
}
