package com.gaba.alex.trafficincidents.Data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = IncidentsDatabase.VERSION, packageName = "com.gaba.alex.trafficincidents.Data.Provider")
public class IncidentsDatabase {
    private IncidentsDatabase(){}

    public static final int VERSION = 1;

    @Table(IncidentsColumns.class) public static final String INCIDENTS = "incidents";
}