Teleparty Android Developer Challenge Submission

This repository contains the completed solutions for the Teleparty Android Developer Challenge, which includes two separate tasks:
	•	Task 1: A DRM-protected video player using ExoPlayer with resolution selection support.
	•	Task 2: A metadata fetcher using a non-public streaming API (reverse-engineered from MX Player).

⸻

Task 1: DRM Video Player with Resolution Selector

Description

An Android application that plays a DRM-protected video using ExoPlayer and allows the user to:
	•	Scrub, pause, and resume video playback.
	•	Manually select from available video resolutions (from the MPD manifest).
	•	Experience a clean, interactive player UI.

Key Features
	•	ExoPlayer implementation with Widevine DRM support.
	•	Custom resolution selector UI with bottom sheet.
	•	Custom playback controls, including integration of a quality (HD) button.
	•	Proper ExoPlayer lifecycle management.

DRM Video
	•	Manifest URL: https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd
	•	License Server: https://cwip-shaka-proxy.appspot.com/no_auth

Technologies Used
	•	Java
	•	ExoPlayer (Media3)
	•	BottomSheetDialog
	•	RecyclerView for resolution list

Demonstration Video

Task 1 - Video Player Demo:- 

⸻

Task 2: Metadata Fetcher Using Non-Public API

Description

An Android application that fetches and displays video metadata by reverse-engineering a non-public API from MX Player.

Features
	•	Accepts a video ID or pre-defined movie title (dropdown).
	•	Fetches metadata including:
	•	Title
	•	Description
	•	Release Date
	•	Duration
	•	Genres
	•	Languages
	•	Tags
	•	Cast
	•	Director(s)
	•	Clean and structured UI using AutoCompleteTextView, HttpURLConnection, and JSON parsing.

Sample Video IDs
	•	Jab We Met: acb8b71b41fa86ace0e3e10d75e78e22
	•	Housefull 2: f11cec31eff8ae5a0984017b3c252e02
	•	Yamla Pagla Deewana: a3b53d8994a29b8cfae0e061427be565
	•	The Monkey King 2: 500f8c5e98bf9d06e109d8b880a76342
	•	Anaconda 3: 6f913706db4658ceb8443d9ef805f529

API Endpoint Used

https://api.mxplayer.in/v1/web/detail/video?type=movie&id={VIDEO_ID}

Technologies Used
	•	Java
	•	AutoCompleteTextView
	•	HttpURLConnection
	•	org.json for JSON parsing

Demonstration Video

Task 2 - Metadata Fetcher Demo:-

⸻

Notes
	•	Both apps are written in Java.
	•	No public APIs were used for Task 2 (fully reverse-engineered endpoint).
	•	Code is cleanly structured and UI elements are implemented with user experience in mind.
	•	The apps are lightweight and optimized for demonstration purposes.

Author

Lalit Kumar Meena
