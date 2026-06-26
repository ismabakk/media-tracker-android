package edu.metrostate.ics342.mediatracker.ui.search

import androidx.lifecycle.ViewModel
import edu.metrostate.ics342.mediatracker.data.FakeMediaRepository
import edu.metrostate.ics342.mediatracker.data.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedType = MutableStateFlow("all")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _results = MutableStateFlow<List<Media>>(emptyList())
    val results: StateFlow<List<Media>> = _results.asStateFlow()

    private var allResults: List<Media> = emptyList()
    private var currentPage = 1

    fun onQueryChange(value: String) {
        _query.value = value
        search()
    }

    fun onTypeChange(type: String) {
        _selectedType.value = type
        search()
    }

    fun search() {
        currentPage = 1

        allResults = FakeMediaRepository.searchMedia(
            query = _query.value,
            type = _selectedType.value
        )

        _results.value = allResults.take(PAGE_SIZE)
    }

    fun loadNextPage() {
        val nextPage = currentPage + 1
        val nextItems = allResults.take(nextPage * PAGE_SIZE)

        if (nextItems.size > _results.value.size) {
            currentPage = nextPage
            _results.value = nextItems
        }
    }
}