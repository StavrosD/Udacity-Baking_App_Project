package gr.sdim.bakingapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;

public class DataQueryTask extends AsyncTask <URL,Void,String> {
    MainActivity parent;
    ProgressBar mProgressBar;

    public DataQueryTask(MainActivity parent, ProgressBar progressBar) {
        this.parent = parent;
        this.mProgressBar = progressBar;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL recipesUrl = urls[0];
    if (mProgressBar != null){
         mProgressBar.setVisibility(View.VISIBLE);
    }
        String results = null;
        try {
            results = NetworkUtils.getResponseFromHttpUrl(recipesUrl);

        }
        catch (IOException e){
            e.printStackTrace();
return  "Error: " +  e.getLocalizedMessage();
        }
        return results;
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressBar.setVisibility(View.INVISIBLE);
        parent.handleDataQueryTaskResponse(s);
    }
}
