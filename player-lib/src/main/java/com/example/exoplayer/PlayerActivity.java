/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;
  private PlaybackStateListener playbackStateListener;
  private static final String TAG = PlayerActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    playbackStateListener = new PlaybackStateListener();
    playerView = findViewById(R.id.video_view);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (Util.SDK_INT >= 24) {
      initializePlayer();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (Util.SDK_INT >= 24) {
      releasePlayer();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (Util.SDK_INT < 24) {
      releasePlayer();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUI();
    if ((Util.SDK_INT < 24 || player == null)) {
      initializePlayer();
    }
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUI() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  private void initializePlayer(){
    if (player == null) {
      DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
      trackSelector.setParameters(
              trackSelector.buildUponParameters().setMaxVideoSizeSd());
      player = new SimpleExoPlayer.Builder(this)
              .setTrackSelector(trackSelector)
              .build();
    }

    //player = new SimpleExoPlayer.Builder(this).build();
    playerView.setPlayer(player);

    MediaItem mediaItem = new MediaItem.Builder()
            .setUri(getString(R.string.media_url_dash))
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build();

    player.setMediaItem(mediaItem);

    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    player.prepare();
  }

  private void releasePlayer() {
    if (player != null) {
      playWhenReady = player.getPlayWhenReady();
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      player.removeListener(playbackStateListener);
      player.release();
      player = null;
    }
  }

  private class PlaybackStateListener implements Player.EventListener {
    @Override
    public void onPlaybackStateChanged(int playbackState) {
      String stateString;
      switch (playbackState) {
        case ExoPlayer.STATE_IDLE:
          stateString = "ExoPlayer.STATE_IDLE      -";
          break;
        case ExoPlayer.STATE_BUFFERING:
          stateString = "ExoPlayer.STATE_BUFFERING -";
          break;
        case ExoPlayer.STATE_READY:
          stateString = "ExoPlayer.STATE_READY     -";
          break;
        case ExoPlayer.STATE_ENDED:
          stateString = "ExoPlayer.STATE_ENDED     -";
          break;
        default:
          stateString = "UNKNOWN_STATE             -";
          break;
      }
      Log.d(TAG, "changed state to " + stateString);
  }
}}
