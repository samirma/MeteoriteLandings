package com.antonio.samir.meteoritelandingsspots.features.list

import MeteoriteList
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.InProgress
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.Success
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteListBinding
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel.ContentStatus.*
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.showActionBar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.stateViewModel


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
class MeteoriteListFragment : Fragment() {

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    private val viewModel: MeteoriteListViewModel by stateViewModel()

    private lateinit var binding: FragmentMeteoriteListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMeteoriteListBinding.inflate(inflater, container, false)

        updateList()

        return binding.root
    }

    private fun updateList() {
        binding.meteoriteList?.setContent {
            MeteoriteList(meteorites = viewModel.searchedLocation) {
                viewModel.selectMeteorite(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBar(getString(R.string.title))

        observeLiveData()

        setupLocation()

        setHasOptionsMenu(true)

        viewModel.searchLocation(viewModel.searchQuery.value)

    }

    private fun observeLiveData() {
        observeMeteorites()

        observeRecoveryAddressStatus()

        observeNetworkLoadingStatus()

        if (!isLandscape()) {
            viewModel.clearSelectedMeteorite()
        }

        viewModel.selectedMeteorite.observe(viewLifecycleOwner) { meteorite ->
            if (meteorite != null) {
                if (isLandscape()) {
                    showMeteoriteLandscape(meteorite)
                } else {
                    showMeteoritePortrait(meteorite)
                }
            }
        }

    }

    private fun setupLocation() {
        viewModel.isAuthorizationRequested().observe(viewLifecycleOwner) {
            if (it) {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        }
        viewModel.updateLocation()
    }

    private fun observeMeteorites() {

        viewModel.getContentStatus().observe(viewLifecycleOwner) { contentStatus ->
            when (contentStatus) {
                ShowContent -> showContent()
                NoContent -> noResult()
                Loading -> showProgressLoader()
            }
        }

    }

    private fun observeRecoveryAddressStatus() {
        viewModel.getRecoverAddressStatus().observe(viewLifecycleOwner) { status ->
            when (status) {
                is InProgress -> showAddressLoading(status.data)
                is Success -> hideAddressLoading()
                is ResultOf.Error -> error(getString(R.string.general_error))
            }
        }
    }

    private fun observeNetworkLoadingStatus() {
        viewModel.fetchMeteoriteList().observe(viewLifecycleOwner) {
            when (it) {
                is InProgress -> networkLoadingStarted()
                is Success -> networkLoadingStopped()
                else -> unableToFetch()
            }
        }
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
    }

    private fun error(messageString: String) {
        hideContent()
        binding.progressLoader.visibility = INVISIBLE
        binding.messageTV.visibility = VISIBLE
        binding.messageTV.text = messageString
    }

    private fun showMeteoriteLandscape(meteorite: MeteoriteItemView) {

        if (meteoriteDetailFragment == null) {

            binding.fragment?.visibility = VISIBLE

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit
                ).apply {
                    val meteoriteId: String = meteorite.id.toString()
                    meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteoriteId)
                    replace(R.id.fragment, meteoriteDetailFragment!!)
                    commit()
                }

        } else {
            meteoriteDetailFragment?.setCurrentMeteorite(meteorite.id.toString())
        }

    }

    private fun showMeteoritePortrait(meteorite: MeteoriteItemView) {
        findNavController().navigate(MeteoriteListFragmentDirections.toDetail(meteorite.id.toString()))
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
        binding.meteoriteList?.visibility = VISIBLE
        binding.messageTV.visibility = INVISIBLE
    }

    private fun hideContent() {
        binding.container?.visibility = INVISIBLE
        binding.meteoriteList?.visibility = INVISIBLE
    }

    private fun showProgressLoader() {
        binding.progressLoader.visibility = VISIBLE
        binding.messageTV.visibility = INVISIBLE
        hideContent()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray,
    ) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        setup(searchView)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setup(searchView: SearchView?) {
        if (searchView != null) {
            with(searchView) {
                isActivated = true
                onActionViewExpanded()
                isIconified = false
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextChange(query: String): Boolean {
                        if (query.isBlank()) {
                            onQueryTextSubmit(query)
                        }
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        loadMeteorites(query)
                        return true
                    }

                    private fun loadMeteorites(query: String) {
                        viewModel.searchLocation(query)
                        updateList()
                    }

                })

            }
        }
    }

    companion object {
        private val TAG = MeteoriteListFragment::class.java.simpleName
        const val LOCATION_REQUEST_CODE = 11111
    }

}
