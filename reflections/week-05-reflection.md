# Week 05 Reflection

**Name:** Ismail Bakkaoui
**Date:** June 18 2026

---

## Commits This Week

**Link:** https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/5#issuecomment-4748283217

---

## Code Review

**Reviewed:** Issa Ismail Ali
**Link to my review:** PASTE YOUR GITHUB REVIEW LINK HERE

### What I Looked At

I reviewed Issa's Week 05 pull request for the registration and login API integration. I mainly tried to focused on the authentication related files including `DefaultUserRepository.kt`, `LoginRequest.kt`, `AuthResponse.kt`, `UserApiService.kt`, `RetrofitInstance.kt`, `AuthViewModel.kt`, and `RegisterViewModel.kt`. I also looked at how request models connect to the repository layers and how the UI state was being updated after register or login responses.

### What I Noticed

One thing I noticed was that `LoginRequest` it uses a default value for `grantType`. This kinda matters because the API expects `grantType` to be sent as `"password"` when user is logging in. If the JSON serializer ddoesn't not include default values, the server may reject the request even if the email, password, clientId, and the clientSecret are correct. This helped me understand really why Retrofit serialization settings are important and why a request model can look correct in Kotlin but can still produce the wrong JSON request body.

I also noticed that the repository separates API response codes into many different result types for example like success, invalid credentials, network error, and unknown error. This is veruy I believe useful because the ViewModel does not need to know the exact HTTP codes. It can just react to the result and show the correct message to the user.

### Comments I Left

I left a comment about checking that the login request sends the `grantType` field in the request body. I spoke about that this is important because the backend requires either `"password"` or `"refreshToken"` and the login call can fail if the serializer skips the default value. I also put a note that separating the API result into sealed result types makes the ViewModel making it easier to read and keeps network logic out of the UI layer.

---

## One Thing I Understood More Deeply

One thing I understood more deeply this week I feel like is how data moves through the app during authentication. Before this week I kinda understood that the button on the screen should trigger login or registration, but I did not fully grasp the idea of and understand all the layers involved. Now I can clearly see it better that the Composable collects state from the ViewModel, the ViewModel calls the repository, the repository calls the Retrofit service, and Retrofit sends the request to the API.

The biggest thing that I noticed iI understood more was was the difference between the Kotlin object and the actual JSON sent to the server. My `LoginRequest` had `grantType = "password"`, so I thought the request was correct. I saw that the server was saying the `grantType` was missing or invalid and that helped me understand that default values are not always included in serialized JSON unless the serializer is configured to include them. Adding `encodeDefaults = true` in the Retrofit JSON configuration fixed the issue and made the login API work.

---

## One Thing I'm Still Confused About

I am still a little confused about the best way to manage sessions after login. I u8nderstood that the API returns an access token and refresh token, but I am not too fully confident yet about where those should really be puit and how the app should use them for future authenticated requests. I also want to better understand when the app needs tp use the refresh token and how to handle the case where the access token expires.

---

## Anything Else *(optional)*

This week took a lot of debugging because registration worked before login di and the most helpful part was reading the actual API error message in Logcat instead of only looking at the error message on the screen and aslo eeing the server response made the problem much clearer and helped me fix the Retrofit serialization issue.
