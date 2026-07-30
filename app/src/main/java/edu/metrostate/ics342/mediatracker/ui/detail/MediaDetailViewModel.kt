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
        val libraryStatus: LibraryStatus?
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

                _uiState.value =
                    MediaDetailUiState.Success(
                        detail = detail,
                        libraryStatus = libraryItem?.status
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

    fun retry() {
        currentMediaId?.let { mediaId ->
            load(mediaId)
        }
    }
}