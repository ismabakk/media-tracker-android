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

    /*
     * Week 11 quote form state
     */

    private val _quoteText =
        MutableStateFlow("")

    val quoteText: StateFlow<String> =
        _quoteText.asStateFlow()

    private val _quotePageNumber =
        MutableStateFlow("")

    val quotePageNumber: StateFlow<String> =
        _quotePageNumber.asStateFlow()

    private val _quoteIsPublic =
        MutableStateFlow(false)

    val quoteIsPublic: StateFlow<Boolean> =
        _quoteIsPublic.asStateFlow()

    private val _isSavingQuote =
        MutableStateFlow(false)

    val isSavingQuote: StateFlow<Boolean> =
        _isSavingQuote.asStateFlow()

    private val _quoteMessage =
        MutableStateFlow<String?>(null)

    val quoteMessage: StateFlow<String?> =
        _quoteMessage.asStateFlow()

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
     * Change the button immediately then call the server.
     * If the request fails restore the original state.
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

    /*
     * Week 11 quote form functions
     */

    fun updateQuoteText(value: String) {
        if (value.length <= 500) {
            _quoteText.value = value
            _quoteMessage.value = null
        }
    }

    fun updateQuotePageNumber(value: String) {
        if (value.isBlank() || value.all { character ->
                character.isDigit()
            }
        ) {
            _quotePageNumber.value = value
            _quoteMessage.value = null
        }
    }

    fun updateQuoteVisibility(isPublic: Boolean) {
        _quoteIsPublic.value = isPublic
    }

    fun saveQuote() {
        val mediaId = currentMediaId ?: return
        val cleanedQuote = _quoteText.value.trim()

        if (cleanedQuote.isBlank()) {
            _quoteMessage.value =
                "Quote text is required."
            return
        }

        if (_isSavingQuote.value) {
            return
        }

        viewModelScope.launch {
            _isSavingQuote.value = true
            _quoteMessage.value = null

            try {
                repository.createQuote(
                    mediaId = mediaId,
                    quoteText = cleanedQuote,
                    pageNumber =
                        _quotePageNumber.value.toIntOrNull(),
                    isPublic = _quoteIsPublic.value
                )

                _quoteText.value = ""
                _quotePageNumber.value = ""
                _quoteIsPublic.value = false
                _quoteMessage.value =
                    "Quote saved successfully."

            } catch (error: Exception) {
                _quoteMessage.value =
                    "Couldn't save quote. Try again."

            } finally {
                _isSavingQuote.value = false
            }
        }
    }

    fun clearQuoteMessage() {
        _quoteMessage.value = null
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