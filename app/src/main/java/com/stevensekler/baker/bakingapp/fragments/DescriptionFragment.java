package com.stevensekler.baker.bakingapp.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT_DISPLAYED;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY_POSITION;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY_SIZE;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_OBJECT;

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
    @BindView(R.id.place_of_video)
    PlayerView exoPlayerView;
    /** ExoPlayer implementation based on Google Codelab and ExoPlayer's Developer guide:
     *  https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
     *  http://google.github.io/ExoPlayer/guide.html
     * */
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private Unbinder unbinder;
    private int arrayPosition;
    private Step[] steps;
    private boolean twoPane;
    PassDataToActivity callback;

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
                if (arrayPosition > 0){
                    arrayPosition--;
                    createNewDescriptionFragment(arrayPosition);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPosition < steps.length - 1) {
                    arrayPosition++;
                    createNewDescriptionFragment(arrayPosition);
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

        int container = 0;

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
                new DefaultTrackSelector(), new DefaultLoadControl());

        exoPlayerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), "bakingapp"), bandwidthMeter);

        String videoUrl = steps[arrayPosition].getVideoURL();

        if (steps[arrayPosition] != null && videoUrl != null) {
            Uri uri = Uri.parse(videoUrl);

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }
    }

    private void releasePlayer(){
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PassDataToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PassDataToActivity");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }

        if (!twoPane) {
            if (arrayPosition == 0) {
                previousButton.setVisibility(View.GONE);
            }

            if (arrayPosition == steps.length - 1) {
                nextButton.setVisibility(View.GONE);
            }
        } else {
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
