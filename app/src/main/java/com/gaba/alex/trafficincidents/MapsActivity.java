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

package com.gaba.alex.trafficincidents;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        double toLat = getIntent().getDoubleExtra("toLat", 0);
        double toLng = getIntent().getDoubleExtra("toLng", 0);
        int severity = getIntent().getIntExtra("severity", 1);
        String description = getIntent().getStringExtra("description");
        LatLng incidentStart = new LatLng(lat, lng);
        LatLng incidentEnd = new LatLng(toLat, toLng);
        LatLngBounds incidentBounds = getIncidentBounds(lat, lng, toLat, toLng);

        mMap.addMarker(new MarkerOptions().position(incidentStart)
                .title(getString(R.string.start) + " - " + description)
                .icon(getIncidentIcon(severity)));
        if (toLat != 0 || toLng != 0) {
            mMap.addMarker(new MarkerOptions().position(incidentEnd)
                    .title(getString(R.string.end) + " - " + description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(incidentBounds, 16));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incidentStart, 16));
        }
    }

    private LatLngBounds getIncidentBounds (double lat, double lng, double toLat, double toLng) {
        LatLng startBounds = new LatLng((lat < toLat) ? lat - 0.0005 : toLat - 0.0005, (lng < toLng) ? lng - 0.0005 : toLng - 0.0005);
        LatLng endBounds = new LatLng((lat > toLat) ? lat + 0.0005 : toLat + 0.0005, (lng > toLng) ? lng + 0.0005 : toLng + 0.0005);
        return new LatLngBounds(startBounds, endBounds);
    }

    private BitmapDescriptor getIncidentIcon(int severity) {
        BitmapDescriptor[] colors = {BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)};
        return colors[severity - 1];
    }
}
