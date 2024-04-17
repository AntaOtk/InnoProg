package com.innoprog.android.feature.auth.authorization.data

import com.innoprog.android.network.data.Response

data class LoginResponse(
    val id: String,
    val name: String,
    val authorities: List<String>
) : Response()
