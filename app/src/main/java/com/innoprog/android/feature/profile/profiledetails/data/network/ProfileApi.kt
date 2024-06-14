package com.innoprog.android.feature.profile.profiledetails.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {

    @GET("/v1/profile")
    suspend fun loadProfile(): ProfileResponse

    @GET("/v1/profile/company")
    suspend fun loadProfileCompany(): ProfileCompanyResponse

    @GET("/v1/feed")
    suspend fun getAll(
        @Query("authorId") authorId: String
    ): IdeaResponse

    @GET("/v1/projects")
    suspend fun getProjects(
        @Query("authorId") authorId: String
    ): ProjectResponse

    @GET("/v1/feed")
    suspend fun getIdeas(
        @Query("type") type: String,
        @Query("authorId") authorId: String
    ): IdeaResponse

    @GET("/v1/feed/likes")
    suspend fun getLikes(
        @Query("pageSize") pageSize: Int
    ): IdeaResponse

    @GET("/v1/feed/favorites")
    suspend fun getFavorites(
        @Query("pageSize") pageSize: Int
    ): IdeaResponse
}
