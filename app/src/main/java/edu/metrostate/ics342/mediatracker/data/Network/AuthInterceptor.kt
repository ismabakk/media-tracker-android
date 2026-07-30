package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionRepository: SessionRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            sessionRepository.getAccessToken()
        }

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!accessToken.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $accessToken"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}