package com.example.mxmetadatafetcher;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_API_URL = "https://api.mxplayer.in/v1/web/detail/video?type=movie&id=";
    private static final String DEFAULT_MESSAGE = "Metadata will appear here...";
    private static final String FETCHING_MESSAGE = "Fetching metadata...";

    private AutoCompleteTextView videoIdInput;
    private TextView outputTextView;
    private Button fetchButton;

    private final Map<String, String> videoTitleToIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupVideoMap();
        setupAutoComplete();
        setupFetchButton();
    }

    private void initializeViews() {
        videoIdInput = findViewById(R.id.videoIdInput);
        outputTextView = findViewById(R.id.outputTextView);
        fetchButton = findViewById(R.id.fetchButton);
    }

    private void setupVideoMap() {
        videoTitleToIdMap.put("Jab We Met", "acb8b71b41fa86ace0e3e10d75e78e22");
        videoTitleToIdMap.put("Housefull 2", "f11cec31eff8ae5a0984017b3c252e02");
        videoTitleToIdMap.put("Yamla Pagla Deewana", "a3b53d8994a29b8cfae0e061427be565");
        videoTitleToIdMap.put("The Monkey King 2", "500f8c5e98bf9d06e109d8b880a76342");
        videoTitleToIdMap.put("Anaconda 3: Offspring", "6f913706db4658ceb8443d9ef805f529");
    }

    private void setupAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                videoTitleToIdMap.keySet().toArray(new String[0])
        );
        videoIdInput.setAdapter(adapter);

        videoIdInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) videoIdInput.showDropDown();
        });

        videoIdInput.setOnClickListener(v -> videoIdInput.showDropDown());

        videoIdInput.setOnItemClickListener((parent, view, position, id) -> {
            clearFocusAndKeyboard();
            outputTextView.setText(DEFAULT_MESSAGE);
        });
    }

    private void setupFetchButton() {
        fetchButton.setOnClickListener(v -> {
            String input = videoIdInput.getText().toString().trim();

            if (input.isEmpty()) {
                outputTextView.setText("Please enter or select a movie title / ID.");
                return;
            }

            clearFocusAndKeyboard();

            String videoId = videoTitleToIdMap.getOrDefault(input, input);
            outputTextView.setText(FETCHING_MESSAGE);
            fetchMetadata(videoId);
        });
    }

    private void clearFocusAndKeyboard() {
        videoIdInput.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(videoIdInput.getWindowToken(), 0);
        }
    }

    private void fetchMetadata(String videoId) {
        new Thread(() -> {
            try {
                String jsonResponse = fetchJson(videoId);
                String parsedHtml = parseMetadata(jsonResponse);
                runOnUiThread(() -> outputTextView.setText(Html.fromHtml(parsedHtml)));
            } catch (Exception e) {
                runOnUiThread(() -> outputTextView.setText("Error fetching metadata:\n" + e.getMessage()));
                Log.e(TAG, "Error fetching metadata", e);
            }
        }).start();
    }

    private String fetchJson(String videoId) throws Exception {
        URL url = new URL(BASE_API_URL + videoId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    private String parseMetadata(String jsonResponse) throws Exception {
        JSONObject json = new JSONObject(jsonResponse);
        String title = json.optString("title");
        String description = json.optString("description");
        String releaseDate = json.optString("releaseDate").split("T")[0];
        int duration = json.optInt("duration");

        String genres = joinArray(json.optJSONArray("genres"));
        String languages = joinArray(json.optJSONArray("languages"));
        String tags = joinTags(json.optJSONArray("tags"));

        JSONArray contributors = json.optJSONArray("contributors");
        StringBuilder actors = new StringBuilder();
        StringBuilder directors = new StringBuilder();
        if (contributors != null) {
            for (int i = 0; i < contributors.length(); i++) {
                JSONObject person = contributors.getJSONObject(i);
                String name = person.optString("name");
                String type = person.optString("type");
                if ("actor".equalsIgnoreCase(type)) {
                    actors.append(name).append(", ");
                } else if ("director".equalsIgnoreCase(type)) {
                    directors.append(name).append(", ");
                }
            }
        }
        if (actors.length() > 2) actors.setLength(actors.length() - 2);
        if (directors.length() > 2) directors.setLength(directors.length() - 2);

        StringBuilder output = new StringBuilder();
        output.append("<b>Title:</b> ").append(title).append("<br>");
        output.append("<b>Description:</b> ").append(description).append("<br>");
        output.append("<b>Release Date:</b> ").append(releaseDate).append("<br>");
        output.append("<b>Duration:</b> ").append(formatDuration(duration)).append("<br>");
        output.append("<b>Genres:</b> ").append(genres).append("<br>");
        output.append("<b>Languages:</b> ").append(languages).append("<br>");
        output.append("<b>Tags:</b> ").append(tags).append("<br>");
        output.append("<b>Cast:</b> ").append(actors).append("<br>");
        output.append("<b>Director(s):</b> ").append(directors).append("<br>");
        return output.toString();
    }

    private String joinArray(JSONArray array) throws Exception {
        if (array == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            result.append(array.getString(i));
            if (i != array.length() - 1) result.append(", ");
        }
        return result.toString();
    }

    private String joinTags(JSONArray tagsArray) throws Exception {
        if (tagsArray == null) return "";
        StringBuilder tags = new StringBuilder();
        for (int i = 0; i < tagsArray.length(); i++) {
            JSONObject tagObj = tagsArray.getJSONObject(i);
            tags.append(tagObj.optString("name"));
            if (i != tagsArray.length() - 1) tags.append(", ");
        }
        return tags.toString();
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}