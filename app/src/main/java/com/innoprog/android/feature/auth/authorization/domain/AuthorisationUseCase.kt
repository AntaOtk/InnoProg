package com.innoprog.android.feature.auth.authorization.domain

import com.innoprog.android.feature.auth.authorization.domain.model.UserData
import com.innoprog.android.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthorisationUseCase {
    fun verify(login: String, password: String): Flow<Resource<UserData>>
}
