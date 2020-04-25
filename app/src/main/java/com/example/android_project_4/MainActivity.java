package com.example.android_project_4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    JSONArray jsonArray;
    private static final String TAG = "ParseJSON";
    int numberentries = -1;
    private SharedPreferences myPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = null;
    private String url;
    private String loc;
    ConnectivityCheck checkNetwork;

    ViewPager2 vp;
    ViewPager2_Adapter csa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        myPreference=PreferenceManager.getDefaultSharedPreferences(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("listPref")) {
                    url = myPreference.getString("listPref","https://www.pcs.cnu.edu/~kperkins/pets/pets.json");
                    imageDownload();

                }
            }
        };

        myPreference.registerOnSharedPreferenceChangeListener(listener);
        url = myPreference.getString("listPref","https://www.pcs.cnu.edu/~kperkins/pets/pets.json");
        imageDownload();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            doPreferences();
            return true;
        }

        //all else fails let super handle it
        return super.onOptionsItemSelected(item);
    }

    private void doPreferences() {
        Intent myintent = new Intent(this, SettingsActivity.class);
        startActivity(myintent);
    }

    public void processJSON(String string) {

        try {
            JSONObject jsonobject = new JSONObject(string);
            jsonArray = jsonobject.getJSONArray("pets");

            //Removed indenting
            Log.d(TAG,jsonArray.toString());
            numberentries = jsonArray.length();

            //TODO populate viewpager2
            //setupSimpleSpinner();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void imageDownload() {
        checkNetwork = new ConnectivityCheck(this);
        boolean netReach = checkNetwork.isNetworkReachable();
        boolean wifiReach = checkNetwork.isWifiReachable();

        numberentries = 0;
        jsonArray = null;

        if (netReach == true || wifiReach == true) {
            DownloadTask_KP download = new DownloadTask_KP(this);
            download.execute(url);
        }
        else {
            filesNotFound();
        }
    }

    public void filesNotFound() {
        String message = "Error";
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(":(", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

}
