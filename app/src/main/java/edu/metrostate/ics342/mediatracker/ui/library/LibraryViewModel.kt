package edu.metrostate.ics342.mediatracker.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: DefaultMediaRepository =
        DefaultMediaRepository()
) : ViewModel() {

    private val _libraryItems =
        MutableStateFlow<List<LibraryItem>>(emptyList())

    val libraryItems: StateFlow<List<LibraryItem>> =
        _libraryItems.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val wantToItems =
                    repository.getLibrary(
                        status = LibraryStatus.WANT_TO
                    )

                val inProgressItems =
                    repository.getLibrary(
                        status = LibraryStatus.IN_PROGRESS
                    )

                val finishedItems =
                    repository.getLibrary(
                        status = LibraryStatus.FINISHED
                    )

                _libraryItems.value =
                    wantToItems +
                            inProgressItems +
                            finishedItems

            } catch (error: Exception) {

                _errorMessage.value =
                    error.message ?: "Failed to load library."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeItem(mediaId: Int) {

        val backup =
            _libraryItems.value.find { item ->
                item.mediaId == mediaId
            } ?: return

        // Optimistic update:
        // remove from the UI immediately
        _libraryItems.value =
            _libraryItems.value.filter { item ->
                item.mediaId != mediaId
            }

        viewModelScope.launch {
            try {

                // Then update the server
                repository.removeFromLibrary(mediaId)

            } catch (error: Exception) {

                // Roll back if the network request fails
                _libraryItems.value =
                    _libraryItems.value + backup

                _errorMessage.value =
                    "Couldn't remove item. Try again."
            }
        }
    }

    fun updateStatus(
        mediaId: Int,
        newStatus: LibraryStatus
    ) {

        val backup =
            _libraryItems.value.find { item ->
                item.mediaId == mediaId
            } ?: return

        if (backup.status == newStatus) {
            return
        }

        // Optimistic update:
        // change the status in the UI immediately
        _libraryItems.value =
            _libraryItems.value.map { item ->

                if (item.mediaId == mediaId) {
                    item.copy(
                        status = newStatus
                    )
                } else {
                    item
                }
            }

        viewModelScope.launch {
            try {

                // Then update the server
                repository.updateLibraryStatus(
                    mediaId = mediaId,
                    status = newStatus
                )

            } catch (error: Exception) {

                // Roll back to the old item if it fails
                _libraryItems.value =
                    _libraryItems.value.map { item ->

                        if (item.mediaId == mediaId) {
                            backup
                        } else {
                            item
                        }
                    }

                _errorMessage.value =
                    "Couldn't update status. Try again."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}