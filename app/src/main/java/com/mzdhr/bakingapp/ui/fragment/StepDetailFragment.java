package com.mzdhr.bakingapp.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
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
import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Step;
import com.mzdhr.bakingapp.ui.activity.IngredientAndStepActivity;
import com.mzdhr.bakingapp.ui.activity.StepDetailActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link IngredientAndStepActivity}
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
    private static String TAG = StepDetailFragment.class.getSimpleName();
    ArrayList<Step> mSteps = new ArrayList<>();
    String mVideoUrl = "";
    String mDescription = "";
    private SimpleExoPlayer mPlayer;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    // Views
    private MediaSessionCompat mMediaSession;
    private PlayerView mPlayerView;
    private TextView mDetailTextView;
    private ImageView mNoAvailableImageView;
    private TextView mNextButton;
    private TextView mBackButton;
    private TextView mCurrentTextView;
    private ImageView mRecipeStepDetailImageView;

    int mTotalSteps;
    int mCurrentStep = 0;
    private long mVideoPlayingPosition;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_step_detail, container, false);

        // Getting Step Values
        mVideoUrl = getArguments().getString(Constant.STEP_VIDEO_URL_KEY);
        mDescription = getArguments().getString(Constant.STEP_DESCRIPTION_KEY);
        mSteps = Parcels.unwrap(getArguments().getParcelable(Constant.STEP_LIST_KEY));
        mCurrentStep = getArguments().getInt(Constant.CURRENT_STEP_KEY);
        mTotalSteps = mSteps.size();

        // When device rotate -> load values from saveInstance
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.CURRENT_STEP_KEY)) {
                mVideoUrl = savedInstanceState.getString(Constant.STEP_VIDEO_URL_KEY);
                mDescription = savedInstanceState.getString(Constant.STEP_DESCRIPTION_KEY);
                mSteps = Parcels.unwrap((Parcelable) savedInstanceState.getParcelable(Constant.STEP_LIST_KEY));
                mCurrentStep = savedInstanceState.getInt(Constant.CURRENT_STEP_KEY);
                mVideoPlayingPosition = savedInstanceState.getLong(Constant.CURRENT_VIDEO_PLAY_POSITION_KEY);
            }
        }

        // Find Views
        mPlayerView = rootView.findViewById(R.id.player_view);
        mDetailTextView = rootView.findViewById(R.id.item_detail);
        mNoAvailableImageView = rootView.findViewById(R.id.no_video_available_ImageView);
        mNextButton = (TextView) rootView.findViewById(R.id.next_button_textView);
        mBackButton = (TextView) rootView.findViewById(R.id.back_button_textView);
        mCurrentTextView = (TextView) rootView.findViewById(R.id.current_textView);
        mRecipeStepDetailImageView = (ImageView) rootView.findViewById(R.id.recipe_step_detail_imageView);


        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStep < mTotalSteps - 1) {
                    // Stop Playing
                    if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        mMediaSession.setActive(false);
                    }
                    // Reset Position
                    mVideoPlayingPosition = 0;

                    mCurrentStep = mCurrentStep + 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            String.valueOf("Step # " + mCurrentStep));
                }
            }
        });

        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStep > 0) {
                    // Stop Playing
                    if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        mMediaSession.setActive(false);
                    }
                    // Reset Position
                    mVideoPlayingPosition = 0;

                    mCurrentStep = mCurrentStep - 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            String.valueOf("Step # " + mCurrentStep));
                }
            }
        });

        populateStepValues(
                mSteps.get(mCurrentStep).getDescription(),
                mSteps.get(mCurrentStep).getVideoURL(),
                mSteps.get(mCurrentStep).getThumbnailURL(),
                String.valueOf("Step # " + mCurrentStep));


        // If phone goes to landscape --Then--> Maximize the PlayerView to FullScreen
        // If a Phone && on Landscape
        if (!getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Set Activity Screen to Immersive --> https://developer.android.com/training/system-ui/immersive.htm
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
            // Hide ActionBar
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            // Hide all views except mPlayerView, So it become a fullscreen video
            mNextButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mCurrentTextView.setVisibility(View.GONE);
            mDetailTextView.setVisibility(View.GONE);
            // Make mPlayerView Width and Height match_parent in size.
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constant.STEP_VIDEO_URL_KEY, mVideoUrl);
        outState.getString(Constant.STEP_DESCRIPTION_KEY, mDescription);
        outState.putParcelable(Constant.STEP_LIST_KEY, Parcels.wrap(mSteps));
        outState.putInt(Constant.CURRENT_STEP_KEY, mCurrentStep);
        outState.putLong(Constant.CURRENT_VIDEO_PLAY_POSITION_KEY, mVideoPlayingPosition);
    }


    private void populateStepValues(String description, String videoUrl, String thumbnailUrl, String currentStep) {
        // Populate Video
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


        if (videoUrl.equals("")) {
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
                String userAgent = Util.getUserAgent(getActivity(), getContext().getString(R.string.app_name));
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), userAgent);
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                MediaSource mediaSource = new ExtractorMediaSource(
                        Uri.parse(videoUrl),
                        dataSourceFactory,
                        extractorsFactory,
                        null,
                        null);

                mPlayer.prepare(mediaSource);
                mPlayer.setPlayWhenReady(true);
            }

            if (mVideoPlayingPosition != C.TIME_UNSET) {
                mPlayer.seekTo(mVideoPlayingPosition);
            }
        }

        // Populate Description
        mDetailTextView.setText(description);

        // Populate Counting TextView -> mCurrentTextView
        mCurrentTextView.setText(currentStep);

        // Populate ThumbnailUrl
        try {
            if (!thumbnailUrl.equals("")) {
                mRecipeStepDetailImageView.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_stove)
                        .error(R.drawable.ic_stove)
                        .into(mRecipeStepDetailImageView);
            }
        } catch (Exception e) {
            Log.d(TAG, "bind: " + e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoUrl.equals("") && mPlayer != null) {
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
                String userAgent = Util.getUserAgent(getActivity(), getContext().getString(R.string.app_name));
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
            
            if (mVideoPlayingPosition != C.TIME_UNSET) {
                mPlayer.seekTo(mVideoPlayingPosition);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mVideoUrl.equals("") && mPlayer != null) {
            // Restore Player Position
            mVideoPlayingPosition = mPlayer.getCurrentPosition();
            // Pause Playing
            mPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Release the player when the activity is destroyed.
        if (!mVideoUrl.equals("") && mPlayer != null) {
            // Stop Playing
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the player when the activity is destroyed.
        if (!mVideoUrl.equals("") && mPlayer != null) {
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

    }


}
