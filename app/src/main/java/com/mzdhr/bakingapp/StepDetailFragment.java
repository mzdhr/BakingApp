package com.mzdhr.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mzdhr.bakingapp.dummy.DummyContent;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Step;

import java.util.ArrayList;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link StepsActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 * <p>
 * <p>
 * <p>
 * I use ExoPlayer Lib
 * https://google.github.io/ExoPlayer/guide.html
 */

public class StepDetailFragment extends Fragment implements OnClickListener, ExoPlayer.EventListener {

    // Objects
    ArrayList<Step> mSteps = new ArrayList<>();

    //private StepDetailActivity.ObserverFromStepDetailActivity mObserver;
    StepDetailActivity mStepDetailActivity;

    String mVideoUrl = "";
    String mDescription = "";

    private SimpleExoPlayer mPlayer;

    private static String TAG = StepDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */

    // Views
    private DummyContent.DummyItem mItem;
    private MediaSessionCompat mMediaSession;
    private PlayerView mPlayerView;
    private TextView mDetailTextView;
    private ImageView mNoAvailableImageView;
    private TextView mNextButton;
    private TextView mBackButton;
    private TextView mCurrentTextView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: Launched!");
//        mVideoUrl = MainActivity.mRecipes.get(0).getSteps().get(0).getVideoURL();
//        Log.d(TAG, "onCreate: mVideoUrl: " + mVideoUrl);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_step_detail, container, false);

//        savedInstanceState.getString()
        mVideoUrl = getArguments().getString(Constant.STEP_VIDEO_URL_KEY);
        mDescription = getArguments().getString(Constant.STEP_DESCRIPTION_KEY);
//        mSteps = Parcels.unwrap(getArguments().getParcelable(Constant.STEP_LIST_KEY));
//
//        for (int i = 0; i < mSteps.size(); i++) {
//            Log.d(TAG, "onCreateView: " + mSteps.get(i).getDescription());
//            Log.d(TAG, "onCreateView: " + mSteps.get(i).getVideoURL());
//        }
//        Log.d(TAG, "onCreate: mVideoURL: " + mVideoUrl);
//        Log.d(TAG, "onCreate: mDescription: " + mDescription);

        // Find Views
        mPlayerView = rootView.findViewById(R.id.player_view);
        mDetailTextView = rootView.findViewById(R.id.item_detail);
        mNoAvailableImageView = rootView.findViewById(R.id.no_video_available_ImageView);
        mNextButton = (TextView) rootView.findViewById(R.id.next_button_textView);
        mBackButton = (TextView) rootView.findViewById(R.id.previous_button_textView);
        mCurrentTextView = (TextView) rootView.findViewById(R.id.current_textView);

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Preparing Media Session
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        // Preparing PlaybackState for media buttons
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(stateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);


        if (mVideoUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            mNoAvailableImageView.setVisibility(View.VISIBLE);
        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            mNoAvailableImageView.setVisibility(View.GONE);
            // Preparing the Player
            if (mPlayer == null) {
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
                mPlayerView.setPlayer(mPlayer);

                mPlayer.addListener(this);

                // Preparing the MediaSource
                String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), userAgent);
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


                MediaSource mediaSource = new ExtractorMediaSource(
                        Uri.parse(mVideoUrl),
                        dataSourceFactory,
                        extractorsFactory,
                        null,
                        null);

                mPlayer.prepare(mediaSource);
                mPlayer.setPlayWhenReady(true);
            }
        }

        // Setting Details in mDetailTextView

        //String value = MainActivity.mRecipes.get(0).getSteps().get(1).getDescription();
        ((TextView) rootView.findViewById(R.id.item_detail)).setText(mDescription);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mVideoUrl.equals("")) {
            // Resume Playing
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mVideoUrl.equals("")) {
            // Pause Playing
            mPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mVideoUrl.equals("")) {
            // Stop Playing
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mMediaSession.setActive(false);
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }


    @Override
    public void onClick(View v) {
        //     Toast.makeText(getActivity(), "You Clicked: " v., Toast.LENGTH_SHORT).show();
    }


}
