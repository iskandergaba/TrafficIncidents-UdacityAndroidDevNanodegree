package com.gaba.alex.trafficincidents.Adapter;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.R;

import java.text.DateFormat;
import java.util.Date;

public class IncidentsAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private int layout;
    private final LayoutInflater inflater;

    private final String[] TYPE_CODES = {"Accident", "Congestion", "Disabled Vehicle",
            "Mass Transit", "Miscellaneous", "Other News", "Planned Event","Road Hazard",
            "Construction", "Alert", "Weather"};
    private final String[] SEVERITY_CODES = {"Low Impact", "Minor", "Moderate", "Serious"};

    public IncidentsAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, 0);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        int type = cursor.getInt(cursor.getColumnIndexOrThrow(IncidentsColumns.TYPE));
        int severity = cursor.getInt(cursor.getColumnIndexOrThrow(IncidentsColumns.SEVERITY));
        final double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(IncidentsColumns.LAT));
        final double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(IncidentsColumns.LNG));
        final long dateInMillis = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.END_DATE)));
        boolean roadClosed = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.ROAD_CLOSED)));
        final String description = "Local description: " +  cursor.getString(cursor.getColumnIndexOrThrow(IncidentsColumns.DESCRIPTION));


        TextView typeTextView = (TextView) view.findViewById(R.id.incident_type);
        if (typeTextView != null) {
            typeTextView.setText(TYPE_CODES[type - 1]);
        }

        TextView severityTextView = (TextView) view.findViewById(R.id.incident_severity);
        if (severityTextView != null) {
            severityTextView.setText(SEVERITY_CODES[severity - 1]);
        }

        TextView roadClosedTextView = (TextView) view.findViewById(R.id.incident_road_closed);
        if (roadClosedTextView != null) {
            if (roadClosed) {
                roadClosedTextView.setText("Road Closed: Yes");
            } else {
                roadClosedTextView.setText("Road Closed: No");
            }
        }

        TextView dateTextView = (TextView) view.findViewById(R.id.incident_update);
        if (dateTextView != null) {
            Date date = new Date(dateInMillis);
            DateFormat formatter = DateFormat.getInstance();
            String dateStr = "Estimated end date: " + formatter.format(date);
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format("geo:%s,%s", lat, lng)));
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
                String shareText = description;
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

            }
        });

    }

}
