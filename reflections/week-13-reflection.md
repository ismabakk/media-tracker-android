Final Reflection — ICS 342

Name: Ismail Bakkaoui

Part 1 — Your App
1a. Commit I am most proud of

Commit URL:
https://github.com/ismabakk/media-tracker-android/pull/11/commits/06bbc08ee1ff6c39c363507626d7a94b3c99120e

The commit I am most proud of is my Week 10 Library and Favorites implementation  This was when I got the library and favorites features  working with the API instead of only displaying media.

I was proud of this because it brought together a lot of things we learned earlier in the class  I had to work with the API repository ViewModel UI state and Compose, I tested it in the emulator by adding media to my library changing its status removing items and saving favorites, this was one of the points where the project started feeling like an actual usable app.

1b. A screen I think is well-built

The screen I think is well-built is my Media Detail screen displays the information about the selected media and also lets the user add it to their library or save it as a favorite.

One thing I did better on this screen was separating the UI from the logic My MediaDetailViewModel handles things like loading the media  whether it is already in the library  and whether it is favorite also the UI then reacts to that state. If I built this during Week 2 I probably would have tried putting most of the logic directly inside the screen.

1c. Something I would improve

One thing I would improve is the error handling the main features work but I think I could make it clearer to the user when something goes wrong.

If I had another week I would test situations like losing internet connection or having an API request fail I would also make the loading and error messages more consistent across the different screens.

Part 2 — A Specific Bug
2a. Hardest bug

One of the harder problems I had was getting the Library to stay in sync with the actual data because at first changing a status or removing an item could change what I saw on the screen but that did not always mean the API data was actually updated.

I realized that changing the local UI state and changing the server data are two different things, I fixed this by updating the screen and then making the repository request I also kept a backup of the original item so if the request failed I could restore it.

I tested it by changing an item to In Progress and making sure it stayed there. I also removed an item and made sure it actually remained removed.

2b. Code that helped fix it
fun updateStatus(
mediaId: Int,
newStatus: LibraryStatus
) {
val backup =
_libraryItems.value.find { item ->
item.mediaId == mediaId
} ?: return


    _libraryItems.value =
        _libraryItems.value.map { item ->
            if (item.mediaId == mediaId) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }


    viewModelScope.launch {
        try {
            repository.updateLibraryStatus(
                mediaId = mediaId,
                status = newStatus
            )
        } catch (error: Exception) {
            _libraryItems.value =
                _libraryItems.value.map { item ->
                    if (item.mediaId == mediaId) backup else item
                }


            _errorMessage.value =
                "Couldn't update status. Try again."
        }
    }
}

This code saves the original item as a backup and then updates what the user sees. After that  it tries to update the actual data through the repository  If the request fails  it restores the original item.

This helped me understand that just because the UI changes does not mean the actual data was successfully changed.

Part 3 — What I Actually Learned
3a. Concept that took the longest to understand

One thing that took me a while to understand was how state works  with ViewModels and Compose. Earlier in the semester  remember  mutableStateOf  StateFlow  and ViewModels all seemed like different  ways of storing information.

What eventually made sense was that  the ViewModel can hold the important state and logic  while the composable observes that state and displays it. When the state changes, Compose updates the UI  If I explained it to someone else  I would say the ViewModel handles what is happening and the composable handles what  the user sees.

3b. Something I used to be confused about

Navigation was confusing to me earlier in the semester  especially when we started passing information between screens.

I learned that the route  navArgument  and the value being read by the destination all have to match. Once I understood how something like a media ID moves from one screen to another  navigation became a lot easier to  understand instead of feeling like I was just copying route code.

3c. Something I learned from a pod mate

One thing I learned from reviewing my pod mates code was how useful reusable composables can be  I saw components like ReviewCard, StatBox,  and SectionTitle separated instead of putting  everything into one large screen.

That made me look at my own code differently. I started thinking about whether  parts of my screens could be separated into smaller composables to make the code easier to read and manage.

Part 4 — Your Bonus Feature
4a. What the feature does

My bonus feature was the Quote Collection feature. It lets users save quotes they like  and come back to them later. The feature also has a My Quotes section and a Public  Quotes section, so users can manage their own quotes and also see quotes shared by other users.

For public quotes, users can like or unlike them. For their own quotes, they can edit or delete them. I started the feature in Week 11 and added more to it in Week 12. The feature ended up including editing, deleting, public/private quotes, and likes.

4b. Technically hardest part

The hardest part for  me was handling the like and unlike behavior correctly in toggleLike().

The ViewModel had to know whether the user already liked the quote, update likedQuoteIds, change the  likeCount, and stop the same quote from being clicked  multiple times while the request was still running.  I used busyQuoteIds for that.

Another part I had to handle was when the server returned a 409  because the quote had already been liked. In that case, I still needed to  mark the quote as liked without increasing  the count again.  That made this part more complicated than just pressing a Like button and adding one to a number.

4c. Most valuable test

The most valuable test I wrote was:

@Test
fun `liking public quote updates liked state and count`() =
runTest(testDispatcher) {


        val viewModel =
            QuotesViewModel(
                repository = repository
            )


        advanceUntilIdle()


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

I think this is my most valuable test because it checks one of the main interactions in the Quote Collection feature. It first checks that the quote starts with 2 likes, then likes the quote and makes sure the count changes to 3. It also checks that the quote is added to likedQuoteIds and that repository.likeQuote(1) was called exactly once.

If this test passes  it  proves that the ViewModel correctly updates the liked state and like count for that situation  It does not prove that the entire Quote  Collection feature works perfectly  because it does not test every UI action, every API failure, or every possible user interaction.

Part 5 — Looking Forward
5a. What I would add next

If I kept working on Media Tracker, I would add a  recommendation feature. The app could recommend movies,  books, or other media based on what the user  has favorited or added to their library.

I would first check what recommendation data  the API provides. Then I would add the repository function  create the ViewModel state  and build the UI to display the recommendations.

5b. Advice to someone starting Android development

My biggest advice to someone starting Android development would be not to expect everything to make sense  immediately.  At the beginning of the class, I could sometimes get  code working without fully understanding why it worked.

Things like ViewModels, state  navigation API calls,  and Compose started making more sense once I used them together in the actual app. I would also tell them to read their error messages instead of randomly changing code.

If I  started ICS 342 again  I would spend more time understanding how the UI, ViewModel repository  and API  connect instead of only trying to get each assignment working.