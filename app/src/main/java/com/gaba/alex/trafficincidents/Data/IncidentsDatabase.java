package com.gaba.alex.trafficincidents.Data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = IncidentsDatabase.VERSION)
public class IncidentsDatabase {
    private IncidentsDatabase(){}

    public static final int VERSION = 2;

    @Table(IncidentsColumns.class) public static final String INCIDENTS = "incidents";
    @Table(SettingsColumns.class) public static final String SETTINGS = "settings";
}