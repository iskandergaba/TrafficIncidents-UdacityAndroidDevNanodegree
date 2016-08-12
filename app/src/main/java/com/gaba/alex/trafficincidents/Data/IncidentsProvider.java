package com.gaba.alex.trafficincidents.Data;

import android.net.Uri;

import com.gaba.alex.trafficincidents.BuildConfig;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = IncidentsProvider.AUTHORITY, database = IncidentsDatabase.class)
public class IncidentsProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String INCIDENTS = "incidents";
        String SETTINGS = "settings";
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

    @TableEndpoint(table = IncidentsDatabase.SETTINGS)
    public static class Settings {
        @ContentUri(
                path = Path.SETTINGS,
                type = "vnd.android.cursor.dir/setting"
        )
        public static final Uri CONTENT_URI = buildUri(Path.SETTINGS);
    }
}