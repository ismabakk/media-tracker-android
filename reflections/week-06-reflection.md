# Week 06 Reflection

**Name:** Ismail Bakkaoui
**Date:** June 25, 2026

---

## Commits This Week

**Link:** https://github.com/ismabakk/media-tracker-android/pull/6/changes/7eba70f3056cb2cc434985edbe4981d4a79a0471

---

## Code Review

**Reviewed:** Issa Ismail Ali
**Link to my review:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/6#issuecomment-4805634225

### What I Looked At

I looked at Issa's Week 06 pull request and the thing I looked at the most and focused on was the search feature. I looked through `SearchViewModel.kt`, `SearchScreen.kt`, and looked at how the fake search results and pagination were being implemented. I also looked at how the ViewModel was hadnling the  filtering and the loading more results while the Composable was displaying the search results.

### What I Noticed

One thing that I did notice was that Issa kept the search and pagination logic inside of the  `SearchViewModel` instead of having it in and moving it into the `SearchScreen.kt`. I liedk the way it was donebecause this is a good design since it keeps the Composable focused on displaying the UI while aat the same time the ViewModel manages the search state and business logic. This separation would also make it easier to replace the fake repository with the real API later on in the semester when we get to that point.

### Comments I Left

I commented that I liked keeping the search and pagination logic inside of the ViewModel because it makes the UI much more simpler and easier to maintain as well as update. I also threw a suggestion making sure the pagination function is not triggered more than once  while scrolling because I ran into that issue when I was debugging my own implementation. Overall, I thought his separation of responsibilities looked good.

---

## One Thing I Understood More Deeply

One thing I understood more deeply this week was is how pagination actually works inside the app. Last weekk and before this week I thought displaying a long list was just showing all of the data just one time but after implementing and using the fake search results I now understand that the ViewModel can control how many items are being displayed at any given time. We started by showing 20 results, then after scrolling it loaded 40, and then if you kept scrolling it finally showed all 60 as the user scrolled. This helped me understand that the UI is only showing the data the ViewModel decides to expose to us and that this same pattern will later work with the real API instead of the fake data we used today.

I also understood more about why `LazyColumn` is used sometimes instead of a regular `Column`. It's because the `LazyColumn` only creates the items that are needed while the user scrolls which is way better in my oinion because it makes it much more efficient when displaying large lists of search results.

---

## One Thing I'm Still Confused About

I still have some questions about how pagination will work once we stop using the fake repository because we haven't used a real one yet and when we switch to the real API it might be different but ZI think it might be the same. I understand that the server returns headers like `X-Next-Cursor` and `X-Has-More` but I'm not 100% completely sure how the values should be stored and passed back to the API when loading the next page. I understand the overall ideaof it all but I would like to see the complete flow from the API response to the ViewModel and back to the next request, I think it would be coo, to see once we get there.

---

## Anything Else *(optional)*

This week was one of the first times that several concepts from previous weeks came together in one feature like a house after it's finihsed being built. We used the repository pattern the ViewModel the Compose state, `LazyColumn`, and fake data together to build a working search screen that came out well. Seeing all of those pieces work together made the overall architecture of the app make a lot more sense to me.
