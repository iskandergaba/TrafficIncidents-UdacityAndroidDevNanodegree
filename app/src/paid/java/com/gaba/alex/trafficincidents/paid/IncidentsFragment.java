/*
Copyright 2016 Iskander Gaba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.gaba.alex.trafficincidents.paid;

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
import com.melnykov.fab.FloatingActionButton;

public class IncidentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String PREF_LAT = "lat";
    final String PREF_LNG = "lng";
    private IncidentsAdapter mAdapter;
    SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_incidents, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(getString(R.string.pref_search_range_key)) || key.equals(PREF_LAT) || key.equals(PREF_LNG)) {
                    restartLoader();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new IncidentsAdapter(getActivity().getBaseContext(), R.layout.list_view_item, null);
        listView.setEmptyView(rootView.findViewById(R.id.empty_view));
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
        double range = Double.parseDouble(preferences.getString(getString(R.string.pref_search_range_key), "0.05"));
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