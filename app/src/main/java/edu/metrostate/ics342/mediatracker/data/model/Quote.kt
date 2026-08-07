package edu.metrostate.ics342.mediatracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    val userId: String,
    val mediaId: Int,
    val quoteText: String,
    val pageNumber: Int? = null,
    val isPublic: Boolean,
    val likeCount: Int,
    val createdAt: String,
    val media: Media
)