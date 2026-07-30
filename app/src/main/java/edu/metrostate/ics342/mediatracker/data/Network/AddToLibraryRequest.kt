package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import kotlinx.serialization.Serializable

@Serializable
data class AddToLibraryRequest(
    val mediaId: Int,
    val status: String
)