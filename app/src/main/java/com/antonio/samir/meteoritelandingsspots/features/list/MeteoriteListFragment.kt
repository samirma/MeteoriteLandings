package com.antonio.samir.meteoritelandingsspots.features.list

import ListScreen
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.isLandscape
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.showActionBar
import com.antonio.samir.meteoritelandingsspots.databinding.FragmentMeteoriteListBinding
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.HeaderState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailFragment
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
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

            ListScreen(
                uiState = uiState,
                onItemClick = viewModel::selectMeteorite,
                onEnterSearch = { viewModel.setHeaderState(HeaderState.Search) },
                onExitSearch = {
                    viewModel.searchLocation(
                        "",
                        activity = requireActivity() as AppCompatActivity
                    )
                    viewModel.setHeaderState(HeaderState.Expanded)
                },
                onTopList = viewModel::onTopList,
                onSearch = {
                    viewModel.searchLocation(
                        query = it,
                        activity = requireActivity() as AppCompatActivity
                    )
                }
            )
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBar(getString(R.string.search_placeholder))

        observeLiveData()

        viewModel.searchLocation(
            query = viewModel.searchQuery.value,
            activity = requireActivity() as AppCompatActivity
        )

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

    companion object {
        private val TAG = MeteoriteListFragment::class.java.simpleName
    }

}
