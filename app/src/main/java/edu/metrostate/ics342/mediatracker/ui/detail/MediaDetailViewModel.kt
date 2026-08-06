package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import edu.metrostate.ics342.mediatracker.data.network.MediaNotFoundException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MediaDetailUiState {

    data object Loading : MediaDetailUiState

    data class Success(
        val detail: MediaDetail,
        val libraryStatus: LibraryStatus?,
        val isFavorited: Boolean
    ) : MediaDetailUiState

    data class Error(
        val message: String
    ) : MediaDetailUiState
}

class MediaDetailViewModel(
    private val repository: DefaultMediaRepository =
        DefaultMediaRepository()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MediaDetailUiState>(
            MediaDetailUiState.Loading
        )

    val uiState: StateFlow<MediaDetailUiState> =
        _uiState.asStateFlow()

    private val _actionError =
        MutableStateFlow<String?>(null)

    val actionError: StateFlow<String?> =
        _actionError.asStateFlow()

    private var currentMediaId: Int? = null

    fun load(mediaId: Int) {
        currentMediaId = mediaId
        _uiState.value = MediaDetailUiState.Loading

        viewModelScope.launch {
            try {
                val detail =
                    repository.getMediaDetail(mediaId)

                val libraryItem =
                    runCatching {
                        repository.getLibraryItem(mediaId)
                    }.getOrNull()

                val favorite =
                    runCatching {
                        repository.getFavorite(mediaId)
                    }.getOrNull()

                _uiState.value =
                    MediaDetailUiState.Success(
                        detail = detail,
                        libraryStatus = libraryItem?.status,
                        isFavorited = favorite != null
                    )

            } catch (error: MediaNotFoundException) {
                _uiState.value =
                    MediaDetailUiState.Error(
                        message = "Media not found."
                    )

            } catch (error: Exception) {
                _uiState.value =
                    MediaDetailUiState.Error(
                        message = error.message
                            ?: "Unable to load media."
                    )
            }
        }
    }

    /*
     * Optimistic add:
     * Change the button immediately, then call the server.
     * If the request fails, restore the original state.
     */
    fun addToLibrary() {
        val currentState =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        val mediaId = currentMediaId ?: return

        if (currentState.libraryStatus != null) {
            return
        }

        _uiState.value =
            currentState.copy(
                libraryStatus = LibraryStatus.WANT_TO
            )

        viewModelScope.launch {
            try {
                repository.addToLibrary(
                    mediaId = mediaId,
                    status = LibraryStatus.WANT_TO
                )

            } catch (error: Exception) {
                val latestState =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latestState.copy(
                        libraryStatus = null
                    )

                _actionError.value =
                    "Couldn't add to library. Try again."
            }
        }
    }

    /*
     * Optimistic favorite toggle:
     * Flip the saved state immediately.
     * POST when saving, DELETE when unsaving.
     * Roll back if the request fails.
     */
    fun toggleFavorite() {
        val currentState =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        val mediaId = currentMediaId ?: return
        val wasFavorited = currentState.isFavorited

        _uiState.value =
            currentState.copy(
                isFavorited = !wasFavorited
            )

        viewModelScope.launch {
            try {
                if (wasFavorited) {
                    repository.removeFavorite(mediaId)
                } else {
                    repository.addFavorite(mediaId)
                }

            } catch (error: Exception) {
                val latestState =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latestState.copy(
                        isFavorited = wasFavorited
                    )

                _actionError.value =
                    "Couldn't update favorite. Try again."
            }
        }
    }

    fun clearActionError() {
        _actionError.value = null
    }

    fun retry() {
        currentMediaId?.let { mediaId ->
            load(mediaId)
        }
    }
}