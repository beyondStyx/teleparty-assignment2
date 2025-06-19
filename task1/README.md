# **DRM Video Player with Resolution Selector**

This Android application demonstrates playback of DRM-protected video content using ExoPlayer and allows the user to select a preferred resolution from available tracks listed in the MPD.

## **Features**

* Built using Java in Android Studio.  
* Uses ExoPlayer for video playback.  
* DRM playback using Widevine license.  
* Customized ExoPlayer UI with an embedded resolution selector icon.  
* Supports scrubbing, pause/play, and adaptive streaming.  
* Available video resolutions are dynamically listed in a bottom sheet.

## **Manifest and License URLs**

* **Manifest URL**: https://bitmovin-a.akamaihd.net/content/art-of-motion\_drm/mpds/11331.mpd  
* **License URL**: https://cwip-shaka-proxy.appspot.com/no\_auth

## **How It Works**

1. The app initializes ExoPlayer with a Widevine DRM configuration.  
2. The manifest is parsed to determine all available video formats.  
3. A resolution selector button (custom HD icon) is embedded in the control bar of ExoPlayer.  
4. On click, a bottom sheet appears with resolution options (Auto, 1080p, 720p, etc.).  
5. Upon selection, the video resolution is changed via track selection override.

## **Customizations**

* **ExoPlayer UI Control Customization**:  
  * The exo\_playback\_control\_view.xml file was copied and modified to add a custom resolution button inside the control bar.  
  * This allows seamless integration of resolution change within the player controls without extra UI elements.

## **Technologies Used**

* Java  
* Android Studio  
* ExoPlayer (Media3)  
* BottomSheetDialog  
* RecyclerView  
* ConstraintLayout

## **Project Structure**

MainActivity.java                    // Core logic and DRM playback  
ResolutionAdapter.java              // RecyclerView adapter for resolution list  
activity\_main.xml                   // Main activity layout  
custom\_exo\_playback\_control\_view.xml // Customized ExoPlayer control view  
bottom\_sheet\_resolution.xml         // Bottom sheet layout for resolution picker  
item\_resolution.xml                 // RecyclerView item layout for each resolution

## **Demonstration**

A short demo video is included showing:

* ExoPlayer playback with Widevine-protected content.  
* Scrubbing, pausing, and resuming the video.  
* Opening the resolution selector and changing quality on the fly.

## **Notes**

* This project is developed for demonstration purposes as part of the Teleparty Android Developer Challenge.  
* All DRM and streaming URLs used are publicly accessible test URLs.

## **Author**

Lalit Kumar Meena

