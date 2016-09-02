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
import android.view.WindowManager;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        if (!getIntent().getBooleanExtra("fromWidget", false)) {
            overridePendingTransition(R.anim.appear_from_bottom, R.anim.hold);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!getIntent().getBooleanExtra("fromWidget", false)) {
            overridePendingTransition(R.anim.hold, R.anim.disappear_to_bottom);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        final double toLat = getIntent().getDoubleExtra("toLat", 0);
        final double toLng = getIntent().getDoubleExtra("toLng", 0);
        final int severity = getIntent().getIntExtra("severity", 1);
        final String description = getIntent().getStringExtra("description");
        final LatLng incidentStart = new LatLng(lat, lng);
        final LatLng incidentEnd = new LatLng(toLat, toLng);
        final LatLngBounds incidentBounds = getIncidentBounds(lat, lng, toLat, toLng);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.addMarker(new MarkerOptions().position(incidentStart)
                        .title(getString(R.string.start) + " - " + description)
                        .icon(getIncidentIcon(severity)));
                if (toLat != 0 || toLng != 0) {
                    mMap.addMarker(new MarkerOptions().position(incidentEnd)
                            .title(getString(R.string.end) + " - " + description)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(incidentBounds, 16));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incidentStart, 16));
                }
            }
        });
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