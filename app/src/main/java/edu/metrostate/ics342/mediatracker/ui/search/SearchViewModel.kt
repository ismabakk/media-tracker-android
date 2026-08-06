package edu.metrostate.ics342.mediatracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: DefaultMediaRepository =
        DefaultMediaRepository()
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedType = MutableStateFlow("all")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _results =
        MutableStateFlow<List<Media>>(emptyList())

    val results: StateFlow<List<Media>> =
        _results.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        search()
    }

    fun onQueryChange(value: String) {
        _query.value = value
        search()
    }

    fun onTypeChange(type: String) {
        _selectedType.value = type
        search()
    }

    fun search() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _results.value =
                    repository.searchMedia(
                        query = _query.value,
                        type = _selectedType.value,
                        limit = PAGE_SIZE
                    )
            } catch (error: Exception) {
                _results.value = emptyList()
                _errorMessage.value =
                    error.message ?: "Unable to search media."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        // Backend pagination will be added later.
    }
}