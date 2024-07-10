package com.innoprog.android.feature.feed.newsfeed.presentation

import com.innoprog.android.feature.feed.newsfeed.domain.models.NewsWithProject
import com.innoprog.android.util.ErrorScreenState

sealed interface FeedScreenState {
    data class Loading(val isPagination: Boolean = false) : FeedScreenState

    class Error(val errorType: ErrorScreenState, val isPagination: Boolean = false) : FeedScreenState

    class Content(val newsFeed: List<NewsWithProject>) : FeedScreenState
}
