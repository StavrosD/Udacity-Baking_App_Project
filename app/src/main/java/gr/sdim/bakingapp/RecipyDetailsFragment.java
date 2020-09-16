package gr.sdim.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipyDetailsFragment extends Fragment implements RecipyStepsAdapter.RecipyStepAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private RecipyStepsAdapter mRecipyStepsAdapter;
    private TextView mRecipyIngredients;
    private JSONObject mRecipy;
    public static final String TAG = "RECIPIY_DETAILS_FRAGMENT";
    static final String STEPS_LIST = "STEPS_LIST";
    public String ingredientsList = "";
    private AdapterView.OnItemSelectedListener listener;

    public RecipyDetailsFragment(){
        mRecipyStepsAdapter = new RecipyStepsAdapter(this);

    }

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    OnRecipyClickListener mCallback;

    @Override
    public void onRecipyStepAdapterClick(Integer position) {
        mCallback.onRecipyStepSelected(position);
    }

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnRecipyClickListener {
        void onRecipyStepSelected(int position);
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
        View rootView = inflater.inflate(R.layout.fragment_recipy_details,container,false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_steps);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecipyStepsAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(STEPS_LIST)){
            try {
                mRecipy = new JSONObject(savedInstanceState.getString(STEPS_LIST));
                mRecipyStepsAdapter.setRecipies(this.mRecipy);
                mRecipyStepsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            getActivity().setTitle(mRecipy.getString("name"));


            JSONObject currentIngredient = null;
            JSONArray ingredients = mRecipy.getJSONArray("ingredients");
            String unit;
            for (int i = 0; i < ingredients.length(); i++){
                currentIngredient = ingredients.getJSONObject(i);
                ingredientsList += currentIngredient.getString("quantity") + " ";
                unit = currentIngredient.getString("measure");
                if (unit != "UNIT") {
                    ingredientsList += unit + " ";
                }
                ingredientsList += ingredients.getJSONObject(i).getString("ingredient");
                if (i < ingredients.length()-1) ingredientsList+= "\r\n";
            }
            mRecipyIngredients = rootView.findViewById(R.id.recipy_ingredients);
            mRecipyIngredients.setText(ingredientsList);

            // Save the recipy ingredients list. It will be loaded on the widget automatically next time.
            SharedPreferences sharedPref = getActivity().getSharedPreferences(String.valueOf(R.string.saved_recipy_ingredients_list), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.saved_recipy_ingredients_list),ingredientsList);
            editor.commit();

            // update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), BakeAppWidgetProvider.class));
            BakeAppWidgetProvider.updateRecipyIngredients(getActivity(), AppWidgetManager.getInstance(getActivity()),ingredientsList,appWidgetIds);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView; // super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setRecipy(JSONObject recipy){
        this.mRecipy = recipy;
        if (mRecipyStepsAdapter != null) {
            mRecipyStepsAdapter.setRecipies(this.mRecipy);
            mRecipyStepsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mRecipyStepsAdapter!= null){
            mRecipyStepsAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipy != null) {
            outState.putString(STEPS_LIST, mRecipy.toString());
        }
    }
}

