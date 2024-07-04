package com.innoprog.android.feature.projects.projectsScreen.data.network

import android.content.Context
import com.innoprog.android.feature.profile.profiledetails.data.db.ProfileDao
import com.innoprog.android.network.data.ApiConstants
import com.innoprog.android.network.data.Response
import com.innoprog.android.util.isInternetReachable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ProjectListNetworkClientImpl @Inject constructor(
    private val apiService: ProjectApiService,
    private val context: Context,
    private val profileDao: ProfileDao
) : ProjectListNetworkClient {
    override suspend fun getProjectList(): Response {
        if (context.isInternetReachable().not()) {
            return Response().apply {
                resultCode = ApiConstants.NO_INTERNET_CONNECTION_CODE
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val userId = getUserId()
                val response = apiService.getProjectList(
                    userId,
                    null,
                    PROJECTS_AMOUNT_PER_PAGE_50
                )

                if (response.isSuccessful) {
                    ProjectListResponse(result = response.body() ?: emptyList()).apply {
                        resultCode = ApiConstants.SUCCESS_CODE
                    }
                } else {
                    Response().apply { resultCode = response.code() }
                }
            } catch (exception: HttpException) {
                Response().apply { resultCode = exception.code() }
            } catch (exception: Exception) {
                Response().apply { resultCode = ApiConstants.INTERNAL_SERVER_ERROR }
            }
        }
    }

    private suspend fun getUserId(): String {
        val profile = profileDao.getProfile()
        return profile.userId
    }

    private companion object {
        const val PROJECTS_AMOUNT_PER_PAGE_50 = 50
    }
}