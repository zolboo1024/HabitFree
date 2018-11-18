# Habit-Free-Application-Entire-Source-Code
Here's the entire source code for Habit Free application.

This application is posted on Google Play Store at https://play.google.com/store/apps/details?id=zz.zolboo

For Users:
In today's media-driven world, it takes a lot of self-control to stay free of the distractions 
that your smartphone can prove to produce. That's why I created this application to help you break 
free of your smartphone habits. If you habitually scroll through Instagram or Facebook when you need 
to be productive like I do, this application is for you. Block any application installed on your 
device for up to 24 hours and track how much time you save by using this app. 
It's completely free of charge and there are absolutely no annoying ads.

Note: This won't be able to function as a system app so once you exit out of it, the digital detox stops working.

For Developers:
This app makes use of number of functions that doesn't exist in Android library.
Here are some things that you may find useful: 

"AllAppsList" class lets you make a list of every applications that are 
installed on your users' phone and display them in a ListView style.
Note: it may not contain "system" or "root" applications 

"MyTimePicker" class lets you create a TimePicker widget that 
for some reason is deprecated from Android API.

"OpenSettingsService" class lets your users grant Dangerous labeled permissions. It takes them
to the settings and pulls the users back once the permission is granted. 

"StartPlanService" and "Plan_UsageStats" classes let you track your users' usage
of their phones and reports it periodically. It works even in the background when 
the app is closed!

"SplashScreen" is an example of a typical splash screen.

Hope you find something useful here!
