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