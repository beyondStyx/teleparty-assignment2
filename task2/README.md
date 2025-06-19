# **MX Player Metadata Fetcher**

This Android application demonstrates how to reverse-engineer a non-public API from MX Player to fetch and display video metadata such as title, description, release date, genres, tags, cast, and more.

## **Features**

* Built using Java in Android Studio.  
* Uses a non-public MX Player API (no API key required).  
* Accepts both movie titles (via dropdown) and manual video IDs.  
* Fetches and displays:  
  * Title  
  * Description  
  * Release Date  
  * Duration  
  * Genres  
  * Languages  
  * Tags  
  * Cast  
  * Director(s)

## **Sample Video IDs**

Here are some sample movie names and their corresponding video IDs:

* **Jab We Met**: acb8b71b41fa86ace0e3e10d75e78e22  
* **Housefull 2**: f11cec31eff8ae5a0984017b3c252e02  
* **Yamla Pagla Deewana**: a3b53d8994a29b8cfae0e061427be565  
* **The Monkey King 2**: 500f8c5e98bf9d06e109d8b880a76342  
* **Anaconda 3: Offspring**: 6f913706db4658ceb8443d9ef805f529

## **API Used**

https://api.mxplayer.in/v1/web/detail/video?type=movie\&id={VIDEO\_ID}

This is a non-public API used by the MX Player website. It was reverse-engineered and does not require an API key.

## **How It Works**

1. User selects or enters a video ID.  
2. The app sends a GET request to the MX Player API.  
3. Metadata is parsed from the JSON response.  
4. The output is displayed in a clear format within the UI.

## **Technologies Used**

* Java  
* Android Studio  
* AutoCompleteTextView  
* HttpURLConnection  
* org.json (for JSON parsing)

## **Project Structure**

MainActivity.java      // Contains all core logic and UI handling  
activity\_main.xml      // Defines the layout for the main screen

## **Demonstration**

[Task 2 \- Metadata Fetcher Demo](https://drive.google.com/file/d/1x58y8ycGnpkXz9g0A5w3cV9i49XpG3_H/view?usp=sharing)

A short video is included with the submission to demonstrate:

* Selecting a title or entering a video ID  
* Successfully fetching metadata  
* Proper display of metadata in the UI

## **Notes**

* This app is for demonstration and educational purposes only.  
* No public API or API key is used.  
* Hardcoded video IDs are permitted for this assignment as per the requirements.

## **Author**

Lalit Kumar Meena

