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

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    JSONArray jsonArray;
    private static final String TAG = "ParseJSON";
    private SharedPreferences myPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String url;
    ConnectivityCheck checkNetwork;

    ViewPager2 vp;
    ViewPager2_Adapter csa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        myPreference=PreferenceManager.getDefaultSharedPreferences(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("listPref")) {
                    url = myPreference.getString("listPref","https://www.pcs.cnu.edu/~kperkins/pets/pets.json");
                    imageDownload();
                    csa.notifyDataSetChanged();

                }
            }
        };

        myPreference.registerOnSharedPreferenceChangeListener(listener);
        url = myPreference.getString("listPref","https://www.pcs.cnu.edu/~kperkins/pets/pets.json");
        imageDownload();

        //get a ref to the viewpager
        vp=findViewById(R.id.view_pager);
        //create an instance of the swipe adapter
        csa = new ViewPager2_Adapter(this);

        //set this viewpager to the adapter
        vp.setAdapter(csa);

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
            if (string != null) {
                JSONObject jsonobject = new JSONObject(string);
                jsonArray = jsonobject.getJSONArray("pets");

                //Removed indenting
                Log.d(TAG, jsonArray.toString());

                csa.getJSONFiles(jsonArray, url);
            }
            else {
                csa.getJSONFiles(null, url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void imageDownload() {
        checkNetwork = new ConnectivityCheck(this);
        boolean netReach = checkNetwork.isNetworkReachable();
        boolean wifiReach = checkNetwork.isWifiReachable();
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
        String message = "No internet connection";
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(":(", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

}
