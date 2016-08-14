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

package com.gaba.alex.trafficincidents.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.R;
import com.gaba.alex.trafficincidents.Utility;

import java.text.DateFormat;
import java.util.Date;

public class IncidentsAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private int mLayout;
    private final LayoutInflater mInflater;

    public IncidentsAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c, new String[0], new int[0], 0);
        mLayout = layout;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(mLayout, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        final int type = cursor.getInt(cursor.getColumnIndexOrThrow(IncidentsColumns.TYPE));
        final int severity = cursor.getInt(cursor.getColumnIndexOrThrow(IncidentsColumns.SEVERITY));
        final double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(IncidentsColumns.LAT));
        final double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(IncidentsColumns.LNG));
        final long dateInMillis = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.END_DATE)));
        final boolean roadClosed = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.ROAD_CLOSED)));
        final String description = cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.DESCRIPTION));


        TextView typeTextView = (TextView) view.findViewById(R.id.incident_type);
        if (typeTextView != null) {
            typeTextView.setText(Utility.getIncidentType(mContext, type));
        }

        TextView severityTextView = (TextView) view.findViewById(R.id.incident_severity);
        if (severityTextView != null) {
            severityTextView.setText(Utility.getIncidentSeverity(mContext, severity));
            severityTextView.setTextColor(ContextCompat.getColor(mContext, Utility.getIncidentColor(severity)));
        }

        TextView roadClosedTextView = (TextView) view.findViewById(R.id.incident_road_closed);
        if (roadClosedTextView != null) {
            roadClosedTextView.setText(String.format("%s: ", mContext.getString(R.string.road_closed)));
        }

        TextView roadClosedContentTextView = (TextView) view.findViewById(R.id.incident_road_closed_content);
        if (roadClosedContentTextView != null) {
            roadClosedContentTextView.setText(roadClosed ? mContext.getString(R.string.yes) : mContext.getString(R.string.no));
        }

        TextView dateTextView = (TextView) view.findViewById(R.id.incident_end_date);
        if (dateTextView != null) {
            dateTextView.setText(String.format("%s: ", mContext.getString(R.string.estimated_end_date)));
        }

        TextView dateContentTextView = (TextView) view.findViewById(R.id.incident_end_date_content);
        if (dateContentTextView != null) {
            dateContentTextView.setText(DateFormat.getDateInstance().format(new Date(dateInMillis)));
        }

        TextView descriptionTextView = (TextView) view.findViewById(R.id.incident_description);
        if (descriptionTextView != null) {
            descriptionTextView.setText(String.format("%s: ", mContext.getString(R.string.local_description)));
        }

        TextView descriptionContentTextView = (TextView) view.findViewById(R.id.incident_description_content);
        if (descriptionContentTextView != null) {
            descriptionContentTextView.setText(description);
        }

        ImageView showOnMapImageView = (ImageView) view.findViewById(R.id.show_on_map_button);
        showOnMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Utility.buildShowOnMapIntent(lat, lng);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        ImageView shareImageView = (ImageView) view.findViewById(R.id.share_button);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareText = Utility.getIncidentType(mContext, type)
                        + "\n\n" + mContext.getString(R.string.local_description) + ": " + description
                        + "\n\n" + mContext.getString(R.string.severity) + ": " + Utility.getIncidentSeverity(mContext, severity)
                        + "\n\n" + mContext.getString(R.string.road_closed) + ": " + (roadClosed ?  mContext.getString(R.string.yes) : mContext.getString(R.string.no))
                        + "\n\n" + mContext.getString(R.string.provided_by) + ": " + mContext.getString(R.string.app_name);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }
}