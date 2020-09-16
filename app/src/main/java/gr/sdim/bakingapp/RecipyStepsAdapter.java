package gr.sdim.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipyStepsAdapter extends RecyclerView.Adapter<RecipyStepsAdapter.RecipyStepsAdapterViewHolder>{
   private JSONObject recipy;


    @NonNull
    @Override
    public RecipyStepsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipy_detail_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new RecipyStepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipyStepsAdapterViewHolder holder, int position) {
        JSONObject recipyStepForIndex = null;
        String recipyStepDescription = null;
        try {
            recipyStepForIndex =  recipy.getJSONArray("steps").getJSONObject(position);
            recipyStepDescription = recipyStepForIndex.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        holder.mRecipyStepTextView.setText(recipyStepDescription);
    }

    @Override
    public int getItemCount() {
        if (null == recipy) {
            return 0;
        }
        try {
            return recipy.getJSONArray("steps").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public class RecipyStepsAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mRecipyStepTextView;

        public RecipyStepsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecipyStepTextView = (TextView) itemView.findViewById(R.id.tvRecipyStep);
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onRecipyStepAdapterClick(adapterPosition);
        }
    }

    public void setRecipies(JSONObject recipy)  {
       this.recipy = recipy;
       notifyDataSetChanged();
    }

    private final RecipyStepAdapterOnClickHandler mClickHandler;

    public interface RecipyStepAdapterOnClickHandler {
        void onRecipyStepAdapterClick(Integer position);
    }

    public RecipyStepsAdapter(RecipyStepAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
}

