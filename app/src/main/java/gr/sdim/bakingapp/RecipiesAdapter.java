package gr.sdim.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipiesAdapter extends RecyclerView.Adapter<RecipiesAdapter.RecipiesAdapterViewHolder>{
    private JSONArray recipies;

    public RecipiesAdapter(RecipyAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecipiesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipy_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new RecipiesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipiesAdapterViewHolder holder, int position) {
        JSONObject recipyForindex = null;
        try {
            recipyForindex =  recipies.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            holder.mRecipyNameTextView.setText(recipyForindex.getString("name"));
            holder.mRecipyStepsTextView.setText("Steps: " + recipyForindex.getJSONArray("steps").length());
            holder.mRecipyServingsTextView.setText("Servings: " + recipyForindex.getString("servings"));
            holder.mRecipyIngredientsTextView.setText("ingredients: " + recipyForindex.getJSONArray("ingredients").length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (null == recipies) {
            return 0;
        }
        return recipies.length();
    }

    public class RecipiesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mRecipyNameTextView, mRecipyIngredientsTextView, mRecipyStepsTextView, mRecipyServingsTextView;

        public RecipiesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecipyNameTextView = (TextView) itemView.findViewById(R.id.tvRecipyName);
            mRecipyIngredientsTextView = (TextView) itemView.findViewById(R.id.tvIngredients);
            mRecipyStepsTextView = (TextView) itemView.findViewById(R.id.tvSteps);
            mRecipyServingsTextView = (TextView) itemView.findViewById(R.id.tvServings);
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onRecipyAdapterClick(adapterPosition);
        }
    }

    public void setRecipies(JSONArray recipies)  {
       this.recipies = recipies;
       notifyDataSetChanged();
    }

    private final RecipyAdapterOnClickHandler mClickHandler;

    public interface RecipyAdapterOnClickHandler {
        void onRecipyAdapterClick(Integer position);
    }



}

