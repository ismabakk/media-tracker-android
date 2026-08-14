# Week 07 Reflection

**Name:** Ismail Bakkaoui
**Date:** July 2, 2026

---

## Commits This Week

**Link:**
https://github.com/ismabakk/media-tracker-android/pull/7/changes/7114dd50ce0836f8f701dfc423e4920ecb65ccfc

---

## Code Review

**Reviewed:** Issa Ismail Ali

**Link to my review:**
https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/7#issuecomment-4871978395

### What I Looked At

I reviewed Issa's Media Detail screen implementation and focused on the way that he structured the screen into smaller composables. I also loked at how the ViewModel loaded the selected media and how the Media Detail screen displayed information from the repository.

### What I Noticed

I liked how on his his screen was broken into reusable composables such as `ReviewCard`, `StatBox`, and `SectionTitle` instead of just putting everything into a big single large composable. I also noticed that some values like the review count and runtime/pages are still hardcoded  which is fine for this week's assignment but will eventually need to come from the API.

### Comments I Left

I commented about the reusable composable structure because it makes the screen easier to read and maintain. I also suggested that the hardcoded values could be replaced with API data which would help in the future once the Media Detail endpoint is connected.

---

## One Thing I Understood More Deeply

This week I understood Android Navigation much better. Before this assignment I knew how to navigate between screens, but I didn't fully understand how route parameters worked. While implementing the Media Detail screen, I realized that the navigation route, the NavGraph arguments, and the composable all have to agree on the same parameter. If one of those doesn't match, the app either crashes or the screen receives the wrong data. Once I updated the route and passed the media ID correctly, clicking a book from the Search screen opened the correct Media Detail screen instead of crashing or showing placeholder data. That helped me understand how data flows through Compose Navigation.

---

## One Thing I'm Still Confused About

I'm still a little confused about what is the best way to organize the larger Compose screens. During class I saw a couple different approaches using helper composables, extension functions, and ViewModels. I kinda understand how each one works individually but I'm still learning when it is better to split code into separate composables versus when to keep it together in one screen file.

---

## Anything Else *(optional)*

During class I compared my implementation with other students implementation approaches to better understand the different ways of building the Media Detail screen. I saw how other students organized their composables and navigation helped me better understand the assignment without copying their implementation. I also sorta learned how small mistakes in navigation routes can cause crashes even when the UI code itself is correct.

---