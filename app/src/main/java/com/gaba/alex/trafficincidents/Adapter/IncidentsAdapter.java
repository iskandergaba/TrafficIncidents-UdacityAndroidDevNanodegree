package com.gaba.alex.trafficincidents.Adapter;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.text.MessageFormat;
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
        final String description = mContext.getString(R.string.local_description) + ": " + cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.DESCRIPTION));


        TextView typeTextView = (TextView) view.findViewById(R.id.incident_type);
        if (typeTextView != null) {
            typeTextView.setText(Utility.getIncidentType(mContext, type));
        }

        TextView severityTextView = (TextView) view.findViewById(R.id.incident_severity);
        if (severityTextView != null) {
            severityTextView.setText(Utility.getIncidentSeverity(mContext, severity));
        }

        TextView roadClosedTextView = (TextView) view.findViewById(R.id.incident_road_closed);
        if (roadClosedTextView != null) {
            roadClosedTextView.setText(MessageFormat.format("{0}{1}",
                    mContext.getString(R.string.road_closed) + ": ",
                    roadClosed ? mContext.getString(R.string.yes) : mContext.getString(R.string.no)));
        }

        TextView dateTextView = (TextView) view.findViewById(R.id.incident_update);
        if (dateTextView != null) {
            Date date = new Date(dateInMillis);
            DateFormat formatter = DateFormat.getInstance();
            String dateStr = mContext.getString(R.string.estimated_end_date) + ": " + formatter.format(date);
            dateTextView.setText(dateStr);
        }

        TextView descriptionTextView = (TextView) view.findViewById(R.id.incident_description);
        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }

        ImageView showOnMapImageView = (ImageView) view.findViewById(R.id.show_on_map_button);
        showOnMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Utility.buildShowOnMapIntent(lat, lng);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        ImageView shareImageView = (ImageView) view.findViewById(R.id.share_button);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareText = Utility.getIncidentType(mContext, type) + "\n\n" + description + "\n\n"
                        + mContext.getString(R.string.severity) + ": " + Utility.getIncidentSeverity(mContext, severity)
                        + "\n\n" + mContext.getString(R.string.road_closed) + ": "
                        + (roadClosed ?  mContext.getString(R.string.yes) : mContext.getString(R.string.no))
                        + "\n\n" + mContext.getString(R.string.provided_by) + ": " + mContext.getString(R.string.app_name);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }
}