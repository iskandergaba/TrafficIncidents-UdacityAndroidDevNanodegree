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

import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = IncidentsDatabase.VERSION)
public class IncidentsDatabase {
    private IncidentsDatabase(){}

    public static final int VERSION = 4;

    @Table(IncidentsColumns.class) public static final String INCIDENTS = "incidents";
    @Table(SettingsColumns.class) public static final String SETTINGS = "settings";

    public static final String[] _MIGRATIONS = {
            "ALTER TABLE " + INCIDENTS +
                    "\nADD COLUMN " + IncidentsColumns.TO_LAT + " REAL NOT NULL DEFAULT '0.0'",
            "ALTER TABLE " + INCIDENTS +
                    "\nADD COLUMN " + IncidentsColumns.TO_LNG + " REAL NOT NULL DEFAULT '0.0'"
    };

    @OnUpgrade
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = _MIGRATIONS[i - 2];
            db.beginTransaction();
            try {
                db.execSQL(migration);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                break;
            } finally {
                db.endTransaction();
            }
        }
    }
}