package com.gaba.alex.trafficincidents;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.melnykov.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Intent mPlacePickerIntent;
    private final int PLACE_PICKER_REQUEST = 1;
    private final String PREF_LAT = "lat";
    private final String PREF_LNG = "lng";
    private final String PREF_ADDRESS = "address";
    private String mAccountName;
    private String mAccountType;
    final String AUTHORITY = IncidentsProvider.AUTHORITY;
    private static final long SECONDS_PER_HOUR = 3600L;
    private double mLat;
    private double mLng;
    private double mRange;
    private int mSeverity;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccountName = getString(R.string.app_name);
        mAccountType = getString(R.string.account_type);
        mAccount = createSyncAccount();
        configurePeriodicSync(mAccount);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case "prefAutoRefresh":
                        configurePeriodicSync(mAccount);
                        break;
                    case "prefNotifications":
                        mSeverity = Integer.parseInt(preferences.getString("prefNotifications", "4"));
                        configurePeriodicSync(mAccount);
                        break;
                    case "prefSearchRange":
                        mRange = Double.parseDouble(preferences.getString("prefSearchRange", "0.05"));
                        configurePeriodicSync(mAccount);
                        break;
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        String address = preferences.getString(PREF_ADDRESS, "Tap to choose a location");
        TextView addressTextView = (TextView)findViewById(R.id.address);
        addressTextView.setText(address);
        if (preferences.contains(PREF_LAT) && preferences.contains(PREF_LNG)) {
            mLat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
            mLng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
            builder.setLatLngBounds(new LatLngBounds(new LatLng(mLat, mLng), new LatLng(mLat, mLng)));
        }
        mRange = Double.parseDouble(preferences.getString("prefSearchRange", "0.05"));
        mSeverity = Integer.parseInt(preferences.getString("prefNotifications", "4"));

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
            refresh();
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);
        super.onDestroy();
    }

    public void refresh() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putDouble(PREF_LAT, mLat);
        settingsBundle.putDouble(PREF_LNG, mLng);
        settingsBundle.putDouble("prefSearchRange", mRange);
        settingsBundle.putInt("prefNotifications", mSeverity);
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    private Account createSyncAccount() {
        Account appAccount = new Account(mAccountName, mAccountType);
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        if (accountManager.addAccountExplicitly(appAccount, null, null)) {
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(appAccount, AUTHORITY, true);
        }
        return appAccount;
    }

    private void configurePeriodicSync(Account appAccount) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putDouble(PREF_LAT, mLat);
        settingsBundle.putDouble(PREF_LNG, mLng);
        settingsBundle.putDouble("prefSearchRange", mRange);
        settingsBundle.putInt("prefNotifications", mSeverity);
        int hourlySyncInterval = Integer.parseInt(preferences.getString("prefAutoRefresh", "6"));
        long SyncInterval = hourlySyncInterval * SECONDS_PER_HOUR;
        if (hourlySyncInterval > 0) {
            ContentResolver.addPeriodicSync(appAccount, AUTHORITY, settingsBundle, SyncInterval);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);
                double newLat = selectedPlace.getLatLng().latitude;
                double newLng = selectedPlace.getLatLng().longitude;
                if (newLat != mLat || newLng != mLng) {
                    mLat = newLat;
                    mLng = newLng;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREF_LAT, newLat + "");
                    editor.putString(PREF_LNG, newLng + "");
                    editor.putString(PREF_ADDRESS, selectedPlace.getAddress().toString());
                    editor.apply();
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    String address = preferences.getString(PREF_ADDRESS, "Location");
                    TextView addressTextView = (TextView)findViewById(R.id.address);
                    addressTextView.setText(address);
                    builder.setLatLngBounds(new LatLngBounds(new LatLng(newLat, newLng), new LatLng(newLat, newLng)));
                    try {
                        mPlacePickerIntent = builder.build(this);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    refresh();
                }

            }
        }
    }
}