package com.gaba.alex.trafficincidents;

import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(getString(R.string.pref_notifications_key)) || key.equals(getString(R.string.pref_auto_refresh_key))) {
                    try {
                        Utility.updateSettings(getApplicationContext());
                    } catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else if (key.equals(getString(R.string.pref_search_range_key))) {
                    try {
                        Utility.updateSettings(getApplicationContext());
                        Utility.updateWidget(getApplicationContext());
                    }catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}