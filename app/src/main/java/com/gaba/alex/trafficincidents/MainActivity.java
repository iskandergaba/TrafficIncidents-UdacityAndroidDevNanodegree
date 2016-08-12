package com.gaba.alex.trafficincidents;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.melnykov.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Intent mPlacePickerIntent;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int PLACE_PICKER_REQUEST = 1;
    private final String PREF_LAT = "lat";
    private final String PREF_LNG = "lng";
    private final String PREF_ADDRESS = "address";
    private String mAccountName;
    private String mAccountType;
    final String AUTHORITY = IncidentsProvider.AUTHORITY;
    private static final long SECONDS_PER_HOUR = 3600L;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccountName = getString(R.string.app_name);
        mAccountType = getString(R.string.account_type);
        checkGooglePlayServices();
        try {
            Utility.updateSettings(getApplicationContext());
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        mAccount = createSyncAccount();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("lat") || key.equals("lng")) {
                    try {
                        Utility.updateSettings(getApplicationContext());
                    } catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else if (key.equals("prefAutoRefresh")) {
                    configurePeriodicSync(mAccount);
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        if (preferences.contains(PREF_LAT) && preferences.contains(PREF_LNG)) {
            double lat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
            double lng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
            builder.setLatLngBounds(new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng)));
        }
        configurePeriodicSync(mAccount);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String address = preferences.getString(PREF_ADDRESS, "Tap to choose a location");
        TextView addressTextView = (TextView)findViewById(R.id.address);
        addressTextView.setText(address);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_refresh:
                refresh();
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);
                double newLat = selectedPlace.getLatLng().latitude;
                double newLng = selectedPlace.getLatLng().longitude;
                double lat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
                double lng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
                if (newLat != lat || newLng != lng) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREF_LAT, newLat + "");
                    editor.putString(PREF_LNG, newLng + "");
                    editor.putString(PREF_ADDRESS, selectedPlace.getAddress().toString());
                    editor.apply();
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    String address = preferences.getString(PREF_ADDRESS, "Tap to choose a location");
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

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);
        super.onDestroy();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                if (dialog != null) {
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            if (ConnectionResult.SERVICE_INVALID == resultCode) {
                                finish();
                            }
                        }
                    });
                }
            }
            return false;
        }
        return true;
    }

    private Account createSyncAccount() {
        Account appAccount = new Account(mAccountName, mAccountType);
        AccountManager accountManager = AccountManager.get(this);
        if (accountManager.addAccountExplicitly(appAccount, null, null)) {
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(appAccount, AUTHORITY, true);
        }
        return appAccount;
    }

    private void refresh() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    private void configurePeriodicSync(Account appAccount) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int hourlySyncInterval = Integer.parseInt(preferences.getString("prefAutoRefresh", "6"));
        if (hourlySyncInterval == 0) {
            ContentResolver.setSyncAutomatically(appAccount, AUTHORITY, false);
        } else {
            //for testing, replace with:
            //long SyncInterval = 60L;
            long syncInterval = hourlySyncInterval * SECONDS_PER_HOUR;
            ContentResolver.setSyncAutomatically(appAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(appAccount, AUTHORITY, Bundle.EMPTY, syncInterval);
            Log.v("fuck", syncInterval + "");
        }
    }
}