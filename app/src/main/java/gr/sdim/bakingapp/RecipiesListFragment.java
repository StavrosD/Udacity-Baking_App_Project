package gr.sdim.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class RecipiesListFragment  extends Fragment implements RecipiesAdapter.RecipyAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private RecipiesAdapter mRecipiesAdapter;
    private JSONArray mRecipies;

    private AdapterView.OnItemSelectedListener listener;
    static final String RECIPIES_LIST = "recipies_list";
    public static final String TAG = "RECIPIES_LIST_FRAGMENT";

    public RecipiesListFragment() {

    }

    // Define a new interface OnRecipyClickListener that triggers a callback in the host activity
    OnRecipyClickListener mCallback;

    @Override
    public void onRecipyAdapterClick(Integer position)
    {
        mCallback.onRecipySelected(position);
    }

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnRecipyClickListener {
        void onRecipySelected(int position);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnRecipyClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipyClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipies_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.frame_layout_recipies_list);

        mRecipiesAdapter = new RecipiesAdapter(this);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            // In landscape
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));

        } else {
            // In portrait
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);

        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecipiesAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPIES_LIST)) {
            try {
                mRecipies = new JSONArray(savedInstanceState.getString(RECIPIES_LIST));

                mRecipiesAdapter.setRecipies(this.mRecipies);
                mRecipiesAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return rootView; // super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setRecipies (JSONArray recipies){
        this.mRecipies = recipies;
        if (mRecipiesAdapter != null) {
            mRecipiesAdapter.setRecipies(this.mRecipies);
            mRecipiesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipies != null) {
            outState.putString(RECIPIES_LIST, mRecipies.toString());
        }
    }
}

