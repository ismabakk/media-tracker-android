package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.Favorite
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.model.Review
import retrofit2.Response

class DefaultMediaRepository(
    private val service: MediaApiService = RetrofitInstance.mediaApiService
) {

    suspend fun searchMedia(
        query: String,
        type: String,
        limit: Int = 20
    ): List<Media> {
        val response = service.searchMedia(
            query = query.ifBlank { null },
            type = if (type == "all") null else type,
            limit = limit
        )

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to search media. Code: ${response.code()}"
            )
        }

        return response.body() ?: emptyList()
    }

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

    suspend fun addToLibrary(
        mediaId: Int,
        status: LibraryStatus
    ): LibraryItem {
        val request = AddToLibraryRequest(
            mediaId = mediaId,
            status = status.toApiString()
        )

        val response = service.addToLibrary(request)

        if (response.code() == 409) {
            return getLibraryItem(mediaId)
                ?: throw IllegalStateException(
                    "Item is already in the library."
                )
        }

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to add item to library. Code: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IllegalStateException(
                "Library response was empty."
            )
    }

    suspend fun updateLibraryStatus(
        mediaId: Int,
        status: LibraryStatus
    ): LibraryItem {
        val request = UpdateLibraryStatusRequest(
            status = status.toApiString()
        )

        val response = service.updateLibraryStatus(
            mediaId = mediaId,
            body = request
        )

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to update library status. Code: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IllegalStateException(
                "Updated library response was empty."
            )
    }

    suspend fun removeFromLibrary(mediaId: Int) {
        val response = service.removeFromLibrary(mediaId)

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to remove item from library. Code: ${response.code()}"
            )
        }
    }

    suspend fun getLibrary(
        status: LibraryStatus
    ): List<LibraryItem> {
        val response = service.getLibrary(
            status = status.toApiString()
        )

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to load library. Code: ${response.code()}"
            )
        }

        return response.body() ?: emptyList()
    }

    suspend fun getFavorite(mediaId: Int): Favorite? {
        val response: Response<Favorite> =
            service.getFavorite(mediaId)

        if (response.code() == 404) {
            return null
        }

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to check favorite. Code: ${response.code()}"
            )
        }

        return response.body()
    }

    suspend fun addFavorite(mediaId: Int): Favorite {
        val request = AddToFavoritesRequest(
            mediaId = mediaId
        )

        val response = service.addFavorite(request)

        if (response.code() == 409) {
            return getFavorite(mediaId)
                ?: throw IllegalStateException(
                    "Item is already saved."
                )
        }

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to save favorite. Code: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IllegalStateException(
                "Favorite response was empty."
            )
    }

    suspend fun removeFavorite(mediaId: Int) {
        val response = service.removeFavorite(mediaId)

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to remove favorite. Code: ${response.code()}"
            )
        }
    }

    suspend fun getReviews(mediaId: Int): List<Review> {
        val response = service.getReviews(mediaId)

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to load reviews. Code: ${response.code()}"
            )
        }

        return response.body() ?: emptyList()
    }

    suspend fun createQuote(
        mediaId: Int,
        quoteText: String,
        pageNumber: Int?,
        isPublic: Boolean
    ): Quote {
        val request = CreateQuoteRequest(
            mediaId = mediaId,
            quoteText = quoteText,
            pageNumber = pageNumber,
            isPublic = isPublic
        )

        val response = service.createQuote(request)

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to save quote. Code: ${response.code()}"
            )
        }

        return response.body()
            ?: throw IllegalStateException(
                "Quote response was empty."
            )
    }

    suspend fun getQuotes(): List<Quote> {
        val response = service.getQuotes()

        if (!response.isSuccessful) {
            throw IllegalStateException(
                "Failed to load quotes. Code: ${response.code()}"
            )
        }

        return response.body() ?: emptyList()
    }
}

class MediaNotFoundException(
    message: String
) : Exception(message)