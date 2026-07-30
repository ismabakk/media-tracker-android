package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import retrofit2.Response

class DefaultMediaRepository(
    private val service: MediaApiService = RetrofitInstance.mediaApiService
) {

    suspend fun getMediaDetail(id: Int): MediaDetail {
        val response = service.getMediaDetail(id)

        if (response.code() == 404) {
            throw MediaNotFoundException("Media not found.")
        }

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to load media. Code: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IllegalStateException(
                "Media response was empty."
            )
    }

    suspend fun getLibraryItem(mediaId: Int): LibraryItem? {
        val response: Response<LibraryItem> =
            service.getLibraryItem(mediaId)

        if (response.code() == 404) {
            return null
        }

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to load library item. Code: ${response.code()}"
            )
        }

        return response.body()
    }
}

class MediaNotFoundException(
    message: String
) : Exception(message)