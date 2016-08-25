package com.jumpybit.upscgrad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> fakeData;
    ListViewCompat weather_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String a[] = new String[]{"hello ", " hello 12 ", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34", "hello 34"};
        fakeData = new ArrayList<>();
        fakeData.addAll(Arrays.asList(a));
        a = null;
        weather_list = (ListViewCompat) findViewById(R.id.weather_list_view);
        weather_list.setAdapter(new ArrayAdapter<String>(this, R.layout.weather_update_textview, R.id.weather_list_view_TextView, fakeData));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            Log.d(TAG,"Action Refresh clicked!!");
            new FetchWeatherTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }
}
