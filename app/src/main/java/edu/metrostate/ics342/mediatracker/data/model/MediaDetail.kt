package edu.metrostate.ics342.mediatracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaDetail(
    val id: Int,
    val mediaType: String,
    val title: String,
    val author: String? = null,
    val director: String? = null,
    val creator: String? = null,
    val network: String? = null,
    val coverUrl: String? = null,
    val publishedYear: Int? = null,
    val averageRating: Float = 0f,
    val ratingCount: Int = 0,
    val genres: List<String> = emptyList(),
    val description: String? = null,
    val pageCount: Int? = null,
    val runtimeMinutes: Int? = null,
    val seasonCount: Int? = null,
    val episodeCount: Int? = null,
    val isbn: String? = null,
    val reviewCount: Int = 0
)