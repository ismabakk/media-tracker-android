# Week 12 Reflection

**Name:** Ismail Bakkaoui
**Date:** August 6, 2026

---

## Commits This Week

**Link:** https://github.com/ismabakk/media-tracker-android/pull/11/changes/a0d94b3288dc0259df27616df0f88a2fc5c8ab7d

---

## Code Review

**Reviewed:** Issa Ismail Ali
**Link to my review:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/11#issuecomment-5211259388

### What I Looked At

I looked through Issa's Week 12 changes and focused mostly on the quotes feature and the testing he added for it. I looked at how the ViewModel handles liking and unliking a public quote and then looked at his `QuotesViewModelTest` to see how he tested those changes.

### What I Noticed

One thing that stood out to me was how his like and unlike test checks more than just whether the function runs. He checks that likedQuoteIds changes and that the quote's likeCount  goes from 0 to 1 when it is liked and then back to 0 when it is unliked.

He also uses coVerify to make sure repository likeQuote(1) and repository.unlikeQuote(1) were actually called and I thought this was good because the test is checking both sides of the feature and It checks that the state the user would see changes correctly but also that the ViewModel actually communicates with the repository.

### Comments I Left

I left a positive comment about his like and unlike test. I mentioned that I liked how  he tested both directions instead of only  testing the like action, I also pointed out that using `coVerify` makes the test stronger because it confirms the repository methods are actually being called instead of only checking the local UI state.

---

## One Thing I Understood More Deeply

One thing I understood more deeply this week was how optimistic UI updates work and why rollback is important when working with an API.

Before this week I thought the app should basically send a request to the server, wait for the response, and then change what the user sees. I understand now that you can change the UI first so the app feels immediate, then send the network request in the background.

This made more sense to me while working on my Library When I change a book from "Want To" to "In Progress" or remove it, the ViewModel can update `libraryItems` right away. That is why the change feels instant to the user.

The part I did not really think about before was what happens if the network request fails. If I remove the book from the screen and the server fails to remove it, then my UI and the server no longer match. The ViewModel has to keep the old item as a backup and put it back if the request fails. That helped me understand that optimistic updates are not only about making the app faster. You also need a way to recover when something goes wrong.

---

## One Thing I'm Still Confused About

One thing I am still confused about is testing coroutine and ViewModel behavior compared to just testing the app myself.

When I run the app in the emulator it makes sense to me because I can add a book change it to "In Progress" restart the app, and physically see whether it stayed there. Unit testing the same type of behavior is harder for me to understand.

I am starting to understand MockK and why we mock the repository to make a network request succeed or fail but things like `StandardTestDispatcher`, `runTest`, and `advanceUntilIdle()` are still confusing to me. I understand what I want the test to prove, but I still need more practice understanding exactly how the coroutine is being controlled during the test.

---

## Anything Else *(optional)*

This week was useful because I finally got to see several parts of the app working together instead of looking at each file separately. I was able to add a book to "Want To," see it in the Library, change it to "In Progress," restart the app and see that it stayed there, and remove it.

Seeing all of that work helped me understand better how the screen, ViewModel, repository, and API connect together. Earlier in the project I would look at those as separate pieces of code, but now I am starting to understand how data actually moves through all of them.