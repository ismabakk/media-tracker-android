package edu.metrostate.ics342.mediatracker.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuotesUiState {

    data object Loading : QuotesUiState

    data class Success(
        val quotes: List<Quote>
    ) : QuotesUiState

    data class Error(
        val message: String
    ) : QuotesUiState
}

class QuotesViewModel(
    private val repository: DefaultMediaRepository =
        DefaultMediaRepository()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<QuotesUiState>(
            QuotesUiState.Loading
        )

    val uiState: StateFlow<QuotesUiState> =
        _uiState.asStateFlow()

    init {
        loadQuotes()
    }

    fun loadQuotes() {
        _uiState.value = QuotesUiState.Loading

        viewModelScope.launch {
            try {
                val quotes = repository.getQuotes()

                _uiState.value =
                    QuotesUiState.Success(
                        quotes = quotes
                    )

            } catch (error: Exception) {
                _uiState.value =
                    QuotesUiState.Error(
                        message = error.message
                            ?: "Unable to load quotes."
                    )
            }
        }
    }

    fun retry() {
        loadQuotes()
    }
}