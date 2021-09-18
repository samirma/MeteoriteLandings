package com.antonio.samir.meteoritelandingsspots.features.list

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.recyclerview.widget.GridLayoutManager
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.InProgress
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.Success
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteListBinding
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel.ContentStatus.*
import com.antonio.samir.meteoritelandingsspots.ui.MeteoriteTheme
import com.antonio.samir.meteoritelandingsspots.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.ui.extension.showActionBar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.util.concurrent.atomic.AtomicBoolean


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
class MeteoriteListFragment : Fragment() {

    private var layoutManager: GridLayoutManager? = null

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    private val viewModel: MeteoriteListViewModel by stateViewModel()

    private lateinit var binding: FragmentMeteoriteListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMeteoriteListBinding.inflate(inflater, container, false)

        binding.meteoriteList?.setContent {
            MeteoriteList(meteorites = viewModel.getMeteorites())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBar(getString(R.string.title))

        setupGridLayout()

        observeLiveData()

        setupLocation()

        setHasOptionsMenu(true)

        if (viewModel.filter.isBlank()) {
            viewModel.loadMeteorites()
        }

    }

    private fun observeLiveData() {
        observeMeteorites()

        observeRecoveryAddressStatus()

        observeNetworkLoadingStatus()

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
        viewModel.getNetworkLoadingStatus().observe(viewLifecycleOwner) {
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

        layoutManager?.spanCount = 1

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

    private fun setupGridLayout() {
        val columnCount = resources.getInteger(R.integer.list_column_count)

        layoutManager = GridLayoutManager(requireContext(), columnCount)

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
                setQuery(viewModel.filter, false)
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
                        viewModel.loadMeteorites(query)
                    }

                })

            }
        }
    }

    @Composable
    fun MeteoriteList(meteorites: Flow<PagingData<MeteoriteItemView>>) {
        val collectAsLazyPagingItems = meteorites.collectAsLazyPagingItems()
        LazyColumn(
            Modifier
                .fillMaxWidth()
        ) {
            items(collectAsLazyPagingItems) { character ->
                character?.let { MeteoriteItem(it) }
            }
        }
    }


    @Composable
    fun MeteoriteItem(itemView: MeteoriteItemView) {
        Card(
            onClick = {
                viewModel.selectMeteorite(itemView)
            },
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.padding(2.dp),
            backgroundColor = colorResource(itemView.cardBackgroundColor),
            elevation = dimensionResource(itemView.elevation)
        ) {
            Column(
                Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    color = colorResource(itemView.titleColor),
                    text = itemView.name!!
                )
                Text(
                    color = colorResource(R.color.detail_accent_label),
                    text = itemView.address!!
                )
            }
        }
    }

    @Preview("Meteorite view")
    @Composable
    fun MeteoriteItem() {
        MeteoriteItem(
            MeteoriteItemView(
                id = 123,
                name = "title",
                address = "address"
            )
        )
    }

    companion object {
        private val TAG = MeteoriteListFragment::class.java.simpleName
        const val LOCATION_REQUEST_CODE = 11111
    }

}
