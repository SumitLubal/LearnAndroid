package com.jumpybit.upscgrad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> fakeData;
    ListViewCompat weather_list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String a[] = new String[]{"hello ", "hello 12 ", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34"};
        fakeData = new ArrayList<>();
        fakeData.addAll(Arrays.asList(a));
        a = null;
        weather_list = (ListViewCompat) findViewById(R.id.weather_list_view);
        adapter = new ArrayAdapter<String>(this, R.layout.weather_update_textview, R.id.weather_list_view_TextView, fakeData);
        weather_list.setAdapter(adapter);
        weather_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplication(),"Item "+(i+1)+" clicked!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,adapter.getItem(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    private void updateWeather(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String locationPIN = sp.getString(getResources().getString(R.string.location_key),getResources().getString(R.string.location));
        new FetchWeatherTask().execute(new String[]{locationPIN});
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.d(TAG, "Action Refresh clicked!!");
            updateWeather();
        }else if(id == R.id.action_settings){
            Log.d(TAG, "Action Refresh clicked!!");
            startActivity(new Intent(this,SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, String[], String[]> {
        final private String TAG = FetchWeatherTask.class.getSimpleName();
        private String postalCode = "411045";
        private String modeType = "json";
        private String unitType = "metric";
        private String countForecast = "7";
        private String appid = "dc31cf7c01ee0b4fe650e22953579996";

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            postalCode = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+postalCode+"&mode=json&units=metric&cnt=7&appid=dc31cf7c01ee0b4fe650e22953579996");
                Uri.Builder builder = new Uri.Builder();


                builder.scheme("http")
                        .authority("api.openweathermap.org").appendPath("data").appendPath("2.5")
                        .appendPath("forecast").appendPath("daily")
                        .appendQueryParameter("q", postalCode).appendQueryParameter("mode", modeType)
                        .appendQueryParameter("units", unitType)
                        .appendQueryParameter("cnt", countForecast).appendQueryParameter("appid", appid);
                Log.d(TAG, builder.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.d(TAG, forecastJsonStr);
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return (new WeatherDataParser()).getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(countForecast));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                fakeData.clear();
                fakeData.addAll(Arrays.asList(strings));
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getApplicationContext(),"No internet access",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
