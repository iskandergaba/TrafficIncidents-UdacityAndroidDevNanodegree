package com.gaba.alex.trafficincidents.paid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.R;
import com.gaba.alex.trafficincidents.SettingsActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity {
    private Intent mPlacePickerIntent;
    private final int PLACE_PICKER_REQUEST = 1;
    private final String PREF_LAT = "lat";
    private final String PREF_LNG = "lng";
    private final String PREF_ADDRESS = "address";
    final String ACCOUNT_NAME = "Traffic Incidents Pro";
    final String ACCOUNT_TYPE = "com.gaba.alex.paid.traffic_incidents";
    final String AUTHORITY = IncidentsProvider.AUTHORITY;
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccount = createSyncAccount();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String address = preferences.getString(PREF_ADDRESS, "Tap to choose a location");
        TextView addressTextView = (TextView)findViewById(R.id.address);
        addressTextView.setText(address);
        if (preferences.contains(PREF_LAT) && preferences.contains(PREF_LNG)) {
            Double lat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
            Double lng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
            builder.setLatLngBounds(new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng)));
        }

        try {
            mPlacePickerIntent = builder.build(this);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(mPlacePickerIntent, PLACE_PICKER_REQUEST);
            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_refresh) {
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
            ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

            Toast.makeText(this, "Refreshing...", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Account createSyncAccount() {
        Account appAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        if (accountManager.addAccountExplicitly(appAccount, null, null)) {
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(appAccount, AUTHORITY, true);
        }
        return appAccount;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_LAT, selectedPlace.getLatLng().latitude + "");
                editor.putString(PREF_LNG, selectedPlace.getLatLng().longitude + "");
                editor.putString(PREF_ADDRESS, selectedPlace.getAddress().toString());
                editor.apply();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Double lat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
                Double lng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
                String address = preferences.getString(PREF_ADDRESS, "Location");
                TextView addressTextView = (TextView)findViewById(R.id.address);
                addressTextView.setText(address);
                builder.setLatLngBounds(new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng)));
                try {
                    mPlacePickerIntent = builder.build(this);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}