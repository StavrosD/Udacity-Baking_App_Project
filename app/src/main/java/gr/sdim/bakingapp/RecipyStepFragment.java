package gr.sdim.bakingapp;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.norulab.exofullscreen.MediaPlayer;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.norulab.exofullscreen.MediaPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipyStepFragment extends Fragment  implements View.OnClickListener{
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private JSONArray mSteps;
    private Integer mSelectedStep;
    private Button mPreviousStepButton,mNextStepButton;
    private TextView mStepDescription;
    private LinearLayout mStepButtons;
    private ImageView mNoVideoAvailable;
    public static final String TAG = "RECIPIY_STEP_FRAGMENT";
    private static final String RECIPY_STEPS = "RECIPY_STEP";
    private static final String STEP_INDEX = "STEP_INDEX";
    private static final String FULL_SCREEN = "FULL_SCREEN";
    private Dialog mFullScreenDialog;
    private Boolean showFullScreen = false;
    public RecipyStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipy_step,container,false);
        mStepDescription = (TextView) rootView.findViewById(R.id.step_description);
        mPreviousStepButton = (Button) rootView.findViewById(R.id.button_previous_step);
        mNextStepButton = (Button) rootView.findViewById(R.id.button_next_step);
        mStepButtons = (LinearLayout) rootView.findViewById(R.id.step_buttons);
        mPlayerView = (PlayerView) rootView.findViewById(R.id.exo_player_view);
        mExoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
        mNoVideoAvailable = (ImageView)rootView.findViewById(R.id.imageView);
        mPreviousStepButton.setOnClickListener(this);
        mNextStepButton.setOnClickListener(this);

        // handle the exoplayer full screen button
        mPlayerView.findViewById(R.id.exo_fullscreen_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showFullScreen = !showFullScreen;
                if (showFullScreen) {
                    openFullscreenDialog();
                } else {
                    closeFullscreenDialog();
                }

            }
        });
        // load stored values, if available
        if (savedInstanceState != null) {
            try {
                mSteps = new JSONArray(savedInstanceState.getString(RECIPY_STEPS));
                showFullScreen = savedInstanceState.getBoolean(FULL_SCREEN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSelectedStep = savedInstanceState.getInt(STEP_INDEX);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateStepInfo(mSelectedStep);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECIPY_STEPS,mSteps.toString());
        outState.putInt(STEP_INDEX, mSelectedStep);
        outState.putBoolean(FULL_SCREEN,showFullScreen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void setStepInfo(JSONArray steps, Integer selectedStep){
        mSteps = steps;
        mSelectedStep = selectedStep;
    }
    // Exoplayer setup and deallocation
    private void initializePlayer(Uri mediaUri) {
        mExoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
        mPlayerView.setPlayer(mExoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "Baking App"));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri);
        mExoPlayer.prepare(videoSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    // handle steps
    private void populateStepInfo(Integer stepIndex){
        try {
            mSelectedStep = stepIndex;

            JSONObject currentStep = mSteps.getJSONObject(mSelectedStep);
            getActivity().setTitle(currentStep.getString("shortDescription"));

            String videoURL = currentStep.getString("videoURL");
            if (!videoURL.trim().isEmpty() && videoURL != null) {
                mNoVideoAvailable.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.VISIBLE);
                initializePlayer(Uri.parse(videoURL));
            } else {
                mPlayerView.setVisibility(View.GONE);
                mNoVideoAvailable.setVisibility(View.VISIBLE);
            }

            // Enable the "Previous step" button if we have reached the first step
            if (mSelectedStep == 0) {
                mPreviousStepButton.setEnabled(false);
            } else {
                mPreviousStepButton.setEnabled(true);
            }

            // Disable the "Next step" button if we have reached the last step
            if (mSelectedStep == (mSteps.length()-1)){
                mNextStepButton.setEnabled(false);
            } else  {
                mNextStepButton.setEnabled(true);
            }

            mStepDescription.setText(currentStep.getString("description"));

            if (mPlayerView.getVisibility() == View.VISIBLE && showFullScreen) {
                openFullscreenDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void nextStep(){
        populateStepInfo(mSelectedStep +1);
    }

    private void previousStep(){
        populateStepInfo(mSelectedStep-1);
    }

    public  void fullScreenPlayer(){
        showFullScreen = true;

    }

    public void normalPlayer(){
        showFullScreen = false;
    }


    private void openFullscreenDialog() {
        if (mFullScreenDialog == null){
            mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        showFullScreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        ((FrameLayout) getActivity().findViewById(R.id.exo_player_container)).addView(mPlayerView);
        showFullScreen = false;
        mFullScreenDialog.dismiss();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_next_step:
                nextStep();
                break;
            case R.id.button_previous_step:
                previousStep();
                break;
        }
    }
}
