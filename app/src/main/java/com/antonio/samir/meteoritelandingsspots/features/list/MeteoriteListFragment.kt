package com.antonio.samir.meteoritelandingsspots.features.list

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteListBinding
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.MeteoriteAdapter
import com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.SpacesItemDecoration
import com.antonio.samir.meteoritelandingsspots.ui.extension.hideActionBar
import com.antonio.samir.meteoritelandingsspots.ui.extension.showActionBar
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean


@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListFragment : Fragment() {

    private var loadedDetail: Pair<String?, Location?>? = null

    private var layoutManager: GridLayoutManager? = null

    private var meteoriteAdapter = MeteoriteAdapter().apply {
        setHasStableIds(false)
    }

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    private val viewModel: MeteoriteListViewModel by viewModel()

    private var _binding: FragmentMeteoriteListBinding? = null

    private val binding get() = _binding!!

    private val shouldOpenMeteorite = AtomicBoolean(true)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeteoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBar("Search over more than 45k meteorites")

        binding.meteoriteRV.adapter = meteoriteAdapter

        binding.meteoriteRV.addItemDecoration(SpacesItemDecoration(
                context = requireContext(),
                verticalMargin = R.dimen.spacing,
                horizontalMargin = R.dimen.horizontal_spacing
        ))

        setupGridLayout()

        observeLiveData()

        setupLocation()

//        with(binding.searchText) {
//            onActionViewExpanded()
//            doOnLayout {
//                clearFocus()
//            }
//            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextChange(query: String): Boolean {
//                    loadMeteorites(query)
//                    return false
//                }
//
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    loadMeteorites(query)
//                    return false
//                }
//
//                private fun loadMeteorites(query: String) {
//                    viewModel.loadMeteorites(query)
//                }
//
//            })
//        }

        showProgressLoader()

    }

    private fun observeLiveData() {
        observeMeteorites()

        observeRecoveryAddressStatus()

        observeNetworkLoadingStatus()

        meteoriteAdapter.openMeteorite.observe(viewLifecycleOwner) {
            viewModel.selectMeteorite(it)
            shouldOpenMeteorite.set(false)
        }

        viewModel.selectedMeteorite.observe(viewLifecycleOwner) { meteorite ->
            if (meteorite != null) {
//                binding.searchText?.clearFocus()
                if (isLandscape()) {
                    showMeteoriteLandscape(meteorite)
                    meteoriteAdapter.updateListUI(meteorite)
                } else {
                    if (shouldOpenMeteorite.get()) {
                        showMeteoritePortrait(meteorite)
                    }
                    shouldOpenMeteorite.set(true)
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
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        })
        viewModel.updateLocation()
        viewModel.getLocation().observe(viewLifecycleOwner, {
            meteoriteAdapter.location = it
        })
    }

    private fun observeMeteorites() {

        viewModel.loadedDetail.observe(viewLifecycleOwner) {
            loadedDetail = it
        }

        viewModel.getMeteorites(loadedDetail).observe(viewLifecycleOwner) { meteorites ->
            if (meteorites.isEmpty()) {
                noResult()
            } else {
                showContent()
                meteoriteAdapter.setData(meteorites)
            }
        }

        if (viewModel.filter.isBlank()) {
            viewModel.loadMeteorites()
        } else {
//            binding.searchText?.setQuery(viewModel.filter, true)
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
        val addressRecoverProgress = binding.addressRecoverProgress
        progress?.let {
            addressRecoverProgress.progress = it
            addressRecoverProgress.secondaryProgress = it + 10
        }
        addressRecoverProgress.progressText = getString(R.string.loading_addresses)
        addressRecoverProgress.visibility = VISIBLE
    }

    private fun hideAddressLoading() {
        binding.addressRecoverProgress.visibility = GONE
    }

    private fun unableToFetch() {
        error(getString(R.string.no_network))
//        binding.searchText?.visibility = GONE
    }

    private fun error(messageString: String) {
        hideContent()
        binding.progressLoader.visibility = INVISIBLE
        binding.messageTV.visibility = VISIBLE
        binding.messageTV.text = messageString
    }

    private fun showMeteoriteLandscape(meteorite: Meteorite) {

        layoutManager?.spanCount = 1

        if (meteoriteDetailFragment == null) {

            binding.fragment?.visibility = VISIBLE

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
        findNavController().navigate(MeteoriteListFragmentDirections.toDetail(meteorite.id.toString()))
    }

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        layoutManager = GridLayoutManager(requireContext(), columnCount)

        binding.meteoriteRV.layoutManager = layoutManager
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
            binding.networkStatusLoading.visibility = INVISIBLE
            showContent()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    private fun showContent() {
        binding.progressLoader.visibility = INVISIBLE
        binding.container?.visibility = VISIBLE
        binding.meteoriteRV.visibility = VISIBLE
        binding.messageTV.visibility = INVISIBLE
    }

    private fun hideContent() {
        binding.container?.visibility = INVISIBLE
        binding.meteoriteRV.visibility = INVISIBLE
    }

    private fun showProgressLoader() {
        binding.progressLoader.visibility = VISIBLE
        binding.messageTV.visibility = INVISIBLE
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        val currentMeteorite = viewModel.selectedMeteorite.value
        if (currentMeteorite != null) {
            savedInstanceState.putParcelable(ITEM_SELECTED, currentMeteorite)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = MeteoriteListFragment::class.java.simpleName
        const val LOCATION_REQUEST_CODE = 11111
        const val ITEM_SELECTED = "ITEM_SELECTED"
    }

}
