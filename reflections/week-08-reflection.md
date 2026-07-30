# Week 08 Reflection

**Name:** Ismail Bakkaoui
**Date:** July 10, 2026

---

## Commits This Week

**Link:** *(Paste the GitHub commits link for your week-08 branch here.)*

---

## Code Review

**Reviewed:** Issa
**Link to my review:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/8#issuecomment-5137165814

### What I Looked At

I reviewed the implementation of the Media Detail screen and I focused on how the ViewModel managed the different UI states Loading Success and Error and how the repository handled API responses, and how the screen responded when a request succeeded or failed. I also looked at the exception handling to make sure API failures would update the UI instead of causing the application to crash.

### What I Noticed

One thing I noticed was that the ViewModel separates the UI into distinct states instead of having the Composable decide what to display on its own. This makes the code easier to read and maintain because the ViewModel is responsible for deciding what state the UI should be in, I  also noticed that the repository checks HTTP response codes before returning data and this helps ensure that errors such as 404 responses or other unsuccessful requests are handled consistently instead of returning invalid data. This separation of responsibilities makes the application easier to debug and extend.

### Comments I Left

I commented that I liked how the implementation separated the Loading, Success, and Error states because it keeps the UI logic organized and improves readability. I also mentioned that the repository's response handling was well structured since it checks for unsuccessful API responses before returning data, allowing the ViewModel to present an appropriate error screen instead of letting the application fail unexpectedly.

---

## One Thing I Understood More Deeply

This week helped me better understand how exceptions travel through different layers of an Android application. Before this assignment, I mostly thought of an exception as something that simply caused the app to crash. While debugging this week, I realized that the Repository is responsible for detecting API failures and throwing an exception, while the ViewModel is responsible for catching that exception and converting it into an Error UI state. The Composable doesn't need to know anything about the network request itself—it only reacts to the current UI state. Understanding this flow made the architecture make much more sense because each layer has a specific responsibility instead of everything being handled in one place.

---

## One Thing I'm Still Confused About

I still want to better understand how authenticated Retrofit services are designed. During this assignment I learned that requests can automatically include an Authorization header, but I would like to understand exactly how OkHttp interceptors work behind the scenes and how they inject authentication tokens into every request. I also want to learn when it is better to create separate Retrofit service instances versus reusing a single authenticated client throughout an application.

---

## Anything Else *(optional)*

While working onthis assignment, I spent a significant amount of time debugging why the Media Detail screen was force closing. Using Logcat helped me identify that the API was returning a 401 Unauthorized response instead of a successful result. After tracing the request through the Repository and ViewModel, I was able to update the error handling so that the application displayed the Error screen instead of crashing. Although it took time to debug, it gave me much more confidence using Logcat to track down problems in Android applications.