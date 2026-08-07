# Week 10 Reflection

**Name:** Ismail Bakkaoui
**Date:** July 23 2026

---

## Commits This Week

**Link:** https://github.com/ismabakk/media-tracker-android/pull/9/changes/06bbc08ee1ff6c39c363507626d7a94b3c99120e

---

## Code Review

**Reviewed:** Issa Ismail Ali
**Link to my review:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/9#issuecomment-5210160400

### What I Looked At

I looked through Issas week 10 changes mainly his LibraryViewModel and the MockK test he added for the rollback. I looked at how removing an item works with the optimistic update and what happens when the API request fails. I also looked at how his test checks the item before the request during the update and after the request fails.

### What I Noticed

I noticed that when an item is removed from the library it disappears from the screen right away instead of waiting for the API request to finish. If the request fails he keeps a backup of the item and adds it back to the library and also shows an error message. I thought this was a good way to do it  because the app feels faster but it can still fix the UI if something goes wrong with the API.

I also noticed his MockK test checks the whole process. It first makes sure the item is in the library then checks that it disappears right after removeItem is called. After the network request fails it checks that the item comes back and that the correct error message shows. It also checks that removeFromLibrary was only called one time.

### Comments I Left

I commented about how I liked that the library updates right away and how it goes  back if the request fails. I also mentioned how the MockK test did a good job testing the failure by checking that the item was there first then disappeared and came back after the network failed. I thought the API failure handling was done good and overall I didnt see any major issues with it.

---

## One Thing I Understood More Deeply

One thing I understood more this week was optimistic updates and how the ViewModel keeps the UI and API data together. Before I understood that the ViewModel sends requests and updates the UI but I didnt really understand why you would change the UI before knowing if the request actually worked.

Now I understand that when someone removes something from their library or changes the status you can update the StateFlow first so the change shows on the screen right away. Then the API request can happen without making the user sit there waiting. What made more sense to me this week was  the rollback part. You have to keep the original item or state because if the API request fails the screen needs to go back to what it was before. If you dont do that the app could show that something was removed even though the server still has it.

---

## One Thing I'm Still Confused About

Something im still confused about is how optimistic updates should work when a user makes multiple changes really fast before the first API request finishes. The rollback makes sense to me when there is only one request because you can save the old item and restore it if something fails.

What im not fully understanding yet is what happens if someone changes the status of an item and then changes it again before the first request finishes. If one of those requests fails im not sure how the ViewModel knows which state it should  go back to without undoing the newer change. I understand the normal rollback better now but I still want to understand how you handle it when multiple requests are happening at once.

---

## Anything Else

Looking at the unit  testing this week also helped me understand why its important to test when something fails and not just when everything works. Testing the rollback showed me that theres multiple states that can happen from one  action and they should all work correctly.