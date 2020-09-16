package gr.sdim.bakingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements RecipiesListFragment.OnRecipyClickListener {
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private FrameLayout mFrameLayout;
    private RecipiesListFragment mRecipiesListFragment;
    private JSONArray mRecipies;
    public static final String INSTANCE_FRAGMENT_ID = "INSTANCE_FRAGMENT_ID";
    public static final String RECIPY_INTENT_ID = "RECIPY_INTENT";
    public static final String RECIPY_ID = "RECIPY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // setup views
    mErrorMessage = (TextView) findViewById(R.id.errorMessageTextView);
    mLoadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
    mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout_recipies);

    // setup fragment
    FragmentManager fragmentManager = getSupportFragmentManager();
    // try to find an existing instance
    Fragment frag = fragmentManager.findFragmentByTag(RecipiesListFragment.TAG);
    if (frag == null) {
        // if none were found, create it
        mRecipiesListFragment = new RecipiesListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.frame_layout_recipies, mRecipiesListFragment, RecipiesListFragment.TAG)
                .commit();
    } else {
        //
        mRecipiesListFragment = (RecipiesListFragment) frag;
    }
    if (savedInstanceState == null) {

    //     fragManager.beginTransaction().replace(R.id.content_frame, frag, Home.TAG).commit();
        URL recipiesURL = null;
        try {
            recipiesURL = new URL("https://go.udacity.com/android-baking-app-json");
        } catch (MalformedURLException e) {
            showErrorMessage();
            return;
        }
        // load from the internet
        new DataQueryTask(this, mLoadingIndicator).execute(recipiesURL);
    } else {
        try {
            mRecipies = new JSONArray(savedInstanceState.getString(RECIPY_ID));
            mRecipiesListFragment.setRecipies(mRecipies);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
public void handleDataQueryTaskResponse(String result)  {
        if (result.startsWith("Error:")){
          mErrorMessage.setText(result);
          showErrorMessage();
        } else if (result != null && result != "" ){
            try {
                mRecipies = new JSONArray(result);
                mRecipiesListFragment.setRecipies(mRecipies);
                showResults();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage();
        }
}

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        getSupportFragmentManager().putFragment(outState,INSTANCE_FRAGMENT_ID,mRecipiesListFragment);
        outState.putString(RECIPY_ID, mRecipies.toString());
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRecipiesListFragment = (RecipiesListFragment) getSupportFragmentManager().getFragment(savedInstanceState,INSTANCE_FRAGMENT_ID);
        try {
            mRecipies = new JSONArray(savedInstanceState.getString(RECIPY_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecipySelected(int position) {
        Bundle bundle = new Bundle();
        try {

            String itemName = mRecipies.getString(position);
            bundle.putString(RECIPY_INTENT_ID,mRecipies.getString(position));
             final Intent intent = new Intent(this,RecipyDetailsActivity.class);
             intent.putExtras(bundle);
             startActivity(intent);
        //    Toast toast = Toast.makeText(this, itemName,Toast.LENGTH_LONG);
          //  toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}