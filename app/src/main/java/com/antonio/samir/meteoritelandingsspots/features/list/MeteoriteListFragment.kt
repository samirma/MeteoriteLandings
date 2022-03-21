package com.antonio.samir.meteoritelandingsspots.features.list

import ListScreen
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.showActionBar
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteListBinding
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
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

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        binding.listScreen?.setContent {

            val uiState by viewModel.uiState.collectAsState()

            ListScreen(uiState = uiState) {
                viewModel.selectMeteorite(it)
            }
        }

        return binding.root
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
