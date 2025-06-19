package com.example.telepartyassignment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.source.MediaLoadData;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

@UnstableApi
public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private DefaultTrackSelector trackSelector;
    private TextView tvCurrentResolution;

    // URLs for streaming and DRM license
    private static final String manifestUrl = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd";
    private static final String licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth";

    private boolean userPaused = false;
    private int currentSelectedResolutionIndex = 0; // 0 = Auto (adaptive)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        tvCurrentResolution = findViewById(R.id.tv_current_resolution);

        initialisePlayer();

        // Set default resolution label
        tvCurrentResolution.setText("Current: Auto (Adaptive Quality)");

        View qualityButton = playerView.findViewById(R.id.exo_quality);
        qualityButton.setOnClickListener(v -> {
            List<Format> formats = getAvailableVideoFormats();
            if (formats != null && !formats.isEmpty()) {
                showResolutionBottomSheet(formats);
            }
        });
    }

    private void initialisePlayer() {
        trackSelector = new DefaultTrackSelector(this);
        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();

        playerView.setPlayer(player);

        // Prepare media item with DRM config
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(manifestUrl)
                .setMimeType(MimeTypes.APPLICATION_MPD)
                .setDrmConfiguration(
                        new MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                                .setLicenseUri(licenseUrl)
                                .setMultiSession(true)
                                .build())
                .build();

        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        // Basic player state listener
        player.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        Log.d("PlayerState", "READY - Playback started");
                        player.setPlayWhenReady(true);
                        playerView.showController();

                        // Log available video formats
                        List<Format> formats = getAvailableVideoFormats();
                        for (Format format : formats) {
                            Log.d("AvailableResolution", format.width + "x" + format.height + " - " + format.bitrate + "bps");
                        }
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d("PlayerState", "BUFFERING...");
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d("PlayerState", "ENDED");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("PlayerState", "IDLE");
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Save if user paused manually
                userPaused = !isPlaying && player.getPlaybackState() == ExoPlayer.STATE_READY;
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("PlayerError", "Error: " + error.getMessage());
            }
        });

        // Log format when changed
        player.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onDownstreamFormatChanged(EventTime eventTime, MediaLoadData mediaLoadData) {
                if (mediaLoadData.trackType == C.TRACK_TYPE_VIDEO && mediaLoadData.trackFormat != null) {
                    Format format = mediaLoadData.trackFormat;
                    Log.d("VideoQuality", "Resolution: " + format.width + "x" + format.height + ", Bitrate: " + format.bitrate + "bps");
                }
            }
        });
    }

    // Collect all supported video formats
    private List<Format> getAvailableVideoFormats() {
        List<Format> videoFormats = new ArrayList<>();

        if (player != null && player.getCurrentTracks() != null) {
            Tracks tracks = player.getCurrentTracks();

            for (Tracks.Group group : tracks.getGroups()) {
                if (group.getType() == C.TRACK_TYPE_VIDEO) {
                    for (int i = 0; i < group.length; i++) {
                        if (group.isTrackSupported(i)) {
                            videoFormats.add(group.getTrackFormat(i));
                        }
                    }
                }
            }
        }

        return videoFormats;
    }

    // Show bottom sheet with resolution options
    private void showResolutionBottomSheet(List<Format> videoFormats) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_resolution, null);
        dialog.setContentView(view);

        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.resolution_list);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        // Prepare resolution labels (e.g., 1080p, 720p, etc.)
        List<String> resolutions = new ArrayList<>();
        resolutions.add("Auto (Adaptive Quality)");
        for (Format format : videoFormats) {
            if (format.height != Format.NO_VALUE) {
                resolutions.add(format.height + "p");
            } else {
                resolutions.add("Unknown");
            }
        }

        // Set adapter and handle selection
        ResolutionAdapter adapter = new ResolutionAdapter(resolutions, currentSelectedResolutionIndex, position -> {
            currentSelectedResolutionIndex = position;
            tvCurrentResolution.setText("Current: " + resolutions.get(position));

            if (position == 0) {
                // Auto quality - remove manual override
                trackSelector.setParameters(
                        trackSelector.buildUponParameters()
                                .clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                                .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, false)
                                .build()
                );
            } else {
                // Apply manual override for selected format
                Format selectedFormat = videoFormats.get(position - 1);
                Tracks tracks = player.getCurrentTracks();

                for (Tracks.Group group : tracks.getGroups()) {
                    if (group.getType() == C.TRACK_TYPE_VIDEO) {
                        for (int i = 0; i < group.length; i++) {
                            Format format = group.getTrackFormat(i);
                            if (format.width == selectedFormat.width && format.height == selectedFormat.height) {
                                TrackGroup trackGroup = group.getMediaTrackGroup();
                                TrackSelectionOverride override = new TrackSelectionOverride(trackGroup, i);
                                trackSelector.setParameters(
                                        trackSelector.buildUponParameters()
                                                .clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                                                .addOverride(override)
                                                .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, false)
                                                .build()
                                );
                                break;
                            }
                        }
                    }
                }
            }

            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    // Pause playback when app goes to background
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            userPaused = !player.isPlaying();
            player.setPlayWhenReady(false);
        }
    }

    // Resume playback if user didn't manually pause
    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            initialisePlayer();
        } else {
            playerView.setPlayer(null);
            playerView.setPlayer(player);
        }

        if (!userPaused) {
            player.setPlayWhenReady(true);
        }
    }

    // Release resources when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}