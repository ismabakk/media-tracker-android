package edu.metrostate.ics342.mediatracker.data.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitInstance {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private lateinit var retrofit: Retrofit

    fun initialize(context: Context) {
        if (::retrofit.isInitialized) {
            return
        }

        val sessionRepository =
            DefaultSessionRepository(
                context = context.applicationContext
            )

        val authInterceptor =
            AuthInterceptor(
                sessionRepository = sessionRepository
            )

        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        val client =
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

        retrofit =
            Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(
                    json.asConverterFactory(
                        "application/json".toMediaType()
                    )
                )
                .build()
    }

    val userApiService: UserApiService
        get() {
            check(::retrofit.isInitialized) {
                "RetrofitInstance has not been initialized."
            }

            return retrofit.create(UserApiService::class.java)
        }

    val mediaApiService: MediaApiService
        get() {
            check(::retrofit.isInitialized) {
                "RetrofitInstance has not been initialized."
            }

            return retrofit.create(MediaApiService::class.java)
        }
}