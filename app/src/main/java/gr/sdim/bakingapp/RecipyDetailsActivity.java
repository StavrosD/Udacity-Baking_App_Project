package gr.sdim.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipyDetailsActivity extends AppCompatActivity implements RecipyDetailsFragment.OnRecipyClickListener{
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private FrameLayout mFrameLayout;
    private FrameLayout mDetailPane;
    private RecipyDetailsFragment mRecipyDetailsFragment;
    private RecipyStepFragment mRecipyStepFragment;

    private Integer selectedStep = -1;

    private JSONObject mRecipy;
    public static final String INSTANCE_FRAGMENT_ID = "instanceFragmentId";
private Boolean fullScreen ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipy_details);

        // setup views
        mErrorMessage = (TextView) findViewById(R.id.errorMessageTextView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        mFrameLayout = (FrameLayout) findViewById(R.id.master_container);
        mDetailPane = (FrameLayout) findViewById(R.id.detail_container);

        // setup fragment
        try {

            Intent intent = getIntent();
            String recipyJson = intent.getStringExtra(MainActivity.RECIPY_INTENT_ID);

            mRecipy = new JSONObject(recipyJson);

            // setup fragment
            FragmentManager fragmentManager = getSupportFragmentManager();

            // try to find an existing instance
            Fragment frag = fragmentManager.findFragmentByTag(RecipyDetailsFragment.TAG);

            if (frag == null) {
                // if none were found, create it
                mRecipyDetailsFragment = new RecipyDetailsFragment();
                mRecipyDetailsFragment.setRecipy(mRecipy);
                fragmentManager.beginTransaction()
                        .add(R.id.master_container, mRecipyDetailsFragment,RecipyDetailsFragment.TAG)
                        .commit();
            } else {
                //
                mRecipyDetailsFragment = (RecipyDetailsFragment) frag;
            }

            frag = fragmentManager.findFragmentByTag(RecipyStepFragment.TAG);
            Boolean masterDetailViewIsAvailable = findViewById(R.id.detail_container) != null;
            if (frag == null){
                mRecipyStepFragment = new RecipyStepFragment();
                mRecipyStepFragment.setStepInfo(mRecipy.getJSONArray("steps"),0);
                if (masterDetailViewIsAvailable) {
                    fragmentManager.beginTransaction()
                            .add(R.id.detail_container, mRecipyStepFragment, RecipyStepFragment.TAG)
                            .commit();
                }
            } else {
                    mRecipyStepFragment = (RecipyStepFragment) frag;
            }

            fullScreen = false;

            if (mDetailPane == null) { // phone
                // Display full screen player if the player is displayed in landscape on a phone
                int orientation = getResources().getConfiguration().orientation;
                if (mRecipyStepFragment instanceof RecipyStepFragment) {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE && frag == null) {
                        fullScreen = true;
                        mRecipyStepFragment.fullScreenPlayer();

                    }
                }
            }


            } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (fullScreen){
            mRecipyStepFragment.fullScreenPlayer();
        } else {
            mRecipyStepFragment.normalPlayer();
        }
    }

    private void showResults(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        mErrorMessage.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,INSTANCE_FRAGMENT_ID,mRecipyDetailsFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRecipyDetailsFragment = (RecipyDetailsFragment) getSupportFragmentManager().getFragment(savedInstanceState,INSTANCE_FRAGMENT_ID);
    }


    @Override
    public void onRecipyStepSelected(int position) {
        Bundle bundle = new Bundle();
        try {
            bundle.putString("recipy",mRecipy.getJSONArray("steps").getJSONObject(position).toString());
            RecipyStepFragment recipyStepFragment = new RecipyStepFragment();
            recipyStepFragment.setStepInfo(mRecipy.getJSONArray("steps"),position);
            if (fullScreen){
                recipyStepFragment.fullScreenPlayer();
            } else {
                recipyStepFragment.normalPlayer();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mDetailPane == null) {  // phone, replace existing view
                fragmentManager.beginTransaction().replace(R.id.master_container, recipyStepFragment).addToBackStack(null).commit();
            } else {                    // tablet, update right view (detail_container)
                fragmentManager.beginTransaction().replace(R.id.detail_container, recipyStepFragment).addToBackStack(null).commit();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}