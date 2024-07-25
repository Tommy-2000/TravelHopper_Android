# TravelHopper - Travel Android App for Holiday Planning and Navigation - Mobile Application Development - September/December 2022

This Android application was developed as a Travel application for people to manage their trips, add personal favourites to a list, track their location through Google Maps and take photos and videos directly through the app. This was developed with a combination of Room data persistence, Google Maps API implementation, Google One-Tape Sign-in UI with Firebase Authentication, quick navigation using Fragments and integrated Camera functionality within an MVVM (Model, View, ViewModel) architecture.


## App Architecture
The overall app consists of two activities with one called the MainActivity that acts as a container for Fragments to be loaded once the user navigates the app and switches between each of the four Fragments:

* My Home - Represented as MyHomeFragment
* My Trips - Represented as MyTripsFragment
* My Gallery - Represented as MyGalleryFragment
* My Map - Represented as MyMapFragment

The inherent design and structure of the application follows a Fragment-based navigation structure that relies on the MainActivity loading and unloading fragment through the FragmentManager. Using Fragments in this manner is less resource-intensive and respects the Android lifecycle when starting, resuming, pausing and destroying Fragments and Activities safely and where its absolutely necessary.


## Navigation

Navigation was implemented through recycling multiple fragments within an Activity called MainActivity, while the camera functionality was implemented into a separate Activity; inheriting key lifecycle methods that work with the CameraX library. Navigating between each fragment is done via a bottom navigation bar that calls the FragmentManager to start transactions for each fragment and then hide the previous Fragments within a Fragment stack. Accessing the builtin Camera is done by redirecting the user to a separate Activity once clicking on the camera icon on the side of the navigation bar.


### ParentFragmentManager Code

```java
// Set up Navigation between the four different Fragments and the BottomNavigationView
        NavigationBarView bottomNavBar = activityMainBinding.bottomNavBar;
        bottomNavBar.setOnItemSelectedListener(parentFragmentNavigation);
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myHomeFragment, "My Home").hide(myHomeFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myTripsFragment, "My Trips").hide(myTripsFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myGalleryFragment, "My Gallery").hide(myGalleryFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myMapFragment, "My Map").hide(myMapFragment).commit();

```

### Bottom Navigation Bar
![Bottom Navigation Bar](bottom-navigation-bar.png)

### onItemSelectedListener Code

```java
private NavigationBarView.OnItemSelectedListener parentFragmentNavigation = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem parentFragmentItem) {
            if (parentFragmentItem.getItemId() == R.id.myHomeFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myHomeFragment).commit();
                activeFragment = myHomeFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myTripsFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myTripsFragment).commit();
                activeFragment = myTripsFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myGalleryFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myGalleryFragment).commit();
                activeFragment = myGalleryFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myMapFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myMapFragment).commit();
                activeFragment = myMapFragment;
                return true;
            }
            return false;
        }
    };
```





## Camera
Camera functionality was built into the app itself through the CameraX library and is bound to the Activity's own lifecycle. This gives the app greater control over when to destroy and resume the CameraService based on the onDestroy and onResume lifecycle methods. MediaStore was also used to store the photo or video to External Storage on the user's device. **NOTE: This was chosen due the storage privacy changes introduced in Android 11 and thus stricter permissions MUST be respected.**

### ContentValues and MediaStore Code

```java
// Check if the imageCapture object is not pointing to null
        if (imageCapture != null) {

            // Create the file name string for the photo file config object - photoContentValues
            String photoFileName = "TravelHopper_Photo-" + new SimpleDateFormat("M", Locale.ENGLISH).format(System.currentTimeMillis()) + ".jpg";
            ContentValues photoContentValues = new ContentValues();
            // Use MediaStore to set the photo file for the content resolver to handle
            photoContentValues.put(MediaStore.Images.Media.TITLE, photoFileName);
            photoContentValues.put(MediaStore.Images.Media.DISPLAY_NAME, photoFileName);
            photoContentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            photoContentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                photoContentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "TravelHopper");
            }

            // Taking the photoContentValues object created, build the output options for the file
            ImageCapture.OutputFileOptions photoOutputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            photoContentValues).build();
```

![Camera Activity](camera-activity-view.png)

MediaStore is a more convenient and privacy-first API for accessing media on the EXTERNAL_STORAGE on the user's device through registering the metadata of the photo or video that will be taken by the user. The metadata was registered through the ContentValues constructor and then passed as an argument to the OutputFileOptions.Builder.


## Google Maps SDK

The Google Maps SDK was implemented to the MyMapFragment as a quick and convenient way for the user to find their current location without having to leave the app. This was done by initialising a child fragment called SupportMapFragment that acts as a view within the parent Fragment (MyMapFragment). This child Fragment retrieves the maps asynchronously with options such as a compass, scrolling, zooming and rotating being enabled within the onCreateView lifecycle method.

![My Map Fragment](google-maps-child-fragment.png)

### SupportMapFragment and GoogleMapOptions Code

```java

// Initialise the GoogleMapOptions object
        GoogleMapOptions gMapOptions = new GoogleMapOptions();

        // Initialise the GoogleMapsFragment as a child fragment on top of the current (parent) fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("googleMapsFragment");

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(googleMap -> {
                // Initialise the Google Maps SDK
                gMapSDK = googleMap;

                // Initialise the marker position in Google Maps
                LatLng kyoto = new LatLng(35.00116, 135.7681);
                gMapSDK.addMarker(new MarkerOptions().position(kyoto).title("Marker in Kyoto, Japan"));
                gMapSDK.moveCamera(CameraUpdateFactory.newLatLng(kyoto));

                gMapSDK.setOnMyLocationClickListener(currentLocation -> Snackbar.make(rootFragmentView, "Current location: \n" + currentLocation, Snackbar.LENGTH_SHORT).show());

                // Initialise the current location button in Google Maps
                gMapSDK.setOnMyLocationButtonClickListener(() -> {
                    Snackbar.make(rootFragmentView, "My Location button is clicked", Snackbar.LENGTH_SHORT).show();
                    return false;
                });

                // Set up options for Google Maps
                gMapOptions.compassEnabled(true);
                gMapOptions.scrollGesturesEnabled(true);
                gMapOptions.rotateGesturesEnabled(true);
                gMapOptions.zoomGesturesEnabled(true);
                gMapOptions.scrollGesturesEnabledDuringRotateOrZoom(true);
                gMapOptions.ambientEnabled(true);


            });
        }

```

## Google One-Tap Sign-in

To ensure that users have a unique experience throughout the application, different trips are recommended to them through a personalised list of trips that are shown through the My Home page. This list is curated through Firebase and deployed to the app from a Firebase Storage bucket. Depending on the user, different trips may be shown to the user once they are signed into the app using their Google Account. As a part of the onboarding experience, the Google One-Tap Sign-in client has been implemented and loaded once the MainActivity has been created. This login client would automatically find the Google Accounts registered with the device and presents them to the user through this UI.


![alt text](google-one-tap-signin.png)


