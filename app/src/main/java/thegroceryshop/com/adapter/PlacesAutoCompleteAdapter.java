package thegroceryshop.com.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.KeyboardVisibilityListener;
import thegroceryshop.com.custom.ValidationUtil;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    private Context context;
    private LatLng latLng;
    private String currentText;
    private boolean isKeybordShowing;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, LatLng latLng, String currentText) {
        super(context, textViewResourceId);
        this.context = context;
        this.latLng = latLng;
        this.currentText = currentText;

        AppUtil.setKeyboardVisibilityListener((Activity)context, new KeyboardVisibilityListener() {
            @Override
            public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
                isKeybordShowing = keyboardVisible;
            }
        });
    }

    @Override
    public int getCount() {
        if (resultList == null) {
            resultList = new ArrayList<>();
        }
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (isKeybordShowing) {
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        if(resultList!= null && resultList.size() >0) {
                            filterResults.values = resultList;
                            filterResults.count = resultList.size();
                        }
                    }

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private static final String LOG_TAG = "AutoApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAz16e7lCk9niP5BP4k18a3Tv4GHiZvZWo";

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);

            sb.append("?location=" + latLng.latitude + "," + latLng.longitude);
            sb.append("&sensor=true&key=" + API_KEY);
            sb.append("&components=country:eg");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));


            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("PlaceAPI", "Error processing Places API URL", e);
            return new ArrayList<>();
        } catch (IOException e) {
            Log.e("PlaceAPI", "Error connecting to Places API", e);
            return new ArrayList<>();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            System.out.println("Print the Server Response :" + jsonResults.toString());
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                if(ValidationUtil.isNullOrBlank(currentText)){
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }else{
                    if(!currentText.equalsIgnoreCase(predsJsonArray.getJSONObject(i).getString("description"))){

                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

}