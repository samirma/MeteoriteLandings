package com.antonio.samir.meteoritelandingsspots.features.list.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailFragment.Companion.METEORITE
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.MeteoriteDiffCallback
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.SpacesItemDecoration
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorFactory
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelectorView
import kotlinx.android.synthetic.main.fragment_meteorite_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListFragment : Fragment(),
        MeteoriteSelectorView {

    private val TAG = MeteoriteListFragment::class.java.simpleName

    private var sglm: GridLayoutManager? = null
    private lateinit var meteoriteAdapter: MeteoriteAdapter
    private var selectedMeteorite: Meteorite? = null

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    private val listViewModel: MeteoriteListViewModel by viewModel()

    companion object {
        const val LOCATION_REQUEST_CODE = 11111
        const val ITEM_SELECTED = "ITEM_SELECTED"
        const val SCROLL_POSITION = "SCROLL_POSITION"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meteorite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(
                isLandscape,
                this
        )

        meteoriteAdapter = MeteoriteAdapter(meteoriteSelector, listViewModel, MeteoriteDiffCallback()).apply {
            setHasStableIds(true)
        }

        meteoriteRV?.adapter = meteoriteAdapter

        meteoriteRV.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing)))

        setupGridLayout()

        val selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState)

        selectedMeteorite?.let { meteoriteSelector.selectItem(it) }

        if (savedInstanceState != null) {
            val anInt = savedInstanceState.getInt(SCROLL_POSITION, -1)
            if (anInt > 0) {
                sglm!!.scrollToPosition(anInt)
            }
        }

        observeMeteorites()

        observeRecoveryAddressStatus()

        observeNetworkLoadingStatus()

//        observeRequestPermission()

        searchText.isActivated = true
        searchText.onActionViewExpanded()
        searchText.isIconified = false
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                loadMeteorites(query)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                loadMeteorites(query)
                return false
            }

            private fun loadMeteorites(query: String) {
                val min_query_lenght = 3
                if (query.isBlank() || query.length > min_query_lenght) {
                    showProgressLoader()
                    listViewModel.loadMeteorites(query)
                }
            }

        })

    }

    override fun onResume() {
        super.onResume()
        searchText?.clearFocus()
        Log.v(TAG, "onResume")
    }

    private fun observeMeteorites() {

        listViewModel.getMeteorites().observe(viewLifecycleOwner, Observer { meteorites ->
            Log.i(TAG, "Meteorites received: ${meteorites.size}")
            if (meteorites.isEmpty()) {
                noResult()
            } else {
                showContent()
                meteoriteAdapter.setData(meteorites)
            }
        })

        if (listViewModel.filter.isBlank()) {
            listViewModel.loadMeteorites()
        } else {
            searchText?.setQuery(listViewModel.filter, true)
        }

    }

    private fun observeRecoveryAddressStatus() {
        listViewModel.getRecoverAddressStatus().observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is InProgress -> showAddressLoading(status.data)
                is Success -> hideAddressLoading()
            }
        })
    }

    private fun observeNetworkLoadingStatus() {
        listViewModel.getNetworkLoadingStatus().observe(viewLifecycleOwner, Observer {
            when (it) {
                is InProgress -> networkLoadingStarted()
                is Success -> networkLoadingStopped()
                else -> unableToFetch()
            }
        })
    }

    private fun noResult() {
        error(getString(R.string.no_result_found))
        hideContent()
    }

    /**
     * Request user permission
     */
    private fun observeRequestPermission() {
        listViewModel.isAuthorizationRequested().observe(viewLifecycleOwner, Observer {
            if (it) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        })
    }

    private fun showAddressLoading(progress: Float?) {
        progress?.let {
            addressRecoverProgress.progress = it
            addressRecoverProgress.secondaryProgress = it + 10
        }
        addressRecoverProgress.progressText = getString(R.string.loading_addresses)
        addressRecoverProgress?.visibility = VISIBLE
    }

    private fun hideAddressLoading() {
        addressRecoverProgress?.visibility = GONE
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        if (selectedMeteorite != null) {
            savedInstanceState.putParcelable(ITEM_SELECTED, selectedMeteorite)
        }

        sglm?.findFirstCompletelyVisibleItemPosition()?.let { savedInstanceState.putInt(SCROLL_POSITION, it) }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }


    private fun unableToFetch() {
        error(getString(R.string.no_network))
        searchText.visibility = GONE
    }

    private fun error(messageString: String) {
        hideContent()
        progressLoader.visibility = INVISIBLE
        messageTV.visibility = VISIBLE
        messageTV.text = messageString
    }

    /**
     * MeteoriteSelectorView implementation
     */
    override fun selectLandscape(meteorite: Meteorite) {
        if (selectedMeteorite == null) {
            if (sglm?.spanCount != 1) {
                sglm?.spanCount = 1
            }
        }
        selectMeteoriteLandscape(meteorite)
    }

    override fun selectPortrait(meteorite: Meteorite) {
        val bundle = Bundle().apply {
            putParcelable(METEORITE, meteorite)
        }
        findNavController().navigate(R.id.toDetail, bundle)

        selectedMeteorite = meteorite

    }

    private fun selectMeteoriteLandscape(meteorite: Meteorite) {

        if (meteoriteDetailFragment == null) {

            fragment?.visibility = VISIBLE

            parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.fragment_slide_left_enter,
                            R.anim.fragment_slide_left_exit).apply {
                        meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite)
                        replace(R.id.fragment, meteoriteDetailFragment!!)
                        commit()
                    }

        } else {
            meteoriteDetailFragment?.setCurrentMeteorite(meteorite)
        }

        selectedMeteorite = meteorite

        meteoriteAdapter.updateListUI(meteorite)

    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        sglm = GridLayoutManager(requireContext(), columnCount)

        meteoriteRV.layoutManager = sglm
    }

    private fun getPreviousSelectedMeteorite(savedInstanceState: Bundle?): Meteorite? {

        var meteorite: Meteorite? = null

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getParcelable(ITEM_SELECTED)
        }

        val intent = Intent()
        val extras = intent.extras
        val isRedeliver = savedInstanceState != null || intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0
        if (meteorite == null && extras != null && !isRedeliver) {
            meteorite = extras.getParcelable(ITEM_SELECTED)
        }

        return meteorite
    }

    private fun networkLoadingStarted() {
        try {
            showContent()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    private fun networkLoadingStopped() {
        try {
            networkStatusLoading.visibility = INVISIBLE
            showContent()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    private fun showContent() {
        progressLoader.visibility = INVISIBLE
        container?.visibility = VISIBLE
        meteoriteRV?.visibility = VISIBLE
        messageTV.visibility = INVISIBLE
    }

    private fun hideContent() {
        container?.visibility = INVISIBLE
        meteoriteRV?.visibility = INVISIBLE
    }

    private fun showProgressLoader() {
        progressLoader.visibility = VISIBLE
        messageTV.visibility = INVISIBLE
        hideContent()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            for (grantResult in grantResults) {
                val isPermitted = grantResult == PackageManager.PERMISSION_GRANTED
                if (isPermitted) {
                    listViewModel.updateLocation()
                }
            }
        }
    }

}