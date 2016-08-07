package com.gaba.alex.trafficincidents.Data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = IncidentsProvider.AUTHORITY,
        database = IncidentsDatabase.class,
        packageName = "com.gaba.alex.trafficincidents.Data.Provider")
public class IncidentsProvider {
    public static final String AUTHORITY = "com.gaba.alex.traffic_incidents.Data.IncidentsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String INCIDENTS = "incidents";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = IncidentsDatabase.INCIDENTS)
    public static class Incidents {
        @ContentUri(
                path = Path.INCIDENTS,
                type = "vnd.android.cursor.dir/incident"
        )
        public static final Uri CONTENT_URI = buildUri(Path.INCIDENTS);
    }
}