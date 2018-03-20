package com.mzdhr.bakingapp.ui.fragment;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
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

public class StepDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    // Objects
    private static String TAG = StepDetailFragment.class.getSimpleName();
    ArrayList<Step> mSteps = new ArrayList<>();
    String mVideoUrl = "";
    String mDescription = "";
    private SimpleExoPlayer mPlayer;
    private int mTotalSteps;
    private int mCurrentStep = 0;
    private long mVideoPlayingPosition;
    private boolean mPlayWhenReady = true;

    // Views
    private MediaSessionCompat mMediaSession;
    private PlayerView mPlayerView;
    private TextView mDetailTextView;
    private ImageView mNoAvailableImageView;
    private TextView mNextButton;
    private TextView mBackButton;
    private TextView mCurrentTextView;
    private ImageView mRecipeStepDetailImageView;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_step_detail, container, false);

        // Getting Step Values
        mVideoUrl = getArguments().getString(Constant.STEP_VIDEO_URL_KEY);
        mDescription = getArguments().getString(Constant.STEP_DESCRIPTION_KEY);
        mSteps = Parcels.unwrap(getArguments().getParcelable(Constant.STEP_LIST_KEY));
        mCurrentStep = getArguments().getInt(Constant.CURRENT_STEP_KEY);
        mTotalSteps = mSteps.size();

        // When device rotate -> load values from saveInstance
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.CURRENT_VIDEO_PLAY_POSITION_KEY)) {
            mVideoUrl = savedInstanceState.getString(Constant.STEP_VIDEO_URL_KEY);
            mDescription = savedInstanceState.getString(Constant.STEP_DESCRIPTION_KEY);
            mSteps = Parcels.unwrap((Parcelable) savedInstanceState.getParcelable(Constant.STEP_LIST_KEY));
            mCurrentStep = savedInstanceState.getInt(Constant.CURRENT_STEP_KEY);
            mVideoPlayingPosition = savedInstanceState.getLong(Constant.CURRENT_VIDEO_PLAY_POSITION_KEY);
            mPlayWhenReady = savedInstanceState.getBoolean(Constant.PLAY_WHEN_READY_KEY);
            Log.d(TAG, "onCreateView: savedInstanceState Called!!!! | mVideoPlayingPosition -> " + mVideoPlayingPosition + "mPlayWhenReady -> " + mPlayWhenReady);
        }

        // Find Views
        mPlayerView = rootView.findViewById(R.id.player_view);
        mDetailTextView = rootView.findViewById(R.id.item_detail);
        mNoAvailableImageView = rootView.findViewById(R.id.no_video_available_ImageView);
        mNextButton = (TextView) rootView.findViewById(R.id.next_button_textView);
        mBackButton = (TextView) rootView.findViewById(R.id.back_button_textView);
        mCurrentTextView = (TextView) rootView.findViewById(R.id.current_textView);
        mRecipeStepDetailImageView = (ImageView) rootView.findViewById(R.id.recipe_step_detail_imageView);


        mNextButton.setOnClickListener(new View.OnClickListener() {
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
                    // Reset Position and Play status on New Step
                    mVideoPlayingPosition = 0;
                    mPlayWhenReady = true;

                    mCurrentStep = mCurrentStep + 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            String.valueOf("Step # " + mCurrentStep));
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
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
                    // Reset Position and Play status on New Step
                    mVideoPlayingPosition = 0;
                    mPlayWhenReady = true;

                    mCurrentStep = mCurrentStep - 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            String.valueOf("Step # " + mCurrentStep));
                }
            }
        });


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
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            populateStepValues(
                    mSteps.get(mCurrentStep).getDescription(),
                    mSteps.get(mCurrentStep).getVideoURL(),
                    mSteps.get(mCurrentStep).getThumbnailURL(),
                    String.valueOf("Step # " + mCurrentStep));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            populateStepValues(
                    mSteps.get(mCurrentStep).getDescription(),
                    mSteps.get(mCurrentStep).getVideoURL(),
                    mSteps.get(mCurrentStep).getThumbnailURL(),
                    String.valueOf("Step # " + mCurrentStep));
        }
    }

    private void populateStepValues(String description, String videoUrl, String thumbnailUrl, String currentStep) {

        if (videoUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            mNoAvailableImageView.setVisibility(View.VISIBLE);
        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            mNoAvailableImageView.setVisibility(View.GONE);
            initializePlayer(videoUrl);
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
        } catch (
                Exception e) {
            Log.d(TAG, "populateStepValues: " + e.toString());
        }
    }

    private void initializePlayer(String videoUrl) {
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
        mMediaSession.setActive(true);

        if (mPlayer == null) {
            // Preparing Player
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mPlayer);

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

            // Forward to Position
            if (mVideoPlayingPosition != 0) {
                mPlayer.seekTo(mVideoPlayingPosition);
                Log.d(TAG, "initializePlayer: mVideoPlayingPosition -> " + mVideoPlayingPosition);
            }
            // Stop or Play as user decided
            mPlayer.setPlayWhenReady(mPlayWhenReady);
            Log.d(TAG, "initializePlayer: mPlayWhenReady -> " + mPlayWhenReady);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mVideoPlayingPosition = mPlayer.getCurrentPosition();
        }
        outState.putString(Constant.STEP_VIDEO_URL_KEY, mVideoUrl);
        outState.getString(Constant.STEP_DESCRIPTION_KEY, mDescription);
        outState.putParcelable(Constant.STEP_LIST_KEY, Parcels.wrap(mSteps));
        outState.putInt(Constant.CURRENT_STEP_KEY, mCurrentStep);
        outState.putLong(Constant.CURRENT_VIDEO_PLAY_POSITION_KEY, mVideoPlayingPosition);
        outState.putBoolean(Constant.PLAY_WHEN_READY_KEY, mPlayWhenReady);
        Log.d(TAG, "onSaveInstanceState: Called" + mVideoPlayingPosition + " " + mPlayWhenReady);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mVideoPlayingPosition = mPlayer.getCurrentPosition();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
