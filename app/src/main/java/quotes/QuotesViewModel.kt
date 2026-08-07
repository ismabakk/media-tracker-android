package edu.metrostate.ics342.mediatracker.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class QuotesListType {
    MY_QUOTES,
    PUBLIC_QUOTES
}

sealed interface QuotesUiState {

    data object Loading : QuotesUiState

    data class Success(
        val quotes: List<Quote>,
        val listType: QuotesListType,
        val likedQuoteIds: Set<Int> = emptySet(),
        val busyQuoteIds: Set<Int> = emptySet(),
        val message: String? = null
    ) : QuotesUiState

    data class Error(
        val message: String,
        val listType: QuotesListType
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

    private var currentListType =
        QuotesListType.MY_QUOTES

    init {
        loadMyQuotes()
    }

    fun loadMyQuotes() {
        currentListType =
            QuotesListType.MY_QUOTES

        loadQuotes()
    }

    fun loadPublicQuotes() {
        currentListType =
            QuotesListType.PUBLIC_QUOTES

        loadQuotes()
    }

    private fun loadQuotes() {
        _uiState.value =
            QuotesUiState.Loading

        viewModelScope.launch {
            try {
                val quotes =
                    when (currentListType) {
                        QuotesListType.MY_QUOTES ->
                            repository.getQuotes()

                        QuotesListType.PUBLIC_QUOTES ->
                            repository.getPublicQuotes()
                    }

                _uiState.value =
                    QuotesUiState.Success(
                        quotes = quotes,
                        listType = currentListType
                    )

            } catch (error: Exception) {
                _uiState.value =
                    QuotesUiState.Error(
                        message =
                            error.message
                                ?: "Unable to load quotes.",
                        listType = currentListType
                    )
            }
        }
    }

    fun updateQuote(
        quoteId: Int,
        quoteText: String,
        pageNumberText: String,
        isPublic: Boolean
    ) {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        val cleanedText =
            quoteText.trim()

        if (cleanedText.isBlank()) {
            showMessage(
                "Quote text is required."
            )
            return
        }

        if (cleanedText.length > 500) {
            showMessage(
                "Quote must be 500 characters or less."
            )
            return
        }

        val pageNumber =
            if (pageNumberText.isBlank()) {
                null
            } else {
                pageNumberText.toIntOrNull()
            }

        if (
            pageNumberText.isNotBlank() &&
            pageNumber == null
        ) {
            showMessage(
                "Page number must be a number."
            )
            return
        }

        if (quoteId in current.busyQuoteIds) {
            return
        }

        setQuoteBusy(
            quoteId = quoteId,
            isBusy = true
        )

        viewModelScope.launch {
            try {
                val updatedQuote =
                    repository.updateQuote(
                        quoteId = quoteId,
                        quoteText = cleanedText,
                        pageNumber = pageNumber,
                        isPublic = isPublic
                    )

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes.map { quote ->
                                if (quote.id == quoteId) {
                                    updatedQuote
                                } else {
                                    quote
                                }
                            },
                        busyQuoteIds =
                            latest.busyQuoteIds -
                                    quoteId,
                        message =
                            "Quote updated."
                    )

            } catch (error: Exception) {
                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        busyQuoteIds =
                            latest.busyQuoteIds -
                                    quoteId,
                        message =
                            error.message
                                ?: "Unable to update quote."
                    )
            }
        }
    }

    fun deleteQuote(
        quoteId: Int
    ) {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        if (quoteId in current.busyQuoteIds) {
            return
        }

        setQuoteBusy(
            quoteId = quoteId,
            isBusy = true
        )

        viewModelScope.launch {
            try {
                repository.deleteQuote(
                    quoteId = quoteId
                )

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes.filterNot {
                                    quote ->
                                quote.id == quoteId
                            },
                        busyQuoteIds =
                            latest.busyQuoteIds -
                                    quoteId,
                        message =
                            "Quote deleted."
                    )

            } catch (error: Exception) {
                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        busyQuoteIds =
                            latest.busyQuoteIds -
                                    quoteId,
                        message =
                            error.message
                                ?: "Unable to delete quote."
                    )
            }
        }
    }

    fun toggleLike(
        quoteId: Int
    ) {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        if (
            current.listType !=
            QuotesListType.PUBLIC_QUOTES
        ) {
            return
        }

        if (quoteId in current.busyQuoteIds) {
            return
        }

        val isCurrentlyLiked =
            quoteId in current.likedQuoteIds

        setQuoteBusy(
            quoteId = quoteId,
            isBusy = true
        )

        viewModelScope.launch {
            try {
                if (isCurrentlyLiked) {
                    repository.unlikeQuote(
                        quoteId = quoteId
                    )

                    updateLikeState(
                        quoteId = quoteId,
                        isLiked = false,
                        changeCount = true
                    )

                } else {
                    val likeWasAdded =
                        repository.likeQuote(
                            quoteId = quoteId
                        )

                    /*
                     * false means the server returned 409.
                     * The user already liked it so we mark it
                     * liked without adding to the count again.
                     */
                    updateLikeState(
                        quoteId = quoteId,
                        isLiked = true,
                        changeCount = likeWasAdded
                    )
                }

            } catch (error: Exception) {
                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        busyQuoteIds =
                            latest.busyQuoteIds -
                                    quoteId,
                        message =
                            error.message
                                ?: "Unable to update like."
                    )
            }
        }
    }

    private fun updateLikeState(
        quoteId: Int,
        isLiked: Boolean,
        changeCount: Boolean
    ) {
        val latest =
            _uiState.value as? QuotesUiState.Success
                ?: return

        val updatedLikedIds =
            if (isLiked) {
                latest.likedQuoteIds + quoteId
            } else {
                latest.likedQuoteIds - quoteId
            }

        val updatedQuotes =
            latest.quotes.map { quote ->
                if (quote.id != quoteId) {
                    quote
                } else {
                    val updatedCount =
                        when {
                            !changeCount ->
                                quote.likeCount

                            isLiked ->
                                quote.likeCount + 1

                            else ->
                                (quote.likeCount - 1)
                                    .coerceAtLeast(0)
                        }

                    quote.copy(
                        likeCount = updatedCount
                    )
                }
            }

        _uiState.value =
            latest.copy(
                quotes = updatedQuotes,
                likedQuoteIds = updatedLikedIds,
                busyQuoteIds =
                    latest.busyQuoteIds -
                            quoteId,
                message = null
            )
    }

    private fun setQuoteBusy(
        quoteId: Int,
        isBusy: Boolean
    ) {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        val updatedBusyIds =
            if (isBusy) {
                current.busyQuoteIds + quoteId
            } else {
                current.busyQuoteIds - quoteId
            }

        _uiState.value =
            current.copy(
                busyQuoteIds = updatedBusyIds
            )
    }

    private fun showMessage(
        message: String
    ) {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        _uiState.value =
            current.copy(
                message = message
            )
    }

    fun clearMessage() {
        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        _uiState.value =
            current.copy(
                message = null
            )
    }

    fun retry() {
        loadQuotes()
    }
}