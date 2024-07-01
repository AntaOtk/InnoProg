package com.innoprog.android.feature.training.courseInformation.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.innoprog.android.base.BaseFragment
import com.innoprog.android.base.BaseViewModel
import com.innoprog.android.databinding.FragmentCourseInformationBinding
import com.innoprog.android.di.AppComponentHolder
import com.innoprog.android.di.ScreenComponent
import com.innoprog.android.feature.training.common.VerticalSpaceDecorator
import com.innoprog.android.feature.training.courseInformation.di.DaggerCourseInformationComponent
import com.innoprog.android.feature.training.courseInformation.domain.model.CourseInformationDocumentModel
import com.innoprog.android.feature.training.courseInformation.domain.model.CourseInformationImageModel
import com.innoprog.android.feature.training.courseInformation.domain.model.CourseInformationVideoModel
import com.innoprog.android.uikit.R

class CourseInformationFragment : BaseFragment<FragmentCourseInformationBinding, BaseViewModel>() {

    override val viewModel by injectViewModel<CourseInformationViewModel>()
    override fun diComponent(): ScreenComponent {
        val appComponent = AppComponentHolder.getComponent()
        return DaggerCourseInformationComponent.builder()
            .appComponent(appComponent)
            .build()
    }

    private val courseId by lazy {
        arguments?.let { args ->
            CourseInformationFragmentArgs.fromBundle(args).courseId
        } ?: ""
    }

    private var videoAdapter: VideoAdapter? = null
    private var documentAdapter: DocumentRecyclerViewAdapter? = null
    private val decorator: VerticalSpaceDecorator by lazy {
        VerticalSpaceDecorator(resources.getDimensionPixelSize(R.dimen.margin_8))
    }
    private var imageList = listOf<CourseInformationImageModel>()
    private var videoList = listOf<CourseInformationVideoModel>()
    private var documentList = listOf<CourseInformationDocumentModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCourseInformationBinding {
        return FragmentCourseInformationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.courseInformationTopBar.setLeftIconClickListener {
            viewModel.navigateUp()
        }

        initVideoRecyclerView()
        initDocumentsRecyclerView()
        courseId?.let { viewModel.getCourseInformation(it) }
    }

    private fun initVideoRecyclerView() {
        videoAdapter = VideoAdapter(requireContext()) {
            viewModel.navigateTo(
                com.innoprog.android.R.id.videoPlayerFragment,
                bundleOf(VIDEO_PLAYER_KEY to it)
            )
        }
        binding.courseInformationVideoRV.addItemDecoration(decorator)
        binding.courseInformationVideoRV.adapter = videoAdapter
    }

    private fun initDocumentsRecyclerView() {
        documentAdapter = DocumentRecyclerViewAdapter { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        binding.courseInformationDocumentsRV.addItemDecoration(decorator)
        binding.courseInformationDocumentsRV.adapter = documentAdapter
    }

    private fun render(state: CourseInformationState) {
        when (state) {
            is CourseInformationState.Content -> {
                installAttributes(state)
            }

            is CourseInformationState.Error -> Unit
            is CourseInformationState.Load -> {
                binding.progress.show()
                binding.courseInformation.visibility = View.INVISIBLE
            }
        }
    }

    private fun installAttributes(state: CourseInformationState) {
        binding.progress.hide()
        binding.courseInformation.visibility = View.VISIBLE
        imageList = viewModel.getImage(state.courseInformation.attachments)
        if (imageList.isNotEmpty()) {
            Glide.with(requireContext())
                .load(imageList)
                .into(binding.courseLogo)
        } else {
            binding.courseLogo.visibility = View.INVISIBLE
        }

        binding.courseInformationTitle.text = state.courseInformation.title
        binding.courseInformationDescription.text = state.courseInformation.description

        val initials =
            state.courseInformation.authorName.split(' ').map { it.first().uppercaseChar() }
                .joinToString(separator = "", limit = 2)
        binding.courseInformationAuthorAvatar.text = initials.ifBlank { "?" }

        binding.courseInformationAuthorName.text = state.courseInformation.authorName
        // binding.courseInformationAuthorPosition.text = state.courseInformation.courseAuthorPosition
        binding.courseInformationDate.text = state.courseInformation.createdDate
        binding.courseInformationDirection.text = state.courseInformation.direction

        videoList = viewModel.getVideo(state.courseInformation.attachments)
        if (videoList.isNotEmpty()) {
            binding.courseInformationVideoTitle.visibility = View.VISIBLE
            binding.courseInformationVideoRV.visibility = View.VISIBLE
            videoAdapter?.setVideoList(videoList)
        } else {
            binding.courseInformationVideoTitle.visibility = View.INVISIBLE
            binding.courseInformationVideoRV.visibility = View.INVISIBLE
        }

        documentList = viewModel.getDocument(state.courseInformation.attachments)
        if (documentList.isNotEmpty()) {
            binding.courseInformationDocumentsTitle.visibility = View.VISIBLE
            binding.courseInformationDocumentsRV.visibility = View.VISIBLE
            documentAdapter?.setDocumentList(documentList)
        } else {
            binding.courseInformationDocumentsTitle.visibility = View.INVISIBLE
            binding.courseInformationDocumentsRV.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val VIDEO_PLAYER_KEY = "VIDEO_PLAYER_KEY"
    }
}
