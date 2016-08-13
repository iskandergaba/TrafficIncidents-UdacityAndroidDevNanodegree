package com.gaba.alex.trafficincidents.free;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gaba.alex.trafficincidents.Adapter.IncidentsAdapter;
import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;

public class IncidentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String PREF_LAT = "lat";
    final String PREF_LNG = "lng";
    private IncidentsAdapter mAdapter;
    SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;
    AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_incidents, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("prefSearchRange") || key.equals(PREF_LAT) || key.equals(PREF_LNG)) {
                    restartLoader();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new IncidentsAdapter(getActivity().getBaseContext(), R.layout.list_view_item, null);
        listView.setAdapter(mAdapter);
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);
        super.onDestroy();
    }

    private void restartLoader() {
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double lat = Double.parseDouble(preferences.getString(PREF_LAT, "0"));
        double lng = Double.parseDouble(preferences.getString(PREF_LNG, "0"));
        double range = Double.parseDouble(preferences.getString("prefSearchRange", "0.05"));
        String selection = "ABS(" + IncidentsColumns.LAT + " - " +  lat + ") <= " + range +
                " AND ABS(" + IncidentsColumns.LNG + " - " +  lng + ") <= " + range;
        Uri uri = IncidentsProvider.Incidents.CONTENT_URI;
        return new CursorLoader(getActivity(), uri, null, selection, null, IncidentsColumns.SEVERITY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}