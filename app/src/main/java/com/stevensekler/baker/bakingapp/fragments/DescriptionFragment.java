package com.stevensekler.baker.bakingapp.fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Step;
import com.stevensekler.baker.bakingapp.utils.Methods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.stevensekler.baker.bakingapp.FragmentsActivity.INITIALIZING_INT_VARIABLE;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY_POSITION;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {
    @BindView(R.id.step_description)
    TextView stepDescription;
    @BindView(R.id.previous_button)
    Button previousButton;
    @BindView(R.id.next_button)
    Button nextButton;
    @BindView(R.id.error_image)
    ImageView errorImage;
    @BindView(R.id.place_of_video)
    PlayerView exoPlayerView;
    /** ExoPlayer implementation based on Google Codelab and ExoPlayer's Developer guide:
     *  https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
     *  http://google.github.io/ExoPlayer/guide.html
     * */
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private long playbackPosition;
    private Unbinder unbinder;
    private int arrayPosition;
    private Step[] steps;
    private boolean twoPane;
    private boolean systemUiState;
    PassDataToActivity callback;
    public static final String PLAY_WHEN_READY = "play_when_ready";
    public static final String PLAYBACK_POSITION = "playback_position";
    public static final String NO_VIDEO_URL = "";
    public static final int ANDROID_MARSHMALLOW = 23;
    public static final int FIRST_ITEM_FROM_ARRAY = 0;

    public DescriptionFragment() {
    }

    public interface PassDataToActivity{
        void descriptionFragmentDisplayed(boolean state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        unbinder = ButterKnife.bind(this, view);
        playWhenReady = true;

        if (getArguments().containsKey(STEP_ARRAY_POSITION)) {
            arrayPosition = getArguments().getInt(STEP_ARRAY_POSITION);
        }

        if (getArguments().containsKey(STEP_ARRAY)) {
            steps = (Step[]) getArguments().getParcelableArray(STEP_ARRAY);
        }

        if (stepDescription != null && steps != null){
            stepDescription.setText(steps[arrayPosition].getDescription());
        }

        if (callback != null) {
            callback.descriptionFragmentDisplayed(true);
        }

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPosition > FIRST_ITEM_FROM_ARRAY){
                    arrayPosition--;
                    createNewDescriptionFragment(arrayPosition);
                    releasePlayer();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPosition < steps.length - 1) {
                    arrayPosition++;
                    createNewDescriptionFragment(arrayPosition);
                    releasePlayer();
                }
            }
        });

        if (getActivity().findViewById(R.id.master_detail_layout) != null){
            twoPane = true;
        } else {
            twoPane = false;
        }
        return view;
    }

    private void createNewDescriptionFragment(int position){
        Bundle args = new Bundle();
        args.putInt(STEP_ARRAY_POSITION, position);
        args.putParcelableArray(STEP_ARRAY, steps);

        int container = INITIALIZING_INT_VARIABLE;

        if (getActivity().findViewById(R.id.master_detail_layout) != null){
            container = R.id.master_description;
        } else {
            container = R.id.fragment_container;
        }

        DescriptionFragment descriptionFragment = new DescriptionFragment();
        descriptionFragment.setArguments(args);


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(container, descriptionFragment, DESCRIPTION_FRAGMENT)
                .commit();
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        exoPlayerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(playbackPosition);

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getResources().getResourceName(R.string.app_name)));

        String videoUrl = steps[arrayPosition].getVideoURL();

        if (steps[arrayPosition] != null && videoUrl != null) {
            Uri uri = Uri.parse(videoUrl);
            if (!uri.toString().equals(NO_VIDEO_URL)) {
                MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                player.prepare(mediaSource, false, false);
            } else{
                displayImage();
            }
        }
    }

    private void releasePlayer(){
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
        showSystemUi();
    }


    private void getPlayerPositionValues(){
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
        }
    }
    /**
     * Based on: http://blog.grio.com/2014/02/androids-hiding-of-the-system-bar-fixed.html
     *
     * */
    private void hideSystemUi() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        systemUiState = true;
    }
/**
 * Based on: http://blog.grio.com/2014/02/androids-hiding-of-the-system-bar-fixed.html
 *
 * */

    private void showSystemUi(){
        if (systemUiState) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void displayImage(){
        exoPlayerView.setVisibility(View.GONE);
        String title = (String) getActivity().getTitle();
        int titleId = Methods.returnCakeImageId(title);
        errorImage.setImageResource(titleId);
        errorImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PassDataToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + getString(R.string.implement_pass_data_to_activity));
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > ANDROID_MARSHMALLOW) {
            initializePlayer();
        }

        if (arrayPosition == FIRST_ITEM_FROM_ARRAY && twoPane){
        exoPlayerView.setVisibility(View.GONE);
        errorImage.setVisibility(View.GONE);
        }

        if (!twoPane) {
            if (arrayPosition == FIRST_ITEM_FROM_ARRAY) {
                previousButton.setVisibility(View.INVISIBLE);
            }

            if (arrayPosition == steps.length - 1) {
                nextButton.setVisibility(View.INVISIBLE);
            }
        } else {
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= ANDROID_MARSHMALLOW || player == null)) {
            initializePlayer();
        }
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && !twoPane) {
            hideSystemUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPlayerPositionValues();
        if (Util.SDK_INT <= ANDROID_MARSHMALLOW) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > ANDROID_MARSHMALLOW) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYBACK_POSITION, playbackPosition);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
