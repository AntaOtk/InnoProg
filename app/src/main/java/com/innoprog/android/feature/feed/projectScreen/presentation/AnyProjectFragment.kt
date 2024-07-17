package com.innoprog.android.feature.feed.projectScreen.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.innoprog.android.R
import com.innoprog.android.base.BaseFragment
import com.innoprog.android.base.BaseViewModel
import com.innoprog.android.databinding.FragmentAnyProjectBinding
import com.innoprog.android.di.AppComponentHolder
import com.innoprog.android.di.ScreenComponent
import com.innoprog.android.feature.feed.projectScreen.di.DaggerAnyProjectComponent
import com.innoprog.android.feature.feed.projectScreen.domain.AnyProjectModel
import com.innoprog.android.feature.newsrecycleview.NewsAdapter
import com.innoprog.android.util.debounceUnitFun
import okhttp3.internal.format

class AnyProjectFragment : BaseFragment<FragmentAnyProjectBinding, BaseViewModel>() {
    private val debounceNavigateTo = debounceUnitFun<Fragment?>(lifecycleScope)
    override val viewModel by injectViewModel<AnyProjectViewModel>()
    private val newsAdapter: NewsAdapter by lazy {
        NewsAdapter { newsWithProject ->
            val action = AnyProjectFragmentDirections.actionProjectFragmentToNewsDetailsFragment(
                newsId = newsWithProject.news.id
            )
            debounceNavigateTo(this) { _ -> findNavController().navigate(action) }
        }
    }

    override fun diComponent(): ScreenComponent {
        val appComponent = AppComponentHolder.getComponent()
        return DaggerAnyProjectComponent.builder()
            .appComponent(appComponent)
            .build()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnyProjectBinding {
        return FragmentAnyProjectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiListeners()
        viewModel.screenState.observe(viewLifecycleOwner) { updateUI(it) }
        val args: AnyProjectFragmentArgs by navArgs()
        viewModel.getAnyProject(id = args.projectId)
        binding.rvPublications.adapter = newsAdapter
        binding.rvPublications.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUiListeners() {
        binding.projectTopBar.setLeftIconClickListener { viewModel.navigateUp() }
        binding.btnProjectDetails.setOnClickListener {
            val args: AnyProjectFragmentArgs by navArgs()
            val action =
                AnyProjectFragmentDirections.actionProjectFragmentToAnyProjectDetailsFragment(
                    projectId = args.projectId
                )
            debounceNavigateTo(this@AnyProjectFragment) { _ ->
                findNavController().navigate(action)
            }
        }
    }

    private fun updateUI(state: AnyProjectScreenState) {
        when (state) {
            is AnyProjectScreenState.Loading -> showLoading()
            is AnyProjectScreenState.Content -> showContent(state.anyProject)
            is AnyProjectScreenState.Error -> showError()
        }
    }

    private fun showLoading() {
        Toast.makeText(requireContext(), "Загрузка", Toast.LENGTH_SHORT).show()
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
    }

    private fun showContent(anyProject: AnyProjectModel) {
        val radius = binding.root.resources.getDimensionPixelSize(R.dimen.corner_radius_10)
        binding.apply {
            Glide.with(requireContext())
                .load(anyProject.logoFilePath)
                .placeholder(R.drawable.ic_placeholder_logo)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(ivProjectLogo)

            tvProjectName.text = anyProject.name
            tvProjectDirection.text = anyProject.area
            tvProjectDescription.text = anyProject.shortDescription
            tvProjectNews.text =
                format(getString(R.string.project_news), anyProject.publicationsCount)
            innoProgButtonView.isVisible = anyProject.itsCustomProject

            if (anyProject.projectNews != null) {
                rvPublications.isVisible = true
                newsAdapter.submitList(anyProject.projectNews)
            } else {
                rvPublications.isVisible = false
            }
        }
    }
}
