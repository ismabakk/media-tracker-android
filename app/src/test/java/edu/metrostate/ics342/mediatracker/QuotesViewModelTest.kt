package edu.metrostate.ics342.mediatracker

import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import edu.metrostate.ics342.mediatracker.ui.quotes.QuotesListType
import edu.metrostate.ics342.mediatracker.ui.quotes.QuotesUiState
import edu.metrostate.ics342.mediatracker.ui.quotes.QuotesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuotesViewModelTest {

    private val testDispatcher =
        StandardTestDispatcher()

    private val repository =
        mockk<DefaultMediaRepository>()

    private val testQuote =
        Quote(
            id = 1,
            userId = "user-2",
            mediaId = 10,
            quoteText = "Test public quote",
            pageNumber = 25,
            isPublic = true,
            likeCount = 2,
            createdAt = "2026-08-07T00:00:00Z",
            media = Media(
                id = 10,
                mediaType = "book",
                title = "Test Book",
                author = "Test Author"
            )
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // QuotesViewModel loads My Quotes when it is created.
        coEvery {
            repository.getQuotes()
        } returns emptyList()

        coEvery {
            repository.getPublicQuotes()
        } returns listOf(testQuote)

        coEvery {
            repository.likeQuote(1)
        } returns true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `liking public quote updates liked state and count`() =
        runTest(testDispatcher) {

            val viewModel =
                QuotesViewModel(
                    repository = repository
                )

            // Finish the automatic My Quotes request.
            advanceUntilIdle()

            // Load the public quotes feed.
            viewModel.loadPublicQuotes()
            advanceUntilIdle()

            val publicState =
                viewModel.uiState.value
                        as QuotesUiState.Success

            assertEquals(
                QuotesListType.PUBLIC_QUOTES,
                publicState.listType
            )

            assertEquals(
                2,
                publicState.quotes.first().likeCount
            )

            // Like the public quote.
            viewModel.toggleLike(quoteId = 1)
            advanceUntilIdle()

            val likedState =
                viewModel.uiState.value
                        as QuotesUiState.Success

            assertTrue(
                1 in likedState.likedQuoteIds
            )

            assertEquals(
                3,
                likedState.quotes.first().likeCount
            )

            coVerify(exactly = 1) {
                repository.likeQuote(1)
            }
        }
}