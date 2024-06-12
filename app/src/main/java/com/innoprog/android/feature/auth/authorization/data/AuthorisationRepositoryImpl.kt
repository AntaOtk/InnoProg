package com.innoprog.android.feature.auth.authorization.data

import android.util.Log
import com.innoprog.android.feature.auth.authorization.data.network.LoginApi
import com.innoprog.android.feature.auth.authorization.domain.AuthorisationRepository
import com.innoprog.android.feature.auth.authorization.domain.model.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.Base64
import javax.inject.Inject

class AuthorisationRepositoryImpl @Inject constructor(
    private val api: LoginApi,
) :
    AuthorisationRepository {

    override fun verify(login: String, password: String): Flow<AuthState> = flow {
        try {
            val authData = "$login:$password"
            val header = "Basic " + Base64.getEncoder().encodeToString(authData.toByteArray())
            val response = api.authorize(header)
            Log.d("1234", response.code().toString())
            when (response.code()) {
                SUCCESS -> {
                    AuthorizationBody.data = authData
                    emit(AuthState.SUCCESS)
                }
                ERROR -> emit(AuthState.VERIFICATION_ERROR)
                else -> emit(AuthState.VERIFICATION_ERROR)
            }
        } catch (e: HttpException) {
            Log.e("myRegistration", " error: $e")
            emit(AuthState.CONNECTION_ERROR)
        } catch (e: SocketTimeoutException) {
            Log.e("myRegistration", " error: $e")
            emit(AuthState.CONNECTION_ERROR)
        }
    }

    companion object {
        const val SUCCESS = 200
        const val ERROR = 400
    }
}
