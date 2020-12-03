package com.antonio.samir.meteoritelandingsspots.features.list

import android.Manifest
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment.Companion.METEORITE_ID
import com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.SpacesItemDecoration
import kotlinx.android.synthetic.main.fragment_meteorite_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean


@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListFragment : Fragment() {

    private var sglm: GridLayoutManager? = null

    private var meteoriteAdapter = MeteoriteAdapter().apply {
        setHasStableIds(true)
    }

    private val shouldLoad = AtomicBoolean(true)

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    private val viewModel: MeteoriteListViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meteorite_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meteoriteRV?.adapter = meteoriteAdapter

        meteoriteRV.addItemDecoration(SpacesItemDecoration(
                context = requireContext(),
                verticalMargin = R.dimen.spacing,
                horizontalMargin = R.dimen.horizontal_spacing
        ))

        setupGridLayout()

        observeLiveData()

        setupLocation()

        with(searchText) {
            isActivated = true
            onActionViewExpanded()
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(query: String): Boolean {
                    loadMeteorites(query)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    loadMeteorites(query)
                    return false
                }

                private fun loadMeteorites(query: String) {
                    val minQueryLenght = 3
                    if (query.isBlank() || query.length > minQueryLenght) {
                        showProgressLoader()
                        viewModel.loadMeteorites(query)
                    }
                }

            })
        }
    }

    private fun observeLiveData() {
        observeMeteorites()

        observeRecoveryAddressStatus()

        observeNetworkLoadingStatus()

        meteoriteAdapter.selectedMeteorite.observe(viewLifecycleOwner) {
            viewModel.selectMeteorite(it)
            shouldLoad.set(false)
        }

        viewModel.selectedMeteorite.observe(viewLifecycleOwner) { meteorite ->
            if (meteorite != null) {
                if (isLandscape()) {
                    showMeteoriteLandscape(meteorite)
                } else if (shouldLoad.getAndSet(true)) {
                    showMeteoritePortrait(meteorite)
                }
            }
        }

    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun setupLocation() {
        viewModel.isAuthorizationRequested().observe(viewLifecycleOwner, {
            if (it) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        })
        viewModel.updateLocation()
        viewModel.getLocation().observe(viewLifecycleOwner, {
            meteoriteAdapter.location = it
        })
    }

    override fun onResume() {
        super.onResume()
        searchText?.clearFocus()
        Log.v(TAG, "onResume")
    }

    private fun observeMeteorites() {

        viewModel.getMeteorites().observe(viewLifecycleOwner, { meteorites ->
            if (meteorites.isEmpty()) {
                noResult()
            } else {
                showContent()
                meteoriteAdapter.setData(meteorites)
            }
        })

        if (viewModel.filter.isBlank()) {
            viewModel.loadMeteorites()
        } else {
            searchText?.setQuery(viewModel.filter, true)
        }

    }

    private fun observeRecoveryAddressStatus() {
        viewModel.getRecoverAddressStatus().observe(viewLifecycleOwner, { status ->
            when (status) {
                is InProgress -> showAddressLoading(status.data)
                is Success -> hideAddressLoading()
                is Result.Error -> error(getString(R.string.general_error))
            }
        })
    }

    private fun observeNetworkLoadingStatus() {
        viewModel.getNetworkLoadingStatus().observe(viewLifecycleOwner, {
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

    private fun showMeteoriteLandscape(meteorite: Meteorite) {

        sglm?.spanCount = 1

        if (meteoriteDetailFragment == null) {

            fragment?.visibility = VISIBLE

            parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.fragment_slide_left_enter,
                            R.anim.fragment_slide_left_exit).apply {
                        val meteoriteId: String = meteorite.id.toString()
                        meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteoriteId)
                        replace(R.id.fragment, meteoriteDetailFragment!!)
                        commit()
                    }

        } else {
            meteoriteDetailFragment?.setCurrentMeteorite(meteorite.id.toString())
        }

    }

    private fun showMeteoritePortrait(meteorite: Meteorite) {
        findNavController().navigate(R.id.toDetail, Bundle().apply {
            putString(METEORITE_ID, meteorite.id.toString())
        })
    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        sglm = GridLayoutManager(requireContext(), columnCount)

        meteoriteRV.layoutManager = sglm
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
                    viewModel.updateLocation()
                }
            }
        }
    }

    companion object {
        private val TAG = MeteoriteListFragment::class.java.simpleName
        const val LOCATION_REQUEST_CODE = 11111
    }

}