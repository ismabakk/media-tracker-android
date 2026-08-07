# Week 11 Reflection

**Name:** Ismail Bakkaoui
**Date:** July 30th, 2026

---

## Commits This Week

**Link:** https://github.com/ismabakk/media-tracker-android/pull/10/changes/514a63b46bf803d421a214a211e66f8c5d433e83

---

## Code Review

**Reviewed:** Issa Ismail Ali  
**Link to my review:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/10#issuecomment-5210655393

### What I Looked At

I looked through Issas Week 11 quote feature and focused mostly on how he connected the quote API to the app. I  looked at his Quote model and request model and how he used GET and POST for quotes. I also looked at how the Media Detail screen lets the user enter a quote page number and choose if the quote should be public or private. I spent some time looking at his QuotesViewModel too because he added the logic for loading the quotes and pagination.

### What I Noticed

One thing 1 I noticed was that he did more than just make the quote screen  display data. He added validation before creating a quote like checking if the quote is empty making sure it doesnt go over 500 characters and checking the page number before sending it. I also noticed how he handled loading empty and error states for the quotes list. Another thing that stood out to me was the pagination. He gets the next cursor and has more values from the response headers and then the ViewModel uses that information to know when there are more quotes to load. I thought that was a good way to do it because the app doesnt have to load every quote at the same time.

### Comments I Left

I left a comment about how I liked the way he separated the quote feature between the API repository ViewModel and UI instead of putting everything into the screen. I also mentioned the validation because it prevents bad quote information from being sent to the API. I pointed out the pagination because looking through how he used the cursor helped me understand why the API sends that information in the headers and how the app can use it to load more data.

---

## One Thing I Understood More Deeply

One thing I understood more deeply this week was how adding a feature to an app goes through multiple layers instead of the screen directly talking to the API. Before this I understood what a ViewModel and repository did separately but the quote feature made it more clear how they all connect together.

For example when  I save a quote the screen collects the quote text page number and whether its public. The ViewModel handles that action and the repository is what actually  calls POST /quotes through the API service. Then when I go to My Quotes the app uses GET /quotes and the returned data updates the state that the screen displays. Seeing my quote actually show up in My Quotes after saving it helped this make more sense to me because I could see the full process working from the UI to the API and then back to the UI.

I also understand better why the Quote data model and CreateQuoteRequest are  separate. The request only needs the information we send when creating the quote while the Quote returned by the server has more information like the id userId likeCount createdAt and media. Before I probably would have tried using one class for everything  but now I understand why separating the request from the response makes the code easier to work with.

---

## One Thing I'm Still Confused About

One thing I'm still a little confused about is pagination with the cursor headers. I understand the basic idea that X-Next-Cursor tells the app where the next set of results starts and X-Has-More tells us if there are more results. What I'm still confused about is how I would handle it when the data changes while somebody is going through the pages.  For example if a new quote gets added or one gets deleted while the user already loaded part of the list I dont fully understand how the cursor keeps the results from becoming duplicated or skipping something.

I can follow the code for getting the cursor from the response and sending it with the next request but I still want to understand more about what the server is doing with that  cursor and why cursor pagination works better for this than just using page numbers.

---

## Anything Else *(optional)*

I tested the quote feature after getting everything connected. I was able to save both a private and public quote and I tested adding a page number too. After saving them they both  showed up under My Quotes with the media title and the correct public or private status. This helped me know that the API call and the UI state were actually working together and not  just compiling.