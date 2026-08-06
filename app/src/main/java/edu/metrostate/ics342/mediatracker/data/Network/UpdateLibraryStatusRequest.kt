package edu.metrostate.ics342.mediatracker.data.network

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLibraryStatusRequest(
    val status: String
)